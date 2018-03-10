package coatapp.coat;

import android.location.Location;

public interface LocationRequestProcessListener {
    void LocationProcessingDone(Location currentLocation);
}
