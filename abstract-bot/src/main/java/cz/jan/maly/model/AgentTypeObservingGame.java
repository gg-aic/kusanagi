package cz.jan.maly.model;

import bwapi.Game;
import cz.jan.maly.model.agent.BWAgentInGame;
import cz.jan.maly.model.metadata.AgentType;
import cz.jan.maly.model.metadata.AgentTypeMakingObservations;
import cz.jan.maly.model.metadata.DesireKey;
import cz.jan.maly.model.metadata.FactKey;
import lombok.Builder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.jan.maly.model.BasicFactsKeys.IS_UNIT;
import static cz.jan.maly.model.BasicFactsKeys.REPRESENTS_UNIT;

/**
 * Type definition - agent type observing game
 * Created by Jan on 03-Apr-17.
 */
public class AgentTypeObservingGame extends AgentTypeMakingObservations<Game> {
    /**
     * Define agent type. Together with initial desires
     *
     * @param name
     * @param desiresForOthers
     * @param desiresWithAbstractIntention
     * @param desiresWithIntentionToAct
     * @param desiresWithIntentionToReason
     * @param usingTypesForFacts
     * @param usingTypesForFactSets
     * @param initializationStrategy
     */
    @Builder
    private AgentTypeObservingGame(String name, Set<DesireKey> desiresForOthers, Set<DesireKey> desiresWithAbstractIntention,
                                   Set<DesireKey> desiresWithIntentionToAct, Set<DesireKey> desiresWithIntentionToReason,
                                   Set<FactKey<?>> usingTypesForFacts, Set<FactKey<?>> usingTypesForFactSets,
                                   AgentType.ConfigurationInitializationStrategy initializationStrategy) {
        super(name, desiresForOthers, desiresWithAbstractIntention, desiresWithIntentionToAct, desiresWithIntentionToReason,

                //add facts related to agent - IS_UNIT, REPRESENTS_UNIT
                Stream.concat(usingTypesForFacts.stream(), Arrays.stream(new FactKey<?>[]{IS_UNIT, REPRESENTS_UNIT})).collect(Collectors.toSet()),
                usingTypesForFactSets, initializationStrategy, BWAgentInGame.observingCommand());
    }

    //builder with default fields
    public static class AgentTypeObservingGameBuilder extends AgentTypeMakingObservationsBuilder {
        private Set<DesireKey> desiresForOthers = new HashSet<>();
        private Set<DesireKey> desiresWithAbstractIntention = new HashSet<>();
        private Set<DesireKey> desiresWithIntentionToAct = new HashSet<>();
        private Set<DesireKey> desiresWithIntentionToReason = new HashSet<>();
        private Set<FactKey<?>> usingTypesForFacts = new HashSet<>();
        private Set<FactKey<?>> usingTypesForFactSets = new HashSet<>();
    }
}