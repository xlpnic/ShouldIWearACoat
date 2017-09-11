package coatapp.coat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EventListener;

public class Home extends AppCompatActivity implements EventListener {

    private String requestEndpoint = "https://api.darksky.net/forecast/";

    public void LocationFound(Location currentLocation){
        int forecast = getForecast(currentLocation);
        setCoatResult(forecast, currentLocation);
    }

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void coatCheck(View view) {
        determineLocation();
    }

    private void setCoatResult(int forecast, Location currentLocation) {
        TextView textResult = (TextView) findViewById(R.id.textResult);
        textResult.setText("No idea yet, mate!");

        TextView textLocation = (TextView) findViewById(R.id.textLocation);
        textLocation.setText(currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
    }

    public static String getForecastRequest(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    private int getForecast(Location currentLocation) {

        String secretKey = getForecastSecretKey();
        String httpRequest = requestEndpoint + secretKey + "/" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();

        int result = 0;

        try {
            GetForecastJob job = new GetForecastJob();
            AsyncTask<String, Void, String> getForecastTask = job.execute(httpRequest);
            String getForecastResponse = getForecastTask.get();
            //TODO: Deserialise JSON object here.
            result = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String getForecastSecretKey(){

        String secretKey = "";

        try{
            secretKey = (String) getResources().getText(R.string.darkSkySecretKey);

        }catch (Exception e){
            e.printStackTrace();
        }

        return secretKey;
    }

    private void determineLocation(){
        int fineLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED && coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return null;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location locationResult) {
                        // Got last known location. In some rare situations this can be null.
                        if (locationResult != null) {
                            LocationFound(locationResult);
                        }
                    }
                });
    }
}
