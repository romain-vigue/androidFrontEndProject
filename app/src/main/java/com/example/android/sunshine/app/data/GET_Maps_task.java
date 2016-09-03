
/*
 * Role of this code: performing a GET to get the position of the doctors on the map
 */

package com.example.android.sunshine.app.data;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshine.app.AsyncResponseMaps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by localuser on 3/28/2015.
 */
public class GET_Maps_task extends AsyncTask<String[], Void, Void> {

    public AsyncResponseMaps delegate=null;
    private final String LOG_TAG = POST_SignUp_task.class.getSimpleName();
    private String result;
    private List<String> nameArray= new ArrayList<String>();

    private List<String> specialtyArray=new ArrayList<String>();

    private List<String> longitudeArray =new ArrayList<String>();
    private List<String> latitudeArray =new ArrayList<String>();

    private void getDataFromJson(String JsonStr)
            throws JSONException {

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.



        // Location information
        final String LIST_NAME = "data";
        final String ID_PARAM = "Id";
        final String DOC_NAME = "Name";
        final String ADDRESS = "Address";
        final String PHONE = "Phone";
        final String LATITUDE = "Latitude";
        final String LONGITUDE = "Longitude";
        final String SPECIALITY = "Speciality";
        final String LANGUAGE = "Language";





        try {
            JSONObject Json = new JSONObject(JsonStr);
            JSONArray jsonArray = Json.getJSONArray(LIST_NAME);

            for(int i = 0; i < jsonArray.length(); i++) {
                // These are the values that will be collected.



                // Get the JSON object representing the day
                JSONObject object_i = jsonArray.getJSONObject(i);

                nameArray.add(new String(object_i.getString(DOC_NAME).getBytes("ISO-8859-1"), "UTF-8"));
                specialtyArray.add(object_i.getString(SPECIALITY));
                longitudeArray.add(object_i.getString(LONGITUDE));
                latitudeArray.add(object_i.getString(LATITUDE));


            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }   catch (UnsupportedEncodingException e) {
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

            final String ID_PARAM = "docSpec";
            final String EMAIL_PARAM = "email";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";

            Log.d(LOG_TAG, "params[0][0] = "+params[0][0].toString());
            Log.d(LOG_TAG, "params[0][1] = "+params[0][1].toString());



            Uri builtUri = Uri.parse(SIGNUP_BASE_URL+params[0][0].toString()+"/?"+ID_PARAM+"="+params[0][1].toString());
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

    @Override
    protected void onPostExecute(Void aVoid) {

        Log.d(LOG_TAG, "onPostExecute");

        super.onPostExecute(aVoid);
        if (delegate !=null) {
            delegate.processFinish(nameArray,specialtyArray, longitudeArray, latitudeArray);
        }


    }
}
