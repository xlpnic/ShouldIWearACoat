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
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.EventListener;

public class Home extends AppCompatActivity implements EventListener {

    public void LocationFound(Location currentLocation){
        JSONObject forecast = getForecast(currentLocation);

        setCostCheckButtonEnabled(false);

        boolean coatWeather = ShouldWearACoat(forecast);

        setCoatResult(coatWeather, currentLocation);
    }

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setResultTextVisible(false);
        setCostCheckButtonEnabled(true);
    }

    public void setCostCheckButtonEnabled(boolean enabled){
        Button coatCheckButton = (Button) findViewById(R.id.coatCheckButton);
        coatCheckButton.setEnabled(enabled);
    }

    public void coatCheck(View view) {
        determineLocation();
    }

    public void setResultTextVisible(boolean visible){
        TextView textResult = (TextView) findViewById(R.id.textResult);

        if(visible){
            textResult.setVisibility(View.VISIBLE);
        }
        else{
            textResult.setVisibility(View.INVISIBLE);
        }
    }

    private void setCoatResult(boolean wearCoat, Location currentLocation) {

        if(wearCoat){
            TextView textResult = (TextView) findViewById(R.id.textResult);
            textResult.setText(R.string.affirmativeResult);
        }
        else{
            TextView textResult = (TextView) findViewById(R.id.textResult);
            textResult.setText(R.string.negativeResult);
        }

        setResultTextVisible(true);

        TextView textLocation = (TextView) findViewById(R.id.textLocation);
        String locationText = currentLocation.getLatitude() + ", " + currentLocation.getLongitude();
        textLocation.setText(locationText);
        textLocation.setVisibility(View.VISIBLE);
    }

    private JSONObject getForecast(Location currentLocation) {

        String secretKey = getForecastSecretKey();
        String requestEndpoint = "https://api.darksky.net/forecast/";
        String requestExclusions = "exclude=minutely,daily,alerts,flags";
        String httpRequest = requestEndpoint + secretKey + "/" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "?" + requestExclusions;

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

    private boolean ShouldWearACoat(JSONObject weatherForecast) {

        WeatherConverter.WeatherType currentWeatherType = JsonHelper.GetCurrentWeatherType(weatherForecast);

        //TODO: Get temperatures from response

        double currentTemperature = 0.0;

        boolean currentlyCoatWeather = IsItCoatWeather(currentWeatherType, currentTemperature);

        if (currentlyCoatWeather){
            return true;
        }

        int numHoursInFutureToCheck = 5;

        WeatherConverter.WeatherType[] hourlyWeatherBreakdown = JsonHelper.GetWeatherTypeByHour(weatherForecast, numHoursInFutureToCheck);

        boolean wearACoat = false;

        for (WeatherConverter.WeatherType hourWeatherType : hourlyWeatherBreakdown) {
            double hourTemperature = 0.0;
            if (IsItCoatWeather(hourWeatherType, hourTemperature)){
                wearACoat = true;
                break;
            }
        }

        return wearACoat;
    }

    private boolean IsItCoatWeather(WeatherConverter.WeatherType weatherType, double temperature) {

        return IsCoatWeatherType(weatherType) || IsPrettyCold(temperature);

    }

    private boolean IsPrettyCold(double temperature){
        double coldestNonCoatTemperature = 60.0;

        return temperature < coldestNonCoatTemperature;

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