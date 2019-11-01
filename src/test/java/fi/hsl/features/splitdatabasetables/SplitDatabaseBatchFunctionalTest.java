package fi.hsl.features.splitdatabasetables;

import fi.hsl.DatabaseSynchronizationGateway;
import fi.hsl.config.DummyBeans;
import fi.hsl.config.H2ReadConfiguration;
import fi.hsl.config.H2WriteConfiguration;
import fi.hsl.configuration.databases.Database;
import fi.hsl.configuration.databases.DatabasePoolFactory;
import fi.hsl.configuration.databases.ReadWriteDatabasePool;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    public void splitBatchJob_succesfulRun_shouldSplitTables() throws Exception {
        from.split(to, new Database.ReadSqlQuery("select * from vehicles", null));
    }
}
