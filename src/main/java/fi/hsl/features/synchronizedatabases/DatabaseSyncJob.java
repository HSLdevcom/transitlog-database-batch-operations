package fi.hsl.features.synchronizedatabases;

import fi.hsl.features.Database;
import fi.hsl.features.ReadDatabase;
import fi.hsl.features.WriteDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

public class DatabaseSyncJob {
    private final PlatformTransactionManager writeTransactionManager;
    private final JobRepository jobRepository;
    private final Database.ExecutableHpqlQuery readQuery;
    private final EntityManagerFactory readEntityManagerFactory;
    private final JobParameters jobStartDate;
    private EntityManagerFactory writeEntityManagerFactory;

    public DatabaseSyncJob(Database.ReadHpqlHpqlQuery readHpqlQuery, ReadDatabase readDatabase, WriteDatabase writeDatabase, JobRepository jobRepository, JobParameters jobStartDate) {
        this.readQuery = readHpqlQuery;
        this.readEntityManagerFactory = readDatabase.getReadEntityManager();
        this.writeEntityManagerFactory = writeDatabase.getWriteEntityManager();
        this.writeTransactionManager = writeDatabase.getWriteTransactionManager();
        this.jobRepository = jobRepository;
        this.jobStartDate = jobStartDate;
    }

    public void launchJob() {
        createDatabaseSyncJob(readQuery, writeEntityManagerFactory);
    }

    private Job createDatabaseSyncJob(Database.ExecutableHpqlQuery executableHpqlQuery, EntityManagerFactory writeEntityManager) {
        return new JobBuilderFactory(jobRepository).get("databaseSyncJob")
                .flow(createSyncStep(writeEntityManager, executableHpqlQuery))
                .end().build();
    }

    private Step createSyncStep(EntityManagerFactory writeEntityManager, Database.ExecutableHpqlQuery executableHpqlQuery) {
        StepBuilderFactory stepBuilderFactory = new StepBuilderFactory(jobRepository, writeTransactionManager);
        return stepBuilderFactory.get("readFromDatabaseStep")
                .chunk(1)
                .reader(createReader(readEntityManagerFactory, executableHpqlQuery.getHpqlQuery(), executableHpqlQuery.getParameterList()))
                .processor(new SyncJobLoggingProcessor())
                .writer(createWriter(writeEntityManager))
                .build();

    }

    private ItemWriter<? super Object> createWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    private ItemReader<?> createReader(EntityManagerFactory entityManagerFactory, String queryString, Map<String, Object> parameterValues) {
        return new JpaPagingItemReaderBuilder<>()
                .name("databaseReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString(queryString)
                .parameterValues(parameterValues)
                .build();
    }

    @Slf4j
    private static class SyncJobLoggingProcessor implements ItemProcessor<Object, Object> {
        @Override
        public Object process(Object item) throws Exception {
            log.info("Found object: {}", item);
            return item;
        }
    }

}
