/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package org.keycloak.connections.jpa.updater.liquibase;

import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.core.NVarcharType;

/**
 * Changes {@code NVARCHAR(x)} type to {@code VARCHAR(x) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci} for MySQL database.
 * 
 * https://dev.mysql.com/doc/refman/8.0/en/charset-national.html
 * 
 */
public class MySQLNVarcharType extends NVarcharType {

    @Override
    public int getPriority() {
        return super.getPriority() + 1; // Always take precedence over NVarcharType
    }

    @Override
    public DatabaseDataType toDatabaseDataType(Database database) {
        // TODO this could be moved to some utility class to get rid of duplicity
        if (database instanceof MySQLDatabase) {
//            String additionalInformation = super.getAdditionalInformation();
//            if (additionalInformation.toLowerCase().contains("character set utf8")) {
//                additionalInformation.replace(additionalInformation, additionalInformation)
//            }
            DatabaseDataType databaseDataType = super.toDatabaseDataType(database);
            String newType = databaseDataType.getType().replace("NVARCHAR", "VARCHAR"); //TODO assuming uppercase
            databaseDataType.setType(newType);
            databaseDataType.addAdditionalInformation("CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci"); // hoping that additionalInformation doesn't contain "character set ..." already, TODO fix
            return databaseDataType;
        }
        return super.toDatabaseDataType(database);
    }
}
