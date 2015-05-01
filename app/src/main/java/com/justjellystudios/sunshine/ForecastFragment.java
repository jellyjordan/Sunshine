package com.justjellystudios.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.justjellystudios.sunshine.cloud.WeatherDataParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Fragment used by the MainActivity to display weather list information.
 */
public class ForecastFragment extends Fragment {
    private static final String LOGTAG_FORECAST_FRAG = "Sunshine Forecast";
    private ArrayAdapter<String> forecastAdapter;


    /**
     * Sets signal to inflate additional menu options
     * @param bundle
     */
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> dummyForecast = new ArrayList<>();
        dummyForecast.add("Today - Sunny 54/32");
        dummyForecast.add("Tomorrow - Rain 34/32");
        dummyForecast.add("Wednesday - Snow 24/12");

        forecastAdapter = new ArrayAdapter<String>(
                getActivity() ,
                R.layout.list_item_forecast ,
                R.id.list_item_forecast_textview ,
                dummyForecast);

        ListView forecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);

        // Starts a new activity which expands the weather details of the listview's child.
        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView forecastTextView = (TextView) view.findViewById(R.id.list_item_forecast_textview);

                Intent getDetailIntent = new Intent(getActivity() , DetailActivity.class);
                getDetailIntent.putExtra(MainActivity.EXTRA_STRING, forecastTextView.getText());
                startActivity(getDetailIntent);
            }
        });
        forecastListView.setAdapter(forecastAdapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecast_menu, menu);
    }

    /**
     * Handles the menu actions for the items inflated by the fragment.
     *
     * @param menuItem
     * Item selected
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.action_refresh:
                new FetchWeatherTask().execute("02809");
                return true;
            default:
                return false;
        }
    }

    /**
     * Updates the forecast ListView with weather data.
     *
     * @param weatherList
     * The list of forecast data
     */
    public void updateWeather(String[] weatherList){
        forecastAdapter.clear();
        forecastAdapter.addAll(weatherList);
    }

    /**
     * Background task responsible for network connection to openweathermap which
     * fetches weather data used to update the fragments listview.
     */
    public class FetchWeatherTask extends AsyncTask<String , Void , String[]>{

        final int FORECAST_COUNT = 7;
        /**
         * Connects to openweather and fetches the forecast
         *
         * @param params
         * Query parameters for weather data
         *
         * @return
         * Returns the array of strings containing weather data for each day.
         */
        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q" , params[0])
                        .appendQueryParameter("mode" , "json")
                        .appendQueryParameter("units" , "metric")
                        .appendQueryParameter("cnt" , Integer.toString(FORECAST_COUNT));

                Log.d(LOGTAG_FORECAST_FRAG , builder.build().toString());
                URL url = new URL(builder.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOGTAG_FORECAST_FRAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            // Attempts to parse the weather data. Returns an array of strings on success.
            String[] weatherData;
            try {
                weatherData = WeatherDataParser.getWeatherData(forecastJsonStr, FORECAST_COUNT);
                return weatherData;
            }
            catch (JSONException ex){
                ex.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            updateWeather(strings);
        }
    }
}
