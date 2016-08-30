package lejendary.oauth2;

import lejendary.oauth2.config.OAuth2Properties;
import lejendary.oauth2.config.PropertiesConfiguration;
import lejendary.oauth2.util.DefaultProfileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableConfigurationProperties({OAuth2Properties.class, LiquibaseProperties.class})
@PropertySources({
        @PropertySource(value = "file:/opt/config/oauth2/application.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:/opt/config/oauth2/database.properties", ignoreResourceNotFound = true)
})
public class OAuth2Application {

    private final static Logger log = LoggerFactory.getLogger(OAuth2Application.class);

    public static void main(String[] args) throws UnknownHostException {
        PropertiesConfiguration.initialize(); // initialize the property files
        SpringApplication app = new SpringApplication(OAuth2Application.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment environment = app.run(args).getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                environment.getProperty("spring.application.name"),
                environment.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                environment.getProperty("server.port"));
    }
}
