package com.philips.lighting.quickstart.DataClass;

/**
 * Created by Nicks on 11/16/2016.
 */
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "profileDataBase.db";
    private static final String PROFILE_TABLE_NAME = "profileNames";
    private static final String CONTACTS_COLUMN_ID = "id";
    private static final String PROFILE_COLUMN_NAME = "name";
    private static final String PROFILE_COLUMN_PICTURE = "picture";
    private static final String PROFILE_COLUMN_ACTIVE = "active";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table ProfileNames " +
                        "(id integer primary key, name text, picture image, active bit)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS profileNames");
        onCreate(db);
    }

    public boolean insertProfile (String name, ImageView imageBytes, Boolean active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("active", active);
        contentValues.put("picture",BitmapToByteArrayConverter(imageBytes));

        db.insert("profileNames", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from profileNames where id="+id+"", null );
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, PROFILE_TABLE_NAME);
    }

    public boolean updateProfile (Integer id, String name, ImageView imageBytes, Boolean active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("active", active);
        contentValues.put("picture", BitmapToByteArrayConverter(imageBytes));
        db.update("profileNames", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteProfile (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PROFILE_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    //Get all profiles
    public ArrayList<PersonalSettings> getAllProfile() {
        ArrayList<PersonalSettings> ListOfProfiles = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + PROFILE_TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PersonalSettings SingleProfile = new PersonalSettings(cursor.getString(cursor.getColumnIndex(PROFILE_COLUMN_NAME)),
                        Boolean.parseBoolean(String.valueOf(cursor.getColumnIndex(PROFILE_COLUMN_ACTIVE))),
                        cursor.getBlob(cursor.getColumnIndex(PROFILE_COLUMN_PICTURE)));
                // Adding contact to list
                ListOfProfiles.add(SingleProfile);
            } while (cursor.moveToNext());
        }

        return ListOfProfiles;
    }


    //Helper function that takes in an image view and changes it to a char array
    //needed for the database
    private byte[] BitmapToByteArrayConverter(ImageView bmp){
        bmp.buildDrawingCache(); //problem is here
        Bitmap bm = bmp.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
