package coatapp.coat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.EventListener;

public class Home extends AppCompatActivity implements EventListener {

    private String requestEndpoint = "https://api.darksky.net/forecast/";
    private String requestExclusions = "exclude=minutely,daily,alerts,flags";

    public void LocationFound(Location currentLocation){
        JSONObject forecast = getForecast(currentLocation);

        boolean coatWeather = isItCoatWeather(forecast);

        setCoatResult(coatWeather, currentLocation);
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

    private void setCoatResult(boolean wearCoat, Location currentLocation) {

        if(wearCoat){
            TextView textResult = (TextView) findViewById(R.id.textResult);
            textResult.setText("Yep!");
        }
        else{
            TextView textResult = (TextView) findViewById(R.id.textResult);
            textResult.setText("Nah, you'll be fine!");
        }

        TextView textLocation = (TextView) findViewById(R.id.textLocation);
        textLocation.setText(currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
    }

    private JSONObject getForecast(Location currentLocation) {

        String secretKey = getForecastSecretKey();
        String httpRequest = requestEndpoint + secretKey + "/" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "?" +  requestExclusions;

        JSONObject forecast = null;

        try {
            ForecastRequestTask forecastRequestTask = new ForecastRequestTask();
            AsyncTask<String, Void, String> forecastResponse = forecastRequestTask.execute(httpRequest);
            String getForecastResponse = forecastResponse.get();

            forecast = new JSONObject(getForecastResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return forecast;
    }

    private boolean isItCoatWeather(JSONObject weatherForecast) {

        boolean wearACoat = false;

        WeatherConverter.WeatherType weatherType = JsonHelper.GetCurrentWeatherType(weatherForecast);

        if (IsCoatWeatherType(weatherType)){
            wearACoat = true;
        }
        else{

            int numHoursInFutureToCheck = 5;

            WeatherConverter.WeatherType[] hourlyWeatherBreakdown = JsonHelper.GetWeatherTypeByHour(weatherForecast, numHoursInFutureToCheck);

            for (WeatherConverter.WeatherType hourWeatherType : hourlyWeatherBreakdown) {
                if (IsCoatWeatherType(hourWeatherType)){
                    wearACoat = true;
                    break;
                }
            }
        }

        return wearACoat;
    }

    private boolean IsCoatWeatherType(WeatherConverter.WeatherType weatherType){
        switch (weatherType){
            case rain:
            case hail:
            case sleet:
            case snow:
            case thunderstorm:
            case tornado:
            case wind:
            case otherWeather:
                return true;
            default:
                return false;
        }
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

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

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
        }
    }

    final int ACCESS_LOCATION_REQUEST = 55555;

    private void determineLocation(){
        int fineLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED && coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_LOCATION_REQUEST);
        }
        else{
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
}