package fi.hsl.domain;

import fi.hsl.features.splitdatabasetables.DatabaseSplitJob;
import fi.hsl.features.synchronizedatabases.DatabaseSyncJob;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class ReadWriteDatabasePool extends Database {
    private final ReadDatabase readReplica;
    private final WriteDatabase writeReplica;

    ReadWriteDatabasePool(ReadDatabase readReplica, WriteDatabase writeReplica, JobLauncher jobLauncher) {
        super(jobLauncher);
        this.readReplica = readReplica;
        this.writeReplica = writeReplica;
    }

    public void sync(ReadHpqlHpqlQuery readHpqlQuery) {
        JobParameters jobStartDate = new JobParameters(Map.of("jobStartDate", new JobParameter(new Date())));
        DatabaseSyncJob databaseSyncJob = new DatabaseSyncJob(readHpqlQuery, readReplica, writeReplica, writeReplica.getJobRepository(), jobStartDate);
        databaseSyncJob.launchJob();
    }

    public void sync(ReadWriteDatabasePool to, ReadHpqlHpqlQuery readHpqlQuery) {
        JobParameters jobStartDate = new JobParameters(Map.of("jobStartDate", new JobParameter(new Date())));
        DatabaseSyncJob databaseSyncJob = new DatabaseSyncJob(readHpqlQuery, readReplica, to.writeReplica, writeReplica.getJobRepository(), jobStartDate);
        databaseSyncJob.launchJob();
    }

    public void split(ReadWriteDatabasePool to, ReadSqlQuery readSqlQuery) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, SQLException {
        JobParameters jobStartDate = new JobParameters(Map.of("jobStartDate", new JobParameter(new Date())));
        DatabaseSplitJob databaseSplitJob = new DatabaseSplitJob(readSqlQuery, readReplica, to.writeReplica, writeReplica.getJobRepository(), jobStartDate, jobLauncher);
        databaseSplitJob.launchJob();
    }

}
