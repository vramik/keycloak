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

package org.keycloak.connection;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.naming.NamingException;
import org.hibernate.cfg.AvailableSettings;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.common.util.StackUtil;
import org.keycloak.common.util.StringPropertyReplacer;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.connections.jpa.JpaConnectionProviderFactory;
import org.keycloak.connections.jpa.JpaKeycloakTransaction;
import org.keycloak.connections.jpa.util.JpaUtils;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.utils.KeycloakModelUtils;

public class ClientMapStorageJpaConnectionProviderFactory implements JpaConnectionProviderFactory {

    private static final String PROVIDER_ID = "jpa-clients";

    private static final Logger logger = Logger.getLogger(ClientMapStorageJpaConnectionProviderFactory.class);

    private volatile EntityManagerFactory emf;

    private Config.Scope config;

    @Override
    public JpaConnectionProvider create(KeycloakSession session) {
        logger.trace("Create JpaConnectionProvider");
        lazyInit(session);

        logger.trace("enlisting EntityManager in JpaKeycloakTransaction");
        EntityManager em = emf.createEntityManager();
        session.getTransactionManager().enlist(new JpaKeycloakTransaction(em));
        return new ClientMapStorageJpaConnectionProvider(em);
    }

    @Override
    public void close() {
        if (emf != null) {
            emf.close();
        }
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void init(Config.Scope config) {
        this.config = config;
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    private void lazyInit(KeycloakSession session) {
        if (emf == null) {
            synchronized (this) {
                if (emf == null) {
                    KeycloakModelUtils.suspendJtaTransaction(session.getKeycloakSessionFactory(), () -> {
                        logger.debugf("Initializing JPA connections%s", StackUtil.getShortStackTrace());

                        Map<String, Object> properties = new HashMap<>();

                        String unitName = "keycloak-default";

                        String dataSource = config.get("dataSource");
                        if (dataSource != null) {
                            properties.put(AvailableSettings.JPA_NON_JTA_DATASOURCE, dataSource);
                        } else {
                            properties.put(AvailableSettings.JPA_JDBC_URL, config.get("url"));
                            properties.put(AvailableSettings.JPA_JDBC_DRIVER, config.get("driver"));

                            String user = config.get("user");
                            if (user != null) {
                                properties.put(AvailableSettings.JPA_JDBC_USER, user);
                            }
                            String password = config.get("password");
                            if (password != null) {
                                properties.put(AvailableSettings.JPA_JDBC_PASSWORD, password);
                            }
                        }

                        String schema = getSchema();
                        if (schema != null) {
                            properties.put(JpaUtils.HIBERNATE_DEFAULT_SCHEMA, schema);
                        }

                        properties.put("hibernate.show_sql", config.getBoolean("showSql", false));
                        properties.put("hibernate.format_sql", config.getBoolean("formatSql", true));

                        Connection connection = getConnection();
                        try {
                            prepareOperationalInfo(connection);

                            logger.trace("Creating EntityManagerFactory");
                            Collection<ClassLoader> classLoaders = new ArrayList<>();
                            if (properties.containsKey(AvailableSettings.CLASSLOADERS)) {
                                classLoaders.addAll((Collection<ClassLoader>) properties.get(AvailableSettings.CLASSLOADERS));
                            }
                            classLoaders.add(getClass().getClassLoader());
                            properties.put(AvailableSettings.CLASSLOADERS, classLoaders);
                            emf = JpaUtils.createEntityManagerFactory(session, unitName, properties, false);
                            logger.trace("EntityManagerFactory created");

                        } finally {
                            // Close after creating EntityManagerFactory to prevent in-mem databases from closing
                            if (connection != null) {
                                try {
                                    connection.close();
                                } catch (SQLException e) {
                                    logger.warn("Can't close connection", e);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    protected void prepareOperationalInfo(Connection connection) {
        try {
            HashMap<String, String> operationalInfo = new LinkedHashMap<>();
            DatabaseMetaData md = connection.getMetaData();
            operationalInfo.put("databaseUrl", md.getURL());
            operationalInfo.put("databaseUser", md.getUserName());
            operationalInfo.put("databaseProduct", md.getDatabaseProductName() + " " + md.getDatabaseProductVersion());
            operationalInfo.put("databaseDriver", md.getDriverName() + " " + md.getDriverVersion());

            logger.infof("Database info: %s", operationalInfo.toString());
        } catch (SQLException e) {
            logger.warn("Unable to prepare operational info due database exception: " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() {
        try {
            String dataSourceLookup = config.get("dataSource");
            if (dataSourceLookup != null) {
                DataSource dataSource = (DataSource) new InitialContext().lookup(dataSourceLookup);
                return dataSource.getConnection();
            } else {
                Class.forName(config.get("driver"));
                return DriverManager.getConnection(
                        StringPropertyReplacer.replaceProperties(config.get("url"), System.getProperties()), 
                        config.get("user"), 
                        config.get("password"));
            }
        } catch (ClassNotFoundException | SQLException | NamingException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    @Override
    public String getSchema() {
        return config.get("schema");
    }

}
