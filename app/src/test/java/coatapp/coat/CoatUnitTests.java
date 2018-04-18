package coatapp.coat;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CoatUnitTests {

    @Test
    public void coatCheck() throws JSONException {
        Home h = new Home();
        JSONObject ForecastHotClearDay = new JSONObject(TestData.ForecastHotClearDay);
        boolean ForecastHotClearDayResult = h.ShouldWearACoat(ForecastHotClearDay);
        assertEquals(false, ForecastHotClearDayResult);

        JSONObject ForecastHotRain = new JSONObject(TestData.ForecastHotRain);
        boolean ForecastHotRainResult = h.ShouldWearACoat(ForecastHotRain);
        assertEquals(true, ForecastHotRainResult);

        JSONObject ForecastColdRain = new JSONObject(TestData.ForecastColdRain);
        boolean ForecastColdRainResult = h.ShouldWearACoat(ForecastColdRain);
        assertEquals(true, ForecastColdRainResult);

        JSONObject ForecastColdClearDay = new JSONObject(TestData.ForecastColdClearDay);
        boolean ForecastColdClearDayResult = h.ShouldWearACoat(ForecastColdClearDay);
        assertEquals(true, ForecastColdClearDayResult);
    }
}