package aic.gas.sc.gg_bot.bot.model.agent.types.implementation.player;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes.BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.AVAILABLE_GAS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.AVAILABLE_MINERALS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_BUILDING_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_RACE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_STATIC_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.ENEMY_STATIC_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ENEMY_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_PLAYER;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_START_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LAST_TIME_SCOUTED;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCKED_BUILDINGS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.LOCKED_UNITS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OUR_BASE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_BUILDING_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_STATIC_AIR_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.OWN_STATIC_GROUND_FORCE_STATUS;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.POPULATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.POPULATION_LIMIT;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.TECH_TO_RESEARCH;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.TIME_OF_HOLD_COMMAND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.UPGRADE_STATUS;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ESTIMATE_ENEMY_FORCE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.ESTIMATE_OUR_FORCE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.READ_PLAYERS_DATA;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.REASON_ABOUT_BASES;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.UPDATE_ENEMY_RACE;
import static aic.gas.sc.gg_bot.bot.model.DesiresKeys.WORKER_SCOUT;

import aic.gas.sc.gg_bot.abstract_bot.model.UnitTypeStatus;
import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.APlayer;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ARace;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.AUnit;
import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.UnitWrapperFactory;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypePlayer;
import aic.gas.sc.gg_bot.mas.model.knowledge.WorkingMemory;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithCommand;
import aic.gas.sc.gg_bot.mas.model.metadata.agents.configuration.ConfigurationWithSharedDesire;
import aic.gas.sc.gg_bot.mas.model.planing.CommitmentDeciderInitializer;
import aic.gas.sc.gg_bot.mas.model.planing.command.ReasoningCommand;
import bwapi.Race;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerAgentType {

  public static final AgentTypePlayer PLAYER = AgentTypePlayer.builder()
      .agentTypeID(AgentTypes.PLAYER)
      .usingTypesForFacts(new HashSet<>(
          Arrays.asList(AVAILABLE_MINERALS, ENEMY_RACE, AVAILABLE_GAS, POPULATION_LIMIT, POPULATION,
              IS_PLAYER, BASE_TO_MOVE, IS_BASE_LOCATION, TIME_OF_HOLD_COMMAND)))
      .usingTypesForFactSets(
          new HashSet<>(Arrays.asList(UPGRADE_STATUS, TECH_TO_RESEARCH, OUR_BASE, ENEMY_BASE,
              OWN_AIR_FORCE_STATUS, OWN_BUILDING_STATUS, OWN_GROUND_FORCE_STATUS,
              ENEMY_AIR_FORCE_STATUS,
              ENEMY_BUILDING_STATUS,
              ENEMY_GROUND_FORCE_STATUS, LOCKED_UNITS, LOCKED_BUILDINGS,
              ENEMY_STATIC_AIR_FORCE_STATUS,
              ENEMY_STATIC_GROUND_FORCE_STATUS,
              OWN_STATIC_AIR_FORCE_STATUS, OWN_STATIC_GROUND_FORCE_STATUS)))
      .initializationStrategy(type -> {

        //send worker to scout
        ConfigurationWithSharedDesire sendWorkerScouting = ConfigurationWithSharedDesire.builder()
            .sharedDesireKey(WORKER_SCOUT)
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> memory.getReadOnlyMemoriesForAgentType(
                        AgentTypes.BASE_LOCATION)
                        .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            IS_START_LOCATION).get())
                        .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(
                            LAST_TIME_SCOUTED).isPresent())
                        .count() > 0
                        && memory.getReadOnlyMemoriesForAgentType(AgentTypes.DRONE).count() > 8)
                .useFactsInMemory(true)
                .build()
            )
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) -> memory.getReadOnlyMemoriesForAgentType(
                        AgentTypes.BASE_LOCATION)
                        .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            IS_START_LOCATION).get())
                        .filter(readOnlyMemory -> !readOnlyMemory.returnFactValueForGivenKey(
                            LAST_TIME_SCOUTED).isPresent())
                        .count() == 0)
                .useFactsInMemory(true)
                .build())
            .counts(1)
            .build();
        type.addConfiguration(WORKER_SCOUT, sendWorkerScouting);

        //read data from player
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf readPlayersData = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                APlayer aPlayer = memory.returnFactValueForGivenKey(IS_PLAYER).get();
                memory.updateFact(AVAILABLE_MINERALS, (double) aPlayer.getMinerals());
                memory.updateFact(AVAILABLE_GAS, (double) aPlayer.getGas());
                memory.updateFact(POPULATION_LIMIT, (double) aPlayer.getSupplyTotal());
                memory.updateFact(POPULATION, (double) aPlayer.getSupplyUsed());
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(READ_PLAYERS_DATA, readPlayersData);

        //estimate enemy force
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf estimateEnemyForce = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                Set<UnitTypeStatus> enemyBuildingsTypes = UnitWrapperFactory
                    .getStreamOfAllAliveEnemyUnits()
                    .filter(enemy -> enemy.getType().isBuilding())
                    .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                    .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                    .collect(Collectors.toSet());
                memory.updateFactSetByFacts(ENEMY_BUILDING_STATUS, enemyBuildingsTypes);
                memory.updateFactSetByFacts(ENEMY_STATIC_AIR_FORCE_STATUS,
                    enemyBuildingsTypes.stream()
                        .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                            .isMilitaryBuildingAntiAir())
                        .collect(Collectors.toSet()));
                memory.updateFactSetByFacts(ENEMY_STATIC_GROUND_FORCE_STATUS,
                    enemyBuildingsTypes.stream()
                        .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                            .isMilitaryBuildingAntiGround())
                        .collect(Collectors.toSet()));
                Set<UnitTypeStatus> enemyUnitsTypes = UnitWrapperFactory
                    .getStreamOfAllAliveEnemyUnits()
                    .filter(enemy -> !enemy.getType().isNotActuallyUnit() && !enemy.getType()
                        .isBuilding())
                    .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                    .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                    .collect(Collectors.toSet());
                memory.updateFactSetByFacts(ENEMY_AIR_FORCE_STATUS, enemyUnitsTypes.stream()
                    .filter(
                        unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackAirUnits())
                    .collect(Collectors.toSet()));
                memory.updateFactSetByFacts(ENEMY_GROUND_FORCE_STATUS, enemyUnitsTypes.stream()
                    .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                        .canAttackGroundUnits())
                    .collect(Collectors.toSet()));
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(ESTIMATE_ENEMY_FORCE, estimateEnemyForce);

        //estimate our force
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf estimateOurForce = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                Set<UnitTypeStatus> ownBuildingsTypes = UnitWrapperFactory
                    .getStreamOfAllAlivePlayersUnits()
                    .filter(enemy -> enemy.getType().isBuilding())
                    .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                    .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                    .collect(Collectors.toSet());
                memory.updateFactSetByFacts(OWN_BUILDING_STATUS, ownBuildingsTypes);
                memory.updateFactSetByFacts(OWN_STATIC_AIR_FORCE_STATUS, ownBuildingsTypes.stream()
                    .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                        .isMilitaryBuildingAntiAir())
                    .collect(Collectors.toSet()));
                memory
                    .updateFactSetByFacts(OWN_STATIC_GROUND_FORCE_STATUS, ownBuildingsTypes.stream()
                        .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                            .isMilitaryBuildingAntiGround())
                        .collect(Collectors.toSet()));
                Set<UnitTypeStatus> ownUnitsTypes = UnitWrapperFactory
                    .getStreamOfAllAlivePlayersUnits()
                    .filter(enemy -> !enemy.getType().isNotActuallyUnit() && !enemy.getType()
                        .isBuilding())
                    .collect(Collectors.groupingBy(AUnit::getType)).entrySet().stream()
                    .map(entry -> new UnitTypeStatus(entry.getKey(), entry.getValue().stream()))
                    .collect(Collectors.toSet());
                memory.updateFactSetByFacts(OWN_AIR_FORCE_STATUS, ownUnitsTypes.stream()
                    .filter(
                        unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper().canAttackAirUnits())
                    .collect(Collectors.toSet()));
                memory.updateFactSetByFacts(OWN_GROUND_FORCE_STATUS, ownUnitsTypes.stream()
                    .filter(unitTypeStatus -> unitTypeStatus.getUnitTypeWrapper()
                        .canAttackGroundUnits())
                    .collect(Collectors.toSet()));
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(ESTIMATE_OUR_FORCE, estimateOurForce);

        //enemy race
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf updateEnemyRace = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                Optional<Race> enemyRace = UnitWrapperFactory.getStreamOfAllAliveEnemyUnits().map(
                    enemy -> enemy.getType().getRace()).findAny();
                enemyRace.ifPresent(race -> memory.updateFact(ENEMY_RACE, ARace.getRace(race)));
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy(
                    (dataForDecision, memory) ->
                        UnitWrapperFactory.getStreamOfAllAliveEnemyUnits().count() > 0)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(UPDATE_ENEMY_RACE, updateEnemyRace);

        //bases
        ConfigurationWithCommand.WithReasoningCommandDesiredBySelf reasonAboutBases = ConfigurationWithCommand.
            WithReasoningCommandDesiredBySelf.builder()
            .commandCreationStrategy(intention -> new ReasoningCommand(intention) {
              @Override
              public boolean act(WorkingMemory memory) {
                memory.updateFactSetByFacts(OUR_BASE,
                    memory.getReadOnlyMemoriesForAgentType(BASE_LOCATION)
                        .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            IS_BASE).orElse(false))
                        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            IS_BASE_LOCATION).get())
                        .collect(Collectors.toSet()));
                memory.updateFactSetByFacts(ENEMY_BASE,
                    memory.getReadOnlyMemoriesForAgentType(BASE_LOCATION)
                        .filter(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            IS_ENEMY_BASE).orElse(false))
                        .map(readOnlyMemory -> readOnlyMemory.returnFactValueForGivenKey(
                            IS_BASE_LOCATION).get())
                        .collect(Collectors.toSet()));
                return true;
              }
            })
            .decisionInDesire(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .decisionInIntention(CommitmentDeciderInitializer.builder()
                .decisionStrategy((dataForDecision, memory) -> true)
                .build())
            .build();
        type.addConfiguration(REASON_ABOUT_BASES, reasonAboutBases);

      })
      .desiresWithIntentionToReason(
          new HashSet<>(Arrays.asList(READ_PLAYERS_DATA, ESTIMATE_ENEMY_FORCE,
              ESTIMATE_OUR_FORCE, UPDATE_ENEMY_RACE, REASON_ABOUT_BASES)))
      .desiresForOthers(new HashSet<>(Collections.singletonList(WORKER_SCOUT)))
      .build();
}
