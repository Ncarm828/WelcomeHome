package com.philips.lighting.quickstart.DataClass.Database;

/**
 * Created by Nicks on 11/16/2016.
 */
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.philips.lighting.quickstart.DataClass.Model.Hardware;
import com.philips.lighting.quickstart.DataClass.Model.HardwareSettings;
import com.philips.lighting.quickstart.DataClass.Model.PersonalSettings;
import com.philips.lighting.quickstart.DataClass.repo.HardwareRepo;
import com.philips.lighting.quickstart.DataClass.repo.HardwareSettingRepo;
import com.philips.lighting.quickstart.DataClass.repo.ProfileSettingRepo;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "profileDataBase.db";
    private static final String TAG = DBHelper.class.getSimpleName().toString();

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(HardwareRepo.createTable());
        db.execSQL(HardwareSettingRepo.createTable());
        db.execSQL(ProfileSettingRepo.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Hardware.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + HardwareSettings.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PersonalSettings.TABLE);
        onCreate(db);
    }

}
