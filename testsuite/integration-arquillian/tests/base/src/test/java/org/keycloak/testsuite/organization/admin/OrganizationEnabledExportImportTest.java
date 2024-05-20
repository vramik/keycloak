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
package org.keycloak.testsuite.organization.admin;

import java.io.File;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.Profile;
import org.keycloak.exportimport.ExportImportConfig;
import org.keycloak.exportimport.singlefile.SingleFileExportProviderFactory;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.OrganizationRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.arquillian.annotation.EnableFeature;
import org.keycloak.testsuite.exportimport.ExportImportTest;
import static org.keycloak.testsuite.organization.admin.AbstractOrganizationTest.createOrganization;
import org.keycloak.testsuite.updaters.RealmAttributeUpdater;
import org.keycloak.testsuite.util.RealmBuilder;

/**
 * Tests the export/import functionality with the organization feature enabled.
 *
 * NOTE: When export/import of organizations is implemented and the organization feature is supported, we should either enhance
 * this class or the existing ExportImportTest to check org-specific settings.
 *
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
@EnableFeature(Profile.Feature.ORGANIZATION)
public class OrganizationEnabledExportImportTest extends ExportImportTest {

    private static final String EXPORT_IMPORT_REALM = "organization-export-import";
    
    @Override
    public void addTestRealms(List<RealmRepresentation> testRealms) {
        testRealms.add(RealmBuilder.create()
                .name("organization-export-import")
                .organizationEnabled(true)
                .build());
        super.addTestRealms(testRealms);
    }

    @Test
    public void testExportImportIdPWithDisabledOrgProvider() throws Exception {
        RealmResource testRealm = adminClient.realm(EXPORT_IMPORT_REALM);
        IdentityProviderRepresentation idpRep = new IdentityProviderRepresentation();
        idpRep.setAlias("dummy");
        idpRep.setProviderId("oidc");
        
        OrganizationRepresentation org = createOrganization(testRealm, getCleanup(), "test-org", idpRep, "test-org.com");

        // disable organizations for the realm
        try (RealmAttributeUpdater rau = new RealmAttributeUpdater(adminClient.realm(EXPORT_IMPORT_REALM))
                .setOrganizationEnabled(Boolean.FALSE)
                .update()) {

            // todo return IdP and check it's disabled
            IdentityProviderRepresentation idpRepresentation = testRealm.identityProviders().get("dummy").toRepresentation();
            assertThat(idpRepresentation, notNullValue());
            assertThat(idpRepresentation.isEnabled(), is(false));

            // export
            testingClient.testing().exportImport().setProvider(SingleFileExportProviderFactory.PROVIDER_ID);
            String targetFilePath = testingClient.testing().exportImport().getExportImportTestDirectory() + File.separator + "singleFile-full.json";
            testingClient.testing().exportImport().setFile(targetFilePath);

            testingClient.testing().exportImport().setAction(ExportImportConfig.ACTION_EXPORT);
            testingClient.testing().exportImport().setRealmName(null);

            testingClient.testing().exportImport().runExport();

            // remove realm and import
            removeRealm(EXPORT_IMPORT_REALM);
            testingClient.testing().exportImport().setAction(ExportImportConfig.ACTION_IMPORT);
            testingClient.testing().exportImport().runImport();
        }
        
        // organizations for the realm should be enabled
        // verify IdP is enabled
        List<IdentityProviderRepresentation> idps = testRealm.organizations().get(org.getId()).identityProviders().getIdentityProviders();
        System.out.println("");
        
    }


}
