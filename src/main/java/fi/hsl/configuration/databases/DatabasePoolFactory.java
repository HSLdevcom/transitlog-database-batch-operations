package fi.hsl.configuration.databases;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;

import static fi.hsl.configuration.databases.Database.DatabaseInstance.*;

@Component
public
class DatabasePoolFactory {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    @Qualifier("devReadTransactionManager")
    private PlatformTransactionManager devReadTransactionManager;
    @Autowired
    @Qualifier("devReadEntityManager")
    private EntityManagerFactory devReadEntityManager;
    @Autowired
    @Qualifier("stageWriteEntityManager")
    private EntityManagerFactory stageWriteEntityManagerFactory;
    @Autowired
    @Qualifier("stageReadEntityManager")
    private EntityManagerFactory stageReadEntityManagerFactory;
    @Autowired
    @Qualifier("stageReadTransactionManager")
    private PlatformTransactionManager stageReadTransactionManager;
    @Autowired
    @Qualifier("stageWriteTransactionManager")
    private PlatformTransactionManager stageWriteTransactionManager;
    @Autowired
    @Qualifier("prodTransactionManager")
    private PlatformTransactionManager prodTransactionManager;
    @Autowired
    @Qualifier("devWriteEntityManager")
    private EntityManagerFactory devWriteEntityManager;
    @Autowired
    @Qualifier("devWriteTransactionManager")
    private PlatformTransactionManager devWriteTransactionManager;
    @Qualifier("prodEntityManager")
    @Autowired
    private EntityManagerFactory prodEntityManagerFactory;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private JobOperator jobOperator;

    public ReadWriteDatabasePool createPooledDatabaseInstance(Database.DatabaseInstance databaseInstance) {
        if (databaseInstance.equals(DEV)) {
            ReadDatabase readDatabase = new ReadDatabase(devReadEntityManager, devReadTransactionManager, jobLauncher);
            WriteDatabase writeDatabase = new WriteDatabase(devWriteTransactionManager, devWriteEntityManager, jobLauncher, jobRepository, jobOperator);
            return new ReadWriteDatabasePool(readDatabase, writeDatabase, jobLauncher, jobExplorer);
        } else if (databaseInstance.equals(STAGE)) {
            return new ReadWriteDatabasePool(new ReadDatabase(stageReadEntityManagerFactory, stageWriteTransactionManager, jobLauncher),
                    new WriteDatabase(stageWriteTransactionManager, stageWriteEntityManagerFactory, jobLauncher, jobRepository, jobOperator), jobLauncher, jobExplorer);
        } else if (databaseInstance.equals(PROD)) {
            throw new NotYetImplementedException("Not yet implemented");
        }
        throw new IllegalArgumentException("Database not supported");
    }
}
