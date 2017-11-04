package com.exemple.eac3_2017s1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BlueStorm on 28/10/2017.
 */

public class DBInterface {
    private static final String _ID = "_id";
    private static final String NAME = "name";
    private static final String FILE = "file";
    private static final String PHOTO_OR_VIDEO = "photo_or_video";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private static final String DB_NAME = "mediadb";
    private static final String TABLE_NAME = "media";

    private static final String TAG = "DBInterface";
    private static final int VERSIO = 1;

    private static final String DB_CREATE =
            "create table " + TABLE_NAME + "( " + _ID + " integer primary key autoincrement, " +
                    NAME + ", " + FILE + ", " + PHOTO_OR_VIDEO + ", " + LATITUDE + ", " + LONGITUDE + ");";

    private final Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBInterface(Context con) {
        this.context = con;
        dbHelper = new DBHelper(context);
    }

    //Obre la BD

    public DBInterface open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

//Tanca la BD

    public void close() {
        dbHelper.close();
    }


    public long insert(Media media) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(NAME, media.getName());
        initialValues.put(FILE, media.getFile());
        initialValues.put(PHOTO_OR_VIDEO, media.getPhotoOrVideo());
        initialValues.put(LATITUDE, media.getLatitude());
        initialValues.put(LONGITUDE, media.getLongitude());

        return db.insert(TABLE_NAME, null, initialValues);
    }

    public List<Media> getAll() {
        List<Media> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, new String[]{_ID, NAME, FILE, PHOTO_OR_VIDEO, LATITUDE, LONGITUDE}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(_ID));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String file = cursor.getString(cursor.getColumnIndex(FILE));
            int photoOrVideo = cursor.getInt(cursor.getColumnIndex(PHOTO_OR_VIDEO));
            double latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
            list.add(new Media(id, name, file, photoOrVideo, latitude, longitude));
        }
        return list;
    }

    //Destruye la tabla y la rehace
    public void dropAndRecreateTable() {
        open();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(DB_CREATE);
        close();
    }

    public void delete(int id) {
        db.delete(TABLE_NAME, _ID + " = ?", new String[]{String.valueOf(id)});
    }

    private static class DBHelper extends SQLiteOpenHelper {
        DBHelper(Context con) {
            super(con, DB_NAME, null, VERSIO);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DB_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int VersioAntiga, int VersioNova) {
            Log.w(TAG, "Actualitzant Base de dades de la versió" + VersioAntiga + " a " + VersioNova + ". Destruirà totes les dades");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            onCreate(db);
        }
    }
}