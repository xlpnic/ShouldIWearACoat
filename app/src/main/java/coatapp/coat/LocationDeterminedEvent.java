package coatapp.coat;

import android.location.Location;

import java.util.EventObject;

public class LocationDeterminedEvent extends EventObject {

    public final Location currentLocation;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public LocationDeterminedEvent(Object source, Location location) {
        super(source);
        currentLocation = location;
    }
}
