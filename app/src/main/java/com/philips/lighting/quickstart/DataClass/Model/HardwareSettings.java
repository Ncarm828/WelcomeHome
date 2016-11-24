package com.philips.lighting.quickstart.DataClass.Model;

/**
 * Created by Nicks on 11/22/2016.
 */

public class HardwareSettings {
    public static final String TAG = HardwareSettings.class.getSimpleName();
    public static final String TABLE = "HardwareSettings";

    // Labels Table Columns names
    public static final String KEY_HardwareSettingId = "HardwareSettingsId";
    public static final String KEY_Name = "HardwareSettingsName";
    public static final String KEY_HardwareName = "HardwareName";
    public static final String KEY_PName = "PersonalSettingsName";
    public static final String KEY_ON_OFF = "LightOnOff";
    public static final String KEY_Brightness = "Brightness";



    private int HardwareSettingsId;
    private String name;
    private String HardwareName;
    private String PersonalSettingsName;
    private int LightOnOff;
    private int Brightness;



    public int getHardwareSettingsId() {
        return HardwareSettingsId;
    }

    public void setHardwareSettingsId(int HardwareSettingsId) {
        this.HardwareSettingsId = HardwareSettingsId;
    }

    public String getProfileName() {
        return PersonalSettingsName;
    }

    public void setProfileName(String PersonalSettingsName) {
        this.PersonalSettingsName = PersonalSettingsName;
    }

    public String getHardwareName() {
        return HardwareName;
    }

    public void setHardwareName(String hardwareName) {
        HardwareName = hardwareName;
    }

    public int getLightOnOff() {
        return LightOnOff;
    }

    public void setLightOnOff(int lightOnOff) {
        LightOnOff = lightOnOff;
    }

    public int getBrightness() {
        return Brightness;
    }

    public void setBrightness(int brightness) {
        Brightness = brightness;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
