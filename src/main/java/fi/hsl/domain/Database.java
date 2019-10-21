package fi.hsl.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.batch.core.launch.JobLauncher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Database entity that allows operations on database entities
 */
public abstract class Database {
    private final List<ExecutableHpqlQuery> executableHpqlQueryList;
    final JobLauncher jobLauncher;

    public Database(JobLauncher jobLauncher) {
        executableHpqlQueryList = new ArrayList<>();
        this.jobLauncher = jobLauncher;
    }

    public enum DatabaseInstance {
        DEV, STAGE, PROD
    }


    @Data
    @AllArgsConstructor
    static abstract class ExecutableSqlQuery {
        private final String sqlQuery;
        private final Map<String, Object> parameterList;

    }

    public static class ReadSqlQuery extends ExecutableSqlQuery {
        public ReadSqlQuery(String sqlQuery, Map<String, Object> parameterList) {
            super(sqlQuery, parameterList);
        }
    }

    @Data
    @AllArgsConstructor
    public static abstract class ExecutableHpqlQuery {
        private final String hpqlQuery;
        private final Map<String, Object> parameterList;
    }

    public static class WriteHpqlHpqlQuery extends ExecutableHpqlQuery {
        WriteHpqlHpqlQuery(String hpqlQuery, Map<String, Object> parameterList) {
            super(hpqlQuery, parameterList);
        }
    }

    public static class ReadHpqlHpqlQuery extends ExecutableHpqlQuery {
        public ReadHpqlHpqlQuery(String hpqlQuery, Map<String, Object> parameterList) {
            super(hpqlQuery, parameterList);
        }
    }
}
