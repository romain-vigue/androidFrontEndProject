/*
 * Role of this code: performing a POST to add a new doctor
 */

package com.example.android.sunshine.app.data;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshine.app.AsyncResponseAddDoc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

public class POST_add_doc_task extends AsyncTask<String[], Void, Void> {

    public AsyncResponseAddDoc delegate=null;
    private final String LOG_TAG = POST_add_doc_task.class.getSimpleName();
    private String addDocResult;



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

            final String ID_PARAM = "id_patient";
            final String CODE_DOCTOR = "codeDocteur";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";


            Log.d(LOG_TAG, "params[0][1] = "+params[0][1].toString());

            Log.d(LOG_TAG, "params[0][2] = "+params[0][2].toString());

            Uri builtUri = Uri.parse(SIGNUP_BASE_URL+"relations"+"/");
//            Uri builtUri = Uri.parse(SIGNUP_BASE_URL+params[0][0].toString()+ID_PARAM+"="+params[0][1].toString()+"&"+CODE_DOCTOR+"="+params[0][2].toString()).buildUpon()
//                    .appendQueryParameter(ID_PARAM, params[0][1].toString())
//                    .appendQueryParameter(CODE_DOCTOR, params[0][2].toString())
//                    .build();
//            Uri builtUri = Uri.parse("http://double-goal-88313.appspot.com/patients/");
            String urlParameters = ID_PARAM+"="+params[0][1].toString()+"&"+CODE_DOCTOR+"="+params[0][2].toString();
            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;
            URL url = new URL(builtUri.toString());
            Log.e(LOG_TAG, "URL______________ " + url.toString());


            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setUseCaches(false);



            try {
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.write(postData);


                urlConnection.connect();
                wr.flush();



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
                reader = new BufferedReader(new InputStreamReader(inputStream));


                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                addDocResult = buffer.toString();

                Log.d(LOG_TAG, "Input stream = " + addDocResult);


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
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
            delegate.processFinishAddDoc(addDocResult);
        }


    }
}
