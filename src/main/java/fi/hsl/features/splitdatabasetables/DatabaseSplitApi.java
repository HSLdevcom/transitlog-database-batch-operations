package fi.hsl.features.splitdatabasetables;

import fi.hsl.configuration.databases.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DatabaseSplitApi {
    @Autowired
    private DatabaseSplitService databaseSplitService;

    @PostMapping("/split")
    public ResponseEntity splitDatabaseJob(@Valid DatabaseSplitRequest databaseSplitRequest) throws Exception {
        this.databaseSplitService.splitDatabaseTable(databaseSplitRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/restore")
    public ResponseEntity restoreDatabaseJob(Database.DatabaseInstance databaseInstance, long jobId) throws Exception {
        this.databaseSplitService.restoreJob(databaseInstance, jobId);
        return ResponseEntity.ok().build();
    }
}
