package fi.hsl.features.splitdatabasetables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DatabaseSplitApi {
    @Autowired
    private DatabaseSplitService databaseSynchronizationService;

    @PostMapping("/split")
    public ResponseEntity splitDatabaseJob(@Valid DatabaseSplitRequest databaseSplitRequest) throws Exception {
        this.databaseSynchronizationService.splitDatabaseTable(databaseSplitRequest);
        return ResponseEntity.ok().build();
    }
}
