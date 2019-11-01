package fi.hsl.common.batch.itemwriters;

import javax.persistence.EntityManagerFactory;

public class PersistingItemWriterFactory {
    public static PersistingItemWriter createItemWriter(EntityManagerFactory entityManagerfactory) {
        PersistingItemWriter persistingItemWriter = new PersistingItemWriter();
        persistingItemWriter.setEntityManagerFactory(entityManagerfactory);
        return persistingItemWriter;
    }
}
