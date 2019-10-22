package fi.hsl.features.synchronizedatabases;

import fi.hsl.features.Database;
import fi.hsl.features.DatabasePoolFactory;
import fi.hsl.features.ReadWriteDatabasePool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
class DatabaseSynchronizationService {

    @Autowired
    private DatabasePoolFactory databasePoolFactory;

    void synchronizeDatabasesOnVehiclesTable(DatabaseSynchronizationRequest databaseSynchronizationRequest) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        ReadWriteDatabasePool from = databasePoolFactory.createPooledDatabaseInstance(databaseSynchronizationRequest.getFrom());
        ReadWriteDatabasePool to = databasePoolFactory.createPooledDatabaseInstance(databaseSynchronizationRequest.getTo());

        String vehiclesQuery = "select v from Vehicle v where v.received_at >=  :beginDate and v.received_at <=  :endDate";
        Map<String, Object> parameterValues = Map.of("beginDate", databaseSynchronizationRequest.getBeginDate(), "endDate", databaseSynchronizationRequest.getEndDate());
        from.sync(to, new Database.ReadHpqlHpqlQuery(vehiclesQuery, parameterValues));
    }

}
