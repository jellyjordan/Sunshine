package com.justjellystudios.android.sunshine;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jelly on 5/15/15.
 */
public class ForecastViewHolder {
    public final TextView minTemp;
    public final TextView maxTemp;
    public final TextView weatherDesc;
    public final TextView forecastDate;
    public final ImageView weatherImage;

    public ForecastViewHolder(View view){
        maxTemp = (TextView)view.findViewById(R.id.list_item_high_textview);
        minTemp = (TextView)view.findViewById(R.id.list_item_low_textview);
        forecastDate = (TextView)view.findViewById(R.id.list_item_date_textview);
        weatherDesc =(TextView)view.findViewById(R.id.list_item_forecast_textview);
        weatherImage = (ImageView)view.findViewById(R.id.list_item_icon);
    }
}
