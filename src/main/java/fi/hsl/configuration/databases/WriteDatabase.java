package fi.hsl.configuration.databases;

import lombok.Data;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;

@Data
public class WriteDatabase extends Database {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager writeTransactionManager;
    private final EntityManagerFactory writeEntityManager;
    private final JobOperator jobOperator;

    WriteDatabase(PlatformTransactionManager writeTransactionManager, EntityManagerFactory writeEntityManager, JobLauncher jobLauncher, JobRepository jobRepository, JobOperator jobOperator) {
        super(jobLauncher);
        this.jobRepository = jobRepository;
        this.writeTransactionManager = writeTransactionManager;
        this.writeEntityManager = writeEntityManager;
        this.jobOperator = jobOperator;
    }

    JobRepository getJobRepository() {
        return jobRepository;
    }
}
