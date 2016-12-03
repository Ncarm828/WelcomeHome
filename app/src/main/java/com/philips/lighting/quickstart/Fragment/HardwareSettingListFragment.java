package com.philips.lighting.quickstart.Fragment;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.quickstart.Activity.MyApplicationActivity;
import com.philips.lighting.quickstart.DataClass.Model.HardwareSettings;
import com.philips.lighting.quickstart.DataClass.Model.ProfileSettings;
import com.philips.lighting.quickstart.DataClass.Model.ProfilesAndHardwareSettings;
import com.philips.lighting.quickstart.DataClass.dummy.LightSettingsAdapter;
import com.philips.lighting.quickstart.DataClass.repo.HardwareSettingRepo;
import com.philips.lighting.quickstart.DataClass.repo.ProfileSettingRepo;
import com.philips.lighting.quickstart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Nicks on 11/29/2016.
 */

public class HardwareSettingListFragment extends Fragment {

    private static final String TAG = HardwareSettingListFragment.class.getSimpleName().toString();

    //Card variables
    private RecyclerView recyclerView;
    private LightSettingsAdapter adapter;
    private ArrayList<ProfileSettings> ProfileList;
    public String ClassName = "ProfileFragment";

    private MyApplicationActivity activity;

    private HardwareSettingRepo hardwareSettingRepo;






    public  HardwareSettingListFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.custom_list_item_title_card, container, false);

        //Get the MainActivity
        activity = (MyApplicationActivity) getActivity();

        //Grabs the database object from the Activity
        hardwareSettingRepo = activity.getHardwareSettingRepo();

        //gets the view, adapter and recycle view
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        ProfileList = new ArrayList<>();

        //Turns all lights off
        activity.StartNewSlate();

        // adapter for the hardware
        adapter = new LightSettingsAdapter(hardwareSettingRepo, new LightSettingsAdapter.ToggleBTNListener() {
            @Override
            public void ToggleBTNClick(View v, int position, ProfilesAndHardwareSettings page) {

                //Toggle switch on off
               Boolean  ToggleState = activity.ToggleLights(position);

                //Save the Toggle state
                List<ProfilesAndHardwareSettings> ProfileList = adapter.GetProfiles();
                List<Integer> Positions = adapter.GetPositions();

                ProfileList.get(Positions.get(position)).setHardwareSettingsONOFF(ToggleState?1:0);

               // page.setHardwareSettingsONOFF(ToggleState?1:0);
            }

            @Override
            public void SeekBarMovement(SeekBar seekBar, int position, int amount, ProfilesAndHardwareSettings page) {

                //Change the light setting so the user can see the change
                 activity.ChangeLightBrightness(position,amount);

                //saves the Brightness
                page.setHardwareSettingBrightness(amount);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new HardwareSettingListFragment.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //places actual items within the cards
        preparePersonalSettingCards();

        //Will show and hide the toolbar title on scroll
        initCollapsingToolbar(view);

        //Places image at top of the screen
        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) view.findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Create and handles the create button for the profile
        Button saveButton;
        saveButton = (Button) view.findViewById(R.id.SaveSettingsButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                List<ProfilesAndHardwareSettings> ProfileList = adapter.GetProfiles();
                List<Integer> Positions = adapter.GetPositions();

                ProfileSettingRepo PS = new ProfileSettingRepo();


                //Update
                for(int i = 0; i < Positions.size(); i++){

                    HardwareSettings HW = new HardwareSettings();

                    HW.setHardwareName(ProfileList.get(Positions.get(i)).getHardwareName());
                    HW.setName(ProfileList.get(Positions.get(i)).getHardwareSettingsName());
                    HW.setBrightness(ProfileList.get(Positions.get(i)).getHardwareSettingBrightness());
                    HW.setProfileName(PS.GetLastName());
                    HW.setLightOnOff(ProfileList.get(Positions.get(i)).getHardwareSettingsONOFF()? 1:0);
                    hardwareSettingRepo.Update(HW);
                }

                activity.replaceFragment("ProfileAddFragment");
                }
        });

        return view;
    }



    /**
     * This will take all the data from the database and display it to the user
     */
    private void preparePersonalSettingCards() {
        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar(View view) {

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

}
