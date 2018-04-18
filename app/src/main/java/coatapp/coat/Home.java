package coatapp.coat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class Home extends AppCompatActivity implements ForecastRequestProcessListener, LocationRequestProcessListener {

    private LocationHandler locationHandler;
    protected int numHoursInFutureToCheck = 2;
    private String[] loadingPhrases = {
            "Analysis cloud fluffiness...",
            "Checking cloud density...",
            "Conversing with weather gods...",
            "Analysing coat likelihood statistics...",
            "Determining likelihood of acid rain...",
            "Googling synonyms for 'rain'...",
            "Asking Jeeves what he reckons...",
            "Sticking finger in the air...",
            "Testing current puddle depth...",
            "Testing apparent puddle temperature...",
            "Checking green-screen weather map...",
            "Turning on the Weather Channel..."};
    protected int maxNumberOfRequestsToAllow = 5;
    private int numberOfRequestsExecuted = 0;
    private String[] buttonPhrases = {
            "Well?",
            "What about now?",
            "Need coat update!",
            "A Coat... Should I wear one?",
            "One more check...",
            "OK, that's enough."
    };
    protected double coldestNonCoatTemperatureFahrenheit = 60.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setResultTextVisible(false);
        setCostCheckButtonEnabled(true);

        this.locationHandler = new LocationHandler(this);
        locationHandler.setLocationRequestProcessListener(this);

        Button requestButton = (Button) findViewById(R.id.coatCheckButton);
        requestButton.setText(buttonPhrases[0]);

        TextView darkSkyCredit = (TextView) findViewById(R.id.darkSkyCreditText);
        String darkSkyLinkText = "Powered by <a href='https://darksky.net/poweredby/'>Dark Sky</a>.";
        darkSkyCredit.setText(Html.fromHtml(darkSkyLinkText));
        darkSkyCredit.setMovementMethod(LinkMovementMethod.getInstance());

        TextView iconCredit = (TextView) findViewById(R.id.iconCreditText);
        String iconLinkText = "Icon made by <a href='https://www.freepik.com/'>Freepik</a> from <a href='https://www.flaticon.com/'>Flaticon</a>.";
        iconCredit.setText(Html.fromHtml(iconLinkText));
        iconCredit.setMovementMethod(LinkMovementMethod.getInstance());

        TextView privacyPolicy = (TextView) findViewById(R.id.privacyPolicyText);
        String privacyPolicyText = "<a href='https://xlpnic.github.io/ShouldIWearACoat/PrivacyPolicy.html'>Privacy Policy</a>.";
        privacyPolicy.setText(Html.fromHtml(privacyPolicyText));
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loadingIndicator);
        loadingSpinner.setVisibility(View.INVISIBLE);

        TextView loadingPhrase = (TextView) findViewById(R.id.loadingPhrase);
        loadingPhrase.setVisibility(View.INVISIBLE);
    }

    public void setCostCheckButtonEnabled(boolean enabled){
        Button coatCheckButton = (Button) findViewById(R.id.coatCheckButton);
        coatCheckButton.setEnabled(enabled);
    }

    public String getRandomLoadingPhrase() {
        int randomIndex = new Random().nextInt(loadingPhrases.length);
        return loadingPhrases[randomIndex];
    }

    public void coatCheck(View view) {
        setCostCheckButtonEnabled(false);
        setResultTextVisible(false);

        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loadingIndicator);
        loadingSpinner.setVisibility(View.VISIBLE);

        TextView loadingPhrase = (TextView) findViewById(R.id.loadingPhrase);
        loadingPhrase.setVisibility(View.VISIBLE);
        String loadingPhraseToSet = getRandomLoadingPhrase();
        loadingPhraseToSet += " Please wait.";
        loadingPhrase.setText(loadingPhraseToSet);

        locationHandler.determineLocation();
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

    private void setCoatResult(boolean wearCoat) {

        if(wearCoat){
            TextView textResult = (TextView) findViewById(R.id.textResult);
            textResult.setText(R.string.affirmativeResult);
            textResult.setTextSize(64);
        }
        else{
            TextView textResult = (TextView) findViewById(R.id.textResult);
            textResult.setText(R.string.negativeResult);
            textResult.setTextSize(36);
        }

        setResultTextVisible(true);

        ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.loadingIndicator);
        loadingSpinner.setVisibility(View.INVISIBLE);

        TextView loadingPhrase = (TextView) findViewById(R.id.loadingPhrase);
        loadingPhrase.setVisibility(View.INVISIBLE);

        changeButtonText();

        if(numberOfRequestsExecuted >= maxNumberOfRequestsToAllow){
            setCostCheckButtonEnabled(false);
        }
        else{
            setCostCheckButtonEnabled(true);
        }

    }

    private void changeButtonText(){

        numberOfRequestsExecuted++;

        if(numberOfRequestsExecuted > maxNumberOfRequestsToAllow){
            numberOfRequestsExecuted = maxNumberOfRequestsToAllow;
        }

        Button requestButton = (Button) findViewById(R.id.coatCheckButton);
        requestButton.setText(buttonPhrases[numberOfRequestsExecuted]);
    }

    private void getForecast(Location currentLocation) {

        String secretKey = getForecastSecretKey();
        String requestEndpoint = "https://api.darksky.net/forecast/";
        String requestExclusions = "exclude=minutely,daily,alerts,flags";
        String httpRequest = requestEndpoint + secretKey + "/" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "?" + requestExclusions;

        try {

            ForecastRequestTask forecastRequestTask = new ForecastRequestTask();
            forecastRequestTask.setForecastProcesslistener(this);

            forecastRequestTask.execute(httpRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean ShouldWearACoat(JSONObject weatherForecast) {

        WeatherConverter.WeatherType currentWeatherType = JsonHelper.GetCurrentWeatherType(weatherForecast);
        double currentTemperature = JsonHelper.GetCurrentTemperature(weatherForecast);

        boolean currentlyCoatWeather = IsItCoatWeather(currentWeatherType, currentTemperature);

        if (currentlyCoatWeather){
            return true;
        }

        HourWeather[] hourlyWeatherBreakdown = JsonHelper.GetWeatherByHour(weatherForecast, numHoursInFutureToCheck);

        boolean wearACoat = false;

        for (HourWeather hourWeather : hourlyWeatherBreakdown) {
            if (IsItCoatWeather(hourWeather.weatherType, hourWeather.temperature)){
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

        return temperature < coldestNonCoatTemperatureFahrenheit;
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

    private void parseResponse(String response){

        JSONObject forecast = null;

        try {
            forecast = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        boolean coatWeather = ShouldWearACoat(forecast);

        setCoatResult(coatWeather);
    }

    @Override
    public void ForecastProcessingDone(String result) {
        parseResponse(result);
    }

    @Override
    public void LocationProcessingDone(Location currentLocation) {
        getForecast(currentLocation);
    }

    @Override
    public void onRequestPermissionsResult (int requestCode,
                                            @NonNull String[] permissions,
                                            @NonNull int[] grantResults){
        if (requestCode == LocationHandler.ACCESS_LOCATION_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        locationHandler.determineLocation();
                    }
                }
                else if (permission.equals(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        locationHandler.determineLocation();
                    }
                }
            }
        }
    }
}

