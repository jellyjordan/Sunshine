package com.justjellystudios.android.sunshine;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity for expanding weather details from the MainActivity
 */
public class DetailActivity extends ActionBarActivity {
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.justjellystudios.android.sunshine.R.layout.activity_detail);
        if (savedInstanceState == null) {

            // Converts the intent to argument for the fragment
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.URI_ARG , getIntent().getData());

            DetailFragment df = new DetailFragment();
            df.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(com.justjellystudios.android.sunshine.R.id.weather_detail_container, df)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.justjellystudios.android.sunshine.R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch( item.getItemId()) {
            case com.justjellystudios.android.sunshine.R.id.action_settings:
                Intent settingsIntent = new Intent(this , SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
