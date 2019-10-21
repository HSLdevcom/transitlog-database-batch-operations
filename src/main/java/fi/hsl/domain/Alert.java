package fi.hsl.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class Alert {
    @Id
    private Long id;
    private Date created_at;
}

