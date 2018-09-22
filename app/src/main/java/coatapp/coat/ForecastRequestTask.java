package coatapp.coat;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForecastRequestTask extends AsyncTask<String, Void, String> {

    private ForecastRequestProcessListener forecastRequestProcessListener;

    public void setForecastProcessListener(ForecastRequestProcessListener forecastProcessListener) {
        this.forecastRequestProcessListener = forecastProcessListener;
    }

    private static String getForecastRequest(String urlToRead) throws Exception {

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


    @Override
    protected String doInBackground(String... params) {

        String forecastRequestResponse = "";

        try {
            forecastRequestResponse = getForecastRequest(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return forecastRequestResponse;
    }

    @Override
    protected void onPostExecute(String message) {
        forecastRequestProcessListener.ForecastProcessingDone(message);
    }
}
