package fi.hsl.features.synchronizedatabases;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController(value = "/api/v1")
@Slf4j
public class DatabaseSynchronizationApi {
    @Autowired
    private DatabaseSynchronizationService databaseSynchronizationService;

    @PostMapping("/sync")
    public ResponseEntity synchronizeDatabases(@Valid DatabaseSynchronizationRequest databaseSynchronizationRequest) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        this.databaseSynchronizationService.synchronizeDatabasesOnVehiclesTable(databaseSynchronizationRequest);
        return ResponseEntity.ok().build();
    }
}