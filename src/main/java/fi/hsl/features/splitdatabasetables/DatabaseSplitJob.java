package fi.hsl.features.splitdatabasetables;

import fi.hsl.domain.Vehicle;
import fi.hsl.features.Database;
import fi.hsl.features.ReadDatabase;
import fi.hsl.features.WriteDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseSplitJob {
    private final Database.ReadSqlQuery readSqlQuery;
    private final ReadDatabase readDatabase;
    private final WriteDatabase writeDatabase;
    private final JobRepository jobRepository;
    private final JobParameters jobStartDate;
    private final JobLauncher jobLauncher;

    public DatabaseSplitJob(Database.ReadSqlQuery readSqlQuery, ReadDatabase readDatabase, WriteDatabase writeDatabase, JobRepository jobRepository, JobParameters jobStartDate, JobLauncher jobLauncher) {
        this.readSqlQuery = readSqlQuery;
        this.readDatabase = readDatabase;
        this.writeDatabase = writeDatabase;
        this.jobRepository = jobRepository;
        this.jobStartDate = jobStartDate;
        this.jobLauncher = jobLauncher;
    }

    public void launchJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, SQLException {
        Job databaseSyncJob = createDatabaseSyncJob(readSqlQuery, readDatabase, writeDatabase);
        jobLauncher.run(databaseSyncJob, jobStartDate);
    }

    private Job createDatabaseSyncJob(Database.ReadSqlQuery executableHpqlQuery, ReadDatabase readDatabase, WriteDatabase writeDatabase) throws SQLException {
        return new JobBuilderFactory(jobRepository).get("databaseSyncJob")
                .flow(createSplitJobReadStep(readDatabase, writeDatabase, executableHpqlQuery)).end().build();
    }

    private Step createSplitJobReadStep(ReadDatabase readDatabase, WriteDatabase writeDatabase, Database.ReadSqlQuery executableSqlQuery) throws SQLException {
        StepBuilderFactory stepBuilderFactory = new StepBuilderFactory(jobRepository, writeDatabase.getWriteTransactionManager());
        return stepBuilderFactory.get("splitJobReadFromDatabase")
                .<Vehicle, Object>chunk(20)
                .reader(createReader(readDatabase, executableSqlQuery.getSqlQuery()))
                .processor(new SplitJobLoggingProcessor())
                .processor(new DomainMappingProcessor())
                .writer(createWriter(writeDatabase))
                .faultTolerant()
                //Do nothing on duplicates
                .skipPolicy(new DuplicateViolationPolicy())
                .build();

    }

    private ItemWriter<? super Object> createWriter(WriteDatabase writeDatabase) {
        PersistingJpaItemWriter persistingJpaItemWriter = new PersistingJpaItemWriter();
        persistingJpaItemWriter.setEntityManagerFactory(writeDatabase.getWriteEntityManager());
        return persistingJpaItemWriter;
    }

    private ItemReader<Vehicle> createReader(ReadDatabase readDatabase, String queryString) {
        return new JdbcCursorItemReaderBuilder<Vehicle>()
                .name("databaseReader")
                .dataSource(readDatabase.getDataSource())
                .fetchSize(500)
                .sql(queryString)
                .driverSupportsAbsolute(true)
                .rowMapper(new VehicleMapper())
                .build();
    }


    @Slf4j
    private static class SplitJobLoggingProcessor extends CompositeItemProcessor<Vehicle, Vehicle> {
        @Override
        public Vehicle process(Vehicle item) {
            log.info("Found object: {}", item);
            return item;
        }

    }

    private class VehicleMapper implements org.springframework.jdbc.core.RowMapper<Vehicle> {
        @Override
        public Vehicle mapRow(ResultSet resultSet, int i) throws SQLException {
            Vehicle vehicle = new Vehicle();
            vehicle.setReceived_at(resultSet.getTimestamp("received_at"));
            vehicle.setTopic_prefix(resultSet.getString("topic_prefix"));
            vehicle.setTopic_version(resultSet.getString("topic_version"));
            vehicle.setJourney_type(Vehicle.JourneyType.valueOf(resultSet.getString("journey_type")));
            vehicle.setIs_ongoing(resultSet.getBoolean("is_ongoing"));
            vehicle.setEvent_type(Vehicle.EventType.valueOf(resultSet.getString("event_type")));
            vehicle.setOwner_operator_id(resultSet.getInt("owner_operator_id"));
            vehicle.setVehicle_number(resultSet.getInt("vehicle_number"));
            vehicle.setUnique_vehicle_id(resultSet.getString("unique_vehicle_id"));
            vehicle.setRoute_id(resultSet.getString("route_id"));
            vehicle.setDirection_id(resultSet.getInt("direction_id"));
            vehicle.setHeadsign(resultSet.getString("headsign"));
            vehicle.setJourney_start_time(resultSet.getTime("journey_start_time"));
            vehicle.setNext_stop_id(resultSet.getString("next_stop_id"));
            vehicle.setGeohash_level(resultSet.getInt("geohash_level"));
            vehicle.setTopic_latitude(resultSet.getDouble("topic_latitude"));
            vehicle.setTopic_longitude(resultSet.getDouble("topic_longitude"));
            vehicle.setDesi(resultSet.getString("desi"));
            vehicle.setDir(resultSet.getInt("dir"));
            vehicle.setOper(resultSet.getInt("oper"));
            vehicle.setVeh(resultSet.getInt("veh"));
            vehicle.setTst(resultSet.getTimestamp("tst"));
            vehicle.setTsi(new BigInteger(Integer.valueOf(resultSet.getInt("tsi")).toString()));
            vehicle.setSpd(resultSet.getDouble("spd"));
            vehicle.setHdg(resultSet.getInt("hdg"));
            vehicle.setLat(resultSet.getDouble("lat"));
            vehicle.setLongitude(resultSet.getDouble("long"));
            vehicle.setAcc(resultSet.getDouble("acc"));
            vehicle.setDl(resultSet.getInt("dl"));
            vehicle.setOdo(resultSet.getDouble("odo"));
            vehicle.setDrst(resultSet.getBoolean("drst"));
            vehicle.setOday(resultSet.getDate("oday"));
            vehicle.setJrn(resultSet.getInt("jrn"));
            vehicle.setLine(resultSet.getInt("line"));
            vehicle.setLocation_quality_method(Vehicle.LocationQualityMethod.valueOf(resultSet.getString("loc")));
            vehicle.setStop(resultSet.getInt("stop"));
            vehicle.setRoute(resultSet.getString("route"));
            vehicle.setOccu(resultSet.getInt("occu"));
            vehicle.setTransport_mode(Vehicle.TransportMode.valueOf(resultSet.getString("mode")));

            return vehicle;
        }
    }

}
