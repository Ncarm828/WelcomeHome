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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import com.philips.lighting.quickstart.Activity.MyApplicationActivity;
import com.philips.lighting.quickstart.DataClass.Model.HardwareSettings;
import com.philips.lighting.quickstart.DataClass.Model.ProfileSettings;
import com.philips.lighting.quickstart.DataClass.repo.HardwareSettingRepo;
import com.philips.lighting.quickstart.DataClass.repo.ProfileSettingRepo;
import com.philips.lighting.quickstart.R;

import java.util.List;


public class ProfileAddFragment extends Fragment {


    private MyApplicationActivity activity;

    //Database for the profile
    private ProfileSettingRepo profileSettingRepo;

    //Database for the HardwareSetting
    private HardwareSettingRepo settings;

    private static final int REQUEST_IMAGE_CAPTURE = 0;

    private TextView name ;
    private TextView warningMessage;
    private TextView Picture;
    private ToggleButton Default;
    private ImageView CurrentPicture;

    //SDK for the lights
    private PHHueSDK phHueSDK;

    public String ClassName = "ProfileAddFragment";

    public ProfileAddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile_add, container, false);

        //Get the MainActivity
        activity = (MyApplicationActivity) getActivity();

        //Get SDK from MainActivity
        phHueSDK = activity.GetMySDK();

        //Grabs the views off the XML file
        name = (TextView) view.findViewById(R.id.editTextName);
        warningMessage = (TextView) view.findViewById(R.id.textViewInformation);
        Picture = (TextView) view.findViewById(R.id.CameraPictureSetting);
        Default = (ToggleButton) view.findViewById(R.id.DefaultToggleButton);
        CurrentPicture = (ImageView) view.findViewById(R.id.ProfilePicture);

        //Grabs the database object from the Activity
        profileSettingRepo = activity.getProfileSettingRepo();
        settings = activity.getHardwareSettingRepo();

        //Create and handles the create button for the profile
        Button CreateProfileButton;
        CreateProfileButton = (Button) view.findViewById(R.id.SaveSettingsButton);
        CreateProfileButton.setOnClickListener(new View.OnClickListener() {

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
            }
        });

        //Button is unused at this point. need for getting an image off line
        Button SearchImage;
        SearchImage = (Button) view.findViewById(R.id.CameraPictureSetting);
        SearchImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }

        });

        //creates and handles the cancel button if the user no longer wants to create a profile
        Button CancelButton;
        CancelButton = (Button)view.findViewById(R.id.CancelButton);
        CancelButton.setOnClickListener(new View.OnClickListener() {

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

        return view;
    }

    //Used for saving the new profile entry into the database
    public void SaveProfile(View view) {
        ProfileSettings profileSettings = new ProfileSettings();

        profileSettings.setName(name.getText().toString());
        profileSettings.setThumbnail(CurrentPicture);
        profileSettings.setActive(Default.isChecked());

        if(profileSettingRepo.insert(profileSettings) > 0){ //returns the ID of the item we just placed
            HandleLights(profileSettings.getName());
            activity.replaceFragment("HardwareSettingListFragment");
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

   private void  HandleLights(String name){
       //Get Lights
       PHBridge bridge = phHueSDK.getSelectedBridge();
       List<PHLight> allLights = bridge.getResourceCache().getAllLights();

       //Notify user that the profile has been saved
       Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();

       //Create new hardware rows
       for (PHLight light : allLights) {

           //takes values from the light and places them into database
           HardwareSettings temp = new HardwareSettings();
           temp.setName(light.getName());
           temp.setLightOnOff(0);
           temp.setBrightness(0);
           temp.setProfileName(name);
           temp.setHardwareName(light.getName());

           //Insert the settings in database
           settings.insert(temp);
       }

   }
}