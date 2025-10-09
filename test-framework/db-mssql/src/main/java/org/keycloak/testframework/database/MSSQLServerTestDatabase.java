package org.keycloak.testframework.database;

import org.jboss.logging.Logger;
import org.keycloak.testframework.util.ContainerImages;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

class MSSQLServerTestDatabase extends AbstractContainerTestDatabase {

    private static final Logger LOGGER = Logger.getLogger(MSSQLServerTestDatabase.class);

    public static final String NAME = "mssql";

    @SuppressWarnings("resource")
    @Override
    public JdbcDatabaseContainer<?> createContainer() {
        return new MSSQLServerContainer<>(DockerImageName.parse(ContainerImages.getContainerImageName(NAME))).withPassword(getPassword()).withEnv("MSSQL_PID", "Express").acceptLicense();
    }

    @Override
    public void withDatabaseAndUser(String database, String username, String password) {
        // MSSQLServerContainer does not support withUsername and withDatabase
    }

    @Override
    public String getDatabaseVendor() {
        return NAME;
    }

    @Override
    public String getUsername() {
        return "sa";
    }

    @Override
    public String getPassword() {
        return "vEry$tron9Pwd";
    }

    @Override
    public String getJdbcUrl(boolean internal) {
        return super.getJdbcUrl(internal) + ";integratedSecurity=false;encrypt=false;trustServerCertificate=true;sendStringParametersAsUnicode=false;";
    }

    @Override
    public List<String> getPostStartCommand() {
        // Adding -N for encryption and -C to trust the self-signed certificate
        String sqlCmdOptions = String.format("-U sa -P '%s' -N -C", getPassword());

        String createDbCommand = String.format("/opt/mssql-tools18/bin/sqlcmd %s -Q 'CREATE DATABASE %s;'", sqlCmdOptions, getDatabase());
        String alterDbCommand = String.format("/opt/mssql-tools18/bin/sqlcmd %s -d %s -Q 'ALTER DATABASE %s SET READ_COMMITTED_SNAPSHOT ON;'", sqlCmdOptions, getDatabase(), getDatabase());

        // Redirect stdout and stderr (2>&1) of the whole sequence to a log file for debugging
        String fullCommand = String.format("(%s && %s) > /tmp/setup-script.log 2>&1", createDbCommand, alterDbCommand);

        return List.of("sh", "-c", fullCommand);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }
}
