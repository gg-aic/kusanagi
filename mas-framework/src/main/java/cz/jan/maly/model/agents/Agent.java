package cz.jan.maly.model.agents;

import cz.jan.maly.model.knowledge.Memory;
import cz.jan.maly.model.knowledge.WorkingMemory;
import cz.jan.maly.model.metadata.AgentType;
import cz.jan.maly.model.metadata.DesireKey;
import cz.jan.maly.model.planing.Command;
import cz.jan.maly.model.planing.DesireForOthers;
import cz.jan.maly.model.planing.DesireFromAnotherAgent;
import cz.jan.maly.model.planing.OwnDesire;
import cz.jan.maly.model.planing.tree.Tree;
import cz.jan.maly.service.CommandManager;
import cz.jan.maly.service.implementation.AgentsRegister;
import cz.jan.maly.service.implementation.DesireMediator;
import cz.jan.maly.service.implementation.KnowledgeMediator;
import cz.jan.maly.service.implementation.ReasoningExecutor;
import lombok.Getter;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Jan on 09-Feb-17.
 */
public class Agent implements AgentTypeBehaviourFactory {

    //instance of reasoning manager, it can be shared by agents as it is stateless
    private static final ReasoningExecutor REASONING_EXECUTOR = new ReasoningExecutor();

    //register of agents - to assign ids to them
    private static final AgentsRegister AGENTS_REGISTER = new AgentsRegister();

    //shared desire mediator
    public static final DesireMediator DESIRE_MEDIATOR = new DesireMediator();

    //shared knowledge mediator
    private static final KnowledgeMediator KNOWLEDGE_MEDIATOR = new KnowledgeMediator();

    @Getter
    private final int id;

    private WorkingMemory beliefs;

    @Getter
    private final AgentType agentType;

    private final Tree tree = new Tree(this);

    //TODO init beliefs using facts in type, other executors
    public Agent(AgentType agentType, Set<CommandManager> commandManagers) {
        this.id = AGENTS_REGISTER.getFreeId();
        this.agentType = agentType;

//        //init desires from types provided by user
//        getInitialOwnDesireWithAbstractIntentionTypes().forEach(desireKey -> tree.addDesire(formOwnDesireWithAbstractIntention(desireKey)));
//        getInitialOwnDesireWithIntentionWithPlanTypes().forEach(desireKey -> tree.addDesire(formOwnDesireWithIntentionWithPlan(desireKey)));
//        getDesireForOthersTypes().forEach(desireKey -> tree.addDesire(formDesireForOthers(desireKey)));
    }

    /**
     * Get memory of agent
     *
     * @return
     */
    public Memory getBeliefs() {
        return beliefs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agent)) return false;

        Agent agent = (Agent) o;

        return id == agent.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public OwnDesire.WithAbstractIntention formOwnDesireWithAbstractIntention(DesireKey desireKey) {
        return;
    }

    @Override
    public OwnDesire.WithAbstractIntention formOwnDesireWithAbstractIntention(DesireKey desireKey, DesireKey parentDesireKey) {
        return;
    }

    @Override
    public OwnDesire.WithIntentionWithPlan formOwnDesireWithIntentionWithPlan(DesireKey desireKey) {
        return;
    }

    @Override
    public OwnDesire.WithIntentionWithPlan formOwnDesireWithIntentionWithPlan(DesireKey desireKey, DesireKey parentDesireKey) {
        return;
    }

    @Override
    public DesireForOthers formDesireForOthers(DesireKey desireKey) {
        return;
    }

    @Override
    public DesireForOthers formDesireForOthers(DesireKey desireKey, DesireKey parentDesireKey) {
        return;
    }

    @Override
    public DesireFromAnotherAgent.WithAbstractIntention formDesireFromOtherAgentWithAbstractIntention(DesireKey desireKey) {
        return;
    }

    @Override
    public DesireFromAnotherAgent.WithIntentionWithPlan formDesireFromOtherAgentWithIntentionWithPlan(DesireKey desireKey) {
        return;
    }
}
