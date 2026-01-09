/*
 * Copyright 2026 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.connections.jpa.updater.liquibase.custom;

import liquibase.exception.CustomChangeException;
import liquibase.statement.core.RawSqlStatement;

/**
 * Ensures ORG.ID column has correct charset for MySQL/MariaDB to allow foreign keys from ORG_INVITATION table.
 *
 * This fixes the issue where foreign key creation fails due to charset mismatch between ORG.ID and
 * ORG_INVITATION.ORGANIZATION_ID columns on MySQL/MariaDB databases.
 *
 * See https://github.com/keycloak/keycloak/issues/45239
 */
public class JpaUpdate26_6_0_OrgTableCharset extends CustomKeycloakTask {

    @Override
    protected void generateStatementsImpl() throws CustomChangeException {
        String orgTableName = getTableName("ORG");

        // Ensure ORG.ID column and its PRIMARY KEY index use the same charset & collation
        statements.add(new RawSqlStatement(
                "ALTER TABLE " + orgTableName + " DROP PRIMARY KEY"
        ));

        statements.add(new RawSqlStatement(
                "ALTER TABLE " + orgTableName +
                        " MODIFY ID VARCHAR(255) " +
                        " CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL"
        ));

        statements.add(new RawSqlStatement(
                "ALTER TABLE " + orgTableName + " ADD PRIMARY KEY (ID)"
        ));

        confirmationMessage.append(
                "Updated ORG.ID column and PRIMARY KEY index to utf8mb3_unicode_ci for FK compatibility"
        );
    }

    @Override
    protected String getTaskId() {
        return "Update ORG table charset and PK collation";
    }
}
