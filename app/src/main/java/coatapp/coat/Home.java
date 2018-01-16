package coatapp.coat;

import android.Manifest;
import android.content.pm.PackageManager;
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

import org.json.JSONArray;
import org.json.JSONException;
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

    private boolean isItCoatWeather(JSONObject obj) {

        boolean wearACoat = false;

        try{
            JSONObject currently = obj.getJSONObject("currently");
            String currentWeatherIcon = currently.getString("icon");

            if (currentWeatherIcon.equals("rain")){
                wearACoat = true;
            }
            else{

                JSONObject hourlyWeatherBreakdown = obj.getJSONObject("hourly");
                JSONArray hourlyWeatherData = hourlyWeatherBreakdown.getJSONArray("data");

                int numHoursInFutureToCheck = 5;

                for(int i =0; i<numHoursInFutureToCheck; i++){
                    JSONObject oneHourWeatherData = hourlyWeatherData.getJSONObject(i);
                    String hourWeatherIcon = oneHourWeatherData.getString("icon");

                    if (hourWeatherIcon.equals("rain")){
                        wearACoat = true;
                        break;
                    }
                }
            }
        }
        catch (JSONException e){

        }

        return wearACoat;
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
