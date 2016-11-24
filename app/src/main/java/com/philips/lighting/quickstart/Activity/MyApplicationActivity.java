package com.philips.lighting.quickstart.Activity;

//Other imports
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.FragmentManager; //keep for now
import android.app.FragmentTransaction; //keep for now
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

//Philips imports
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.quickstart.DataClass.Database.DBHelper;
import com.philips.lighting.quickstart.DataClass.Database.DatabaseManager;
import com.philips.lighting.quickstart.DataClass.repo.HardwareRepo;
import com.philips.lighting.quickstart.DataClass.repo.HardwareSettingRepo;
import com.philips.lighting.quickstart.DataClass.repo.ProfileSettingRepo;
import com.philips.lighting.quickstart.Fragment.ListOfLightsFragment;
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

    private static boolean PastLightStatus = false; //TEST



    //Fragment objects
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    //Fragments
    ProfileFragment MainDisplayFragment;
    ProfileAddFragment AddFragment;
    ListOfLightsFragment LightListFragment;




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

        //Connects to Philips SDK
        phHueSDK = PHHueSDK.create();

        //Create one instance of this object
        AddFragment = new ProfileAddFragment();
        MainDisplayFragment = new ProfileFragment();
        LightListFragment = new ListOfLightsFragment();


        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.MainFragmentChange, MainDisplayFragment);
        fragmentTransaction.commit();



        //used to turn light on and off
        //current the layout has changed so the button does show
        Button randomButton;
        randomButton = (Button) findViewById(R.id.button);
        randomButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TurnLightsOn();
            }

        });

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
        public void onReceivingLightDetails(PHLight arg0) {

        }

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {
            for(int i = 0; i < arg0.size(); i++){
                System.out.println(arg0.get(i).getName());
                //hardwareRepo.CheckIsDataAlreadyInDBorNot("");
            }
        }

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
        }else if(name == "ListOfLightsFragment"){
            fragmentTransaction.replace(R.id.MainFragmentChange, LightListFragment).addToBackStack(null);

        }
        fragmentTransaction.commit();
    }



    //This is the function to turn lights on and off
    //unused for now, will use later
    public void TurnLightsOn() {


        PHBridge bridge = GetMySDK().getSelectedBridge();

        // List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        PHLightState lightState = new PHLightState();

        PHLight light = bridge.getResourceCache().getLights().get("2");

        if (PastLightStatus) {

            lightState.setOn(false);
            lightState.setTransitionTime(0);
            PastLightStatus = false;
        } else {
            lightState.setOn(true);
            lightState.setBrightness(100);
            lightState.setTransitionTime(0);
            PastLightStatus = true;
        }

        System.out.println("Toggling Light State: " + lightState.isOn());
        bridge.updateLightState(light, lightState, GetMyListener());

    }
}
