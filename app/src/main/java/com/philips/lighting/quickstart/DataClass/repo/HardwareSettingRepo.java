package com.philips.lighting.quickstart.DataClass.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.philips.lighting.quickstart.DataClass.Database.DatabaseManager;
import com.philips.lighting.quickstart.DataClass.Model.Hardware;
import com.philips.lighting.quickstart.DataClass.Model.HardwareSettings;
import com.philips.lighting.quickstart.DataClass.Model.ProfileSettings;
import com.philips.lighting.quickstart.DataClass.Model.ProfilesAndHardwareSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicks on 11/22/2016.
 */

public class HardwareSettingRepo {
    private final String TAG = HardwareSettingRepo.class.getSimpleName().toString();

    public HardwareSettingRepo() {}

    private HardwareSettings settings;

    public static String createTable(){
        return "CREATE TABLE " + HardwareSettings.TABLE  + "("
                + HardwareSettings.KEY_HardwareSettingId + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HardwareSettings.KEY_Name + " TEXT, "
                + HardwareSettings.KEY_HardwareName + " TEXT, "
                + HardwareSettings.KEY_PName + " TEXT, "
                + HardwareSettings.KEY_Brightness + " INTEGER, "
                + HardwareSettings.KEY_ON_OFF + " INTEGER )";
    }

    public int insert(HardwareSettings settings) {

        //For debugging
        int Id;

        //Synchronized access to read and write to database
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        //Puts values into database
        values.put(HardwareSettings.KEY_Name, settings.getName());
        values.put(HardwareSettings.KEY_HardwareName, settings.getHardwareName());
        values.put(HardwareSettings.KEY_ON_OFF, settings.getLightOnOff());
        values.put(HardwareSettings.KEY_Brightness, settings.getBrightness());
        values.put(HardwareSettings.KEY_PName, settings.getProfileName());

        // Inserting Row
        Id=(int)db.insert(HardwareSettings.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return Id;
    }

    public boolean Update(HardwareSettings setting) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        //Putting new values in
        values.put(HardwareSettings.KEY_Name, setting.getName());
        values.put(HardwareSettings.KEY_HardwareName, setting.getHardwareName());
        values.put(HardwareSettings.KEY_ON_OFF, setting.getLightOnOff());
        values.put(HardwareSettings.KEY_Brightness, setting.getBrightness());
        values.put(HardwareSettings.KEY_PName, setting.getProfileName());

        String[] whereClauseArgument = new String[2];
        whereClauseArgument[0] = setting.getHardwareName();
        whereClauseArgument[1] = setting.getProfileName();

        // Updating Row
        db.update(HardwareSettings.TABLE, values, HardwareSettings.KEY_HardwareName +
                " = ?" + " AND " + HardwareSettings.KEY_PName + " = ?", whereClauseArgument);

        DatabaseManager.getInstance().closeDatabase();

       // PrintDB();

        return true;
    }


    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(HardwareSettings.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }



    public List<ProfilesAndHardwareSettings> getProfilesAndHardwareSettings(){
        ProfilesAndHardwareSettings profileSettingList;
        List<ProfilesAndHardwareSettings> ProfileSettingsLists = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT Hardware." + Hardware.KEY_HardwareId
                + ", Hardware." + Hardware.KEY_Name
                + ", HardwareSettings." + HardwareSettings.KEY_HardwareSettingId
                + ", HardwareSettings." + HardwareSettings.KEY_Name
                + ", HardwareSettings." + HardwareSettings.KEY_PName
                + ", HardwareSettings." + HardwareSettings.KEY_ON_OFF
                + ", HardwareSettings." + HardwareSettings.KEY_Brightness
                + ", ProfileSettings." + ProfileSettings.KEY_ProfileSettingsId
                + ", ProfileSettings." + ProfileSettings.KEY_Active
                + ", ProfileSettings." + ProfileSettings.KEY_Thumbnail
                + ", ProfileSettings." + ProfileSettings.KEY_Name
                + " FROM " + HardwareSettings.TABLE + " HardwareSettings "
                + " INNER JOIN " + Hardware.TABLE + " Hardware ON Hardware." + Hardware.KEY_Name + " = HardwareSettings." + HardwareSettings.KEY_HardwareName
                + " INNER JOIN " + ProfileSettings.TABLE + " ProfileSettings ON ProfileSettings." + ProfileSettings.KEY_Name + " = HardwareSettings." + HardwareSettings.KEY_PName;

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                profileSettingList = new ProfilesAndHardwareSettings();
                profileSettingList.setHardwareID(cursor.getString(cursor.getColumnIndex(Hardware.KEY_HardwareId)));
                profileSettingList.setHardwareName(cursor.getString(cursor.getColumnIndex(Hardware.KEY_Name)));
                profileSettingList.setHardwareSettingsID(cursor.getString(cursor.getColumnIndex(HardwareSettings.KEY_HardwareSettingId)));
                profileSettingList.setHardwareSettingsName(cursor.getString(cursor.getColumnIndex( HardwareSettings.KEY_Name)));
                profileSettingList.setHardwareSettingsPName(cursor.getString(cursor.getColumnIndex(HardwareSettings.KEY_PName)));
                profileSettingList.setHardwareSettingsONOFF(Integer.parseInt(cursor.getString(cursor.getColumnIndex( HardwareSettings.KEY_ON_OFF))));
                profileSettingList.setHardwareSettingBrightness(Integer.parseInt(cursor.getString(cursor.getColumnIndex(HardwareSettings.KEY_Brightness))));
                profileSettingList.setPersonalSettingsID(cursor.getString(cursor.getColumnIndex(ProfileSettings.KEY_ProfileSettingsId)));
                profileSettingList.setPersonalSettingsActive(Boolean.parseBoolean(String.valueOf(cursor.getString(cursor.getColumnIndex(ProfileSettings.KEY_Active)))));
                profileSettingList.setPersonalSettingsThumbnail(cursor.getBlob(cursor.getColumnIndex(ProfileSettings.KEY_Thumbnail)));

                profileSettingList.setPersonalSettingsName(cursor.getString(cursor.getColumnIndex(ProfileSettings.KEY_Name)));

                ProfileSettingsLists.add(profileSettingList);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return ProfileSettingsLists;

    }

    public void Delete (String name) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.beginTransaction();

        try {
            db.execSQL("delete from " + ProfileSettings.TABLE + " where " +ProfileSettings.KEY_Name + " = '" + name + "'");
            db.execSQL("delete from " + HardwareSettings.TABLE + " where " +HardwareSettings.KEY_PName + " = '" + name + "'");
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d("Database Profile: ", "Error while trying to delete  users detail");
        } finally {
            db.endTransaction();
            DatabaseManager.getInstance().closeDatabase();
        }
    }

    //Testing. prints table
    public void PrintDB (){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + HardwareSettings.TABLE, null);
        System.out.println("=======================================================================================================================");
        if (cursor.moveToFirst()) {
            do {

                System.out.println("Hardware light  Name        " + "Hardware Profile Name     " + "Hardware ON/OFF       " + "Hardware Brightness    "  );
                System.out.println(cursor.getString(cursor.getColumnIndex("HardwareName")) +
                        "              " + cursor.getString(cursor.getColumnIndex(HardwareSettings.KEY_PName)) +
                        "                       " +cursor.getString(cursor.getColumnIndex( HardwareSettings.KEY_ON_OFF)) +
                        "                        " + cursor.getString(cursor.getColumnIndex(HardwareSettings.KEY_Brightness)));

            } while (cursor.moveToNext());
        }
    }

}
