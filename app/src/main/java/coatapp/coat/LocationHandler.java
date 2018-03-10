package coatapp.coat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

class LocationHandler extends ActivityCompat {

    static final int ACCESS_LOCATION_REQUEST = 55555;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequestProcessListener locationRequestProcessListener;
    private AppCompatActivity activityContext;

    LocationHandler(AppCompatActivity activityContext) {
        this.activityContext = activityContext;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activityContext);
    }

    void setLocationRequestProcessListener(LocationRequestProcessListener locationRequestProcessListener) {
        this.locationRequestProcessListener = locationRequestProcessListener;
    }

    void determineLocation() {

        int fineLocationPermission = ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED
                && coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

            requestPermission();
        }
        else{
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activityContext, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location locationResult) {
                            // Got last known location. In some rare situations this can be null.
                            if (locationResult != null) {
                                locationRequestProcessListener.LocationProcessingDone(locationResult);
                            }
                        }
                    });
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(activityContext,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                ACCESS_LOCATION_REQUEST);
    }
}

