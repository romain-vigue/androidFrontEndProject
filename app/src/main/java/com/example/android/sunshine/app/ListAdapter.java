/*
 * Role of this code: utility class to load the list of doctors or patients
 *
 *
 */

package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ListAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class ListAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView newMessagesView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            newMessagesView = (TextView) view.findViewById(R.id.list_item_new_messages_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    public ListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }



    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Use placeholder image for now
        viewHolder.iconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor
        String name = cursor.getString(ListFragment.COL_DATA_NAME);
        // Find TextView and set formatted date on it
        viewHolder.dateView.setText(name);

        // Read weather forecast from cursor
        String description = cursor.getString(ListFragment.COL_DATA_SPECIALTY);
        // Find TextView and set weather forecast on it
        viewHolder.descriptionView.setText(description);


        // Read high temperature from cursor
        int number = cursor.getInt(ListFragment.COL_DATA_NEW_MESSAGES);
        viewHolder.newMessagesView.setText("(" + Integer.toString(number) + ")");
        if (number >0 ){
            viewHolder.newMessagesView.setTypeface(viewHolder.newMessagesView.getTypeface(), Typeface.BOLD);
            viewHolder.newMessagesView.setTextColor(Color.RED);
        }
        else {
            viewHolder.newMessagesView.setTypeface(viewHolder.newMessagesView.getTypeface(),Typeface.NORMAL);
            viewHolder.newMessagesView.setTextColor(Color.DKGRAY);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}