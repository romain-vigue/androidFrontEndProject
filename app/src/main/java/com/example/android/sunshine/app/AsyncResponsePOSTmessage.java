/*
 * Role of this code: utility class to interface a background task to the main thread
 */

package com.example.android.sunshine.app;

/**
 * Created by localuser on 3/17/2015.
 */
public interface AsyncResponsePOSTmessage {
    void processFinishPOSTmessage(String messagePOSTresult);
}
