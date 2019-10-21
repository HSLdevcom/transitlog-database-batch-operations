package fi.hsl.domain;

import javax.persistence.Entity;

@Entity
public class OtherEvent extends Event {
    public OtherEvent(Vehicle item) {
        super(item);
    }

    public OtherEvent() {

    }
}
