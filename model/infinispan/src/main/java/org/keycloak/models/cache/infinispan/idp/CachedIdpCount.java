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
package org.keycloak.models.cache.infinispan.idp;

import org.keycloak.models.RealmModel;
import org.keycloak.models.cache.infinispan.entities.AbstractRevisioned;
import org.keycloak.models.cache.infinispan.entities.InRealm;

public class CachedIdpCount extends AbstractRevisioned implements InRealm {

    private final RealmModel realm;
    private final long count;

    public CachedIdpCount(Long revision, RealmModel realm, long count) {
        super(revision, InfinispanIDPProvider.cacheKeyIdpCount(realm));
        this.realm = realm;
        this.count = count;
    }

    @Override
    public String getRealm() {
        return realm.getId();
    }

    public long getCount() {
        return count;
    }
}
