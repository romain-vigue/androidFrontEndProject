/*
 * Role of this code: display the list of doctors or patients
 *
 *
 */
package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshine.app.data.DbContract;
import com.example.android.sunshine.app.discussion.HelloBubblesActivity;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */

public class ListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String[] intentStringArray;

    private static final String LOG_TAG = ListFragment.class.getSimpleName();


    private static final int LIST_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] LIST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            DbContract.DataEntry.TABLE_NAME + "." + DbContract.DataEntry._ID,
            DbContract.DataEntry.COLUMN_UNIQUE_ID_KEY,
            DbContract.DataEntry.COLUMN_NAME,
            DbContract.DataEntry.COLUMN_SPECIALTY,
            DbContract.DataEntry.COLUMN_NEW_MESSAGES
    };

    // These indices are tied to LIST_COLUMNS.  If LIST_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_UNIQUE_ID_KEY = 1;
    static final int COL_DATA_NAME = 2;
    static final int COL_DATA_SPECIALTY = 3;
    static final int COL_DATA_NEW_MESSAGES = 4;
    static final int COL_WEATHER_MIN_TEMP = 5;
    static final int COL_LOCATION_SETTING = 6;
    static final int COL_WEATHER_CONDITION_ID = 7;
    static final int COL_COORD_LAT = 8;
    static final int COL_COORD_LONG = 9;

    private ListAdapter mListAdapter;

    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return true;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The CursorAdapter will take data from our cursor and populate the ListView.
        mListAdapter = new ListAdapter(getActivity(), null, 0);

        Bundle bundle = this.getArguments();
        intentStringArray = bundle.getStringArray("intent");

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mListAdapter);

        //We'll call our MainActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);


                if (cursor != null ) {
                    String idOfClicked = cursor.getString(cursor.getColumnIndex(DbContract.DataEntry.COLUMN_UNIQUE_ID_KEY));

                    Intent intent = new Intent(getActivity(), HelloBubblesActivity.class);
                    SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.package_name),getActivity().MODE_PRIVATE);
                    String myPrefs = prefs.getString("params",null);
                    String [] intentBubbleArray= myPrefs.split(",");
                    intentBubbleArray[2] = idOfClicked;
                    intent.putExtra("params", intentBubbleArray);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

//    // since we read the location when we create the loader, all we need to do is restart things
//    void onLocationChanged( ) {
//        updateData();
//        //getLoaderManager().restartLoader(LIST_LOADER, null, this);
//    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {


        // Sort order:  Ascending, by new messages.
        String sortOrder = DbContract.DataEntry.COLUMN_NEW_MESSAGES + " DESC";
        Uri getDataUri = DbContract.DataEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                getDataUri,
                LIST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListAdapter.swapCursor(null);
    }



}
