package fi.hsl.features.synchronizedatabases;

import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity synchronizeDatabases(@Valid DatabaseSynchronizationRequest databaseSynchronizationRequest) throws Exception {
        this.databaseSynchronizationService.synchronizeDatabasesOnVehiclesTable(databaseSynchronizationRequest);
        return ResponseEntity.ok().build();
    }
}