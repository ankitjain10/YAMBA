package com.example.dell.mmb;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import winterwell.jtwitter.Twitter;

/**
 * Created by dell on 10/22/2015.
 */
public class StatusProvider extends ContentProvider {
    public static final String TAG = StatusProvider.class.getSimpleName();
    public static final String AUTHORITY = "content://com.example.dell.mmb.StatusProvider";
    public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
    DbHelper mDbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        String ret = getContext().getContentResolver().getType(CONTENT_URI);
        Log.d(TAG, "getType returning: " + ret);
        return ret;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = mDbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(DbHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (rowId != -1) {
            return Uri.withAppendedPath(uri, String.valueOf(rowId));
        }
        Toast.makeText(getContext(), "StatusProvider insert success", Toast.LENGTH_SHORT).show();

        return uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.TABLE_NAME, projection, selection,
                null, null, null, sortOrder);
        Toast.makeText(getContext(), "StatusProvider query succes", Toast.LENGTH_SHORT).show();
        return cursor;
    }
}

class DbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "status.db";
    public static final String TABLE_NAME = "STATUS_TABLE";
    public static final int DB_VERSION = 1;
    public static final String C_ID = "_id";
    public static final String C_CREATED_AT = "created_at";
    public static final String C_USER = "user";
    public static final String C_TEXT = "text";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_status_table = "create table " + TABLE_NAME + " (" + C_ID + " integer " +
                "primary key," + C_CREATED_AT + " string," + C_USER + " string," + C_TEXT + " string )";
        String create_status_table1 = String.format("create table %s (%s integer primary key," +
                "%s string," +
                "%s string," +
                "%s string )", TABLE_NAME, C_ID, C_CREATED_AT, C_USER, C_TEXT);

        Log.v("SQL", create_status_table + "\n" + create_status_table1);
        db.execSQL(create_status_table);
        Log.v("SQL", "Success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop if exists " + TABLE_NAME);
        onCreate(db);
    }


    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
                String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }
    public static ContentValues getContentValues(Twitter.Status status){
        ContentValues cv = new ContentValues();
        cv.put(C_ID, status.id);
        cv.put(C_CREATED_AT, status.createdAt.getTime());
        cv.put(C_USER, status.user.name);
        cv.put(C_TEXT, status.text);
        return cv;
    }
}

