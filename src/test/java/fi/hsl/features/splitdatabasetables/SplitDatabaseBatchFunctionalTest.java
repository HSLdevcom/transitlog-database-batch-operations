package fi.hsl.features.splitdatabasetables;

import fi.hsl.DatabaseSynchronizationGateway;
import fi.hsl.config.DummyBeans;
import fi.hsl.config.H2ReadConfiguration;
import fi.hsl.config.H2WriteConfiguration;
import fi.hsl.features.Database;
import fi.hsl.features.DatabasePoolFactory;
import fi.hsl.features.ReadWriteDatabasePool;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DatabaseSynchronizationGateway.class, H2WriteConfiguration.class, H2ReadConfiguration.class, DummyBeans.class})
@ActiveProfiles(value = {"integration-test"})
@TestPropertySource(value = "classpath:/application.properties")
public class SplitDatabaseBatchFunctionalTest {

    @Autowired
    private DatabasePoolFactory databasePoolFactory;
    private ReadWriteDatabasePool from;
    private ReadWriteDatabasePool to;

    @Before
    public void init() {
        from = databasePoolFactory.createPooledDatabaseInstance(Database.DatabaseInstance.DEV);
        to = databasePoolFactory.createPooledDatabaseInstance(Database.DatabaseInstance.DEV);
    }

    @Test
    @Ignore
    public void splitBatchJob_succesfulRun_shouldSplitTables() throws SQLException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        from.split(to, new Database.ReadSqlQuery("select * from vehicles", null));
    }
}
