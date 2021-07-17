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
package org.keycloak.models.tmp;

import java.util.Collections;
import java.util.List;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.models.JpaClientEntity;

/**
 *
 * @author vramik
 */
public class TmpJpaMapStorageClientEntityProvider implements JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        return Collections.<Class<?>>singletonList(JpaClientEntity.class);
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/tmp-client-changelog.xml";
    }

    @Override
    public String getFactoryId() {
        return TmpJpaMapStorageClientEntityProviderFactory.ID;
    }

    @Override
    public void close() {
    }

}
