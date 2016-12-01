package com.philips.lighting.quickstart.Fragment;


import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.quickstart.Activity.MyApplicationActivity;
import com.philips.lighting.quickstart.DataClass.Model.Hardware;
import com.philips.lighting.quickstart.DataClass.Model.ProfileSettings;
import com.philips.lighting.quickstart.DataClass.Model.ProfilesAndHardwareSettings;
import com.philips.lighting.quickstart.DataClass.dummy.PersonalSettingAdapter;
import com.philips.lighting.quickstart.DataClass.repo.HardwareRepo;
import com.philips.lighting.quickstart.DataClass.repo.HardwareSettingRepo;
import com.philips.lighting.quickstart.DataClass.repo.ProfileSettingRepo;
import com.philips.lighting.quickstart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //Card variables
    private RecyclerView recyclerView;
    private PersonalSettingAdapter adapter;
    private ArrayList<ProfileSettings> ProfileList;
    public String ClassName = "ProfileFragment";

    private MyApplicationActivity activity;

    private HardwareSettingRepo hardwareSettingRepo;
    private HardwareRepo hardwareRepo;
    private ProfileSettingRepo profileSettingRepo;

    private PHHueSDK phHueSDK;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Get the MainActivity
        activity = (MyApplicationActivity) getActivity();

        //Get SDK from MainActivity
        phHueSDK = activity.GetMySDK();

        //Grabs the database object from the Activity
        hardwareSettingRepo = activity.getHardwareSettingRepo();
        hardwareRepo = activity.getHardwareRepo();
        profileSettingRepo = activity.getProfileSettingRepo();

        //Thread to the database. Used for checking for new hardware
        HardwareList();

        //gets the view, adapter and recycle view
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        ProfileList = new ArrayList<>();
        adapter = new PersonalSettingAdapter(activity,hardwareSettingRepo, profileSettingRepo, new PersonalSettingAdapter.BTNListener() {
            @Override
            public void LightBtn(View v, int position) {
                UpdateLight(v,position);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new ProfileFragment.GridSpacingItemDecoration(2, dpToPx(10), true));
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

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.replaceFragment(ClassName);
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

    private void HardwareList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                PHBridge bridge = phHueSDK.getSelectedBridge();
                List<PHLight> allLights = bridge.getResourceCache().getAllLights();

                //Create new hardware rows
                for (PHLight light : allLights) {
                    Hardware temp = new Hardware();
                    if (!hardwareRepo.CheckIsDataAlreadyInDBorNot(light.getName())){
                        temp.setName(light.getName());
                        hardwareRepo.insert(temp);
                    }
                }
            }
        }).start();
    }

    public PHHueSDK GetMySDK() {
        return phHueSDK;
    }

    //Handles Database calls and sets lights to their corresponding settings
    private void UpdateLight(final View v, final int position){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Create object for Databases
                ProfileSettingRepo PSR = new ProfileSettingRepo();
                HardwareSettingRepo HSR = new HardwareSettingRepo();
                ProfilesAndHardwareSettings PHS;


                //Get the profile that was clicked
                ProfileSettings PS = PSR.getProfile(position);
                String ProfileName = PS.getName();

                //Get all light settings
                List<ProfilesAndHardwareSettings> AllAttributes  = HSR.getProfilesAndHardwareSettings();

                for(int i = 0; i < AllAttributes.size(); i++){

                    //Gets each row in the database
                    PHS = AllAttributes.get(i);

                    //Checks to see if the clicked profile name matches any row. Note: unknown amount because it depends in what hardware is connected
                    if(PHS.getPersonalSettingsName().equals(ProfileName)){
                        System.out.println(PHS.getHardwareName() + " Nick here");
                        activity.TurnLightsOn(PHS.getHardwareName());

                    }
                }


            }
        }).start();
    }

}
