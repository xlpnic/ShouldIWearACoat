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

    static WeatherConverter.WeatherType[] GetWeatherTypeByHour(JSONObject weatherForecast, int numHoursInFutureToCheck){

        WeatherConverter.WeatherType[] hourlyBreakdown = new WeatherConverter.WeatherType[numHoursInFutureToCheck];

        try{
            JSONObject hourlyWeatherBreakdown = weatherForecast.getJSONObject("hourly");
            JSONArray hourlyWeatherData = hourlyWeatherBreakdown.getJSONArray("data");

            for(int i =0; i<numHoursInFutureToCheck; i++){
                JSONObject oneHourWeatherData = hourlyWeatherData.getJSONObject(i);
                String hourWeatherIcon = oneHourWeatherData.getString("icon");

                WeatherConverter.WeatherType hourWeatherType = WeatherConverter.GetWeatherType(hourWeatherIcon);

                hourlyBreakdown[i] = hourWeatherType;
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        return hourlyBreakdown;
    }
}
