package com.enolic.mypoi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mypois.db";
    private static final String DB_TABLE = "POIS";

    // Columns
    private static final String ID = "ID";
    private static final String POI_TITLE = "POI_TITLE";
    private static final String POI_TIMESTAMP = "POI_TIMESTAMP";
    private static final String POI_LOCATION = "POI_LOCATION";
    private static final String POI_DESCRIPTION = "POI_DESCRIPTION";
    private static final String POI_CATEGORY = "POI_CATEGORY";

    //
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            DB_TABLE + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            POI_TITLE + " TEXT, " +
            POI_TIMESTAMP + " TEXT, " +
            POI_LOCATION + " TEXT, " +
            POI_DESCRIPTION + " TEXT, " +
            POI_CATEGORY + " TEXT " + ")";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(sqLiteDatabase);
    }


    // method to insert data
    public boolean insertData(String poiTitle, String poiTs, String poiGps, String poiDesc, String poiCat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POI_TITLE, poiTitle);
        contentValues.put(POI_TIMESTAMP, poiTs);
        contentValues.put(POI_LOCATION, poiGps);
        contentValues.put(POI_DESCRIPTION, poiDesc);
        contentValues.put(POI_CATEGORY, poiCat);

        long result = db.insert(DB_TABLE, null, contentValues);

        return result != -1; // if result = -1 data doesnt insert
    }

    public boolean updateData(int id, String poiTitle, String poiTs, String poiGps, String poiDesc, String poiCat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POI_TITLE, poiTitle);
        contentValues.put(POI_TIMESTAMP, poiTs);
        contentValues.put(POI_LOCATION, poiGps);
        contentValues.put(POI_DESCRIPTION, poiDesc);
        contentValues.put(POI_CATEGORY, poiCat);

        long result = db.update(DB_TABLE, contentValues, "ID=?", new String[]{String.valueOf(id)});

        return result != -1; // if result = -1 data doesnt insert
    }

    // method to view data
    public Cursor viewAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DB_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor viewDataById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE ID=" + id, null);

        return cursor;
    }

    public Cursor viewDataByTitle(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE POI_TITLE='" + title +"'", null);

        return cursor;
    }
    public Cursor viewDataByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE POI_CATEGORY='" + category +"'", null);

        return cursor;
    }

    public void deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE, ID + "=" + id, null);
    }
}




























