package com.philips.lighting.quickstart.DataClass.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.philips.lighting.quickstart.DataClass.Database.DatabaseManager;
import com.philips.lighting.quickstart.DataClass.Model.Hardware;

/**
 * Created by Nicks on 11/22/2016.
 */

public class HardwareRepo {
    private Hardware hardware;

    public HardwareRepo(){

        hardware = new Hardware();

    }


    public static String createTable(){
        return "CREATE TABLE " + Hardware.TABLE  + "("
                + Hardware.KEY_HardwareId  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Hardware.KEY_Name + " TEXT )";
    }


    public int insert(Hardware hardware) {
        int courseId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
      //  values.put(Hardware.KEY_HardwareId, hardware.getHardwareId());
        values.put(Hardware.KEY_Name, hardware.getName());

        // Inserting Row
        courseId=(int)db.insert(Hardware.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return courseId;
    }



    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Hardware.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }


    public static boolean CheckIsDataAlreadyInDBorNot(String fieldValue) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String Query = "Select * from " + Hardware.TABLE + " where " + Hardware.KEY_Name + " = " + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

}
