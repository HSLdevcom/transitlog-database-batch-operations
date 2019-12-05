package fi.hsl.features.synchronizedatabases;

import fi.hsl.configuration.databases.Database;
import fi.hsl.configuration.databases.DatabasePoolFactory;
import fi.hsl.configuration.databases.ReadWriteDatabasePool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
class DatabaseSynchronizationService {

    @Autowired
    private DatabasePoolFactory databasePoolFactory;

    void synchronizeDatabasesOnVehiclesTable(DatabaseSynchronizationRequest databaseSynchronizationRequest) throws Exception {
        ReadWriteDatabasePool from = databasePoolFactory.createPooledDatabaseInstance(databaseSynchronizationRequest.getFrom());
        ReadWriteDatabasePool to = databasePoolFactory.createPooledDatabaseInstance(databaseSynchronizationRequest.getTo());

        String vehiclesQuery = "select v from Vehicle v where v.received_at >=  :beginDate and v.received_at <=  :endDate";
        Map<String, Object> parameterValues = Map.of("beginDate", databaseSynchronizationRequest.getBeginDate(), "endDate", databaseSynchronizationRequest.getEndDate());
        from.sync(to, new Database.ReadHpqlHpqlQuery(vehiclesQuery, parameterValues));
    }

}
