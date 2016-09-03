/*
 * Role of this code: display Google Maps
 *
 *
 */


package com.example.android.sunshine.app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.sunshine.app.data.GET_Maps_task;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


public class MapsActivity extends ActionBarActivity implements AsyncResponseMaps {

    GET_Maps_task myGETMapsdataTask = new GET_Maps_task();
    private final String LOG_TAG = MapsActivity.class.getSimpleName();
    private List<String> specialityList;
    private List<String> longitudeList;
    private List<String> latitudeList;
    private List<String> nameList;
    private List<String> specialityWithoutDuplicates;
    private Spinner spinner;
    private ArrayAdapter<String> adapter1;
    private LatLng CURRENT_LOCATION;
    private double radius =0.05;
    private  Toast toastMapsloading;





    private GoogleMap map;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toastMapsloading = Toast.makeText(getApplicationContext(), getString(R.string.maps_loading), Toast.LENGTH_LONG);
        Toast toastNointernet = Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG);

        //Search radius
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        radius = Double.parseDouble(prefs.getString(getString(R.string.pref_radius_key), "0.05"));

        //Location
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            CURRENT_LOCATION = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        } else {
            CURRENT_LOCATION = new LatLng(49.120, 6.177);
        }

        setContentView(R.layout.activity_maps);
        spinner = (Spinner) findViewById(R.id.maps_spinner);



        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        //        R.array.planets_array, android.R.layout.simple_spinner_item);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        Log.e(LOG_TAG, "onCreateMap");




        myGETMapsdataTask.delegate = this;
        String[] intent = {"doctorSearch", URLEncoder.encode("All doctors")};

        myGETMapsdataTask.execute(intent);
        if (isOnline()) {
            toastMapsloading.setGravity(Gravity.CENTER, 0, 0);
            toastMapsloading.show();
        } else{
            toastNointernet.setGravity(Gravity.CENTER, 0, 0);
            toastNointernet.show();
        }


    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsMapsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void processFinish(List<String> nameArray, List<String> specialtyArray, List<String> longitudeArray, List<String> latitudeArray) {
        toastMapsloading.cancel();
        Log.d(LOG_TAG, "processFinishSignIn");
        ArrayList<String> specialtyHashArray = new ArrayList<String>(new LinkedHashSet<String>(specialtyArray));
        specialityWithoutDuplicates = specialtyHashArray;
        specialityList = specialtyArray;
        nameList = nameArray;
        longitudeList = longitudeArray;
        latitudeList = latitudeArray;

        adapter1 = new ArrayAdapter<String>(
                this, // The current context (this activity)
                android.R.layout.simple_spinner_item, // The name of the layout ID.
                specialityWithoutDuplicates);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);




        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "onItemSelected");
                // An item was selected. You can retrieve the selected item using
                String item = (parent.getItemAtPosition(position)).toString();
//                Log.d(LOG_TAG, "item = _________________________________" + item);
//                Log.d(LOG_TAG, "size of specialityList = ___________________________________" + specialityList.size());


                if (map != null) {
                    map.clear();
                    LatLngBounds bounds = new LatLngBounds(new LatLng(CURRENT_LOCATION.latitude-radius,CURRENT_LOCATION.longitude-radius), new LatLng(CURRENT_LOCATION.latitude+radius,CURRENT_LOCATION.longitude+radius));
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                    map.moveCamera(cu);
                    map.addMarker(new MarkerOptions().position(CURRENT_LOCATION).title(getString(R.string.current_position_on_maps)));
                    boolean markers_found = false;


                    for (int p = 0; p< specialityList.size(); p++){

                        if (specialityList.get(p).equals(item)) {

                            if (Math.abs(Double.parseDouble(latitudeList.get(p)) - CURRENT_LOCATION.latitude) <= radius && Math.abs(Double.parseDouble(longitudeList.get(p)) - CURRENT_LOCATION.longitude) <= radius) {

                                Marker marker = map.addMarker(new MarkerOptions()

                                        .position(new LatLng(Double.parseDouble(latitudeList.get(p)), Double.parseDouble(longitudeList.get(p))))
                                        .title("\u200e"+nameList.get(p))
                                        .snippet(specialityList.get(p))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                dropPinEffect(marker);
                                markers_found = true;
                            }

                        }
                     }
                    if (markers_found == false) {
//                        AlertDialog.Builder alert = new AlertDialog.Builder(g);
//
//                        alert.setTitle(R.string.no_doctor_title);
//                        alert.setMessage(R.string.no_doctor_message);
//
//                        alert.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                            }
//                        });
//
//                        alert.setNegativeButton(R.string.dialog_Cancel, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                // Canceled.
//                            }
//                        });
//                        alert.show();

                        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.no_doctor1)+ "\n"+getString(R.string.no_doctor2), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect to the server", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void dropPinEffect(final Marker marker) {

        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator

        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);

                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);
                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });

    }
}
