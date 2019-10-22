package fi.hsl.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@PropertySource({"classpath:application.properties"})
@EnableTransactionManagement
@Profile(value = "integration-test")
@EnableJpaRepositories(
        basePackages = "fi.hsl.domain",
        entityManagerFactoryRef = "devWriteEntityManager",
        transactionManagerRef = "devWriteTransactionManager"
)
public class H2WriteConfiguration {
    @Autowired
    private Environment env;

    @Bean
    @Primary
    public PlatformTransactionManager devWriteTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                writeEntityManager().getObject());
        return transactionManager;
    }

    @Bean(name = "devWriteEntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean writeEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(writeDataSource());
        em.setPackagesToScan(
                "fi.hsl.domain");
        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    @Primary
    public DataSource writeDataSource() {
        HikariDataSource dataSource
                = new HikariDataSource();
        dataSource.setDriverClassName(
                env.getProperty("jdbc.driverClassName"));
        dataSource.setJdbcUrl(env.getProperty("dev.write.jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.pass"));
        dataSource.setAutoCommit(false);
        return dataSource;

    }

}

