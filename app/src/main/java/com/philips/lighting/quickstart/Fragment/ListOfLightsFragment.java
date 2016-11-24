package com.philips.lighting.quickstart.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.quickstart.Activity.MyApplicationActivity;
import com.philips.lighting.quickstart.DataClass.dummy.MyListOfLightsRecyclerViewAdapter;
import com.philips.lighting.quickstart.R;

import java.util.List;


public class ListOfLightsFragment extends Fragment {


    private int mColumnCount = 1;


    private MyApplicationActivity activity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListOfLightsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listoflights_list, container, false);

        activity = (MyApplicationActivity) getActivity();

        PHBridge bridge = activity.GetMySDK().getSelectedBridge();
        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyListOfLightsRecyclerViewAdapter(allLights));
        }
        return view;
    }


}
