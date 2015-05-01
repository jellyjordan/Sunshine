package com.justjellystudios.sunshine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;


/**
 * Fragment for displaying weather data representing an expanded view
 * of the forecast item clicked within the MainActivity.
 */
public class DetailFragment extends Fragment {
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    /**
     * Inflates the layout and updates the elements with data packaged with
     * the Intent.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Updates details TextView with Intent extra message from calling Activity.
        TextView detailsTextView = (TextView) rootView.findViewById(R.id.expanded_details);
        detailsTextView.setText(getActivity().getIntent().getStringExtra(MainActivity.EXTRA_STRING));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);
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
            default:
                return false;
        }
    }

}
