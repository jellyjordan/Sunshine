package com.justjellystudios.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.justjellystudios.android.sunshine.data.WeatherContract;


/**
 * Main entry activity for Sunshine app. UI is contained in the
 * ForecastFragment class and xml files.
 */
public class MainActivity extends ActionBarActivity implements Callback{
    public static boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static final String FORECASTFRAGMENT_TAG = "Forecast Fragment";
    public static final String EXTRA_STRING = "com.justjellystudios.sunshine";
    private static final String LOGTAG_MAIN = "Sunshine MainActivity";

    private static String currentLocation;

    @Override
    protected void onResume() {
        super.onResume();

        String location = Utility.getPreferredLocation(this);

        // Location changed in Setting Activity
        if(location != null && !location.equals(currentLocation)){

            // Update Forecast Fragment.
            ForecastFragment forecastFragment = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if(forecastFragment != null){
                forecastFragment.onLocationChange();
            }

            // Update Detail Fragment
            DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(detailFragment != null){
                detailFragment.onLocationChange(location);
            }
            currentLocation = Utility.getPreferredLocation(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.justjellystudios.android.sunshine.R.layout.activity_main);

        if(findViewById(R.id.weather_detail_container) != null){
            mTwoPane = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.weather_detail_container,
                        new DetailFragment(),
                        DETAILFRAGMENT_TAG
                ).commit();
            }
        }
        else {
            mTwoPane = false;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentLocation = preferences.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default)
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.justjellystudios.android.sunshine.R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case(com.justjellystudios.android.sunshine.R.id.action_settings):
                Intent settingsIntent = new Intent(this , SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case(com.justjellystudios.android.sunshine.R.id.action_location):
                // Get's user location preference
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String locationData = sharedPreferences.getString(getString(com.justjellystudios.android.sunshine.R.string.pref_location_key) , getString(com.justjellystudios.android.sunshine.R.string.pref_location_default));
                Uri geolocation = Uri.parse("geo:0,0?q=" + locationData);

                Intent locationIntent = new Intent(Intent.ACTION_VIEW);
                locationIntent.setData(geolocation);
                if(locationIntent.resolveActivity(getPackageManager()) != null){
                    startActivity(locationIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.URI_ARG, uri);

        if(mTwoPane){
            DetailFragment df = new DetailFragment();
            df.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(
                    R.id.weather_detail_container,
                    df,
                    DETAILFRAGMENT_TAG
            ).commit();
        }
        else{
            Intent intent = new Intent(this, DetailActivity.class).setData(uri);
            startActivity(intent);
        }
    }
}
