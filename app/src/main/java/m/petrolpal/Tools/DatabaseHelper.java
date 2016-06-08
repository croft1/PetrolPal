package m.petrolpal.Tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import m.petrolpal.Models.FuelStop;

/**
 * Created by Michaels on 10/5/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FuelStopDB";
    public static final int DATABASE_VERSION = 1;
    public static final int FS_ID = 0;
    public static final int FS_COST = 1;
    public static final int FS_DATE = 2;
    public static final int FS_QUANTITY = 3;
    public static final int FS_ODOMETER = 4;
    public static final int FS_LATITUDE = 5;
    public static final int FS_LONGITUDE = 6;

    public DatabaseHelper(Context c){
        super(c, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FuelStop.CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FuelStop.TABLE_NAME);
        onCreate(db);
    }

    public void addStop(FuelStop f){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // values.put(FuelStop.COLUMN_ID, f.getId());
        values.put(FuelStop.COLUMN_COST, f.getOverallCost());
        values.put(FuelStop.COLUMN_DATE, f.DATE_FORMAT_DEFAULT.format(f.getDate()));
        values.put(FuelStop.COLUMN_QUANTITY, f.getQuantityBought());
        values.put(FuelStop.COLUMN_ODOMETER, f.getOdometer());
        values.put(FuelStop.COLUMN_LATITUDE, f.getLatitude());
        values.put(FuelStop.COLUMN_LONGITUDE, f.getLongitude());
        db.insert(f.TABLE_NAME, null, values);
        db.close();
    }

    public void removeStop(FuelStop f){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FuelStop.TABLE_NAME,
                FuelStop.COLUMN_ID + " = ?",
                new String[]{String.valueOf(f.getId())}
        );
        db.close();
    }

    public void updateStop(FuelStop f){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FuelStop.COLUMN_ID, f.getId());
        values.put(FuelStop.COLUMN_COST, f.getOverallCost());
        values.put(FuelStop.COLUMN_DATE, f.DATE_FORMAT_DEFAULT.format(f.getDate()));
        values.put(FuelStop.COLUMN_QUANTITY, f.getQuantityBought());
        values.put(FuelStop.COLUMN_ODOMETER, f.getOdometer());
        values.put(FuelStop.COLUMN_LATITUDE, f.getLatitude());
        values.put(FuelStop.COLUMN_LONGITUDE, f.getLongitude());
        db.update(FuelStop.TABLE_NAME, values, "_id="+f.getId(), null);
    }

    public HashMap<Long, FuelStop> getAllFuelStops(){
        HashMap<Long, FuelStop> fuelStops = new LinkedHashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FuelStop.TABLE_NAME, null);

        if(cursor.moveToFirst()){
            do{
                FuelStop fs = new FuelStop(
                        cursor.getLong(FS_ID),
                        //new java.util.Date(cursor.getInt(FS_DATE)),
                        cursor.getString(FS_DATE),
                        cursor.getDouble(FS_QUANTITY),
                        cursor.getDouble(FS_COST),
                        cursor.getInt(FS_ODOMETER),
                        cursor.getDouble(FS_LATITUDE),
                        cursor.getDouble(FS_LONGITUDE)
                );
                fuelStops.put(fs.getId(), fs);
            }while(cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return fuelStops;
    }


}
