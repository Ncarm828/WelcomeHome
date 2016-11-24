package com.philips.lighting.quickstart.DataClass.Model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.ImageView;

import com.philips.lighting.quickstart.Activity.MyApplicationActivity;
import com.philips.lighting.quickstart.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by Nicks on 11/9/2016.
 * This will be the class that contains information about the card. the card show little information
 * for the user so he or she knows which profile is which
 */

public class PersonalSettings {

    public static final String TAG = PersonalSettings.class.getSimpleName();
    public static final String TABLE = "PersonalSettings";

    public static final String KEY_PersonalSettingsId = "PersonalSettingsId";
    public static final String KEY_Name = "PersonalSettingsName";
    public static final String KEY_Thumbnail = "Thumbnail";
    public static final String KEY_Active = "Active";



    private int PersonalSettingsId;
    private String Name;
    private byte[] Thumbnail;
    private int Active;

    public PersonalSettings(){}

    public PersonalSettings(String name, boolean active, byte[] thumbnail) {
        this.Name = name;
        this.Active = active ? 1 : 0;
        this.Thumbnail = thumbnail;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public int  getActive() {
        return Active;
    }

    public void setActive(boolean active) {
        this.Active = active ? 1 : 0;
    }

    public byte[] getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(ImageView thumbnail) {
        this.Thumbnail = BitmapToByteArrayConverter(thumbnail);
    }

    public int getPersonalSettingsId() {
        return PersonalSettingsId;
    }

    public void setPersonalSettingsId(int personalSettingsId) {
        PersonalSettingsId = personalSettingsId;
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
