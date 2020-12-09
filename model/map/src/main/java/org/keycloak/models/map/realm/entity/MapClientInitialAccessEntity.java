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

package org.keycloak.models.map.realm.entity;

import java.util.Objects;
import org.keycloak.common.util.Time;
import org.keycloak.models.ClientInitialAccessModel;
import org.keycloak.models.utils.KeycloakModelUtils;

public class MapClientInitialAccessEntity {

    private String id;
    private int timestamp;
    private int expiration;
    private int count;
    private int remainingCount;

    private MapClientInitialAccessEntity() {}

    public static MapClientInitialAccessEntity createEntity(int expiration, int count) {
        int currentTime = Time.currentTime();

        MapClientInitialAccessEntity entity = new MapClientInitialAccessEntity();
        entity.setId(KeycloakModelUtils.generateId());
        entity.setTimestamp(currentTime);
        entity.setExpiration(expiration);
        entity.setCount(count);
        entity.setRemainingCount(count);
        return entity;
    }

    public static ClientInitialAccessModel toModel(MapClientInitialAccessEntity entity) {
        if (entity == null) return null;
        ClientInitialAccessModel model = new ClientInitialAccessModel();
        model.setId(entity.getId());
        model.setTimestamp(entity.getTimestamp());
        model.setExpiration(entity.getExpiration());
        model.setCount(entity.getCount());
        model.setRemainingCount(entity.getRemainingCount());
        return model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(int remainingCount) {
        this.remainingCount = remainingCount;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MapClientInitialAccessEntity)) return false;
        final MapClientInitialAccessEntity other = (MapClientInitialAccessEntity) obj;
        return Objects.equals(other.getId(), getId());
    }
}
