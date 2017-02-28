package cz.jan.maly.model.planing.tree;

import cz.jan.maly.model.metadata.DesireKey;

import java.util.List;

/**
 * Contract for each node representing intention
 * Created by Jan on 28-Feb-17.
 */
interface IntentionNodeInterface {

    /**
     * Remove commitment to this intention and replace itself by desire
     *
     * @return
     */
    boolean removeCommitment();

    /**
     * Add own desire key to list + when intermediate node - ask childes
     *
     * @param list
     */
    void collectKeysOfCommittedDesiresInSubtree(List<DesireKey> list);

    /**
     * Add desire key to list - ask childes (if they are desires)
     *
     * @param list
     */
    void collectKeysOfDesiresInSubtree(List<DesireKey> list);

}