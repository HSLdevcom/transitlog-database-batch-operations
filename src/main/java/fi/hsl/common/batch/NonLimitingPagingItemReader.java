package fi.hsl.common.batch;

import org.springframework.batch.item.ItemCountAware;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JdbcPagingItemReader;

public class NonLimitingPagingItemReader<T> extends JdbcPagingItemReader<T> {
    private int currentItemCount;

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException {
        //DON'T check for currentItemCount because it breaks with our massive dataset
        currentItemCount++;
        T item = doRead();
        if (item instanceof ItemCountAware) {
            ((ItemCountAware) item).setItemCount(currentItemCount);
        }
        return item;
    }
}
