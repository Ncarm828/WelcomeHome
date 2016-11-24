package com.philips.lighting.quickstart.DataClass.Model;

/**
 * Created by Nicks on 11/23/2016.
 */

public class ProfilesAndHardwareSettings {

    private String hardwareID;
    private String hardwareName;
    private String HardwareSettingsID;
    private String HardwareSettingsName;
    private String HardwareSettingsPName;
    private int HardwareSettingsONOFF;
    private int HardwareSettingBrightness;
    private String PersonalSettingsID;
    private int PersonalSettingsActive;
    private byte[] PersonalSettingsThumbnail;

    public String getPersonalSettingsName() {
        return PersonalSettingsName;
    }

    public void setPersonalSettingsName(String personalSettingsName) {
        PersonalSettingsName = personalSettingsName;
    }

    private String PersonalSettingsName;


    public String getHardwareID() {
        return hardwareID;
    }

    public void setHardwareID(String hardwareID) {
        this.hardwareID = hardwareID;
    }

    public String getHardwareName() {
        return hardwareName;
    }

    public void setHardwareName(String hardName) {
        this.hardwareName = hardName;
    }

    public String getHardwareSettingsID() {
        return HardwareSettingsID;
    }

    public void setHardwareSettingsID(String hardwareSettingsID) {
        HardwareSettingsID = hardwareSettingsID;
    }

    public String getHardwareSettingsName() {
        return HardwareSettingsName;
    }

    public void setHardwareSettingsName(String hardwareSettingsName) {
        HardwareSettingsName = hardwareSettingsName;
    }

    public String getHardwareSettingsPName() {
        return HardwareSettingsPName;
    }

    public void setHardwareSettingsPName(String hardwareSettingsPName) {
        HardwareSettingsPName = hardwareSettingsPName;
    }

    public int getHardwareSettingsONOFF() {
        return HardwareSettingsONOFF;
    }

    public void setHardwareSettingsONOFF(int hardwareSettingsONOFF) {
        HardwareSettingsONOFF = hardwareSettingsONOFF;
    }

    public int getHardwareSettingBrightness() {
        return HardwareSettingBrightness;
    }

    public void setHardwareSettingBrightness(int hardwareSettingBrightness) {
        HardwareSettingBrightness = hardwareSettingBrightness;
    }

    public String getPersonalSettingsID() {
        return PersonalSettingsID;
    }

    public void setPersonalSettingsID(String personalSettingsID) {
        PersonalSettingsID = personalSettingsID;
    }

    public boolean isPersonalSettingsActive() {
        if (PersonalSettingsActive == 1){
            return true;
        }
        return false;
    }

    public void setPersonalSettingsActive(boolean personalSettingsActive) {
        PersonalSettingsActive = personalSettingsActive?1:0;
    }

    public byte[] getPersonalSettingsThumbnail() {
        return PersonalSettingsThumbnail;
    }

    public void setPersonalSettingsThumbnail(byte[] personalSettingsThumbnail) {
        PersonalSettingsThumbnail = personalSettingsThumbnail;
    }

}
