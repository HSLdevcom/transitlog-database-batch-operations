package fi.hsl.features.splitdatabasetables;

import fi.hsl.configuration.databases.Database;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DatabaseSplitApi {
    @Autowired
    private DatabaseSplitService databaseSynchronizationService;

    @PostMapping("/restartsplit")
    public ResponseEntity restartSplitJob(Long executionId, Database.DatabaseInstance databaseInstance) throws JobParametersInvalidException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, NoSuchJobException {
        this.databaseSynchronizationService.restartJob(executionId, databaseInstance);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/split")
    public ResponseEntity splitDatabaseJob(@Valid DatabaseSplitRequest databaseSplitRequest) throws Exception {
        this.databaseSynchronizationService.splitDatabaseTable(databaseSplitRequest);
        return ResponseEntity.ok().build();
    }
}
