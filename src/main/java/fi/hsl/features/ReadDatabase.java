package fi.hsl.features;

import lombok.Data;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReadDatabase extends Database {
    private final EntityManagerFactory readEntityManager;
    private final PlatformTransactionManager readTransactionManager;
    private final List<ExecutableHpqlQuery> executableHpqlQueryList;

    ReadDatabase(EntityManagerFactory readEntityManager, PlatformTransactionManager readTransactionManager, JobLauncher jobLauncher) {
        super(jobLauncher);
        this.readEntityManager = readEntityManager;
        this.readTransactionManager = readTransactionManager;
        this.executableHpqlQueryList = new ArrayList<>();
    }

    public DataSource getDataSource() {
        return ((JpaTransactionManager) readTransactionManager).getDataSource();
    }
}
