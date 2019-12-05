package fi.hsl.configuration.databases;

import fi.hsl.features.splitdatabasetables.DatabaseSplitJob;
import fi.hsl.features.synchronizedatabases.DatabaseSyncJob;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;

import java.util.Date;
import java.util.Map;

public class ReadWriteDatabasePool extends Database {
    private final ReadDatabase readReplica;
    private final WriteDatabase writeReplica;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    ReadWriteDatabasePool(ReadDatabase readReplica, WriteDatabase writeReplica, JobLauncher jobLauncher, JobExplorer jobExplorer, JobOperator jobOperator) {
        super(jobLauncher);
        this.readReplica = readReplica;
        this.writeReplica = writeReplica;
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
    }


    public void sync(ReadWriteDatabasePool to, ReadHpqlHpqlQuery readHpqlQuery) throws Exception {
        JobParameters jobStartDate = new JobParameters(Map.of("jobStartDate", new JobParameter(new Date())));
        DatabaseSyncJob databaseSyncJob = new DatabaseSyncJob(readHpqlQuery, readReplica, to.writeReplica, writeReplica.getJobRepository(), jobStartDate);
        databaseSyncJob.launchJob();
    }

    public void split(ReadWriteDatabasePool to, ReadSqlQuery readSqlQuery, int currentRow) throws Exception {
        JobParameters jobStartDate = new JobParameters(Map.of("jobStartDate", new JobParameter(new Date())));
        DatabaseSplitJob databaseSplitJob = new DatabaseSplitJob(readSqlQuery, readReplica, to.writeReplica, writeReplica.getJobRepository(), jobStartDate, jobLauncher, jobExplorer, jobOperator);
        databaseSplitJob.launchJob(currentRow);
    }

    public void restoreJob(ReadWriteDatabasePool to, Long executionId, ReadSqlQuery readSqlQuery) throws Exception {
        DatabaseSplitJob databaseSplitJob = new DatabaseSplitJob(readSqlQuery, readReplica, to.writeReplica, writeReplica.getJobRepository(), null, jobLauncher, jobExplorer, jobOperator);
        databaseSplitJob.restoreJob(executionId);
    }

}
