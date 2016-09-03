
/*
 * Role of this code: performing a GET to get the list of doctors or patients after signing in
 */

package com.example.android.sunshine.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GET_SignIn_task extends AsyncTask<String[], Void, Void> {


    private final String LOG_TAG = GET_SignIn_task.class.getSimpleName();
    private String result;

    private String[] paramsSaved;

    private final Context mContext;

    public GET_SignIn_task(Context context) {
        mContext = context;
    }

    private List<String> idArray = new ArrayList<String>();

    private List<String> uniqueIdKey = new ArrayList<String>();

    private List<String> nameArray= new ArrayList<String>();

    private List<String> specialtyArray=new ArrayList<String>();


    private void getDataFromJson(String JsonStr)
            throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.



        // Location information
        final String LIST_NAME = "data";
        final String ID_PARAM = "id";
        final String UNIQUE_ID_KEY_PARAM ="id";
        final String FIRST_NAME_PARAM = "firstname";
        final String LAST_NAME_PARAM = "lastname";



        try {
            JSONObject Json = new JSONObject(JsonStr);
            JSONArray jsonArray = Json.getJSONArray(LIST_NAME);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(jsonArray.length());

            for(int i = 0; i < jsonArray.length(); i++) {
                // These are the values that will be collected.



                // Get the JSON object representing the day
                JSONObject object_i = jsonArray.getJSONObject(i);

                uniqueIdKey.add(object_i.getString(UNIQUE_ID_KEY_PARAM));

                nameArray.add(object_i.getString(FIRST_NAME_PARAM)+" "+object_i.getString(LAST_NAME_PARAM));

//                nameArray.add(new String(object_i.getString(DOC_NAME).getBytes("ISO-8859-1"), "UTF-8"));
//                specialtyArray.add(object_i.getString(SPECIALITY));

            }

            for(int i = 0; i < jsonArray.length(); i++) {
                ContentValues dataValues = new ContentValues();
                dataValues.put(DbContract.DataEntry.COLUMN_UNIQUE_ID_KEY, uniqueIdKey.get(i));
                dataValues.put(DbContract.DataEntry.COLUMN_NAME, nameArray.get(i));
                cVVector.add(dataValues);
            }

            int inserted = 0;
            int deleted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                deleted = mContext.getContentResolver().delete(DbContract.DataEntry.CONTENT_URI,null,null);
                inserted = mContext.getContentResolver().bulkInsert(DbContract.DataEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "GET_SignIn_Task Complete. " + deleted + " Deleted");
            Log.d(LOG_TAG, "GET_SignIn_Task Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    @Override
    protected Void doInBackground(String[]... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        paramsSaved =params[0];
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;



        String format = "json";
        String units = "metric";
        int numDays = 3;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String SIGNUP_BASE_URL =
                    "http://double-goal-88313.appspot.com/";

            final String ID_PARAM = "id";
            final String REL_PARAM = "relations";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";

            Log.d(LOG_TAG, "params[0][0] = "+params[0][0].toString());
            Log.d(LOG_TAG, "params[0][1] = "+params[0][1].toString());
            Log.d(LOG_TAG, "params[0][2] = "+params[0][2].toString());
            Log.d(LOG_TAG, "params[0][3] = "+params[0][3].toString());



            Uri builtUri = Uri.parse(SIGNUP_BASE_URL+REL_PARAM+"/?"+ID_PARAM+"="+params[0][1].toString());
//            Uri builtUri = Uri.parse(SIGNUP_BASE_URL+params[0][0].toString()+ID_PARAM+"="+params[0][1].toString()+"&"+EMAIL_PARAM+"="+params[0][2].toString()).buildUpon()
//                    .appendQueryParameter(ID_PARAM, params[0][1].toString())
//                    .appendQueryParameter(EMAIL_PARAM, params[0][2].toString())
//                    .build();
//            Uri builtUri = Uri.parse("http://double-goal-88313.appspot.com/patients/");

            URL url = new URL(builtUri.toString());
            Log.d(LOG_TAG, "URL  = _____________________"+url.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");



            try {

                urlConnection.connect();



                //Read the input stream into a String

                InputStream inputStream;
                if (urlConnection.getResponseCode() / 100 == 2) { // 2xx code means success
                    inputStream = urlConnection.getInputStream();
                } else {

                    inputStream = urlConnection.getErrorStream();

                    String result = inputStream.toString();
                    Log.e(LOG_TAG,"Error " + Integer.toString(urlConnection.getResponseCode())+" "+result);

                }

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));


                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                result = buffer.toString();
                Log.d(LOG_TAG, "Input stream = " + result);

                getDataFromJson(result);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close() ;
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
