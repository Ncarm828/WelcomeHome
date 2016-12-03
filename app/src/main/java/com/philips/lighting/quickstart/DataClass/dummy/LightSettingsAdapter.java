package com.philips.lighting.quickstart.DataClass.dummy;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philips.lighting.quickstart.DataClass.Model.ProfilesAndHardwareSettings;
import com.philips.lighting.quickstart.DataClass.repo.HardwareSettingRepo;
import com.philips.lighting.quickstart.DataClass.repo.ProfileSettingRepo;
import com.philips.lighting.quickstart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicks on 11/29/2016.
 */

public class LightSettingsAdapter extends RecyclerView.Adapter<LightSettingsAdapter.MyViewHolder> {

    public interface ToggleBTNListener {
        void ToggleBTNClick(View v,int position, ProfilesAndHardwareSettings page);
        void SeekBarMovement(SeekBar seekBar,int position,int amount,ProfilesAndHardwareSettings page);
    }

    private static final String TAG = LightSettingsAdapter.class.getSimpleName().toString();

    private List<ProfilesAndHardwareSettings> ProfileList;
    private List<Integer> ListPosition;
    private ToggleBTNListener mListener;
    private ProfilesAndHardwareSettings page;


    public LightSettingsAdapter(HardwareSettingRepo hardwareSettingRepo,ToggleBTNListener listener ) {
        this.ProfileList = hardwareSettingRepo.getProfilesAndHardwareSettings();
        this.ListPosition = getListPositions(this.ProfileList);
        this.mListener = listener;
    }


    @Override
    public LightSettingsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_list_item_card, parent, false);
        return new LightSettingsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LightSettingsAdapter.MyViewHolder holder, int position) {
         page = ProfileList.get(ListPosition.get(position));
        System.out.println("NICK       " + page.getHardwareSettingsONOFF());
       if(page.getPersonalSettingsName().equals(ProfileSettingRepo.GetLastName())) {
           System.out.println("NICK     2   " +  page.getHardwareSettingsONOFF());
           holder.title.setText(page.getHardwareName());
           holder.On_Off.setChecked(page.getHardwareSettingsONOFF());
           holder.Brightness.setProgress(page.getHardwareSettingBrightness());
       }
    }

    @Override
    public int getItemCount() {
        return ListPosition.size();
    }

    private List<Integer> getListPositions(List<ProfilesAndHardwareSettings> ProfileList){
        List<Integer> list = new ArrayList<>();
        int count = 0;
        for(ProfilesAndHardwareSettings Item :ProfileList){
           if(Item.getPersonalSettingsName().equals(ProfileSettingRepo.GetLastName())) {
               list.add(count);
           }
            count++;
        }
        return list;
    }

    public List<Integer> GetPositions(){
        return ListPosition;
    }

    public List<ProfilesAndHardwareSettings> GetProfiles(){
        return ProfileList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, Description; //Description is for a later use
        public ToggleButton On_Off;
        public SeekBar Brightness;


        public MyViewHolder(final View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            On_Off = (ToggleButton) view.findViewById(R.id.SwitchToggleButton);
            Brightness = (SeekBar) view.findViewById(R.id.BrightnessSeekBar);

            On_Off.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.ToggleBTNClick(v, getAdapterPosition(), page);
                }
            });

            Brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progress = 0;
                @Override
                public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                    progress = progressValue;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //Need for the Listener, dont do anything
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //Set database to the new value
                    mListener.SeekBarMovement(seekBar,getAdapterPosition(),progress,page);
                }
            });
        }
    }
}
