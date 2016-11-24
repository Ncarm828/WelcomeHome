package com.philips.lighting.quickstart.Activity;

//Other imports
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.FragmentManager; //keep for now
import android.app.FragmentTransaction; //keep for now
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

//Philips imports
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.quickstart.DataClass.Database.DBHelper;
import com.philips.lighting.quickstart.DataClass.Database.DatabaseManager;
import com.philips.lighting.quickstart.DataClass.Model.Hardware;
import com.philips.lighting.quickstart.DataClass.Model.HardwareSettings;
import com.philips.lighting.quickstart.DataClass.Model.PersonalSettings;
import com.philips.lighting.quickstart.DataClass.repo.HardwareRepo;
import com.philips.lighting.quickstart.DataClass.repo.HardwareSettingRepo;
import com.philips.lighting.quickstart.DataClass.repo.ProfileSettingRepo;
import com.philips.lighting.quickstart.Fragment.ProfileAddFragment;
import com.philips.lighting.quickstart.Fragment.ProfileFragment;
import com.philips.lighting.quickstart.R;


public class MyApplicationActivity extends Activity {

    //Hue variables
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    private final String TAG = "PHSDKAPP";

   //private DBHelper mydb;REMOVE
    private static DBHelper dbHelper;
    private HardwareRepo hardwareRepo;
    private HardwareSettingRepo hardwareSettingRepo;
    private ProfileSettingRepo profileSettingRepo;



    //Fragment objects
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ProfileFragment MainDisplayFragment;
    ProfileAddFragment AddFragment;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);

        //Database
       // mydb = new DBHelper(this); //Save just in case
        dbHelper = new DBHelper(this);
        DatabaseManager.initializeInstance(dbHelper);
        hardwareRepo = new HardwareRepo();
        hardwareSettingRepo = new HardwareSettingRepo();
        profileSettingRepo = new ProfileSettingRepo();


        phHueSDK = PHHueSDK.create(); //Connects to Philips SDK

        //Create one instance of this object
        AddFragment = new ProfileAddFragment();
        MainDisplayFragment = new ProfileFragment();

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.MainFragmentChange, MainDisplayFragment);
        fragmentTransaction.commit();

    }
    
    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }

    // Handle the response from the bridge, create a PHLightListener object.
    private PHLightListener listener = new PHLightListener() {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
            Log.w(TAG, "Light has updated");
        }

        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };


    //created so the fragments can work with lights
    public PHHueSDK GetMySDK() {
        return phHueSDK;
    }

    //Method so the fragments can use the Light Listen
    public PHLightListener GetMyListener(){
        return listener;
    }

    //Keep in case database needs changing
    /*public DBHelper GetMyDB(){
        return mydb;
    }*/

    public HardwareRepo getHardwareRepo() {
        return hardwareRepo;
    }

    public HardwareSettingRepo getHardwareSettingRepo() {
        return hardwareSettingRepo;
    }

    public ProfileSettingRepo getProfileSettingRepo() {
        return profileSettingRepo;
    }


    public void replaceFragment(String name) {

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (name == "ProfileAddFragment") {
            fragmentTransaction.replace(R.id.MainFragmentChange, MainDisplayFragment);
        }else if(name == "ProfileFragment"){
            fragmentTransaction.replace(R.id.MainFragmentChange, AddFragment);
        }
        fragmentTransaction.commit();
    }
}
