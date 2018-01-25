package aic.gas.sc.gg_bot.bot.model.agent;

import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.BASE_TO_MOVE;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_BASE_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_ISLAND;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_MINERAL_ONLY;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.IS_START_LOCATION;
import static aic.gas.sc.gg_bot.abstract_bot.model.bot.FactKeys.MADE_OBSERVATION_IN_FRAME;

import aic.gas.sc.gg_bot.abstract_bot.model.game.wrappers.ABaseLocationWrapper;
import aic.gas.sc.gg_bot.bot.model.agent.types.AgentTypeBaseLocation;
import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Agent for base location in game INSTANCE OF THIS AGENT SHOULD NOT SEND ANY COMMAND TO GAME. ONLY
 * REASON AND OBSERVE
 */
public class AgentBaseLocation extends AgentObservingGame<AgentTypeBaseLocation> {

  @Getter
  private final ABaseLocationWrapper location;

  public AgentBaseLocation(AgentTypeBaseLocation agentType, BotFacade botFacade,
      ABaseLocationWrapper location) {
    super(agentType, botFacade);

    this.location = location;

    //add itself to knowledge
    beliefs.updateFact(IS_BASE_LOCATION, location);
    beliefs.updateFact(MADE_OBSERVATION_IN_FRAME, 0);
    beliefs.updateFact(IS_MINERAL_ONLY, location.isMineralOnly());
    beliefs.updateFact(IS_ISLAND, location.isIsland());
    beliefs.updateFact(IS_START_LOCATION, location.isStartLocation());
    beliefs.updateFact(BASE_TO_MOVE, beliefs.returnFactValueForGivenKey(IS_BASE_LOCATION).get());
  }

  public String getCommitmentsAsText() {
    return "Location: " + location.getPosition().getWrappedPosition().toString() + "\n"
        + getTopCommitments().entrySet().stream()
        .map(entry -> (entry.getValue() ? "C" : "D") + " : " + entry.getKey().getName())
        .collect(Collectors.joining("\n"));
  }
}
