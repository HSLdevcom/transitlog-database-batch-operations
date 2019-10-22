package fi.hsl.features.synchronizedatabases;

import fi.hsl.features.Database;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
class DatabaseSynchronizationRequest {
    @Getter
    @Setter
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;
    @Getter
    @Setter
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @NotNull
    @Getter
    @Setter
    private Database.DatabaseInstance from;

    @NotNull
    @Getter
    @Setter
    private Database.DatabaseInstance to;
}
