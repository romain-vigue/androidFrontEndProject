/*
 * Role of this code: creating the messaging interface
 */

package com.example.android.sunshine.app.discussion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ListView;

import android.os.Handler;

import com.example.android.sunshine.app.AsyncResponseGETmessage;
import com.example.android.sunshine.app.AsyncResponsePOSTmessage;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.data.GET_message_task;
import com.example.android.sunshine.app.data.POST_message_task;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.svenjacobs.loremipsum.LoremIpsum;

public class HelloBubblesActivity extends Activity implements AsyncResponseGETmessage, AsyncResponsePOSTmessage {
	private DiscussArrayAdapter adapter;
	private ListView lv;
	private LoremIpsum ipsum;
	private EditText editText1;
	private static Random random;
    private POST_message_task myPOSTmessageTask = new POST_message_task();
    private List<String> messagingGET_docIsSender = new ArrayList<String>();
    private List<String> messagingGET_message = new ArrayList<String>();
    private String[] params;
    private boolean isDoctor = false;
    private String[] paramsGET;
    private int mInterval = 3000;
    private Handler mHandler;
    GET_message_task contextMyGETMessageTask = new GET_message_task();



    GoogleCloudMessaging gcm;
    String PROJECT_NUMBER= "104627423161";
    String regid;



    public void getRegId(){

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params)
            {
                String msg= "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg= "Device registered, registration ID="+regid;

                } catch(IOException ex) {
                       msg = "Error: "+ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg){
                editText1.setText(msg+"\n");
            }



        }.execute(null,null,null);
    }


    private final String LOG_TAG = HelloBubblesActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discuss);
		random = new Random();
		ipsum = new LoremIpsum();

        //Handler task repeater
        mHandler = new Handler();


		lv = (ListView) findViewById(R.id.listView1);

		adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);

		lv.setAdapter(adapter);

		editText1 = (EditText) findViewById(R.id.editText1);

        final Context mcontext = this;
        myPOSTmessageTask.delegate = this;
        contextMyGETMessageTask.delegate =this;

        if (this.getIntent() != null) {

            params = this.getIntent().getStringArrayExtra("params");
            Log.e(LOG_TAG,"params  ______________"+params[0]+","+params[1]+","+params[2]+","+params[3]);

            String id_doctor;
            String id_patient;
            if (params[0].equals("doctors")) {
                id_doctor = params[1];
                id_patient = params[2];
                isDoctor = true;
            } else {
                id_doctor = params[2];
                id_patient = params[1];
                isDoctor = false;
            }


            paramsGET= new String[] {params[0],id_doctor,id_patient};
            Log.e(LOG_TAG,"params GET ______________"+paramsGET[0]+","+paramsGET[1]+","+paramsGET[2]);

            if (isOnline()) {
                GET_message_task myGETmessageTask = new GET_message_task();
                myGETmessageTask.delegate = this;
                myGETmessageTask.execute(paramsGET);
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(mcontext);

                alert.setTitle(R.string.no_internet_title);
                alert.setMessage(R.string.no_internet);

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

        startRepeatingTask();

        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lv.requestFocus();
                return false;
            }
        });





//        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                Log.e(LOG_TAG, "onScrollStateChanged");
//                if (scrollState != SCROLL_STATE_IDLE && scrollState !=SCROLL_STATE_FLING && scrollState ==SCROLL_STATE_TOUCH_SCROLL){
//                    Log.e(LOG_TAG, "onScrollStateChanged NOT IDLE");
//                    lv.requestFocus();
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//
//            }
//
//        });

        final View activityRootView = findViewById(R.id.activityRoot);



        editText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {


            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               Log.e(LOG_TAG, "EDITTEXT OnFocusChange");
               if (hasFocus){
                   activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {


                       @Override
                       public void onGlobalLayout() {

                           int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                           if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                               Log.e(LOG_TAG, "________________ HEIGHT DIFF ________________");
                               lv.smoothScrollToPosition(adapter.getCount());
                           }
                       }
                   });

                   Log.e(LOG_TAG, "EDITTEXT hasFocus ______________"+hasFocus);
               } else {
                   lv.requestFocus();
               }
            }
        });


		editText1.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                Log.e(LOG_TAG, "setOnClickListener");

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    if (isOnline() && (params != null)) {
                        params[3] = editText1.getText().toString();
                        adapter.add(new OneComment(false, editText1.getText().toString()));
                        lv.smoothScrollToPosition(adapter.getCount() - 1);
                        editText1.setText("");
                        POST_message_task myNewPOSTmessageTask = new POST_message_task();
                        myNewPOSTmessageTask.delegate = myPOSTmessageTask.delegate;

                        Log.e(LOG_TAG, "params POST ______________" + params[0] + "," + params[1] + "," + params[2] + "," + params[3]);
                        myNewPOSTmessageTask.execute(params);

                    } else {
                        editText1.setText("");
                        AlertDialog.Builder alert = new AlertDialog.Builder(mcontext);

                        alert.setTitle(R.string.title_POST_message_failure);
                        alert.setMessage(R.string.message_POST_message_failure);

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

                    return true;
                }
                return false;
            }
        });



	}

    //Handler repeating task
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            GET_message_task myGETmessageTask = new GET_message_task();
            myGETmessageTask.delegate = contextMyGETMessageTask.delegate;
            myGETmessageTask.execute(paramsGET);
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
    }



    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

	private void addItems() {
        adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);

        lv.setAdapter(adapter);

        String sender;
        if (isDoctor){
            sender = "1";
        }
        else {
            sender ="0";
        }
        Log.e(LOG_TAG,"addItems()");
        Log.e(LOG_TAG,"messaginGET_size ______________"+messagingGET_message.size());

		for (int i = 0; i < messagingGET_docIsSender.size(); i++) {
//			boolean left = getRandomInteger(0, 1) == 0 ? true : false;
//			int word = getRandomInteger(1, 10);
//			int start = getRandomInteger(1, 40);
//			String words = ipsum.getWords(word, start);


            if (messagingGET_docIsSender.get(i).equals(sender)){
                Log.e(LOG_TAG,"CASE FALSE"+messagingGET_docIsSender.get(i).equals(sender));
                adapter.add(new OneComment(false, messagingGET_message.get(i)));
            } else{
                Log.e(LOG_TAG,"CASE TRUE"+messagingGET_docIsSender.get(i).equals(sender));
                adapter.add(new OneComment(true, messagingGET_message.get(i)));
            }

		}
        lv.setSelection(adapter.getCount() - 1);
	}

	private static int getRandomInteger(int aStart, int aEnd) {
		if (aStart > aEnd) {
			throw new IllegalArgumentException("Start cannot exceed End.");
		}
		long range = (long) aEnd - (long) aStart + 1;
		long fraction = (long) (range * random.nextDouble());
		int randomNumber = (int) (fraction + aStart);
		return randomNumber;
	}

    @Override
    public void processFinishGETmessage(List<String> messagingGETresult_docIsSender, List<String> messagingGETresult_message) {
        Log.e(LOG_TAG,"processFinishGETmessage()");
        if (messagingGETresult_docIsSender.size() != 0){
            Log.e(LOG_TAG,"messaginGETresult_docIsSender[0] ______________"+messagingGETresult_docIsSender.get(0));
            Log.e(LOG_TAG,"messaginGETresult_message[0] ______________"+messagingGETresult_message.get(0));
        }

        messagingGET_docIsSender = new ArrayList<String>(messagingGETresult_docIsSender);
        messagingGET_message = new ArrayList<String>(messagingGETresult_message);

        addItems();
    }

    @Override
    public void processFinishPOSTmessage(String messagePOSTresult) {
        if (isOnline()) {


            GET_message_task myGETmessageTask = new GET_message_task();
            myGETmessageTask.delegate = this;
            myGETmessageTask.execute(paramsGET);
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle(R.string.no_internet_title);
            alert.setMessage(R.string.no_internet);

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
}