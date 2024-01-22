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

import java.util.Locale;
import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.core.ClobType;
import liquibase.util.StringUtil;

/**
 * Originally {@code NCLOB} is transferred to {@code LONGTEXT CHARACTER SET utf8}: 
 * https://github.com/liquibase/liquibase/blob/043acd1581b695ad19c484ee985c6283666040d0/liquibase-standard/src/main/java/liquibase/datatype/core/ClobType.java#L82-L95
 * 
 * In MySQL 8.0 the utf8 character set is alias for utf8mb3, which is at the same time deprecated:
 * https://dev.mysql.com/doc/refman/8.0/en/charset-unicode-utf8.html
 * 
 * Therefore this class changes it to {@code LONGTEXT CHARACTER SET CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci}
 */
public class MySQLClobType extends ClobType {

    @Override
    public int getPriority() {
        return super.getPriority() + 1; // Always take precedence over NVarcharType
    }

    @Override
    public DatabaseDataType toDatabaseDataType(Database database) {
        // this would have to watched and maintained with each liquibase change in: https://github.com/liquibase/liquibase/blob/043acd1581b695ad19c484ee985c6283666040d0/liquibase-standard/src/main/java/liquibase/datatype/core/ClobType.java#L82-L95
        // TODO try to make it resilient 
        if (database instanceof MySQLDatabase) {
            String originalDefinition = StringUtil.trimToEmpty(getRawDefinition());

            if (originalDefinition.toLowerCase(Locale.US).startsWith("nclob")) {
                DatabaseDataType type = new DatabaseDataType("LONGTEXT");
                type.addAdditionalInformation("CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
                return type;
            }
        }
        return super.toDatabaseDataType(database);
    }
}
