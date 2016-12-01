package com.philips.lighting.quickstart.Activity;

//Other imports
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.FragmentManager; //keep for now
import android.app.FragmentTransaction; //keep for now
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
import com.philips.lighting.quickstart.DataClass.Model.ProfileSettings;
import com.philips.lighting.quickstart.DataClass.repo.HardwareRepo;
import com.philips.lighting.quickstart.DataClass.repo.HardwareSettingRepo;
import com.philips.lighting.quickstart.DataClass.repo.ProfileSettingRepo;
import com.philips.lighting.quickstart.Fragment.HardwareSettingListFragment;
import com.philips.lighting.quickstart.Fragment.ProfileAddFragment;
import com.philips.lighting.quickstart.Fragment.ProfileFragment;
import com.philips.lighting.quickstart.R;


public class MyApplicationActivity extends Activity{

    //Hue variables
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    private final String TAG = "PHSDKAPP";
    private PHBridge bridge;

   //Database Objects
    private static DBHelper dbHelper;
    private HardwareRepo hardwareRepo;
    private HardwareSettingRepo hardwareSettingRepo;
    private ProfileSettingRepo profileSettingRepo;

    //Fragment objects
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    //Fragments
    ProfileFragment MainDisplayFragment;
    ProfileAddFragment AddFragment;
    HardwareSettingListFragment LightListFragment;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);

        //Database
        dbHelper = new DBHelper(this);
        DatabaseManager.initializeInstance(dbHelper);
        hardwareRepo = new HardwareRepo();
        hardwareSettingRepo = new HardwareSettingRepo();
        profileSettingRepo = new ProfileSettingRepo();


        //For testing
        //hardwareRepo.delete();
       // hardwareSettingRepo.delete();
       // profileSettingRepo.delete();

        //Connects to Philips SDK
        phHueSDK = PHHueSDK.create();
        bridge = phHueSDK.getSelectedBridge();

        //Create one instance of this object
        AddFragment = new ProfileAddFragment();
        MainDisplayFragment = new ProfileFragment();
        LightListFragment = new HardwareSettingListFragment();

        //Fragment Handler
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


    public HardwareRepo getHardwareRepo() {
        return hardwareRepo;
    }


    public HardwareSettingRepo getHardwareSettingRepo() {
        return hardwareSettingRepo;
    }


    public ProfileSettingRepo getProfileSettingRepo() {
        return profileSettingRepo;
    }


    public void PrintDataBase(){
        //Check DB manager comments before calling this function
        System.out.println(dbHelper.getTableAsString(DatabaseManager.getInstance().openDatabase(), ProfileSettings.TABLE));
    }


    //Function that will turn all lights off in the room your working with
    public void StartNewSlate(){

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light:allLights){
            PHLightState lightState = new PHLightState();
            lightState.setOn(false);
            phHueSDK.getSelectedBridge().updateLightState(light, lightState, GetMyListener());
        }

    }


    //Change the brightness of the lights
    public void ChangeLightBrightness(int position,int BrightnessSetting){

        PHLightState lightState = new PHLightState();
        PHLight light = bridge.getResourceCache().getLights().get(String.valueOf(position+1));

        //Since we are using REST API there is a possible null returned if the state DNE, this is defensive check
        try{
            if(light.getLastKnownLightState().isOn()){

                //CLAMP is a defensive check because if the value is out of this range the program will crash :(
                BrightnessSetting = CLAMP(BrightnessSetting,0,254);

                lightState.setBrightness(BrightnessSetting);
            }
        }catch (NullPointerException e) {
            Log.i(TAG,"The light has a NULL state on the Bridges cache");
        }
        bridge.updateLightState(light, lightState,listener);
    }


    //This is the function to turn lights on and off
    public boolean TurnLightsOn(int position) {

        boolean returnState = false;

        PHLightState lightState = new PHLightState();
        PHLight light = bridge.getResourceCache().getLights().get(String.valueOf(position+1));

        //Since we are using REST API there is a possible null returned if the state DNE, this is defensive check
        try{
            lightState.setOn(!light.getLastKnownLightState().isOn());
            returnState = light.getLastKnownLightState().isOn();
        }catch (NullPointerException e) {
            Log.i(TAG,"The light has a NULL state on the Bridges cache");
        }
        bridge.updateLightState(light, lightState, listener);

        return returnState;
    }

    //This is the function to turn lights on and off
    public boolean TurnLightsOn(String name) {

        boolean returnState = false;

        PHLightState lightState = new PHLightState();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        System.out.println("Testing the profile name: " + name + " == " + allLights.get(1).getName());
        for (PHLight light : allLights) {

            if(light.getName().equals(name)) {
                //Since we are using REST API there is a possible null returned if the state DNE, this is defensive check
                try {
                    lightState.setOn(!light.getLastKnownLightState().isOn());
                    returnState = light.getLastKnownLightState().isOn();
                } catch (NullPointerException e) {
                    Log.i(TAG, "The light has a NULL state on the Bridges cache");
                }
                bridge.updateLightState(light, lightState, listener);
            }
        }

        return returnState;
    }


    private int CLAMP (int ValueChecked, int min , int max){
        if( ValueChecked < min){
            ValueChecked = min;
        }else if(ValueChecked > max){
            ValueChecked = max;
        }
        return ValueChecked;
    }



    public void replaceFragment(String name) {

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (name == "ProfileAddFragment") {
            fragmentTransaction.replace(R.id.MainFragmentChange, MainDisplayFragment).addToBackStack(null);
        }else if(name == "ProfileFragment"){
            fragmentTransaction.replace(R.id.MainFragmentChange, AddFragment).addToBackStack(null);
        }else if(name == "HardwareSettingListFragment"){
            fragmentTransaction.replace(R.id.MainFragmentChange,LightListFragment ).addToBackStack(null);
        }
        fragmentTransaction.commit();
    }


    /******************************************************************
     * Starts up the LoginActivity - called on user sign out
     *****************************************************************/
    public void runWelcomeActivity() {
        Intent intent = new Intent(MyApplicationActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }

}
