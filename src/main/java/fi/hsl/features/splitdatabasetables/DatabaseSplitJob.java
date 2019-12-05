package fi.hsl.features.splitdatabasetables;

import fi.hsl.common.batch.DomainMappingProcessor;
import fi.hsl.common.batch.JdbcCursorItemReader;
import fi.hsl.common.batch.JdbcLongCountingCursorItemReaderBuilder;
import fi.hsl.common.batch.LongRowMapper;
import fi.hsl.configuration.databases.Database;
import fi.hsl.configuration.databases.ReadDatabase;
import fi.hsl.configuration.databases.WriteDatabase;
import fi.hsl.domain.Event;
import fi.hsl.domain.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class DatabaseSplitJob {
    private final Database.ReadSqlQuery readSqlQuery;
    private final ReadDatabase readDatabase;
    private final WriteDatabase writeDatabase;
    private final JobRepository jobRepository;
    private final JobParameters jobStartDate;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final ThreadPoolTaskExecutor threadTaskPool;

    public DatabaseSplitJob(Database.ReadSqlQuery readSqlQuery, ReadDatabase readDatabase, WriteDatabase writeDatabase, JobRepository jobRepository, JobParameters jobStartDate, JobLauncher jobLauncher, JobExplorer jobExplorer, JobOperator jobOperator) {
        this.readSqlQuery = readSqlQuery;
        this.readDatabase = readDatabase;
        this.writeDatabase = writeDatabase;
        this.jobRepository = jobRepository;
        this.jobStartDate = jobStartDate;
        this.jobLauncher = jobLauncher;
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
        this.threadTaskPool = new ThreadPoolTaskExecutor();
        threadTaskPool.setMaxPoolSize(16);
        threadTaskPool.setCorePoolSize(16);
        threadTaskPool.initialize();
    }

    public void restoreJob(long executionId) throws Exception {
        JobExecution jobExecution = this.jobExplorer.getJobExecution(executionId);
        Job databaseSyncJob = createDatabaseSplitjob(readDatabase, writeDatabase, new ArrayList<>(jobExecution.getStepExecutions()).get(0).getReadCount());
        jobLauncher.run(databaseSyncJob, new JobParameters(Map.of("jobStartDate", new JobParameter(new Date()))));
    }

    private Job createDatabaseSplitjob(ReadDatabase readDatabase, WriteDatabase writeDatabase, int currentRow) throws Exception {
        return new JobBuilderFactory(jobRepository).get("databaseSplitJob")
                .flow(createSplitJobReadStep(readDatabase, writeDatabase, currentRow)).end().build();
    }

    private Step createSplitJobReadStep(ReadDatabase readDatabase, WriteDatabase writeDatabase, int currentRow) throws Exception {
        StepBuilderFactory stepBuilderFactory = new StepBuilderFactory(jobRepository, writeDatabase.getWriteTransactionManager());
        return stepBuilderFactory.get("splitJobReadFromDatabase")
                .<Vehicle, Event>chunk(1000)
                .reader(createReader(readDatabase, currentRow))
                .faultTolerant()
                .retryPolicy(new AlwaysRetryPolicy())
                .listener(new ItemReadListener<>() {
                    private long timeNow;

                    @Override
                    public void beforeRead() {
                        this.timeNow = System.currentTimeMillis();
                    }

                    @Override
                    public void afterRead(Vehicle item) {
                        log.trace("Time taken to read an item: {} ms", System.currentTimeMillis() - timeNow);
                    }

                    @Override
                    public void onReadError(Exception ex) {

                    }
                })
                .processor(new DomainMappingProcessor())
                .writer(createWriter(writeDatabase))
                .faultTolerant()
                .retryPolicy(new AlwaysRetryPolicy())
                .listener(new ItemWriteListener<>() {
                    private long timeNow;

                    @Override
                    public void beforeWrite(List<? extends Event> items) {
                        this.timeNow = System.currentTimeMillis();
                    }

                    @Override
                    public void afterWrite(List<? extends Event> items) {
                        log.trace("Time taken to execute batch: {} ms of size: {}", System.currentTimeMillis() - timeNow, items.size());
                    }

                    @Override
                    public void onWriteError(Exception exception, List<? extends Event> items) {
                        log.trace("Error thrown: {}", exception);

                    }
                })
                .listener(new ChunkListener() {
                    private long timeNow;

                    @Override
                    public void beforeChunk(ChunkContext context) {
                        this.timeNow = System.currentTimeMillis();
                    }

                    @Override
                    public void afterChunk(ChunkContext context) {
                        log.trace("Chunk execution time: {} ms", System.currentTimeMillis() - timeNow);
                    }

                    @Override
                    public void afterChunkError(ChunkContext context) {

                    }
                })
                .taskExecutor(threadTaskPool)
                .build();

    }

    private ItemWriter<? super Event> createWriter(WriteDatabase writeDatabase) throws IOException {
        return new JpaItemWriterBuilder<>().entityManagerFactory(writeDatabase.getWriteEntityManager()).build();
    }

    private ItemStreamReader<Vehicle> createReader(ReadDatabase readDatabase, int currentRow) throws Exception {
        JdbcCursorItemReader<Vehicle> jdbcCursorItemReader = new JdbcLongCountingCursorItemReaderBuilder<Vehicle>()
                .name("databaseReader")
                .dataSource(readDatabase.getDataSource())
                .fetchSize(1000)
                .currentItemCount(currentRow)
                .sql("select * from vehicles")
                .rowMapper(new VehicleMapper())
                .build();
        jdbcCursorItemReader.afterPropertiesSet();
        return jdbcCursorItemReader;
    }

    public void launchJob(int currentRow) throws Exception {
        Job databaseSyncJob = createDatabaseSplitjob(readDatabase, writeDatabase, currentRow);
        jobLauncher.run(databaseSyncJob, jobStartDate);
    }

    private static class VehicleMapper implements LongRowMapper<Vehicle> {
        @Override
        public Vehicle mapRow(ResultSet resultSet, long i) throws SQLException {
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
            String transportMode = resultSet.getString("mode");
            if (transportMode != null) {
                Vehicle.TransportMode mode = Vehicle.TransportMode.valueOf(transportMode);
                vehicle.setTransport_mode(mode);
            }
            return vehicle;
        }

    }

}
