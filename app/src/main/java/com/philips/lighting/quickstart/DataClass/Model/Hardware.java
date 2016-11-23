package com.philips.lighting.quickstart.DataClass.Model;



public class Hardware {
    public static final String TAG = Hardware.class.getSimpleName();
    public static final String TABLE = "Hardware";

    // Labels Table Columns names
    public static final String KEY_HardwareId = "CourseId";
    public static final String KEY_Name = "Name";

    private String HardwareId;
    private String name;


    public String getHardwareId() {
        return HardwareId;
    }

    public void setHardwareId(String HardwareId) {
        this.HardwareId = HardwareId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
