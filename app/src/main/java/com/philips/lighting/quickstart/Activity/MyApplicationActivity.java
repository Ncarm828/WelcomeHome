package com.philips.lighting.quickstart.Activity;

//Other imports
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager; //keep for now
import android.app.FragmentTransaction; //keep for now
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener; //keep for now
import android.widget.Button; //keep for now

//Philips imports
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.quickstart.Fragment.ProfileFragment;
import com.philips.lighting.quickstart.R;


public class MyApplicationActivity extends Activity {

    //Hue variables
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;



    private int mContainerId;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private final static String TAG = "MyApplicationActivity";


    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);

        phHueSDK = PHHueSDK.create(); //Connects to Philips SDK

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ProfileFragment fragment = new ProfileFragment();
        fragmentTransaction.add(R.id.MainFragmentChange, fragment);
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

    // If you want to handle the response from the bridge, create a PHLightListener object.
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

    public PHLightListener GetMyListener(){
        return listener;
    }


    public void replaceFragment(Fragment fragment, String TAG) {

        try {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(mContainerId, fragment, TAG);
            fragmentTransaction.addToBackStack(TAG);
            fragmentTransaction.commitAllowingStateLoss();

        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}
