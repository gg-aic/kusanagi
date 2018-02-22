package aic.gas.sc.gg_bot.bot.model.agent;

import aic.gas.sc.gg_bot.bot.service.implementation.BotFacade;
import aic.gas.sc.gg_bot.mas.model.IResponseReceiver;
import aic.gas.sc.gg_bot.mas.model.agents.Agent;
import aic.gas.sc.gg_bot.mas.model.metadata.AgentType;
import aic.gas.sc.gg_bot.mas.model.planing.command.ActCommand;
import lombok.extern.slf4j.Slf4j;

/**
 * AbstractAgent is agent which makes no observation and send no commands to game. It only reasons
 * and share desires
 */
@Slf4j
public class AbstractAgent extends Agent<AgentType> {

  public AbstractAgent(AgentType agentType, BotFacade botFacade) {
    super(agentType, botFacade.getMasFacade());
  }

  @Override
  public boolean sendCommandToExecute(ActCommand<?> command,
      IResponseReceiver<Boolean> responseReceiver) {
    log.error("Trying to send command on behalf of abstract agent.");
    throw new RuntimeException("Trying to send command on behalf of abstract agent.");
  }
}
