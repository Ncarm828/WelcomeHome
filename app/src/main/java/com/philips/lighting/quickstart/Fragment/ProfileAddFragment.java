package com.philips.lighting.quickstart.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState; //Keep for now

import com.philips.lighting.quickstart.Activity.MyApplicationActivity;
import com.philips.lighting.quickstart.DataClass.DBHelper;
import com.philips.lighting.quickstart.R;



/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileAddFragment extends Fragment {

    private static boolean PastLightStatus = false;
    private MyApplicationActivity activity;

    private DBHelper mydb;

    private static final int REQUEST_IMAGE_CAPTURE = 0;

    private TextView name ;
    private TextView warningMessage;
    private TextView Picture;
    private ToggleButton Default;
    private ImageView CurrentPicture;

    public String ClassName = "ProfileAddFragment";

    public ProfileAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile_add, container, false);

        activity = (MyApplicationActivity) getActivity();

        name = (TextView) view.findViewById(R.id.editTextName);
        warningMessage = (TextView) view.findViewById(R.id.textViewInformation);
        Picture = (TextView) view.findViewById(R.id.CameraPictureSetting);
        Default = (ToggleButton) view.findViewById(R.id.DefaultToggleButton);
        CurrentPicture = (ImageView) view.findViewById(R.id.ProfilePicture);

        //Grabs the database object from the Activity
        mydb = activity.GetMyDB();

        Button SaveButton;
        SaveButton = (Button) view.findViewById(R.id.SaveSettingsButton);
        SaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(name.getText().toString().trim().length() != 0){
                    SaveProfile(v);
                }else{
                    name.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                    warningMessage.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar
                            .make(view, "There were some elements missing", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
                //Create New Database here
            }
        });

        Button SearchImage;
        SearchImage = (Button) view.findViewById(R.id.CameraPictureSetting);
        SearchImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }

        });

        Button ToggleButton;
        ToggleButton = (Button)view.findViewById(R.id.CancelButton);
        ToggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                activity.replaceFragment(ClassName);
            }

        });


        Default.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!Default.isChecked()) {
                    Default.setTextColor(getResources().getColor(R.color.red));
                }else{
                    Default.setTextColor(getResources().getColor(R.color.green));
                }
            }

        });

        //currently trying to get the default data into database
        //after try and get picture off the internet and store into database
        //figure out how to create new database based off each row in the first database


        //used to turn light on and off
        //current the layout has changed so the button does show
       /* Button randomButton;
        randomButton = (Button) view.findViewById(R.id.button);
        randomButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TurnLightsOn();
            }

        });*/

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

    //Used for saving the new profile entry into the database
    public void SaveProfile(View view) {
        if(mydb.insertProfile(name.getText().toString(),CurrentPicture,Default.isChecked())){
            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
            activity.replaceFragment(ClassName);
        } else{
            Toast.makeText(getActivity(), "Not Saved, there were some issues",
                    Toast.LENGTH_SHORT).show();
        }
    }


    //Creates a call to the camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //The Activity handles this call but in this case the fragment is wanting the return of the camera ans the image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            CurrentPicture.setImageBitmap(imageBitmap);
        }
    }
}