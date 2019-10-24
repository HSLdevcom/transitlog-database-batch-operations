package fi.hsl.features.splitdatabasetables;

import fi.hsl.configuration.databases.Database;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
class DatabaseSplitRequest {
    @NotNull
    @Getter
    @Setter
    private Database.DatabaseInstance from;

    @NotNull
    @Getter
    @Setter
    private Database.DatabaseInstance to;

}
