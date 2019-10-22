package fi.hsl.configuration;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
public class EnableBatchProcessing {
    @Bean
    @Qualifier("batchExecutor")
    TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(16);
        return threadPoolTaskExecutor;
    }
}
