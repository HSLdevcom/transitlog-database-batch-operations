package fi.hsl.domain;


import javax.persistence.Entity;

@Entity
public class LightPriorityEvent extends Event {
    public LightPriorityEvent(Vehicle item) {
        super(item);
    }

    public LightPriorityEvent() {
    }
}
