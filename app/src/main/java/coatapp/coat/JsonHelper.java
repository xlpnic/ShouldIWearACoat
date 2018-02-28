package coatapp.coat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

final class JsonHelper {

    static WeatherConverter.WeatherType GetCurrentWeatherType(JSONObject weatherForecast){

        WeatherConverter.WeatherType weatherType;

        try{
            JSONObject currently = weatherForecast.getJSONObject("currently");
            String currentWeatherIcon = currently.getString("icon");

            weatherType = WeatherConverter.GetWeatherType(currentWeatherIcon);
        }
        catch(JSONException e){
            e.printStackTrace();
            weatherType = WeatherConverter.WeatherType.otherWeather;
        }

        return weatherType;
    }

    static HourWeather[] GetWeatherByHour(JSONObject weatherForecast, int numHoursInFutureToCheck){

        HourWeather[] hourlyBreakdown = new HourWeather[numHoursInFutureToCheck];

        try{
            JSONObject hourlyWeatherBreakdown = weatherForecast.getJSONObject("hourly");
            JSONArray hourlyWeatherData = hourlyWeatherBreakdown.getJSONArray("data");

            for(int i =0; i<numHoursInFutureToCheck; i++){
                JSONObject oneHourWeatherData = hourlyWeatherData.getJSONObject(i);
                String hourWeatherIcon = oneHourWeatherData.getString("icon");
                double hourTemperature = oneHourWeatherData.getDouble("apparentTemperature");

                WeatherConverter.WeatherType hourWeatherType = WeatherConverter.GetWeatherType(hourWeatherIcon);

                HourWeather hourWeather = new HourWeather();
                hourWeather.weatherType = hourWeatherType;
                hourWeather.temperature = hourTemperature;

                hourlyBreakdown[i] = hourWeather;
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        return hourlyBreakdown;
    }

    static double GetCurrentTemperature(JSONObject weatherForecast) {

        double temperature = 0.0;

        try{
            JSONObject currently = weatherForecast.getJSONObject("currently");
            temperature = currently.getDouble("apparentTemperature");
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        return temperature;
    }
}