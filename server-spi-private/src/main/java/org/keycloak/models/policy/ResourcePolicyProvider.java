package org.keycloak.models.policy;

import java.util.List;
import org.keycloak.provider.Provider;

/**
 * TODO: Maybe we want to split the provider into two???
 * * Time based 
 *   ** UserCreationDatePolicyProvider, LastAuthenticationTimePolicyProvider ...
 * * Origin based
 *   ** IdpResourceFilterProvider, LdapResourceFilterProvider, AllResourceFilterProvider
 * 
 */
public interface ResourcePolicyProvider extends Provider {

    /**
     * Finds all resources that are eligible for the first action of a policy.
     *
     * @param time The time delay for the first action.
     * @return A list of eligible resource IDs.
     */
    List<String> getEligibleResourcesForInitialAction(long time);

    /** 
     * This method checks a list of candidates and returns only those that are eligible based on time.
     */ 
    List<String> filterEligibleResources(List<String> candidateResourceIds, long time);
}
