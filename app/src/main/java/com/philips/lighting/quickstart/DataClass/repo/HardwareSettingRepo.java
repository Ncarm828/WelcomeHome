package com.philips.lighting.quickstart.DataClass.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.philips.lighting.quickstart.DataClass.Database.DatabaseManager;
import com.philips.lighting.quickstart.DataClass.Model.Hardware;
import com.philips.lighting.quickstart.DataClass.Model.HardwareSettings;
import com.philips.lighting.quickstart.DataClass.Model.PersonalSettings;
import com.philips.lighting.quickstart.DataClass.Model.ProfilesAndHardwareSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicks on 11/22/2016.
 */

public class HardwareSettingRepo {
    private final String TAG = HardwareSettingRepo.class.getSimpleName().toString();

    public HardwareSettingRepo() {

    }

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

    public void insert(HardwareSettings settings) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
      //  values.put(HardwareSettings.KEY_HardwareSettingId, settings.getHardwareSettingsId());
        values.put(HardwareSettings.KEY_Name, settings.getName());
        values.put(HardwareSettings.KEY_HardwareName, settings.getHardwareName());
        values.put(HardwareSettings.KEY_ON_OFF, settings.getLightOnOff());
        values.put(HardwareSettings.KEY_Brightness, settings.getBrightness());
        values.put(HardwareSettings.KEY_PName, settings.getProfileName());

        // Inserting Row
        db.insert(HardwareSettings.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

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
                + ", Hardware." + Hardware.KEY_Name  + " As HardwareName"
                + ", HardwareSettings." + HardwareSettings.KEY_HardwareSettingId
                + ", HardwareSettings." + HardwareSettings.KEY_Name  + " As HardwareSettingName"
                + ", HardwareSettings." + HardwareSettings.KEY_PName
                + ", HardwareSettings." + HardwareSettings.KEY_ON_OFF
                + ", HardwareSettings." + HardwareSettings.KEY_Brightness
                + ", PersonalSettings." + PersonalSettings.KEY_PersonalSettingsId
                + ", PersonalSettings." + PersonalSettings.KEY_Active
                + ", PersonalSettings." + PersonalSettings.KEY_Thumbnail
                + ", PersonalSettings." + PersonalSettings.KEY_Name
                + " FROM " + HardwareSettings.TABLE
                + " INNER JOIN " + Hardware.TABLE + " Hardware ON Hardware." + Hardware.KEY_Name + " = HardwareSettings." + HardwareSettings.KEY_HardwareName
                + " INNER JOIN " + PersonalSettings.TABLE + " PersonalSettings ON PersonalSettings." + PersonalSettings.KEY_Name + " = HardwareSettings." + HardwareSettings.KEY_PName;

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                profileSettingList = new ProfilesAndHardwareSettings();
                profileSettingList.setHardwareID(cursor.getString(cursor.getColumnIndex(Hardware.KEY_HardwareId)));
                profileSettingList.setHardwareName(cursor.getString(cursor.getColumnIndex("HardwareName")));
                profileSettingList.setHardwareSettingsID(cursor.getString(cursor.getColumnIndex(HardwareSettings.KEY_HardwareSettingId)));
                profileSettingList.setHardwareName(cursor.getString(cursor.getColumnIndex("HardwareSettingName")));
                profileSettingList.setHardwareSettingsPName(cursor.getString(cursor.getColumnIndex(HardwareSettings.KEY_PName)));
                profileSettingList.setHardwareSettingsONOFF(Integer.parseInt(cursor.getString(cursor.getColumnIndex( HardwareSettings.KEY_ON_OFF))));
                profileSettingList.setHardwareSettingBrightness(Integer.parseInt(cursor.getString(cursor.getColumnIndex(HardwareSettings.KEY_Brightness))));
                profileSettingList.setPersonalSettingsID(cursor.getString(cursor.getColumnIndex(PersonalSettings.KEY_PersonalSettingsId)));
                profileSettingList.setPersonalSettingsActive(Boolean.parseBoolean(String.valueOf(cursor.getString(cursor.getColumnIndex(PersonalSettings.KEY_Active)))));
                profileSettingList.setPersonalSettingsThumbnail(cursor.getBlob(cursor.getColumnIndex(PersonalSettings.KEY_Thumbnail)));
                profileSettingList.setPersonalSettingsName(cursor.getString(cursor.getColumnIndex(PersonalSettings.KEY_Name)));

                ProfileSettingsLists.add(profileSettingList);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        /*TESTING REASONS*/
        for(int i = 0; i > ProfileSettingsLists.size();i++){
            profileSettingList = new ProfilesAndHardwareSettings();
            System.out.println(profileSettingList.isPersonalSettingsActive());
            System.out.println(profileSettingList.getPersonalSettingsID());
            System.out.println(profileSettingList.getPersonalSettingsThumbnail());
            System.out.println(profileSettingList.getPersonalSettingsName());

        }

        return ProfileSettingsLists;

    }

    public void DeleteProfile(String name){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery1 =
                " DELETE FROM PersonalSettings WHERE " + PersonalSettings.KEY_Name + " IN (SELECT " + PersonalSettings.KEY_Name + " FROM HardwareSettings WHERE "+HardwareSettings.KEY_PName +" = " + name +"); ";
        String selectQuery2 =
                " DELETE FROM HardwareSettings WHERE "+HardwareSettings.KEY_PName + " = " + name +";";

        try{
            db.beginTransaction();

            Log.d(TAG, selectQuery1);
            Log.d(TAG, selectQuery2);
            db.execSQL(selectQuery1);
            db.execSQL(selectQuery2);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }finally {
            db.endTransaction();
        }

        DatabaseManager.getInstance().closeDatabase();

    }

}
