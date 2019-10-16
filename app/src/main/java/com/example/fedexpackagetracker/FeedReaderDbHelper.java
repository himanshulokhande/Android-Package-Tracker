package com.example.fedexpackagetracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper{
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "himanshu";
    private static FeedReaderDbHelper helper;

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized FeedReaderDbHelper getInstance(Context context)
    {
        if(helper == null)
        {
            helper = new FeedReaderDbHelper(context);
        }

        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_CREATE_PACKAGE);
        db.execSQL(SQL_CREATE_ENTRIES_TRACK_PACKAGE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_TRACK_PACKAGE);
        db.execSQL(SQL_DELETE_ENTRIES_CREATE_PACKAGE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private static final String SQL_CREATE_ENTRIES_CREATE_PACKAGE =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE1 + " (" +
                    FeedReaderContract.FeedEntry.Tracking_Number + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.FeedEntry.Weight + " TEXT," +
                    FeedReaderContract.FeedEntry.Quantity + " TEXT," +
                    FeedReaderContract.FeedEntry.Sender_Name + " TEXT," +
                    FeedReaderContract.FeedEntry.Receiver_Name + " TEXT," +
                    FeedReaderContract.FeedEntry.Source + " TEXT," +
                    FeedReaderContract.FeedEntry.Destination + " TEXT," +
                    FeedReaderContract.FeedEntry.Package_Type + " TEXT," +
                    FeedReaderContract.FeedEntry.Signature_Required + " BOOLEAN," +
                    FeedReaderContract.FeedEntry.Create_Date + " TEXT," +
                    FeedReaderContract.FeedEntry.Update_Date + " TEXT," +
                    FeedReaderContract.FeedEntry.Comments + " TEXT)";

    private static final String SQL_CREATE_ENTRIES_TRACK_PACKAGE =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE2 + " (" +
                    FeedReaderContract.FeedEntry.Tracking_Number + " INTEGER," +
                    FeedReaderContract.FeedEntry.Center_Name + " TEXT," +
                    FeedReaderContract.FeedEntry.Arrival_Time + " TEXT," +
                    FeedReaderContract.FeedEntry.Departure_Time + " TEXT," +
                    "FOREIGN KEY(tracking_number) REFERENCES package_details(tracking_number))";

    private static final String SQL_DELETE_ENTRIES_CREATE_PACKAGE =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE1;

    private static final String SQL_DELETE_ENTRIES_TRACK_PACKAGE =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE1;
}
