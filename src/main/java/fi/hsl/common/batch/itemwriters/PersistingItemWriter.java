package fi.hsl.common.batch.itemwriters;

import fi.hsl.domain.Event;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.database.JpaItemWriter;

import javax.persistence.EntityManager;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class PersistingItemWriter extends JpaItemWriter<Event> {
    @Override
    protected void doWrite(EntityManager entityManager, List<? extends Event> items) {

        if (logger.isDebugEnabled()) {
            logger.debug("Writing to JPA with " + items.size() + " items.");
        }

        if (!items.isEmpty()) {
            long persistCount = 0;
            for (Event item : items) {
                if (!entityManager.contains(item)) {
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

