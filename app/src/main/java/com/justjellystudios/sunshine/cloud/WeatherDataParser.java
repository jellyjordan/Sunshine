package com.justjellystudios.sunshine.cloud;

import android.text.format.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
;import java.text.SimpleDateFormat;

/**
 * Static methods for parsing the JSON string retrieved from
 * the weather api into formats which can be used to update
 * the gui.
 */
public class WeatherDataParser {

    /**
     * Converts the timestamp into a date.
     *
     * @param time
     * Timestamp of the day.
     *
     * @return
     * Date format for gui.
     */
    private static String getReadableDateString(long time){
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }


    /**
     * Formats the high and low daily temperature to the format used for the gui.
     *
     * @param high
     * Daily high temperature
     *
     * @param low
     * Daily low temperature
     *
     * @return
     * A formatted String containing both the high and low temperature.
     */
    private static String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Gets the weather data for each day in the JSON string.
     *
     * @param weatherJsonString
     * Unformatted JSON string retrieved from openweather api
     *
     * @param dayCount
     * The amount of days used for the query.
     *
     * @return
     * Returns a string array containing weather data for each day.
     *
     * @throws JSONException
     */
    public static String[] getWeatherData(String weatherJsonString , int dayCount)
    throws JSONException{

        // Open Weather Map JSON Object labels
        final String OWM_DATA_ARRAY = "list";
        final String OWM_DAY_TEMPS = "temp";
        final String OWM_MAX_TEMP = "max";
        final String OWM_MIN_TEMP = "min";
        final String OWM_WEATHER = "weather";
        final String OWM_CONDITION = "main";


        // The json array containing the weather data we need.
        JSONArray weatherData = new JSONObject(weatherJsonString).getJSONArray("list");

        //Gets UTC time offset
        Time dayTime = new Time();
        dayTime.setToNow();

        int julianStartDay = Time.getJulianDay(System.currentTimeMillis() , dayTime.gmtoff);

        dayTime = new Time();

        String[] dayData = new String[dayCount];
        for(int i = 0; i < dayCount; i++){
            String day;
            String decription;
            String temps;

            // Gets the JSON object for the specific day
            JSONObject dayWeather = weatherData.getJSONObject(i);

            // Gets a formatted date String.
            long dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            // Weather description for the day.
            JSONObject weatherDescription = dayWeather.getJSONArray(OWM_WEATHER).getJSONObject(0);
            decription = weatherDescription.getString(OWM_CONDITION);

            // High and Low temps for the day.
            JSONObject weatherTemperature = dayWeather.getJSONObject(OWM_DAY_TEMPS);
            double high = weatherTemperature.getDouble(OWM_MAX_TEMP);
            double low = weatherTemperature.getDouble(OWM_MIN_TEMP);

            temps = formatHighLows(high , low);
            dayData[i] = day + " - " + decription + " - " + temps;

        }

        return dayData;
    }
}
