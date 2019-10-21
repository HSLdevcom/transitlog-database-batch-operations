package fi.hsl.domain;

import javax.persistence.Entity;

@Entity
public class UnsignedEvent extends Event {
    public UnsignedEvent(Vehicle item) {
        super(item);
    }

    public UnsignedEvent() {
    }
}
