package fi.hsl.common.batch;

public class JdbcLongCountingCursorItemReader<T> extends JdbcCursorItemReader<T> {

    public JdbcLongCountingCursorItemReader() {
        super();
    }

    @Override
    public synchronized T read() throws Exception {
        return super.read();
    }
}
