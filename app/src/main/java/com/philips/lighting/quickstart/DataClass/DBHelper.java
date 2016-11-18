package com.philips.lighting.quickstart.DataClass;

/**
 * Created by Nicks on 11/16/2016.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ProfileDataBase.db";
    private static final String PROFILE_TABLE_NAME = "ProfileNames";
    private static final String CONTACTS_COLUMN_ID = "id";
    private static final String PROFILE_COLUMN_NAME = "Name";
    private static final String PROFILE_COLUMN_PICTURE = "Picture";
    private static final String PROFILE_COLUMN_ACTIVE = "Active";
    private HashMap hp;

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
        db.execSQL("DROP TABLE IF EXISTS ProfileNames");
        onCreate(db);
    }

    public boolean insertProfile (String name, byte[] imageBytes, Boolean active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("Picture",imageBytes);
        contentValues.put("active", active);
        db.insert("ProfileNames", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from ProfileNames where id="+id+"", null );
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, PROFILE_TABLE_NAME);
    }

    public boolean updateProfile (Integer id, String name, byte[] imageBytes, Boolean active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("picture", imageBytes);
        contentValues.put("active", active);
        db.update("ProfileNames", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteProfile (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("ProfileNames",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllProfile() {
        ArrayList<String> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ProfileNames", null );
        res.moveToFirst();

        while(res.isAfterLast()){
            array_list.add(res.getString(res.getColumnIndex(PROFILE_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    // Get the image from SQLite DB
    // We will just get the last image we just saved for convenience...
    public byte[] retreiveImageFromDB(SQLiteDatabase db) {
        Cursor cur = db.query(true, PROFILE_TABLE_NAME, new String[]{PROFILE_COLUMN_PICTURE,},
                null, null, null, null,
                CONTACTS_COLUMN_ID + " DESC", "1");
        if (cur.moveToFirst()) {
            byte[] blob = cur.getBlob(cur.getColumnIndex(PROFILE_COLUMN_PICTURE));
            cur.close();
            return blob;
        }
        cur.close();
        return null;
    }
}
