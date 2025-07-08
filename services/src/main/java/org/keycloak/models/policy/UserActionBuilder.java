package org.keycloak.models.policy;

import java.time.Duration;

public class UserActionBuilder {

    private final ResourceAction action;

    private UserActionBuilder(ResourceAction action) {
        this.action = action;
    }

    public static UserActionBuilder builder(String providerId) {
        ResourceAction action = new ResourceAction(providerId);
        return new UserActionBuilder(action);
    }

    public ResourceAction build() {
        return action;
    }

    public UserActionBuilder after(Duration duration) {
        action.setAfter(duration.toMillis());
        return this;
    }
}
