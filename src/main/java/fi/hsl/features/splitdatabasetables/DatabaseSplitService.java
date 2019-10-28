package fi.hsl.features.splitdatabasetables;

import fi.hsl.configuration.databases.Database;
import fi.hsl.configuration.databases.DatabasePoolFactory;
import fi.hsl.configuration.databases.ReadWriteDatabasePool;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;

@Service
public class DatabaseSplitService {
    private final SimpleJobOperator simpleJobOperator;
    @Autowired
    private DatabasePoolFactory databasePoolFactory;

    public DatabaseSplitService() {
        this.simpleJobOperator = new SimpleJobOperator();
    }

    void splitDatabaseTable(DatabaseSplitRequest databaseSplitRequest) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, SQLException, IOException {
        ReadWriteDatabasePool from = databasePoolFactory.createPooledDatabaseInstance(databaseSplitRequest.getFrom());
        ReadWriteDatabasePool to = databasePoolFactory.createPooledDatabaseInstance(databaseSplitRequest.getTo());


        from.split(to, new Database.ReadSqlQuery("select * from vehicles", null));
    }

}
