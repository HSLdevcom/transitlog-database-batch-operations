package fi.hsl.features.splitdatabasetables;


import org.springframework.batch.item.database.JpaItemWriter;

import javax.persistence.EntityManager;
import java.util.List;

public class PersistingJpaItemWriter<T> extends JpaItemWriter<T> {
    @Override
    protected void doWrite(EntityManager entityManager, List<? extends T> items) {

        if (logger.isDebugEnabled()) {
            logger.debug("Writing to JPA with " + items.size() + " items.");
        }

        if (!items.isEmpty()) {
            long persistCount = 0;
            for (T item : items) {
                //This depends heavily on the events equals-method being properly defined on the entity primary key!
                if (!entityManager.contains(item)) {
                    //Persist INSTEAD of merge to avoid entitymanager SELECT-checks. Propagates duplication checking to database because the entities we have have primary keys
                    entityManager.persist(item);
                    persistCount++;
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(persistCount + " entities persisted.");
                logger.debug((items.size() - persistCount) + " entities found in persistence context.");
            }
        }


    }
}
