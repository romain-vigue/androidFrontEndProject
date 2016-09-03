/*
 * Role of this code: utility class to interface a background task to the main thread
 */

package com.example.android.sunshine.app;

import java.util.List;

/**
 * Created by localuser on 3/17/2015.
 */
public interface AsyncResponseMaps {
    void processFinish(List<String> nameArray, List<String> specialtyArray, List<String> longitudeArray, List<String> latitudeArray);
}
