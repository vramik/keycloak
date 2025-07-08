package org.keycloak.models.policy;

import java.util.List;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class DisableUserActionProvider implements ResourceActionProvider {

    private final KeycloakSession session;
    private final ComponentModel actionModel;
    private final Logger log = Logger.getLogger(DisableUserActionProvider.class);

    public DisableUserActionProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.actionModel = model;
    }

    @Override
    public void close() {
    }

    @Override
    public void run(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        RealmModel realm = session.getContext().getRealm();

        for (String id : userIds) {
            UserModel user = session.users().getUserById(realm, id);
            if (user != null && user.isEnabled()) {
                log.debugv("Disabling user {0} ({1})", user.getUsername(), user.getId());
                user.setEnabled(false);
            }
        }
    }

    @Override
    public boolean isRunnable() {
        return actionModel.get("after") != null;
    }
}
