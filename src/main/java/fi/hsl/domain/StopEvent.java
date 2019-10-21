package fi.hsl.domain;


import javax.persistence.Entity;

@Entity
public class StopEvent extends Event {
    public StopEvent(Vehicle item) {
        super(item);
    }

    public StopEvent() {

    }
}
