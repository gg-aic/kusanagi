package aic.gas.sc.gg_bot.replay_parser.model.watcher.agent_watcher_type_extension;

import aic.gas.sc.gg_bot.abstract_bot.model.bot.AgentTypes;
import aic.gas.sc.gg_bot.mas.model.metadata.FactKey;
import aic.gas.sc.gg_bot.replay_parser.model.watcher.AgentWatcherType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;

/**
 * Extension of AgentWatcherType to EcoManagerWatcherType
 */
public class EcoManagerWatcherType extends AgentWatcherType {

  @Builder
  private EcoManagerWatcherType(
      AgentTypes agentType,
      Set<FactKey<?>> factSetsKeys,
      List<PlanWatcherInitializationStrategy> planWatchers) {
    super(agentType, factSetsKeys, planWatchers, null);
  }

  /**
   * Builder with default values
   */
  public static class EcoManagerWatcherTypeBuilder extends AgentWatcherTypeBuilder {
    private Set<FactKey<?>> factSetsKeys = new HashSet<>();
  }
}
