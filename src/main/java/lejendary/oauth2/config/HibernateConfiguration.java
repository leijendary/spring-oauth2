package lejendary.oauth2.config;

import lejendary.oauth2.util.AppConstants;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 2:49 PM
 */

@Configuration
@EnableTransactionManagement
@ComponentScan(AppConstants.PACKAGE_NAME)
public class HibernateConfiguration {

    private final Environment environment;
    private final DataSource dataSource;

    @Inject
    public HibernateConfiguration(Environment environment, DataSource dataSource) {
        this.environment = environment;
        this.dataSource = dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(dataSource);
        localSessionFactoryBean.setPackagesToScan(AppConstants.PACKAGE_NAME);
        localSessionFactoryBean.setHibernateProperties(hibernateProperties());

        return localSessionFactoryBean;
    }

    @Bean
    @Inject
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
        hibernateTransactionManager.setSessionFactory(sessionFactory);

        return hibernateTransactionManager;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();

        for (PropertySource<?> environmentPropertySource : ((AbstractEnvironment) environment).getPropertySources()) {
            if (environmentPropertySource instanceof MapPropertySource) {
                ((MapPropertySource) environmentPropertySource)
                        .getSource()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().startsWith("hibernate."))
                        .forEach(entry -> properties.put(entry.getKey(), entry.getValue()));
            }
        }

        return properties;
    }
}
