package fi.hsl.common.batch;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.*;
import org.springframework.batch.support.DatabaseType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Map;

public class JdbcNonLimitingPagingItemReaderBuilder<T> {

    private DataSource dataSource;

    private int fetchSize = JdbcPagingItemReader.VALUE_NOT_SET;

    private PagingQueryProvider queryProvider;

    private RowMapper<T> rowMapper;

    private Map<String, Object> parameterValues;

    private int pageSize = 10;

    private String groupClause;

    private String selectClause;

    private String fromClause;

    private String whereClause;

    private Map<String, Order> sortKeys;

    private boolean saveState = true;

    private String name;

    private int maxItemCount = Integer.MAX_VALUE;

    private int currentItemCount;

    /**
     * Configure if the state of the {@link org.springframework.batch.item.ItemStreamSupport}
     * should be persisted within the {@link org.springframework.batch.item.ExecutionContext}
     * for restart purposes.
     *
     * @param saveState defaults to true
     * @return The current instance of the builder.
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> saveState(boolean saveState) {
        this.saveState = saveState;

        return this;
    }

    /**
     * The name used to calculate the key within the
     * {@link org.springframework.batch.item.ExecutionContext}. Required if
     * {@link #saveState(boolean)} is set to true.
     *
     * @param name name of the reader instance
     * @return The current instance of the builder.
     * @see org.springframework.batch.item.ItemStreamSupport#setName(String)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> name(String name) {
        this.name = name;

        return this;
    }

    /**
     * Configure the max number of items to be read.
     *
     * @param maxItemCount the max items to be read
     * @return The current instance of the builder.
     * @see org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader#setMaxItemCount(int)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> maxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;

        return this;
    }

    /**
     * Index for the current item. Used on restarts to indicate where to start from.
     *
     * @param currentItemCount current index
     * @return this instance for method chaining
     * @see org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader#setCurrentItemCount(int)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> currentItemCount(int currentItemCount) {
        this.currentItemCount = currentItemCount;

        return this;
    }

    /**
     * The {@link DataSource} to query against.  Required.
     *
     * @param dataSource the {@link DataSource}
     * @return this instance for method chaining
     * @see JdbcPagingItemReader#setDataSource(DataSource)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> dataSource(DataSource dataSource) {
        this.dataSource = dataSource;

        return this;
    }

    /**
     * A hint to the underlying RDBMS as to how many records to return with each fetch.
     *
     * @param fetchSize number of records
     * @return this instance for method chaining
     * @see JdbcPagingItemReader#setFetchSize(int)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> fetchSize(int fetchSize) {
        this.fetchSize = fetchSize;

        return this;
    }

    /**
     * The {@link RowMapper} used to map the query results to objects.  Required.
     *
     * @param rowMapper a {@link RowMapper} implementation
     * @return this instance for method chaining
     * @see JdbcPagingItemReader#setRowMapper(RowMapper)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> rowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;

        return this;
    }

    /**
     * A {@link Map} of values to set on the SQL's prepared statement.
     *
     * @param parameterValues Map of values
     * @return this instance for method chaining
     * @see JdbcPagingItemReader#setParameterValues(Map)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> parameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;

        return this;
    }

    /**
     * The number of records to request per page/query.  Defaults to 10.  Must be greater
     * than zero.
     *
     * @param pageSize number of items
     * @return this instance for method chaining
     * @see JdbcPagingItemReader#setPageSize(int)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> pageSize(int pageSize) {
        this.pageSize = pageSize;

        return this;
    }

    /**
     * The SQL <code>GROUP BY</code> clause for a db specific @{@link PagingQueryProvider}.
     * This is only used if a PagingQueryProvider is not provided.
     *
     * @param groupClause the SQL clause
     * @return this instance for method chaining
     * @see AbstractSqlPagingQueryProvider#setGroupClause(String)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> groupClause(String groupClause) {
        this.groupClause = groupClause;

        return this;
    }

    /**
     * The SQL <code>SELECT</code> clause for a db specific {@link PagingQueryProvider}.
     * This is only used if a PagingQueryProvider is not provided.
     *
     * @param selectClause the SQL clause
     * @return this instance for method chaining
     * @see AbstractSqlPagingQueryProvider#setSelectClause(String)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> selectClause(String selectClause) {
        this.selectClause = selectClause;

        return this;
    }

    /**
     * The SQL <code>FROM</code> clause for a db specific {@link PagingQueryProvider}.
     * This is only used if a PagingQueryProvider is not provided.
     *
     * @param fromClause the SQL clause
     * @return this instance for method chaining
     * @see AbstractSqlPagingQueryProvider#setFromClause(String)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> fromClause(String fromClause) {
        this.fromClause = fromClause;

        return this;
    }

    /**
     * The SQL <code>WHERE</code> clause for a db specific {@link PagingQueryProvider}.
     * This is only used if a PagingQueryProvider is not provided.
     *
     * @param whereClause the SQL clause
     * @return this instance for method chaining
     * @see AbstractSqlPagingQueryProvider#setWhereClause(String)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> whereClause(String whereClause) {
        this.whereClause = whereClause;

        return this;
    }

    /**
     * The keys to sort by.  These keys <em>must</em> create a unique key.
     *
     * @param sortKeys keys to sort by and the direction for each.
     * @return this instance for method chaining
     * @see AbstractSqlPagingQueryProvider#setSortKeys(Map)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> sortKeys(Map<String, Order> sortKeys) {
        this.sortKeys = sortKeys;

        return this;
    }

    /**
     * A {@link PagingQueryProvider} to provide the queries required.  If provided, the
     * SQL fragments configured via {@link #selectClause(String)},
     * {@link #fromClause(String)}, {@link #whereClause(String)}, {@link #groupClause},
     * and {@link #sortKeys(Map)} are ignored.
     *
     * @param provider the db specific query provider
     * @return this instance for method chaining
     * @see JdbcPagingItemReader#setQueryProvider(PagingQueryProvider)
     */
    public JdbcNonLimitingPagingItemReaderBuilder<T> queryProvider(PagingQueryProvider provider) {
        this.queryProvider = provider;

        return this;
    }

    /**
     * Provides a completely built instance of the {@link JdbcPagingItemReader}
     *
     * @return a {@link JdbcPagingItemReader}
     */
    public JdbcPagingItemReader<T> build() {
        Assert.isTrue(this.pageSize > 0, "pageSize must be greater than zero");
        Assert.notNull(this.dataSource, "dataSource is required");

        if (this.saveState) {
            Assert.hasText(this.name,
                    "A name is required when saveState is set to true");
        }

        JdbcPagingItemReader<T> reader = new JdbcPagingItemReader<>();

        reader.setMaxItemCount(this.maxItemCount);
        reader.setCurrentItemCount(this.currentItemCount);
        reader.setName(this.name);
        reader.setSaveState(this.saveState);
        reader.setDataSource(this.dataSource);
        reader.setFetchSize(this.fetchSize);
        reader.setParameterValues(this.parameterValues);

        if (this.queryProvider == null) {
            Assert.hasLength(this.selectClause, "selectClause is required when not providing a PagingQueryProvider");
            Assert.hasLength(this.fromClause, "fromClause is required when not providing a PagingQueryProvider");
            Assert.notEmpty(this.sortKeys, "sortKeys are required when not providing a PagingQueryProvider");

            reader.setQueryProvider(determineQueryProvider(this.dataSource));
        } else {
            reader.setQueryProvider(this.queryProvider);
        }

        reader.setRowMapper(this.rowMapper);
        reader.setPageSize(this.pageSize);

        return reader;
    }

    private PagingQueryProvider determineQueryProvider(DataSource dataSource) {

        try {
            DatabaseType databaseType = DatabaseType.fromMetaData(dataSource);

            AbstractSqlPagingQueryProvider provider;

            switch (databaseType) {

                case DERBY:
                    provider = new DerbyPagingQueryProvider();
                    break;
                case DB2:
                case DB2VSE:
                case DB2ZOS:
                case DB2AS400:
                    provider = new Db2PagingQueryProvider();
                    break;
                case H2:
                    provider = new H2PagingQueryProvider();
                    break;
                case HSQL:
                    provider = new HsqlPagingQueryProvider();
                    break;
                case SQLSERVER:
                    provider = new SqlServerPagingQueryProvider();
                    break;
                case MYSQL:
                    provider = new MySqlPagingQueryProvider();
                    break;
                case ORACLE:
                    provider = new OraclePagingQueryProvider();
                    break;
                case POSTGRES:
                    provider = new PostgresPagingQueryProvider();
                    break;
                case SYBASE:
                    provider = new SybasePagingQueryProvider();
                    break;
                case SQLITE:
                    provider = new SqlitePagingQueryProvider();
                    break;
                default:
                    throw new IllegalArgumentException("Unable to determine PagingQueryProvider type " +
                            "from database type: " + databaseType);
            }

            provider.setSelectClause(this.selectClause);
            provider.setFromClause(this.fromClause);
            provider.setWhereClause(this.whereClause);
            provider.setGroupClause(this.groupClause);
            provider.setSortKeys(this.sortKeys);

            return provider;
        } catch (MetaDataAccessException e) {
            throw new IllegalArgumentException("Unable to determine PagingQueryProvider type", e);
        }


    }
}
