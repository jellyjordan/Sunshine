package com.justjellystudios.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.justjellystudios.android.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext, high, isMetric) + "/" + Utility.formatTemperature(mContext, low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int maxTemp = cursor.getInt(ForecastFragment.COL_WEATHER_MAX_TEMP);
        int minTemp = cursor.getInt(ForecastFragment.COL_WEATHER_MIN_TEMP);

        String highAndLow = formatHighLows(maxTemp , minTemp);

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    @Override
    public int getItemViewType(int position){
        return (position == 0 ) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount(){
        return 2;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutID = -1;

        switch (viewType) {
            case VIEW_TYPE_TODAY:
                layoutID = R.layout.list_item_forecast_today;
                break;
            case VIEW_TYPE_FUTURE_DAY:
                layoutID = R.layout.list_item_forecast;
                break;
        }

        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ForecastViewHolder viewHolder = new ForecastViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ForecastViewHolder viewHolder = (ForecastViewHolder) view.getTag();

        viewHolder.maxTemp.setText(
                Utility.formatTemperature(
                    mContext,
                    cursor.getInt(ForecastFragment.COL_WEATHER_MAX_TEMP) ,
                    Utility.isMetric(context)));

        viewHolder.minTemp.setText(
                Utility.formatTemperature(
                    mContext,
                    cursor.getInt(ForecastFragment.COL_WEATHER_MIN_TEMP) ,
                    Utility.isMetric(context)));

        viewHolder.forecastDate.setText(
                Utility.getFriendlyDayString(
                    context , cursor.getLong(ForecastFragment.COL_WEATHER_DATE)));

        viewHolder.weatherDesc.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC));

        switch(getItemViewType(cursor.getPosition())){
            case VIEW_TYPE_TODAY:
                viewHolder.weatherImage.setImageResource(
                        Utility.getArtResourceForWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID))
                );
                break;
            case VIEW_TYPE_FUTURE_DAY:
                viewHolder.weatherImage.setImageResource(
                        Utility.getIconResourceForWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID))
                );
                break;
        }

    }
}
