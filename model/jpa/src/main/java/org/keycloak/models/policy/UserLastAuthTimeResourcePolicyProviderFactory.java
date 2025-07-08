package org.keycloak.models.policy;

import java.util.List;

import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class UserLastAuthTimeResourcePolicyProviderFactory implements ResourcePolicyProviderFactory<UserLastAuthTimeResourcePolicyProvider> {

    public static final String ID = "user-last-auth-time-resource-policy";

    @Override
    public ResourceType getType() {
        return ResourceType.USERS;
    }

    @Override
    public UserLastAuthTimeResourcePolicyProvider create(KeycloakSession session, ComponentModel model) {
        return new UserLastAuthTimeResourcePolicyProvider(session, model);
    }

    @Override
    public void init(Config.Scope config) {
        // no-op
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // no-op
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getHelpText() {
        return "";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }
}
