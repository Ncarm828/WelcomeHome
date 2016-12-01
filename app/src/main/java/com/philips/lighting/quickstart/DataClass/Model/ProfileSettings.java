package com.philips.lighting.quickstart.DataClass.Model;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Nicks on 11/9/2016.
 * This will be the class that contains information about the card. the card show little information
 * for the user so he or she knows which profile is which
 */

public class ProfileSettings {

    public static final String TAG = ProfileSettings.class.getSimpleName();
    public static final String TABLE = "ProfileSettings";

    public static final String KEY_ProfileSettingsId = "ProfileSettingsId";
    public static final String KEY_Name = "PersonalSettingsName";
    public static final String KEY_Thumbnail = "Thumbnail";
    public static final String KEY_Active = "Active";



    private int ProfileSettingsId;
    private String Name;
    private byte[] Thumbnail;
    private int Active;

    public ProfileSettings(){}

    public ProfileSettings(String name, boolean active, byte[] thumbnail) {
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

    public void setThumbnail(byte[] thumbnail) {
        this.Thumbnail = thumbnail;
    }

    public int getProfileSettingsId() {
        return ProfileSettingsId;
    }

    public void setProfileSettingsId(int profileSettingsId) {
        ProfileSettingsId = profileSettingsId;
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
