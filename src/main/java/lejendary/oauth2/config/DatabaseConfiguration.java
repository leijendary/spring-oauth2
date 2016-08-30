package lejendary.oauth2.config;

import lejendary.oauth2.config.liquibase.AsyncSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 2:38 PM
 */

@Configuration
public class DatabaseConfiguration {

    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);
    private final Environment environment;
    private final TaskExecutor taskExecutor;

    @Inject
    public DatabaseConfiguration(Environment environment, @Qualifier("taskExecutor") TaskExecutor taskExecutor) {
        this.environment = environment;
        this.taskExecutor = taskExecutor;
    }

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) throws SQLException {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        log.info("Configuring DataSource");
        if (dataSourceProperties.getUrl() == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                            " cannot start. Please check your Spring profile, current profiles are: {}",
                    Arrays.toString(environment.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }

        String connectionProperties = "";
        if (environment.getProperty("spring.datasource.connection-properties") != null)
            connectionProperties = environment.getProperty("spring.datasource.connection-properties");

        driverManagerDataSource.setUrl(dataSourceProperties.getUrl() + dataSourceProperties.getName() + connectionProperties);
        driverManagerDataSource.setUsername(dataSourceProperties.getUsername());
        driverManagerDataSource.setPassword(dataSourceProperties.getPassword());

        log.info("Datasource is now configured");

        if (dataSourceProperties.isInitialize())
            createSchema(dataSourceProperties, driverManagerDataSource, connectionProperties);

        return driverManagerDataSource;
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource, DataSourceProperties dataSourceProperties,
                                     LiquibaseProperties liquibaseProperties) {

        // Use liquibase.integration.spring.SpringLiquibase if you don't want Liquibase to start asynchronously
        SpringLiquibase liquibase = new AsyncSpringLiquibase(environment, taskExecutor);
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        log.debug("Configuring Liquibase");

        return liquibase;
    }

    private void createSchema(DataSourceProperties dataSourceProperties, DriverManagerDataSource driverManagerDataSource, String connectionProperties) throws SQLException {
        log.info("Checking if the '{}' database exists...", dataSourceProperties.getName());

        Connection connection = null;

        try {
            connection = driverManagerDataSource.getConnection();

            log.info("Database '{}' already exists", dataSourceProperties.getName());
        } catch (SQLException e) {

            if (dataSourceProperties.getName() == null || dataSourceProperties.getName().trim().length() == 0)
                throw new SQLException("Database name is empty");

            if (("Unknown database '" + dataSourceProperties.getName() + "'").equals(e.getMessage())) {

                log.info("Database '{}' is missing, creating database", dataSourceProperties.getName());

                driverManagerDataSource = new DriverManagerDataSource();
                driverManagerDataSource.setUrl(dataSourceProperties.getUrl());
                driverManagerDataSource.setUsername(dataSourceProperties.getUsername());
                driverManagerDataSource.setPassword(dataSourceProperties.getPassword());

                try {
                    connection = driverManagerDataSource.getConnection();

                    Statement statement = connection.createStatement();
                    statement.executeUpdate("CREATE SCHEMA " + dataSourceProperties.getName() + ";");
                    statement.close();

                    log.info("Reconnecting to the database");

                    driverManagerDataSource = new DriverManagerDataSource();
                    driverManagerDataSource.setUrl(dataSourceProperties.getUrl() + dataSourceProperties.getName() + connectionProperties);
                    driverManagerDataSource.setUsername(dataSourceProperties.getUsername());
                    driverManagerDataSource.setPassword(dataSourceProperties.getPassword());

                    log.info("Database '{}' was successfully created", dataSourceProperties.getName());

                } catch (SQLException e1) {
                    throw new SQLException(e1.getMessage());
                }

            } else {
                log.error("Error creating the database: {}", e);
            }

        } finally {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (SQLException e) {
                log.warn("Connection was not closed: {}", e.getMessage());
            }
        }
    }
}
