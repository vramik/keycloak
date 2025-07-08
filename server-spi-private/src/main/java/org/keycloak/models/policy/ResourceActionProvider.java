package org.keycloak.models.policy;

import java.util.List;
import org.keycloak.provider.Provider;

public interface ResourceActionProvider extends Provider {

    void run(List<String> resourceIds);

    boolean isRunnable();
}
