package com.philips.lighting.quickstart.Activity;

//Other imports
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.FragmentManager; //keep for now
import android.app.FragmentTransaction; //keep for now
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.philips.lighting.quickstart.DataClass.Model.ProfilesAndHardwareSettings;
import com.philips.lighting.quickstart.DataClass.ThreadClass.NFCReaderTask;
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

    //Fragments
    ProfileFragment MainDisplayFragment;
    ProfileAddFragment AddFragment;
    HardwareSettingListFragment LightListFragment;

    public Tag MyTag;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);

        //Database
        dbHelper = new DBHelper(this);
        DatabaseManager.initializeInstance(dbHelper);
        hardwareRepo = new HardwareRepo(); //Lights
        hardwareSettingRepo = new HardwareSettingRepo(); //Lights settings
        profileSettingRepo = new ProfileSettingRepo(); //Profiles

        //For testing - Keep for later use
       // hardwareRepo.delete();
        //hardwareSettingRepo.delete();
        //profileSettingRepo.delete();

        //Connects to Philips SDK
        phHueSDK = PHHueSDK.create();
        bridge = phHueSDK.getSelectedBridge();

        //Create one instance of this object
        AddFragment = new ProfileAddFragment();
        MainDisplayFragment = new ProfileFragment();
        LightListFragment = new HardwareSettingListFragment();

        if (savedInstanceState == null) {

            //Fragment Handler
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.MainFragmentChange, MainDisplayFragment);
            fragmentTransaction.commit();
        }

        //NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };

    }

    @Override
    public void onResume() {
        super.onResume();
        WriteModeOn();
    }

    @Override
    public void onPause() {
        super.onPause();
        WriteModeOff();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            MyTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            new  NFCReaderTask(this).execute(MyTag);
            MainDisplayFragment.ReceiveDataForNFC(MyTag);
        }
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
    public synchronized void ChangeLightBrightness(int position,int BrightnessSetting){

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
    public synchronized boolean ToggleLights (int position) {

        boolean returnState = false;

        PHLightState lightState = new PHLightState();
        PHLight light = bridge.getResourceCache().getLights().get(String.valueOf(position+1));

        //Since we are using REST API there is a possible null returned if the state DNE, this is defensive check
        try{
            lightState.setOn(!light.getLastKnownLightState().isOn());
            returnState = lightState.isOn();
        }catch (NullPointerException e) {
            Log.i(TAG,"The light has a NULL state on the Bridges cache");
        }
        bridge.updateLightState(light, lightState, listener);

        return returnState;
    }

    //This is the function to turn lights on and off
    public synchronized boolean TurnLightsOn(String name, boolean CurrentState) {

        boolean returnState = false;

        PHLightState lightState = new PHLightState();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {

            if(light.getName().equals(name)) {
                //Since we are using REST API there is a possible null returned if the state DNE, this is defensive check
                try {
                    lightState.setOn(CurrentState);
                    returnState = lightState.isOn();
                } catch (NullPointerException e) {
                    Log.i(TAG, "The light has a NULL state on the Bridges cache");
                }
                bridge.updateLightState(light, lightState, listener);
            }
        }

        return returnState;
    }

    //Set brightness of the lights
    public synchronized void setBrightness(String name ,int brightness){
        PHLightState lightState = new PHLightState();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {
            if(light.getName().equals(name)) {
                //Since we are using REST API there is a possible null returned if the state DNE, this is defensive check
                try {
                    lightState.setBrightness(brightness);
                } catch (NullPointerException e) {
                    Log.i(TAG, "The light has a NULL state on the Bridges cache");
                }
                bridge.updateLightState(light, lightState, listener);
            }
        }
    }

    //Defensive check that clamp the value
    private int CLAMP (int ValueChecked, int min , int max){
        if( ValueChecked < min){
            ValueChecked = min;
        }else if(ValueChecked > max){
            ValueChecked = max;
        }
        return ValueChecked;
    }



    /******************************************************************************
     **********************************Enable Write********************************
     ******************************************************************************/
    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    /******************************************************************************
     **********************************Disable Write*******************************
     ******************************************************************************/
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    //Testing
    public void SetLights(String msg){
        MainDisplayFragment.SetLights(msg);

    }


    //used for fragment replacement
    public void replaceFragment(String name) {

        FragmentTransaction  fragmentTransaction = getFragmentManager().beginTransaction();

        if (name == "ProfileAddFragment") {
            fragmentTransaction.replace(R.id.MainFragmentChange, MainDisplayFragment).addToBackStack(null);
        }else if(name == "ProfileFragment"){
            fragmentTransaction.replace(R.id.MainFragmentChange, AddFragment).addToBackStack(null);
        }else if(name == "HardwareSettingListFragment"){
            fragmentTransaction.replace(R.id.MainFragmentChange,LightListFragment ).addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

}
