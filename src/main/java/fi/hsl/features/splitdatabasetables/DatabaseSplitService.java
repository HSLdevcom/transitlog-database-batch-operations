package fi.hsl.features.splitdatabasetables;

import fi.hsl.configuration.databases.Database;
import fi.hsl.configuration.databases.DatabasePoolFactory;
import fi.hsl.configuration.databases.ReadWriteDatabasePool;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseSplitService {
    private final SimpleJobOperator simpleJobOperator;
    @Autowired
    private DatabasePoolFactory databasePoolFactory;

    public DatabaseSplitService() {
        this.simpleJobOperator = new SimpleJobOperator();
    }

    void splitDatabaseTable(DatabaseSplitRequest databaseSplitRequest) throws Exception {
        ReadWriteDatabasePool from = databasePoolFactory.createPooledDatabaseInstance(databaseSplitRequest.getFrom());
        ReadWriteDatabasePool to = databasePoolFactory.createPooledDatabaseInstance(databaseSplitRequest.getTo());

        from.split(to, new Database.ReadSqlQuery("select * from vehicles", null));
    }

    void restoreJob(Database.DatabaseInstance from, long jobId) throws Exception {
        ReadWriteDatabasePool restoreFrom = databasePoolFactory.createPooledDatabaseInstance(from);
        ReadWriteDatabasePool to = databasePoolFactory.createPooledDatabaseInstance(from);
        restoreFrom.restoreJob(to, jobId, new Database.ReadSqlQuery("select * from vehicles", null));
    }
}
