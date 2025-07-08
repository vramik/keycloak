package org.keycloak.models.policy;

import org.keycloak.provider.Provider;
import java.util.List;
import java.util.Set;

/**
 * Interface serves as state check for policy actions.
 */
public interface ResourcePolicyStateProvider extends Provider {

    /**
     * Finds resource IDs that have completed a specific action within a policy.
     */
    List<String> findResourceIdsByLastCompletedAction(String policyId, String lastCompletedActionId);

    /**
     * Updates the state for a list of resources that have just completed a new action.
     * This will perform an update for existing states or an insert for new states.
     */
    void updateState(String policyId, String policyProviderId, List<String> resourceIds, String newLastCompletedActionId);

    /**
     * Deletes the state record for a specific resource, effectively resetting its progress. 
     * todo: do we need this method?
     */
    void deleteState(String policyId, String resourceId);

    /**
     * Deletes the orphaned state records.
     */
    void deleteStatesByCompletedActions(String policyId, Set<String> deletedActionIds);
}
