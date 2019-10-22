package fi.hsl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;

import static org.mockito.Mockito.mock;

@Configuration
@Profile(value = "integration-test")
public class DummyBeans {

    @Bean(name = "stageWriteEntityManager")
    public EntityManagerFactory stageWriteEntityManager() {
        return mock(EntityManagerFactory.class);
    }

    @Bean(name = "stageReadEntityManager")
    public EntityManagerFactory stageReadEntityManager() {
        return mock(EntityManagerFactory.class);
    }

    @Bean
    public EntityManagerFactory prodEntityManager() {
        return mock(EntityManagerFactory.class);
    }

    @Bean
    public PlatformTransactionManager prodTransactionManager() {
        return mock(PlatformTransactionManager.class);
    }

    @Bean
    public PlatformTransactionManager stageWriteTransactionManager() {
        return mock(PlatformTransactionManager.class);
    }

    @Bean
    public PlatformTransactionManager stageReadTransactionManager() {
        return mock(PlatformTransactionManager.class);
    }
}
