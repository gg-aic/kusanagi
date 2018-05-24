package aic.gas.sc.gg_bot.replay_parser.service.implementation;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.FeatureContainerHeaders;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.MapSizeEnums;
import aic.gas.sc.gg_bot.abstract_bot.model.decision.NextActionEnumerations;
import aic.gas.sc.gg_bot.abstract_bot.model.features.FeatureContainerHeader;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentTypeID;
import aic.gas.sc.gg_bot.mas.model.metadata.DesireKeyID;
import aic.gas.sc.gg_bot.replay_parser.configuration.Configuration;
import aic.gas.sc.gg_bot.replay_parser.configuration.DecisionLearningConfiguration;
import aic.gas.sc.gg_bot.replay_parser.model.irl.DecisionState;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.State;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.Trajectory;
import aic.gas.sc.gg_bot.replay_parser.model.tracking.TrajectoryWrapper;
import aic.gas.sc.gg_bot.replay_parser.service.IDecisionLearnerService;
import aic.gas.sc.gg_bot.replay_parser.service.IPolicyLearningService;
import aic.gas.sc.gg_bot.replay_parser.service.IStorageService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of IDecisionLearnerService
 */
@Slf4j
public class DecisionLearnerService implements IDecisionLearnerService {

  private static final List<MapSizeEnums> MAP_SIZES = Stream.of(MapSizeEnums.values())
      .collect(Collectors.toList());
  private static final List<ARace> RACES = Stream.of(ARace.values())
      .filter(race -> !race.equals(ARace.UNKNOWN))
      .collect(Collectors.toList());
  private final IStorageService storageService = StorageService.getInstance();
  private final IPolicyLearningService policyLearningService = new PolicyLearningService();

  @Override
  public void learnDecisionMakers(int parallelismLevel) {
    List<Tuple> toLearn = MAP_SIZES.stream().flatMap(mapSize -> RACES.stream().flatMap(
        race -> storageService.getParsedAgentTypesWithDesiresTypesContainedInStorage(mapSize, race)
            .entrySet().stream().flatMap(entry -> entry.getValue().stream()
                .map(desireKeyID -> new Tuple(mapSize, race, entry.getKey(), desireKeyID)))))
        .filter(tuple -> {
          String path = storageService
              .getLearntDecisionPath(tuple.agentTypeID, tuple.desireKeyID, tuple.mapSize,
                  tuple.race);
          return !new File(path).exists();
        })
        .collect(Collectors.toList());

    //execute
    List<MyTask> tasks = toLearn.parallelStream()
        .map(MyTask::new)
        .collect(Collectors.toList());
    log.info("Clustering finished...");

    ExecutorService executor = Executors.newFixedThreadPool(parallelismLevel);
    tasks.forEach(executor::execute);

    executor.shutdown();

    log.info("Finished...");
  }

  /**
   * Do clustering and prepare data set for policy learning. Learn policy
   */
  @AllArgsConstructor
  public class MyTask implements Runnable {

    private final Tuple tuple;

    @Override
    public void run() {
      String path = storageService
          .getLearntDecisionPath(tuple.agentTypeID, tuple.desireKeyID, tuple.mapSize,
              tuple.race);

      if (new File(path).exists()) {
        log.info("File " + path + " exists, skipping.");
        return;
      }

      // touch file right away
      try {
        File file = new File(path);
        file.getParentFile().mkdirs();
        Files.createFile(file.toPath());
        log.info("Created placeholder " + path);
      } catch (IOException e) {
        log.warn("Failed to create " + path);
      }

      log.info("Starting computation for " + path);

      Configuration configuration = DecisionLearningConfiguration
          .getConfiguration(tuple.agentTypeID, tuple.desireKeyID);

      List<TrajectoryWrapper> trajectoriesWrapped = storageService
          .getRandomListOfTrajectories(tuple.agentTypeID, tuple.desireKeyID, tuple.getMapSize(),
              tuple.getRace(), -1);

      //load headers
      Optional<FeatureContainerHeader> header = FeatureContainerHeaders
          .getHeader(tuple.agentTypeID, tuple.desireKeyID);
      String[] headers;
      if (header.isPresent()) {
        headers = header.get().getHeaders();
      } else {

        log.error(
            "Could not find header for " + tuple.agentTypeID.getName() + ", " + tuple.desireKeyID
                .getName());

        //get number of features for state
        int numberOfFeatures = trajectoriesWrapped.get(0).getTrajectory().getNumberOfFeatures();
        headers = new String[numberOfFeatures];
        IntStream.range(0, numberOfFeatures).boxed()
            .forEach(integer -> headers[integer] = "N/A");
      }

      //encountered states
      List<State> states = trajectoriesWrapped.stream()
          .map(TrajectoryWrapper::getTrajectory)
          .map(Trajectory::getStates)
          .flatMap(Collection::stream)
          .collect(Collectors.toList());

      log.info("Number of trajectories: " + trajectoriesWrapped.size()
          + " with cardinality of features: " + headers.length + " for " + tuple.desireKeyID
          .getName() + " of " + tuple.agentTypeID.getName() + " on " + tuple.mapSize + " with "
          + tuple.race + ". Number of states: " + states.size() + ". With configuration "
          + configuration.toString());

      //create states with corresponding mean
//      List<DecisionState> decisionStates = IntStream.range(0, classes.size()).boxed()
//          .map(integer -> new DecisionState(classes.get(integer), integer))
//          .collect(Collectors.toList());

      log.info("Creating MDP... for " + tuple.desireKeyID.getName() + " of " + tuple.agentTypeID
          .getName() + " on " + tuple.mapSize + " with " + tuple.race);

      //build transitions pair lists
//      List<List<StateTransitionPair>> transitionPairLists = buildTransitionsPairsFromTrajectories(
//          trajectoriesWrapped.stream()
//              .map(TrajectoryWrapper::getTrajectory), normalizers, decisionStates)
//          .collect(Collectors.toList());

      //learn transitions
//      transitionPairLists.forEach(stateTransitionPairs -> {
//
//        StateTransitionPair stateTransitionPair = stateTransitionPairs.get(0);
//
//        //there is no transition in last state
//        for (int i = 1; i < stateTransitionPairs.size(); i++) {
//          StateTransitionPair nextPair = stateTransitionPairs.get(i);
//          stateTransitionPair.getDecisionState()
//              .addTransition(stateTransitionPair.nextActionEnumeration, nextPair.decisionState);
//          stateTransitionPair = nextPair;
//        }
//      });

      //create episodes to learn from
//      List<Episode> episodesToLearnFrom = buildTransitionsPairsFromTrajectories(
//          trajectoriesWrapped.stream()
//              .filter(TrajectoryWrapper::isUsedToLearnPolicy)
//              .map(TrajectoryWrapper::getTrajectory),
//          normalizers, decisionStates)
//          .map(stateTransitionPairs -> {
//
//            Episode episode = new Episode();
//
//            StateTransitionPair stateTransitionPair = stateTransitionPairs.get(0);
//            episode.addState(stateTransitionPair.decisionState);
//
//            //there is no transition in last state
//            for (int i = 1; i < stateTransitionPairs.size(); i++) {
//              StateTransitionPair nextPair = stateTransitionPairs.get(i);
//
//              //add transition to episode. Do not add last transition if agent is committed. add positive reward only
//              episode.transition(new SimpleAction(stateTransitionPair.nextActionEnumeration.NAME()),
//                  nextPair.decisionState,
//                  Math.abs(DecisionDomainGenerator.getRandomRewardInInterval(configuration)));
//
//              stateTransitionPair = nextPair;
//            }
//            return episode;
//          })
//          .collect(Collectors.toList());

      //init transition probabilities
//      decisionStates.forEach(DecisionState::initTransitionProbabilities);
//      DecisionModel decisionModel = new DecisionModel(decisionStates.size());

      //learn policy
//      DecisionDomainGenerator decisionDomainGenerator = new DecisionDomainGenerator(decisionModel,
//          configuration);
//      SADomain domain = decisionDomainGenerator.generateDomain();
//
//      log.info("Learning policy for " + tuple.desireKeyID.getName() + " of " + tuple.agentTypeID
//          .getName() + " on " + tuple.mapSize + " with " + tuple.race + ". Using examples: #"
//          + episodesToLearnFrom.size());

      //do IRL to recover reward function + do RL to learn policy then
//      BatchIterator batchIterator = new BatchIterator(
//          configuration.getCountOfTrajectoriesPerIRLBatch(), episodesToLearnFrom);
//      burlap.behavior.policy.Policy policy = policyLearningService
//          .learnPolicy(domain, batchIterator.sampleBatchFromEpisodes(), configuration,
//              classes.size());
//
//      //form decision point data structure and store it
//      Policy mdpForDecisionWithPolicy = createDecisionPoint(normalizers,
//          decisionStates,
//          policy);

      //are all actions present
//      Set<NextActionEnumerations> actions = mdpForDecisionWithPolicy.getStates().stream()
//          .flatMap(stateWithPolicy -> stateWithPolicy.getNextActions().entrySet().stream()
//              .filter(entry -> entry.getValue() > 0)
//              .map(Entry::getKey))
//          .distinct()
//          .collect(Collectors.toSet());
//      if (Arrays.stream(NextActionEnumerations.values())
//          .anyMatch(nextActionEnumerations -> !actions.contains(nextActionEnumerations))) {
//        log.error("Missing action in " + tuple.mapSize.NAME() + ", " + tuple.race.NAME() + ", "
//            + tuple.agentTypeID
//            .getName() + ", " + tuple.desireKeyID.getName());
//      }

      try {
//        storageService.storeLearntDecision(mdpForDecisionWithPolicy, tuple.getAgentTypeID(),
//            tuple.getDesireKeyID(), tuple.getMapSize(), tuple.getRace());
        log.info(
            "Successfully learn decisions for " + tuple.getDesireKeyID().getName() + " of " + tuple
                .getAgentTypeID().getName() + " on " + tuple.mapSize + " with " + tuple.race);
      } catch (Exception e) {
        e.printStackTrace();
        log.error(e.getLocalizedMessage());
      }
    }
  }

  @AllArgsConstructor
  @Getter
  private static class Tuple {

    private final MapSizeEnums mapSize;
    private final ARace race;
    private final AgentTypeID agentTypeID;
    private final DesireKeyID desireKeyID;
  }

  @AllArgsConstructor
  @Getter
  private static class StateTransitionPair {

    private DecisionState decisionState;
    private NextActionEnumerations nextActionEnumeration;
  }

}
