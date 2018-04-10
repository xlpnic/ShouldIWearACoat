Coat?
=====

An Android app that gets the weather forecast for the near future and tells you if you should wear a coat or not.

Location
----------------

The user's location is provided by [Android OS Location Services](https://developer.android.com/guide/topics/location/index.html).

Weather Forecast
----------------

The forecast is provided by [DarkSky API](https://darksky.net/dev/docs).

Coat Calculations
-----------------

The app will advise the user to wear a coat if the current weather, or the weather conditions over the next 2 hours, meet at least one of the following criteria:
 
* The forecast summary is one of the following:

  * rain
  * hail
  * sleet
  * snow
  * thunderstorm
  * tornado
  * wind
  
* The temperature is below 60 degrees Fahrenheit (~15 degrees Celsius)

The app will also advise to wear a coat if the forecast summary is unrecognised - better safe than sorry!
