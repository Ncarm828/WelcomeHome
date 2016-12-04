package com.philips.lighting.quickstart.DataClass.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.philips.lighting.quickstart.DataClass.Database.DatabaseManager;
import com.philips.lighting.quickstart.DataClass.Model.ProfileSettings;

import java.util.ArrayList;

/**
 * Created by Nicks on 11/22/2016.
 */

public class ProfileSettingRepo {

    private ProfileSettings settings;

    public ProfileSettingRepo(){

        settings = new ProfileSettings();

    }

    public static String createTable(){
        return "CREATE TABLE " + com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.TABLE  + "("
                + com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_ProfileSettingsId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Thumbnail + " IMAGE, "
                + com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Active + " INTEGER, "
                + com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Name + " TEXT )";
    }

    public int insert(ProfileSettings setting) {
        int Id;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Name, setting.getName());
        values.put(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Thumbnail, setting.getThumbnail());
        values.put(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Active, setting.getActive());

        // Inserting Row
        Id=(int)db.insert(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return Id;
    }

    public boolean Update (ProfileSettings setting, int id) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();

        values.put(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Name, setting.getName());
        values.put(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Thumbnail, setting.getThumbnail());
        values.put(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Active, setting.getActive());

        // Updating Row
        db.update(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.TABLE, values, "id = ? ", new String[] { Integer.toString(id) });
        DatabaseManager.getInstance().closeDatabase();

        return true;
    }

    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    //Get all profiles
    public ArrayList<ProfileSettings> getAllProfile() {
        ArrayList<ProfileSettings> ListOfProfiles = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.TABLE;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProfileSettings SingleProfile = new ProfileSettings(cursor.getString(cursor.getColumnIndex(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Name)),
                        Boolean.parseBoolean(String.valueOf(cursor.getString(cursor.getColumnIndex(com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.KEY_Active)))),
                        cursor.getBlob(cursor.getColumnIndex(ProfileSettings.KEY_Thumbnail)));
                // Adding contact to list
                ListOfProfiles.add(SingleProfile);

            } while (cursor.moveToNext());
        }

        return ListOfProfiles;
    }

    //Gets a profile
    public ProfileSettings getProfile(int position) {

        ProfileSettings Profile = new ProfileSettings();

        String selectQuery = "SELECT  * FROM " + ProfileSettings.TABLE;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try{
            if(cursor.moveToPosition(position)) {
                Profile.setName(cursor.getString(cursor.getColumnIndex(ProfileSettings.KEY_Name)));
                Profile.setThumbnail(cursor.getBlob(cursor.getColumnIndex(ProfileSettings.KEY_Thumbnail)));
                Profile.setActive(Boolean.parseBoolean(String.valueOf(cursor.getString(cursor.getColumnIndex(ProfileSettings.KEY_Active)))));
            }
        }finally {
            cursor.close();
            DatabaseManager.getInstance().closeDatabase();
        }
        return Profile;
    }

    //Deletes single Profile
    //Maybe an error, test thoroughly
    public Integer deleteProfile (String name) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        try {
            db.beginTransaction();
            db.execSQL("delete from " + com.philips.lighting.quickstart.DataClass.Model.ProfileSettings.TABLE + " where name ='" + name + "'");
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d("Database Profile: ", "Error while trying to delete  users detail");
            return -1;
        } finally {
            db.endTransaction();
        }
        return 1;
    }


    //Get Last Element
    public static String GetLastName(){

        String name = null;
        String selectQuery = "SELECT  * FROM " + ProfileSettings.TABLE;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try{
            if(cursor.moveToLast()) {
                name = cursor.getString(cursor.getColumnIndex(ProfileSettings.KEY_Name));
            }
        }finally {
            cursor.close();
            DatabaseManager.getInstance().closeDatabase();
        }

        return name;
    }
}
