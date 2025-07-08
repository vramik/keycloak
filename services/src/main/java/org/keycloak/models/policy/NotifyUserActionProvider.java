package org.keycloak.models.policy;

import java.util.List;
import org.jboss.logging.Logger;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class NotifyUserActionProvider implements ResourceActionProvider {

    private final KeycloakSession session;
    private final ComponentModel actionModel;
    private final Logger log = Logger.getLogger(NotifyUserActionProvider.class);

    public NotifyUserActionProvider(KeycloakSession session, ComponentModel model) {
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
            if (user != null) {
                log.debugv("Disabling user {0} ({1})", user.getUsername(), user.getId());
                user.setSingleAttribute("notification_sent", "true");
            }
        }
    }

    @Override
    public boolean isRunnable() {
        return actionModel.get("after") != null;
    }
}
