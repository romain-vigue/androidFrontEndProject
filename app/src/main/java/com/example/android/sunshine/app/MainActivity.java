/*
 * Role of this code: display the main screen of the application (menu and list)
 *
 *
 */
package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.android.sunshine.app.data.GET_SignIn_task;
import com.example.android.sunshine.app.data.POST_SignUp_task;
import com.example.android.sunshine.app.data.POST_add_doc_task;
import com.example.android.sunshine.app.data.POST_add_patient_task;


public class MainActivity extends ActionBarActivity implements AsyncResponseSignUp, AsyncResponseAddDoc, AsyncResponseAddPatient {

    public static final String SAVE_INTENT = "intent_saved";
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String LISTFRAGMENT_TAG = "FFTAG";
    private String[] intent;
    private String[] intent_recovered = null;
    private String[] intent_generator_params;
    private String[] intent_add_doctor_code;
    private String doctor_or_patient ="patients";
    private boolean generate_code_isfinished;
    POST_add_patient_task contextPOSTaddPatientTask = new POST_add_patient_task();
    POST_add_doc_task contextPOSTaddDoctorTAsk = new POST_add_doc_task();
    private String doctorCodePrivate;
    private String value;
    private SharedPreferences prefs;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextPOSTaddDoctorTAsk.delegate = this;
        contextPOSTaddPatientTask.delegate = this;


        generate_code_isfinished = true;

        //TODO get shared preferences to save intent
        prefs = this.getSharedPreferences(getString(R.string.package_name),this.MODE_PRIVATE);

        String myPrefs = prefs.getString("params",null);
        intent = myPrefs.split(",");
        doctor_or_patient = intent[0];


//        if (intent_recovered == null) {
//            Log.e(LOG_TAG, "intent_recovered == null: _______");
//
//        } else {
//            Log.e(LOG_TAG, "intent_recovered !=null: _______" + intent_recovered.toString());
//            intent = intent_recovered;
//        }
//        try {
//            doctor_or_patient = intent[0].toString();
//        } catch (NullPointerException e){
//            doctor_or_patient = "patients";
//            Log.e(LOG_TAG, "NullPointerException");
//        }



        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();

            bundle.putStringArray("intent", intent);
            ListFragment fragInfo = new ListFragment();
            fragInfo.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragInfo, LISTFRAGMENT_TAG)
            .commit();

//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new ListFragment(), LISTFRAGMENT_TAG)
//                    .commit();

            updateData();

        }


    }




    @Override
    @TargetApi(11)
    protected void onPause() {
        super.onPause();
        Log.e(LOG_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("params",intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateData();
            return true;
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsMapsActivity.class));
            return true;
        }

        if (id == R.id.add_new) {
            Log.d(LOG_TAG, "add new");

            if (doctor_or_patient.equals("patients")) {


                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle(R.string.title_add_new_doctor);
                alert.setMessage(R.string.message_add_new_doctor);

                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);

                alert.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        value = input.getText().toString();

                        intent_add_doctor_code = intent;
                        Log.e(LOG_TAG, "____________________ intent add doctor ID is = ___________ "+intent_add_doctor_code[2]);
                        intent_add_doctor_code[2] = value;

                        POST_add_doc_task myNewPostAddDoctorTask = new POST_add_doc_task();
                        myNewPostAddDoctorTask.delegate = contextPOSTaddDoctorTAsk.delegate;
                        myNewPostAddDoctorTask.execute(intent_add_doctor_code);
                    }
                });

                alert.setNegativeButton(R.string.dialog_Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();


            }

            if (doctor_or_patient.equals("doctors")) {

                intent_generator_params = intent;
                intent_generator_params[0] = "generator";

                if(generate_code_isfinished == true) {
                    generate_code_isfinished = false;

                    POST_add_patient_task myNewPostGenerator = new POST_add_patient_task();
                    myNewPostGenerator.delegate = contextPOSTaddPatientTask.delegate;
                    myNewPostGenerator.execute(intent_generator_params);
                }


            }


            return true;
        }


        if (id == R.id.google_map) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateData() {

        POST_SignUp_task POST_SignUp = new POST_SignUp_task();
        GET_SignIn_task GET_SignIn = new GET_SignIn_task(this);



        //TODO add an error message
        if (intent == null) {
            return;
        }

        String[] data = intent;

        GET_SignIn.execute(data);

    }



    @Override
    @TargetApi(11)
    protected void onResume() {
        super.onResume();
        Log.e(LOG_TAG, "onResume");
        String myPrefs = prefs.getString("params",null);
        intent = myPrefs.split(",");
        doctor_or_patient = intent[0];
        Log.e(LOG_TAG, "intent_recovered onResume: _______" + intent.toString());


    }

    //TODO SIGNUP Verify the utility of this
    public void processFinishSignUp(String doctorCode){

        GET_SignIn_task GET_SignIn = new GET_SignIn_task(this);


        String[] data = intent;

        GET_SignIn.execute(data);



    }


    @Override
    public void processFinishAddDoc(String addDocResult) {
        String codeInvalid = "Code_Invalid";
        Log.e(LOG_TAG, "Code invalid: _______" + addDocResult);
        Log.e(LOG_TAG, "TRUE: _______" + addDocResult.equals(" Code Invalid"));

        if ("Code Invalid".equals(addDocResult)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle(R.string.title_add_new_doctor_failure);
            alert.setMessage(R.string.message_add_new_doctor_failure);

            alert.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });

            alert.setNegativeButton(R.string.dialog_Cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });
            alert.show();
        } else {
            updateData();
        }

    }

    @Override
    public void processFinishAddPatient(String doctorCode) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(R.string.title_add_new_patient);
        alert.setMessage(getString(R.string.message_add_new_patient) + "\n" + "\n" + doctorCode);

        Log.d(LOG_TAG, "processFinishDoctoCode");
        generate_code_isfinished = true;

        alert.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.setNegativeButton(R.string.dialog_Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();


    }
}
