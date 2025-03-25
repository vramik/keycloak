/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.services.resources.admin.permissions;

import static java.lang.Boolean.TRUE;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.keycloak.authorization.AdminPermissionsSchema;
import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.common.DefaultEvaluationContext;
import org.keycloak.authorization.identity.UserModelIdentity;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.permission.ResourcePermission;
import org.keycloak.authorization.policy.evaluation.EvaluationContext;
import org.keycloak.models.AdminRoles;
import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.ImpersonationConstants;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.representations.idm.authorization.Permission;

class UserPermissionsV2 extends UserPermissions {

    UserPermissionsV2(KeycloakSession session, AuthorizationProvider authz, MgmtPermissionsV2 root) {
        super(session, authz, root);
    }

    @Override
    public boolean canView(UserModel user) {
        if (root.hasOneAdminRole(AdminRoles.MANAGE_USERS, AdminRoles.VIEW_USERS)) {
            return true;
        }

        return TRUE.equals(hasPermission(user, null, AdminPermissionsSchema.VIEW, AdminPermissionsSchema.MANAGE)) || canViewByGroup(user);
    }

    @Override
    public boolean canView() {
        if (root.hasOneAdminRole(AdminRoles.MANAGE_USERS, AdminRoles.VIEW_USERS)) {
            return true;
        }

        return TRUE.equals(hasPermission((UserModel) null, null, AdminPermissionsSchema.VIEW, AdminPermissionsSchema.MANAGE));
    }

    @Override
    public boolean canManage(UserModel user) {
        if (root.hasOneAdminRole(AdminRoles.MANAGE_USERS)) {
            return true;
        }

        Boolean canManage = hasPermission(user, null, AdminPermissionsSchema.MANAGE);

        // user permission exists and was evaluated to false
        if (canManage != null && !canManage) {
            return false;
        }

        Boolean canManageByGroup = canManageByGroup(user);

        // user permission exists and was evaluated to true -> check group permission
        if (canManage != null) {
            // group permission does not exist or is evaluated to true
            return canManageByGroup == null || canManageByGroup;
        }

        // user permission does not exist -> if group permission exists, return the outcome, otherwise return false
        return canManageByGroup != null && canManageByGroup;
    }

    @Override
    public boolean canImpersonate(UserModel user, ClientModel requester) {
        if (root.hasOneAdminRole(ImpersonationConstants.IMPERSONATION_ROLE)) {
            return true;
        }

        DefaultEvaluationContext context = requester == null ? null :
                new DefaultEvaluationContext(new UserModelIdentity(root.realm, user), Map.of("kc.client.id", List.of(requester.getClientId())), session);

        return TRUE.equals(hasPermission(user, context, AdminPermissionsSchema.IMPERSONATE));
    }

    @Override
    public boolean canMapRoles(UserModel user) {
        if (root.hasOneAdminRole(AdminRoles.MANAGE_USERS)) {
            return true;
        }

        return TRUE.equals(hasPermission(user, null, AdminPermissionsSchema.MANAGE, AdminPermissionsSchema.MAP_ROLES)) || canManageByGroup(user);
    }

    @Override
    public boolean canManageGroupMembership(UserModel user) {
        if (root.hasOneAdminRole(AdminRoles.MANAGE_USERS)) {
            return true;
        }

        return TRUE.equals(hasPermission(user, null, AdminPermissionsSchema.MANAGE, AdminPermissionsSchema.MANAGE_GROUP_MEMBERSHIP)) || canManageByGroup(user);
    }

    private Boolean hasPermission(UserModel user, EvaluationContext context, String... scopes) {
        if (!root.isAdminSameRealm()) {
            return false;
        }

        ResourceServer server = root.realmResourceServer();

        if (server == null) {
            return false;
        }

        Resource resource = user == null ? null : resourceStore.findByName(server, user.getId());

        if (resource == null) {
            // check if there is permission for "all-users". If so, load its resource and proceed with evaluation
            resource = AdminPermissionsSchema.SCHEMA.getResourceTypeResource(session, server, AdminPermissionsSchema.USERS_RESOURCE_TYPE);

            if (policyStore.findByResource(server, resource).isEmpty()) {
                return null;
            }
        }

        Collection<Permission> permissions = (context == null) ?
                root.evaluatePermission(new ResourcePermission(resource, resource.getScopes(), server), server) :
                root.evaluatePermission(new ResourcePermission(resource, resource.getScopes(), server), server, context);

        List<String> expectedScopes = Arrays.asList(scopes);

        for (Permission permission : permissions) {
            if (permission.getResourceId().equals(resource.getId())) {
                for (String scope : permission.getScopes()) {
                    if (expectedScopes.contains(scope)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Boolean evaluateHierarchy(UserModel user, Function<GroupModel, Boolean> eval) {
        Set<GroupModel> visited = new HashSet<>();
        return user.getGroupsStream()
                .map(group -> evaluateHierarchy(eval, group, visited))
                .filter(Objects::nonNull) // Ensure null is propagated correctly
                .findFirst()
                .orElse(null); // Propagate null if all results are null
    }

    private Boolean evaluateHierarchy(Function<GroupModel, Boolean> eval, GroupModel group, Set<GroupModel> visited) {
        if (visited.contains(group)) return false;
        visited.add(group);

        Boolean result = eval.apply(group); // This can return true, false, or null
        if (result != null) {
            return result; // Propagate true or false
        }

        if (group.getParent() == null) return false;

        return evaluateHierarchy(eval, group.getParent(), visited);
    }

    private Boolean canManageByGroup(UserModel user) {
        return evaluateHierarchy(user, group -> root.groups().canManageMembers(group));
    }

    // todo this method should be removed and replaced by canImpersonate(user, client); once V1 is removed
    @Override
    public boolean canClientImpersonate(ClientModel client, UserModel user) {
        return canImpersonate(user, client);
    }

    @Override
    public boolean isImpersonatable(UserModel user, ClientModel requester) {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public boolean isImpersonatable(UserModel user) {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public boolean isPermissionsEnabled() {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public void setPermissionsEnabled(boolean enable) {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public Map<String, String> getPermissions() {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public Resource resource() {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public Policy managePermission() {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public Policy viewPermission() {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public Policy manageGroupMembershipPermission() {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public Policy mapRolesPermission() {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public Policy adminImpersonatingPermission() {
        throw new UnsupportedOperationException("Not supported in V2");
    }

    @Override
    public Policy userImpersonatedPermission() {
        throw new UnsupportedOperationException("Not supported in V2");
    }
}
