package coatapp.coat;

final class WeatherConverter{

    public enum WeatherType{
        clearDay,
        clearNight,
        rain,
        snow,
        sleet,
        wind,
        fog,
        cloudy,
        partlyCloudyDay,
        partlyCloudyNight,
        hail,
        thunderstorm,
        tornado,
        otherWeather
    }

    static WeatherType GetWeatherType(String weatherName){
        if(weatherName.equalsIgnoreCase("clear-day")){
            return WeatherType.clearDay;
        }

        if(weatherName.equalsIgnoreCase("clear-night")){
            return WeatherType.clearNight;
        }

        if(weatherName.equalsIgnoreCase("rain")){
            return WeatherType.rain;
        }
        if(weatherName.equalsIgnoreCase("snow")){
            return WeatherType.snow;
        }

        if(weatherName.equalsIgnoreCase("sleet")){
            return WeatherType.sleet;
        }
        if(weatherName.equalsIgnoreCase("wind")){
            return WeatherType.wind;
        }

        if(weatherName.equalsIgnoreCase("fog")){
            return WeatherType.fog;
        }

        if(weatherName.equalsIgnoreCase("cloudy")){
            return WeatherType.cloudy;
        }

        if(weatherName.equalsIgnoreCase("partly-cloudy-day")){
            return WeatherType.partlyCloudyDay;
        }

        if(weatherName.equalsIgnoreCase("partly-cloudy-night")){
            return WeatherType.partlyCloudyNight;
        }

        if(weatherName.equalsIgnoreCase("hail")){
            return WeatherType.hail;
        }

        if(weatherName.equalsIgnoreCase("thunderstorm")){
            return WeatherType.thunderstorm;
        }

        if(weatherName.equalsIgnoreCase("tornado")){
            return WeatherType.tornado;
        }

        return WeatherType.otherWeather;
    }
}
