package com.philips.lighting.quickstart.Fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState; //Keep for now

import com.philips.lighting.quickstart.Activity.MyApplicationActivity;
import com.philips.lighting.quickstart.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileAddFragment extends Fragment {

    private static boolean PastLightStatus = false;
    private MyApplicationActivity activity;

    public ProfileAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile_add, container, false);

        activity = (MyApplicationActivity) getActivity();

        //used to turn light on and off
        //current the layout has changed so the button does show
        Button randomButton;
        randomButton = (Button) view.findViewById(R.id.button);
        randomButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TurnLightsOn();
            }

        });

        return view;
    }


    //This is the function to turn lights on and off
    //unused for now, will use later
    public void TurnLightsOn() {


            PHBridge bridge = activity.GetMySDK().getSelectedBridge();

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
            bridge.updateLightState(light, lightState, activity.GetMyListener());

        }

}
