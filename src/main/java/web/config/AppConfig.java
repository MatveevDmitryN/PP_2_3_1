package web.config;


import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
@PropertySource("classpath:db.properties")
@EnableTransactionManagement
@ComponentScan(value = "web")
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private Environment env;

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.driver}")
    private String dbDriver;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.initialSize}")
    private int dbInitialSize;

    @Value("${db.minIdle}")
    private int dbMinIdle;

    @Value("${db.maxIdle}")
    private int dbMaxIdle;

    @Value("${db.timeBetweenEvictionRunsMillis}")
    private long dbTimeBetweenEvictionRunsMillis;

    @Value("${db.minEvictableIdleTimeMillis}")
    private long dbMinEvictableIdleTimeMillis;

    @Value("${db.testOnBorrow}")
    private boolean dbTestOnBorrow;

    @Value("${db.validationQuery}")
    private String dbValidationQuery;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(env.getRequiredProperty("db.entity.package"));
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(getHibernateProperties());
        return em;
    }

    private Properties getHibernateProperties() {
        try {
            Properties properties = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("hibernate.properties");
            if (is == null) {
                throw new IllegalStateException("File hibernate.properties is not found");
            }
            properties.load(is);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load hibernate.properties", e);
        }
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        logger.info("Configuring DataSource...");
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setInitialSize(dbInitialSize);
        dataSource.setMinIdle(dbMinIdle);
        dataSource.setMaxIdle(dbMaxIdle);
        dataSource.setTimeBetweenEvictionRunsMillis(dbTimeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(dbMinEvictableIdleTimeMillis);
        dataSource.setTestOnBorrow(dbTestOnBorrow);
        dataSource.setValidationQuery(dbValidationQuery);
        logger.info("DataSource configured successfully.");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager manager = new JpaTransactionManager();
        if (entityManagerFactory.getObject() == null) {
            throw new IllegalStateException("EntityManagerFactory is not initialized");
        }
        manager.setEntityManagerFactory(entityManagerFactory.getObject());
        return manager;
    }
}
