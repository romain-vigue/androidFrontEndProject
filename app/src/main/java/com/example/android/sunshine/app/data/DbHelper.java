/*
 * Role of this code: utility class for the local database, creating the local database and storing the version
 * This code is derived from the Google Android Udacity tutorial available under open source license here:
 * https://github.com/udacity/Sunshine
 *
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.sunshine.app.data.DbContract.DataEntry;

/**
 * Manages a local database for weather data.
 */
public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 15;

    static final String DATABASE_NAME = "data.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude

        final String SQL_CREATE_DATA_TABLE = "CREATE TABLE " + DataEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                DataEntry.COLUMN_UNIQUE_ID_KEY + " TEXT, " +
                DbContract.DataEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DataEntry.COLUMN_SPECIALTY + " TEXT, " +
                DataEntry.COLUMN_SURNAME + " TEXT," +

                DbContract.DataEntry.COLUMN_MIN + " REAL, " +
                DataEntry.COLUMN_NEW_MESSAGES + " REAL, " +

                DataEntry.COLUMN_HUMIDITY + " REAL, " +
                DataEntry.COLUMN_PRESSURE + " REAL, " +
                DataEntry.COLUMN_WIND_SPEED + " REAL, " +
                DataEntry.COLUMN_DEGREES + " REAL); ";

        final String SQL_CREATE_DOCSEARCH_TABLE = "CREATE TABLE " + DbContract.DocSearchEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                DbContract.DocSearchEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                DbContract.DocSearchEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                DbContract.DocSearchEntry.COLUMN_SPECIALTY + " TEXT, " +

                DbContract.DocSearchEntry.COLUMN_LONGITUDE + " TEXT, " +
                DbContract.DocSearchEntry.COLUMN_LATITUDE + " REAL); ";


        sqLiteDatabase.execSQL(SQL_CREATE_DATA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbContract.DocSearchEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
