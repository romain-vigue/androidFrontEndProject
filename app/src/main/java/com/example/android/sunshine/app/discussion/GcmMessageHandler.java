/*
 * Role of this code: utility class for Google Cloud Messaging to handle a new message
 * [not implemented yet]
 */

package com.example.android.sunshine.app.discussion;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService{

	String mes;
	private Handler handler;
	
	public GcmMessageHandler(){
		super("GcmMessageHandler");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		
		String messageType = gcm.getMessageType(intent);
		
		mes = extras.getString("title");
		showToast();
		
		Log.i("GCM", "Received: (" + messageType + ") " + extras.getString("title"));
		
		GcmReceiver.completeWakefulIntent(intent);
	}

	public void showToast() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
			}
		});
	}
}
