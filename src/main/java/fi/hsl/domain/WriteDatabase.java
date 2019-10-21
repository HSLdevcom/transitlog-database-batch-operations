package fi.hsl.domain;

import lombok.Data;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Data
public class WriteDatabase extends Database {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager writeTransactionManager;
    private final EntityManagerFactory writeEntityManager;

    WriteDatabase(PlatformTransactionManager writeTransactionManager, EntityManagerFactory writeEntityManager, JobLauncher jobLauncher, JobRepository jobRepository) {
        super(jobLauncher);
        this.jobRepository = jobRepository;
        this.writeTransactionManager = writeTransactionManager;
        this.writeEntityManager = writeEntityManager;
    }

    public DataSource getDataSource() {
        return ((JpaTransactionManager) writeTransactionManager).getDataSource();
    }

    JobRepository getJobRepository() {
        return jobRepository;
    }
}
