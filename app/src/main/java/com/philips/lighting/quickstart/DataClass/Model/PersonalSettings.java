package com.philips.lighting.quickstart.DataClass.Model;

import android.media.Image;

/**
 * Created by Nicks on 11/9/2016.
 * This will be the class that contains information about the card. the card show little information
 * for the user so he or she knows which profile is which
 */

public class PersonalSettings {
    private String Name;
    private byte[] Thumbnail;
    private boolean Active;
    private int dbID; //keeps track of where the item is in the database

    public PersonalSettings(){}

    public PersonalSettings(String name, boolean active, byte[] thumbnail, int dbID) {
        this.Name = name;
        this.Active = active;
        this.Thumbnail = thumbnail;
        this.dbID = dbID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public boolean getActive() {
        return Active;
    }

    public void setActive(boolean active) {
        this.Active = active;
    }

    public byte[] getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.Thumbnail = thumbnail;
    }

    public int getDbID() {
        return dbID;
    }

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }



}
