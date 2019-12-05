package fi.hsl.common.batch.itemwriters;

import fi.hsl.domain.Event;
import org.springframework.batch.item.database.JpaItemWriter;

public class RemovingEntityWriter extends JpaItemWriter<Event> {
}
