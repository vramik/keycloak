/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.models.map.realm;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.keycloak.common.util.Time;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.map.common.AbstractEntity;
import org.keycloak.models.map.realm.entity.MapAuthenticationExecutionEntity;
import org.keycloak.models.map.realm.entity.MapAuthenticationFlowEntity;
import org.keycloak.models.map.realm.entity.MapAuthenticatorConfigEntity;
import org.keycloak.models.map.realm.entity.MapClientInitialAccessEntity;
import org.keycloak.models.map.realm.entity.MapComponentEntity;
import org.keycloak.models.map.realm.entity.MapIdentityProviderEntity;
import org.keycloak.models.map.realm.entity.MapIdentityProviderMapperEntity;
import org.keycloak.models.map.realm.entity.MapOTPPolicyEntity;
import org.keycloak.models.map.realm.entity.MapRequiredActionProviderEntity;
import org.keycloak.models.map.realm.entity.MapRequiredCredentialEntity;
import org.keycloak.models.map.realm.entity.MapWebAuthnPolicyEntity;

public abstract class AbstractRealmEntity<K> implements AbstractEntity<K> {

    private final K id;
    private String name;

    private boolean enabled;
    private boolean registrationAllowed;
    private boolean registrationEmailAsUsername;
    private boolean verifyEmail;
    private boolean resetPasswordAllowed;
    private boolean loginWithEmailAllowed;
    private boolean duplicateEmailsAllowed;
    private boolean rememberMe;
    private boolean editUsernameAllowed;
    private boolean revokeRefreshToken;
    private boolean adminEventsEnabled;
    private boolean adminEventsDetailsEnabled;
    private boolean internationalizationEnabled;
    private boolean allowUserManagedAccess;
    private boolean offlineSessionMaxLifespanEnabled;
    private boolean eventsEnabled;
    private int actionTokenGeneratedByUserLifespan = 12 * 60 * 60;
    private int refreshTokenMaxReuse;
    private int ssoSessionIdleTimeout;
    private int ssoSessionMaxLifespan;
    private int ssoSessionIdleTimeoutRememberMe;
    private int ssoSessionMaxLifespanRememberMe;
    private int offlineSessionIdleTimeout;
    private int accessTokenLifespan;
    private int accessTokenLifespanForImplicitFlow;
    private int accessCodeLifespan;
    private int accessCodeLifespanUserAction;
    private int accessCodeLifespanLogin;
    private int notBefore;
    private int clientSessionIdleTimeout;
    private int clientSessionMaxLifespan;
    private int clientOfflineSessionIdleTimeout;
    private int clientOfflineSessionMaxLifespan;
    private int actionTokenGeneratedByAdminLifespan;
    private int offlineSessionMaxLifespan;
    private long eventsExpiration;
    private String displayName;
    private String displayNameHtml;
    private String passwordPolicy;
    private String sslRequired;
    private String loginTheme;
    private String accountTheme;
    private String adminTheme;
    private String emailTheme;
    private String masterAdminClient;
    private String defaultRoleId;
    private String defaultLocale;
    private String browserFlow;
    private String registrationFlow;
    private String directGrantFlow;
    private String resetCredentialsFlow;
    private String clientAuthenticationFlow;
    private String dockerAuthenticationFlow;
    private MapOTPPolicyEntity otpPolicy = MapOTPPolicyEntity.fromModel(OTPPolicy.DEFAULT_POLICY);;
    private MapWebAuthnPolicyEntity webAuthnPolicy = MapWebAuthnPolicyEntity.defaultWebAuthnPolicy();;
    private MapWebAuthnPolicyEntity webAuthnPolicyPasswordless = MapWebAuthnPolicyEntity.defaultWebAuthnPolicy();;

    private final Set<String> defaultGroupIds = new HashSet<>();
    private final Set<String> eventsListeners = new HashSet<>();
    private final Set<String> enabledEventTypes = new HashSet<>();
    private final Set<String> supportedLocales = new HashSet<>();
    private final Set<String> defaultClientScopes = new HashSet<>();
    private final Set<String> optionalClientScopes = new HashSet<>();
    private final Map<String, String> browserSecurityHeaders = new HashMap<>();
    private final Map<String, String> smtpConfig = new HashMap<>();
    private final Map<String, String> attributes = new HashMap<>();
    private final Map<String, Map<String, String>> localizationTexts = new HashMap<>();
    private final Map<String, MapClientInitialAccessEntity> clientInitialAccesses = new HashMap<>();
    private final Map<String, MapComponentEntity> components = new HashMap<>();
    private final Map<String, MapAuthenticationFlowEntity> authenticationFlows = new HashMap<>();
    private final Map<String, MapAuthenticationExecutionEntity> authenticationExecutions = new HashMap<>();
    private final Map<String, MapRequiredCredentialEntity> requiredCredentials = new HashMap<>();
    private final Map<String, MapAuthenticatorConfigEntity> authenticatorConfigs = new HashMap<>();
    private final Map<String, MapIdentityProviderEntity> identityProviders = new HashMap<>();
    private final Map<String, MapIdentityProviderMapperEntity> identityProviderMappers = new HashMap<>();
    private final Map<String, MapRequiredActionProviderEntity> requiredActionProviders = new HashMap<>();

    /**
     * Flag signalizing that any of the setters has been meaningfully used.
     */
    protected boolean updated;

    protected AbstractRealmEntity() {
        this.id = null;
    }

    public AbstractRealmEntity(K id) {
        Objects.requireNonNull(id, "id");

        this.id = id;
    }

    @Override
    public K getId() {
        return this.id;
    }

    @Override
    public boolean isUpdated() {
        return this.updated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.updated |= ! Objects.equals(this.name, name);
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.updated |= ! Objects.equals(this.displayName, displayName);
        this.displayName = displayName;
    }

    public String getDisplayNameHtml() {
        return displayNameHtml;
    }

    public void setDisplayNameHtml(String displayNameHtml) {
        this.updated |= ! Objects.equals(this.displayNameHtml, displayNameHtml);
        this.displayNameHtml = displayNameHtml;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.updated |= ! Objects.equals(this.enabled, enabled);
        this.enabled = enabled;
    }

    public boolean isRegistrationAllowed() {
        return registrationAllowed;
    }

    public void setRegistrationAllowed(boolean registrationAllowed) {
        this.updated |= ! Objects.equals(this.registrationAllowed, registrationAllowed);
        this.registrationAllowed = registrationAllowed;
    }

    public boolean isRegistrationEmailAsUsername() {
        return registrationEmailAsUsername;
    }

    public void setRegistrationEmailAsUsername(boolean registrationEmailAsUsername) {
        this.updated |= ! Objects.equals(this.registrationEmailAsUsername, registrationEmailAsUsername);
        this.registrationEmailAsUsername = registrationEmailAsUsername;
    }

    public boolean isVerifyEmail() {
        return verifyEmail;
    }

    public void setVerifyEmail(boolean verifyEmail) {
        this.updated |= ! Objects.equals(this.verifyEmail, verifyEmail);
        this.verifyEmail = verifyEmail;
    }
    

    public boolean isResetPasswordAllowed() {
        return resetPasswordAllowed;
    }

    public void setResetPasswordAllowed(boolean resetPasswordAllowed) {
        this.updated |= ! Objects.equals(this.resetPasswordAllowed, resetPasswordAllowed);
        this.resetPasswordAllowed = resetPasswordAllowed;
    }

    public boolean isLoginWithEmailAllowed() {
        return loginWithEmailAllowed;
    }

    public void setLoginWithEmailAllowed(boolean loginWithEmailAllowed) {
        this.updated |= ! Objects.equals(this.loginWithEmailAllowed, loginWithEmailAllowed);
        this.loginWithEmailAllowed = loginWithEmailAllowed;
    }

    public boolean isDuplicateEmailsAllowed() {
        return duplicateEmailsAllowed;
    }

    public void setDuplicateEmailsAllowed(boolean duplicateEmailsAllowed) {
        this.updated |= ! Objects.equals(this.duplicateEmailsAllowed, duplicateEmailsAllowed);
        this.duplicateEmailsAllowed = duplicateEmailsAllowed;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.updated |= ! Objects.equals(this.rememberMe, rememberMe);
        this.rememberMe = rememberMe;
    }

    public boolean isEditUsernameAllowed() {
        return editUsernameAllowed;
    }

    public void setEditUsernameAllowed(boolean editUsernameAllowed) {
        this.updated |= ! Objects.equals(this.editUsernameAllowed, editUsernameAllowed);
        this.editUsernameAllowed = editUsernameAllowed;
    }

    public boolean isRevokeRefreshToken() {
        return revokeRefreshToken;
    }

    public void setRevokeRefreshToken(boolean revokeRefreshToken) {
        this.updated |= ! Objects.equals(this.revokeRefreshToken, revokeRefreshToken);
        this.revokeRefreshToken = revokeRefreshToken;
    }

    public boolean isAdminEventsEnabled() {
        return adminEventsEnabled;
    }

    public void setAdminEventsEnabled(boolean adminEventsEnabled) {
        this.updated |= ! Objects.equals(this.adminEventsEnabled, adminEventsEnabled);
        this.adminEventsEnabled = adminEventsEnabled;
    }

    public boolean isAdminEventsDetailsEnabled() {
        return adminEventsDetailsEnabled;
    }

    public void setAdminEventsDetailsEnabled(boolean adminEventsDetailsEnabled) {
        this.updated |= ! Objects.equals(this.adminEventsDetailsEnabled, adminEventsDetailsEnabled);
        this.adminEventsDetailsEnabled = adminEventsDetailsEnabled;
    }

    public boolean isInternationalizationEnabled() {
        return internationalizationEnabled;
    }

    public void setInternationalizationEnabled(boolean internationalizationEnabled) {
        this.updated |= ! Objects.equals(this.internationalizationEnabled, internationalizationEnabled);
        this.internationalizationEnabled = internationalizationEnabled;
    }

    public boolean isAllowUserManagedAccess() {
        return allowUserManagedAccess;
    }

    public void setAllowUserManagedAccess(boolean allowUserManagedAccess) {
        this.updated |= ! Objects.equals(this.allowUserManagedAccess, allowUserManagedAccess);
        this.allowUserManagedAccess = allowUserManagedAccess;
    }

    public boolean isOfflineSessionMaxLifespanEnabled() {
        return offlineSessionMaxLifespanEnabled;
    }

    public void setOfflineSessionMaxLifespanEnabled(boolean offlineSessionMaxLifespanEnabled) {
        this.updated |= ! Objects.equals(this.offlineSessionMaxLifespanEnabled, offlineSessionMaxLifespanEnabled);
        this.offlineSessionMaxLifespanEnabled = offlineSessionMaxLifespanEnabled;
    }

    public boolean isEventsEnabled() {
        return eventsEnabled;
    }

    public void setEventsEnabled(boolean eventsEnabled) {
        this.updated |= ! Objects.equals(this.eventsEnabled, eventsEnabled);
        this.eventsEnabled = eventsEnabled;
    }

    public int getRefreshTokenMaxReuse() {
        return refreshTokenMaxReuse;
    }

    public void setRefreshTokenMaxReuse(int refreshTokenMaxReuse) {
        this.updated |= ! Objects.equals(this.refreshTokenMaxReuse, refreshTokenMaxReuse);
        this.refreshTokenMaxReuse = refreshTokenMaxReuse;
    }

    public int getSsoSessionIdleTimeout() {
        return ssoSessionIdleTimeout;
    }

    public void setSsoSessionIdleTimeout(int ssoSessionIdleTimeout) {
        this.updated |= ! Objects.equals(this.ssoSessionIdleTimeout, ssoSessionIdleTimeout);
        this.ssoSessionIdleTimeout = ssoSessionIdleTimeout;
    }

    public int getSsoSessionMaxLifespan() {
        return ssoSessionMaxLifespan;
    }

    public void setSsoSessionMaxLifespan(int ssoSessionMaxLifespan) {
        this.updated |= ! Objects.equals(this.ssoSessionMaxLifespan, ssoSessionMaxLifespan);
        this.ssoSessionMaxLifespan = ssoSessionMaxLifespan;
    }

    public int getSsoSessionIdleTimeoutRememberMe() {
        return ssoSessionIdleTimeoutRememberMe;
    }

    public void setSsoSessionIdleTimeoutRememberMe(int ssoSessionIdleTimeoutRememberMe) {
        this.updated |= ! Objects.equals(this.ssoSessionIdleTimeoutRememberMe, ssoSessionIdleTimeoutRememberMe);
        this.ssoSessionIdleTimeoutRememberMe = ssoSessionIdleTimeoutRememberMe;
    }

    public int getSsoSessionMaxLifespanRememberMe() {
        return ssoSessionMaxLifespanRememberMe;
    }

    public void setSsoSessionMaxLifespanRememberMe(int ssoSessionMaxLifespanRememberMe) {
        this.updated |= ! Objects.equals(this.ssoSessionMaxLifespanRememberMe, ssoSessionMaxLifespanRememberMe);
        this.ssoSessionMaxLifespanRememberMe = ssoSessionMaxLifespanRememberMe;
    }

    public int getOfflineSessionIdleTimeout() {
        return offlineSessionIdleTimeout;
    }

    public void setOfflineSessionIdleTimeout(int offlineSessionIdleTimeout) {
        this.updated |= ! Objects.equals(this.offlineSessionIdleTimeout, offlineSessionIdleTimeout);
        this.offlineSessionIdleTimeout = offlineSessionIdleTimeout;
    }

    public int getAccessTokenLifespan() {
        return accessTokenLifespan;
    }

    public void setAccessTokenLifespan(int accessTokenLifespan) {
        this.updated |= ! Objects.equals(this.accessTokenLifespan, accessTokenLifespan);
        this.accessTokenLifespan = accessTokenLifespan;
    }

    public int getAccessTokenLifespanForImplicitFlow() {
        return accessTokenLifespanForImplicitFlow;
    }

    public void setAccessTokenLifespanForImplicitFlow(int accessTokenLifespanForImplicitFlow) {
        this.updated |= ! Objects.equals(this.accessTokenLifespanForImplicitFlow, accessTokenLifespanForImplicitFlow);
        this.accessTokenLifespanForImplicitFlow = accessTokenLifespanForImplicitFlow;
    }

    public int getAccessCodeLifespan() {
        return accessCodeLifespan;
    }

    public void setAccessCodeLifespan(int accessCodeLifespan) {
        this.updated |= ! Objects.equals(this.accessCodeLifespan, accessCodeLifespan);
        this.accessCodeLifespan = accessCodeLifespan;
    }

    public int getAccessCodeLifespanUserAction() {
        return accessCodeLifespanUserAction;
    }

    public void setAccessCodeLifespanUserAction(int accessCodeLifespanUserAction) {
        this.updated |= ! Objects.equals(this.accessCodeLifespanUserAction, accessCodeLifespanUserAction);
        this.accessCodeLifespanUserAction = accessCodeLifespanUserAction;
    }

    public int getAccessCodeLifespanLogin() {
        return accessCodeLifespanLogin;
    }

    public void setAccessCodeLifespanLogin(int accessCodeLifespanLogin) {
        this.updated |= ! Objects.equals(this.accessCodeLifespanLogin, accessCodeLifespanLogin);
        this.accessCodeLifespanLogin = accessCodeLifespanLogin;
    }

    public int getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(int notBefore) {
        this.updated |= ! Objects.equals(this.notBefore, notBefore);
        this.notBefore = notBefore;
    }

    public int getClientSessionIdleTimeout() {
        return clientSessionIdleTimeout;
    }

    public void setClientSessionIdleTimeout(int clientSessionIdleTimeout) {
        this.updated |= ! Objects.equals(this.clientSessionIdleTimeout, clientSessionIdleTimeout);
        this.clientSessionIdleTimeout = clientSessionIdleTimeout;
    }

    public int getClientSessionMaxLifespan() {
        return clientSessionMaxLifespan;
    }

    public void setClientSessionMaxLifespan(int clientSessionMaxLifespan) {
        this.updated |= ! Objects.equals(this.clientSessionMaxLifespan, clientSessionMaxLifespan);
        this.clientSessionMaxLifespan = clientSessionMaxLifespan;
    }

    public int getClientOfflineSessionIdleTimeout() {
        return clientOfflineSessionIdleTimeout;
    }

    public void setClientOfflineSessionIdleTimeout(int clientOfflineSessionIdleTimeout) {
        this.updated |= ! Objects.equals(this.clientOfflineSessionIdleTimeout, clientOfflineSessionIdleTimeout);
        this.clientOfflineSessionIdleTimeout = clientOfflineSessionIdleTimeout;
    }

    public int getClientOfflineSessionMaxLifespan() {
        return clientOfflineSessionMaxLifespan;
    }

    public void setClientOfflineSessionMaxLifespan(int clientOfflineSessionMaxLifespan) {
        this.updated |= ! Objects.equals(this.clientOfflineSessionMaxLifespan, clientOfflineSessionMaxLifespan);
        this.clientOfflineSessionMaxLifespan = clientOfflineSessionMaxLifespan;
    }

    public int getActionTokenGeneratedByAdminLifespan() {
        return actionTokenGeneratedByAdminLifespan;
    }

    public void setActionTokenGeneratedByAdminLifespan(int actionTokenGeneratedByAdminLifespan) {
        this.updated |= ! Objects.equals(this.actionTokenGeneratedByAdminLifespan, actionTokenGeneratedByAdminLifespan);
        this.actionTokenGeneratedByAdminLifespan = actionTokenGeneratedByAdminLifespan;
    }

    public int getActionTokenGeneratedByUserLifespan() {
        return actionTokenGeneratedByUserLifespan;
    }

    public void setActionTokenGeneratedByUserLifespan(int actionTokenGeneratedByUserLifespan) {
        this.updated |= ! Objects.equals(this.actionTokenGeneratedByUserLifespan, actionTokenGeneratedByUserLifespan);
        this.actionTokenGeneratedByUserLifespan = actionTokenGeneratedByUserLifespan;
    }

    public int getOfflineSessionMaxLifespan() {
        return offlineSessionMaxLifespan;
    }

    public void setOfflineSessionMaxLifespan(int offlineSessionMaxLifespan) {
        this.updated |= ! Objects.equals(this.offlineSessionMaxLifespan, offlineSessionMaxLifespan);
        this.offlineSessionMaxLifespan = offlineSessionMaxLifespan;
    }

    public long getEventsExpiration() {
        return eventsExpiration;
    }

    public void setEventsExpiration(long eventsExpiration) {
        this.updated |= ! Objects.equals(this.eventsExpiration, eventsExpiration);
        this.eventsExpiration = eventsExpiration;
    }

    public String getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(String passwordPolicy) {
        this.updated |= ! Objects.equals(this.passwordPolicy, passwordPolicy);
        this.passwordPolicy = passwordPolicy;
    }

    public String getSslRequired() {
        return sslRequired;
    }

    public void setSslRequired(String sslRequired) {
        this.updated |= ! Objects.equals(this.sslRequired, sslRequired);
        this.sslRequired = sslRequired;
    }

    public String getLoginTheme() {
        return loginTheme;
    }

    public void setLoginTheme(String loginTheme) {
        this.updated |= ! Objects.equals(this.loginTheme, loginTheme);
        this.loginTheme = loginTheme;
    }

    public String getAccountTheme() {
        return accountTheme;
    }

    public void setAccountTheme(String accountTheme) {
        this.updated |= ! Objects.equals(this.accountTheme, accountTheme);
        this.accountTheme = accountTheme;
    }

    public String getAdminTheme() {
        return adminTheme;
    }

    public void setAdminTheme(String adminTheme) {
        this.updated |= ! Objects.equals(this.adminTheme, adminTheme);
        this.adminTheme = adminTheme;
    }

    public String getEmailTheme() {
        return emailTheme;
    }

    public void setEmailTheme(String emailTheme) {
        this.updated |= ! Objects.equals(this.emailTheme, emailTheme);
        this.emailTheme = emailTheme;
    }

    public String getMasterAdminClient() {
        return masterAdminClient;
    }

    public void setMasterAdminClient(String masterAdminClient) {
        this.updated |= ! Objects.equals(this.masterAdminClient, masterAdminClient);
        this.masterAdminClient = masterAdminClient;
    }

    public String getDefaultRoleId() {
        return defaultRoleId;
    }

    public void setDefaultRoleId(String defaultRoleId) {
        this.updated |= ! Objects.equals(this.defaultRoleId, defaultRoleId);
        this.defaultRoleId = defaultRoleId;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.updated |= ! Objects.equals(this.defaultLocale, defaultLocale);
        this.defaultLocale = defaultLocale;
    }

    public String getBrowserFlow() {
        return browserFlow;
    }

    public void setBrowserFlow(String browserFlow) {
        this.updated |= ! Objects.equals(this.browserFlow, browserFlow);
        this.browserFlow = browserFlow;
    }

    public String getRegistrationFlow() {
        return registrationFlow;
    }

    public void setRegistrationFlow(String registrationFlow) {
        this.updated |= ! Objects.equals(this.registrationFlow, registrationFlow);
        this.registrationFlow = registrationFlow;
    }

    public String getDirectGrantFlow() {
        return directGrantFlow;
    }

    public void setDirectGrantFlow(String directGrantFlow) {
        this.updated |= ! Objects.equals(this.directGrantFlow, directGrantFlow);
        this.directGrantFlow = directGrantFlow;
    }

    public String getResetCredentialsFlow() {
        return resetCredentialsFlow;
    }

    public void setResetCredentialsFlow(String resetCredentialsFlow) {
        this.updated |= ! Objects.equals(this.resetCredentialsFlow, resetCredentialsFlow);
        this.resetCredentialsFlow = resetCredentialsFlow;
    }

    public String getClientAuthenticationFlow() {
        return clientAuthenticationFlow;
    }

    public void setClientAuthenticationFlow(String clientAuthenticationFlow) {
        this.updated |= ! Objects.equals(this.clientAuthenticationFlow, clientAuthenticationFlow);
        this.clientAuthenticationFlow = clientAuthenticationFlow;
    }

    public String getDockerAuthenticationFlow() {
        return dockerAuthenticationFlow;
    }

    public void setDockerAuthenticationFlow(String dockerAuthenticationFlow) {
        this.updated |= ! Objects.equals(this.dockerAuthenticationFlow, dockerAuthenticationFlow);
        this.dockerAuthenticationFlow = dockerAuthenticationFlow;
    }

    public MapOTPPolicyEntity getOTPPolicy() {
        return otpPolicy;
    }

    public void setOTPPolicy(MapOTPPolicyEntity otpPolicy) {
        this.updated |= ! Objects.equals(this.otpPolicy, otpPolicy);
        this.otpPolicy = otpPolicy;
    }

    public MapWebAuthnPolicyEntity getWebAuthnPolicy() {
        return webAuthnPolicy;
    }

    public void setWebAuthnPolicy(MapWebAuthnPolicyEntity webAuthnPolicy) {
        this.updated |= ! Objects.equals(this.webAuthnPolicy, webAuthnPolicy);
        this.webAuthnPolicy = webAuthnPolicy;
    }

    public MapWebAuthnPolicyEntity getWebAuthnPolicyPasswordless() {
        return webAuthnPolicyPasswordless;
    }

    public void setWebAuthnPolicyPasswordless(MapWebAuthnPolicyEntity webAuthnPolicyPasswordless) {
        this.updated |= ! Objects.equals(this.webAuthnPolicyPasswordless, webAuthnPolicyPasswordless);
        this.webAuthnPolicyPasswordless = webAuthnPolicyPasswordless;
    }

    public void setAttribute(String name, String value) {
        this.updated = true;
        attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        this.updated |= attributes.remove(name) != null;
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void addDefaultClientScope(String scopeId) {
        this.updated |= this.defaultClientScopes.add(scopeId);
    }

    public Stream<String> getDefaultClientScopeIds() {
        return defaultClientScopes.stream();
    }

    public void addOptionalClientScope(String scopeId) {
        this.updated |= this.optionalClientScopes.add(scopeId);
    }

    public Stream<String> getOptionalClientScopeIds() {
        return optionalClientScopes.stream();
    }

    public void removeDefaultOrOptionalClientScope(String scopeId) {
        if (this.defaultClientScopes.remove(scopeId)) {
            this.updated = true;
            return ;
        }
        this.updated |= this.optionalClientScopes.remove(scopeId);
    }

    public Stream<String> getDefaultGroupIds() {
        return defaultGroupIds.stream();
    }

    public void addDefaultGroup(String groupId) {
        this.updated |= this.defaultGroupIds.add(groupId);
    }

    public void removeDefaultGroup(String groupId) {
        this.updated |= this.defaultGroupIds.remove(groupId);
    }

    public Set<String> getEventsListeners() {
        return eventsListeners;
    }

    public void setEventsListeners(Set<String> eventsListeners) {
        this.updated |= ! Objects.equals(eventsListeners, this.eventsListeners);
        this.eventsListeners.clear();
        this.eventsListeners.addAll(eventsListeners);
    }

    public Set<String> getEnabledEventTypes() {
        return enabledEventTypes;
    }

    public void setEnabledEventTypes(Set<String> enabledEventTypes) {
        this.updated |= ! Objects.equals(enabledEventTypes, this.enabledEventTypes);
        this.enabledEventTypes.clear();
        this.enabledEventTypes.addAll(enabledEventTypes);
    }

    public Set<String> getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(Set<String> supportedLocales) {
        this.updated |= ! Objects.equals(supportedLocales, this.supportedLocales);
        this.supportedLocales.clear();
        this.supportedLocales.addAll(supportedLocales);
    }

    public Map<String, Map<String, String>> getLocalizationTexts() {
        return localizationTexts;
    }

    public Map<String, String> getLocalizationText(String locale) {
        if (localizationTexts.containsKey(locale)) {
            return localizationTexts.get(locale);
        }
        return Collections.emptyMap();
    }

    public void addLocalizationTexts(String locale, Map<String, String> texts) {
        if (! localizationTexts.containsKey(locale)) {
            updated = true;
            localizationTexts.put(locale, texts);
        }
    }

    public void updateLocalizationTexts(String locale, Map<String, String> texts) {
        this.updated |= localizationTexts.replace(locale, texts) != null;
    }

    public boolean removeLocalizationTexts(String locale) {
        if (! localizationTexts.containsKey(locale)) {
            return false;
        }

        this.updated = true;
        localizationTexts.remove(locale);
        return true;
    }

    public Map<String, String> getBrowserSecurityHeaders() {
        return browserSecurityHeaders;
    }

    public void setBrowserSecurityHeaders(Map<String, String> headers) {
        this.updated |= ! Objects.equals(this.smtpConfig, headers);
        this.browserSecurityHeaders.clear();
        this.browserSecurityHeaders.putAll(headers);
    }

    public Map<String, String> getSmtpConfig() {
        return smtpConfig;
    }

    public void setSmtpConfig(Map<String, String> smtpConfig) {
        this.updated |= ! Objects.equals(this.smtpConfig, smtpConfig);
        this.smtpConfig.clear();
        this.smtpConfig.putAll(smtpConfig);
    }

    public Stream<MapRequiredCredentialEntity> getRequiredCredentials() {
        return requiredCredentials.values().stream();
    }

    public void addRequiredCredential(MapRequiredCredentialEntity requiredCredential) {
        if (requiredCredentials.containsKey(requiredCredential.getType())) {
            throw new ModelDuplicateException("An RequiredCredential with given type already exists");
        }
        this.updated = true;
        requiredCredentials.put(requiredCredential.getType(), requiredCredential);
    }

    public void updateRequiredCredential(MapRequiredCredentialEntity requiredCredential) {
        this.updated |= requiredCredentials.replace(requiredCredential.getType(), requiredCredential) != null;
    }

    public Stream<MapComponentEntity> getComponents() {
        return components.values().stream();
    }

    public MapComponentEntity getComponent(String id) {
        return components.get(id);
    }

    public void addComponent(MapComponentEntity component) {
        if (components.containsKey(component.getId())) {
            throw new ModelDuplicateException("A Component with given id already exists");
        }
        this.updated = true;
        components.put(component.getId(), component);
    }

    public void updateComponent(MapComponentEntity component) {
        this.updated |= components.replace(component.getId(), component) != null;
    }

    public boolean removeComponent(String id) {
        boolean removed = this.components.remove(id) != null;
        this.updated |= removed;
        return removed;
    }

    public Stream<MapAuthenticationFlowEntity> getAuthenticationFlows() {
        return authenticationFlows.values().stream();
    }

    public MapAuthenticationFlowEntity getAuthenticationFlow(String flowId) {
        return authenticationFlows.get(flowId);
    }

    public void addAuthenticationFlow(MapAuthenticationFlowEntity authenticationFlow) {
        if (authenticationFlows.containsKey(authenticationFlow.getId())) {
            throw new ModelDuplicateException("An AuthenticationFlow with given id already exists");
        }
        this.updated = true;
        authenticationFlows.put(authenticationFlow.getId(), authenticationFlow);
    }

    public boolean removeAuthenticationFlow(String flowId) {
        if (!authenticationFlows.containsKey(flowId)) {
            return false;
        }

        this.updated = true;
        this.authenticationFlows.remove(flowId);
        return true;
    }

    public void updateAuthenticationFlow(MapAuthenticationFlowEntity authenticationFlow) {
        this.updated |= authenticationFlows.replace(authenticationFlow.getId(), authenticationFlow) != null;
    }

    public void addAuthenticatonExecution(MapAuthenticationExecutionEntity authenticationExecution) {
        if (authenticationExecutions.containsKey(authenticationExecution.getId())) {
            throw new ModelDuplicateException("An RequiredActionProvider with given id already exists");
        }

        this.updated = true;
        authenticationExecutions.put(authenticationExecution.getId(), authenticationExecution);
    }

    public void updateAuthenticatonExecution(MapAuthenticationExecutionEntity authenticationExecution) {
        this.updated |= authenticationExecutions.replace(authenticationExecution.getId(), authenticationExecution) != null;
    }

    public boolean removeAuthenticatonExecution(String id) {
        if (!authenticationExecutions.containsKey(id)) {
            return false;
        }

        this.updated = true;
        this.authenticationExecutions.remove(id);
        return true;
    }

    public MapAuthenticationExecutionEntity getAuthenticationExecution(String id) {
        return authenticationExecutions.get(id);
    }

    public Stream<MapAuthenticationExecutionEntity> getAuthenticationExecutions() {
        return authenticationExecutions.values().stream();
    }

    public Stream<MapAuthenticatorConfigEntity> getAuthenticatorConfigs() {
        return authenticatorConfigs.values().stream();
    }

    public void addAuthenticatorConfig(MapAuthenticatorConfigEntity authenticatorConfig) {
        this.updated |= ! Objects.equals(authenticatorConfigs.put(authenticatorConfig.getId(), authenticatorConfig), authenticatorConfig);
    }

    public void updateAuthenticatorConfig(MapAuthenticatorConfigEntity authenticatorConfig) {
        this.updated |= authenticatorConfigs.replace(authenticatorConfig.getId(), authenticatorConfig) != null;
    }

    public boolean removeAuthenticatorConfig(String id) {
        if (!authenticatorConfigs.containsKey(id)) {
            return false;
        }

        this.updated = true;
        this.authenticatorConfigs.remove(id);
        return true;
    }

    public MapAuthenticatorConfigEntity getAuthenticatorConfig(String id) {
        return authenticatorConfigs.get(id);
    }

    public Stream<MapRequiredActionProviderEntity> getRequiredActionProviders() {
        return requiredActionProviders.values().stream();
    }

    public void addRequiredActionProvider(MapRequiredActionProviderEntity requiredActionProvider) {
        if (requiredActionProviders.containsKey(requiredActionProvider.getId())) {
            throw new ModelDuplicateException("An RequiredActionProvider with given id already exists");
        }

        this.updated = true;
        requiredActionProviders.put(requiredActionProvider.getId(), requiredActionProvider);
    }

    public void updateRequiredActionProvider(MapRequiredActionProviderEntity requiredActionProvider) {
        this.updated |= requiredActionProviders.replace(requiredActionProvider.getId(), requiredActionProvider) != null;
    }

    public boolean removeRequiredActionProvider(String id) {
        if (!requiredActionProviders.containsKey(id)) {
            return false;
        }

        this.updated = true;
        this.requiredActionProviders.remove(id);
        return true;
    }

    public MapRequiredActionProviderEntity getRequiredActionProvider(String id) {
        return requiredActionProviders.get(id);
    }

    public Stream<MapIdentityProviderEntity> getIdentityProviders() {
        return identityProviders.values().stream();
    }

    public void addIdentityProvider(MapIdentityProviderEntity identityProvider) {
        if (identityProviders.containsKey(identityProvider.getId())) {
            throw new ModelDuplicateException("An IdentityProvider with given id already exists");
        }

        this.updated = true;
        identityProviders.put(identityProvider.getId(), identityProvider);
    }

    public boolean removeIdentityProvider(String id) {
        if (!identityProviders.containsKey(id)) {
            return false;
        }

        this.updated = true;
        this.identityProviders.remove(id);
        return true;
    }

    public void updateIdentityProvider(MapIdentityProviderEntity identityProvider) {
        this.updated |= identityProviders.replace(identityProvider.getId(), identityProvider) != null;
    }

    public Stream<MapIdentityProviderMapperEntity> getIdentityProviderMappers() {
        return identityProviderMappers.values().stream();
    }

    public void addIdentityProviderMapper(MapIdentityProviderMapperEntity identityProviderMapper) {
        if (identityProviderMappers.containsKey(identityProviderMapper.getId())) {
            throw new ModelDuplicateException("An IdentityProviderMapper with given id already exists");
        }

        this.updated = true;
        identityProviderMappers.put(identityProviderMapper.getId(), identityProviderMapper);
    }

    public boolean removeIdentityProviderMapper(String id) {
        if (!identityProviderMappers.containsKey(id)) {
            return false;
        }

        this.updated = true;
        this.identityProviderMappers.remove(id);
        return true;
    }

    public void updateIdentityProviderMapper(MapIdentityProviderMapperEntity identityProviderMapper) {
        this.updated |= identityProviderMappers.replace(identityProviderMapper.getId(), identityProviderMapper) != null;
    }

    public MapIdentityProviderMapperEntity getIdentityProviderMapper(String id) {
        return identityProviderMappers.get(id);
    }

    public boolean hasClientInitialAccess() {
        return !clientInitialAccesses.isEmpty();
    }

    public void removeExpiredClientInitialAccesses() {
        clientInitialAccesses.values().stream()
            .filter(this::checkIfExpired)
            .map(MapClientInitialAccessEntity::getId)
            .collect(Collectors.toSet())
            .forEach(this::removeClientInitialAccess);
    }

    private boolean checkIfExpired(MapClientInitialAccessEntity cia) {
        return cia.getRemainingCount() < 1 || 
                (cia.getExpiration() > 0 && (cia.getTimestamp() + cia.getExpiration()) < Time.currentTime());
    }

    public void addClientInitialAccess(MapClientInitialAccessEntity clientInitialAccess) {
        this.updated = true;
        clientInitialAccesses.put(clientInitialAccess.getId(), clientInitialAccess);
    }

    public void updateClientInitialAccess(MapClientInitialAccessEntity clientInitialAccess) {
        this.updated |= clientInitialAccesses.replace(clientInitialAccess.getId(), clientInitialAccess) != null;
    }

    public MapClientInitialAccessEntity getClientInitialAccess(String id) {
        return clientInitialAccesses.get(id);
    }

    public boolean removeClientInitialAccess(String id) {
        if (!clientInitialAccesses.containsKey(id)) {
            return false;
        }

        this.updated = true;
        this.clientInitialAccesses.remove(id);
        return true;
    }

    public Stream<MapClientInitialAccessEntity> getClientInitialAccesses() {
        return clientInitialAccesses.values().stream();
    }
}
