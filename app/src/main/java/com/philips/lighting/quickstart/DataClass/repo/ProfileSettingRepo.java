package com.philips.lighting.quickstart.DataClass.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.philips.lighting.quickstart.DataClass.Database.DatabaseManager;
import com.philips.lighting.quickstart.DataClass.Model.PersonalSettings;

import java.util.ArrayList;

/**
 * Created by Nicks on 11/22/2016.
 */

public class ProfileSettingRepo {

    private PersonalSettings settings;

    public ProfileSettingRepo(){

        settings = new PersonalSettings();

    }

    public static String createTable(){
        return "CREATE TABLE " + PersonalSettings.TABLE  + "("
                + PersonalSettings.KEY_PersonalSettingsId  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PersonalSettings.KEY_Thumbnail + " IMAGE, "
                + PersonalSettings.KEY_Active + " INTEGER, "
                + PersonalSettings.KEY_Name + " TEXT )";
    }

    public int insert(PersonalSettings setting) {
        int Id;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        //values.put(PersonalSettings.KEY_PersonalSettingsId, setting.getPersonalSettingsId());
        values.put(PersonalSettings.KEY_Name, setting.getName());
        values.put(PersonalSettings.KEY_Thumbnail, setting.getThumbnail());
        values.put(PersonalSettings.KEY_Active, setting.getActive());

        // Inserting Row
        Id=(int)db.insert(PersonalSettings.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return Id;
    }

    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(PersonalSettings.TABLE,null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    //Get all profiles
    public ArrayList<PersonalSettings> getAllProfile() {
        ArrayList<PersonalSettings> ListOfProfiles = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + PersonalSettings.TABLE;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                //Note: Fix later !!!!
                //!!!!

              /*  PersonalSettings SingleProfile = new PersonalSettings(cursor.getString(cursor.getColumnIndex(PersonalSettings.KEY_Name))),
                        Boolean.parseBoolean(String.valueOf(cursor.getString(cursor.getColumnIndex(PersonalSettings.KEY_Active)))),
                        cursor.getBlob(cursor.getColumnIndex(PersonalSettings.KEY_Thumbnail));*/
                // Adding contact to list
              //  ListOfProfiles.add(SingleProfile);

            } while (cursor.moveToNext());
        }

        return ListOfProfiles;
    }


}
