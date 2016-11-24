package com.philips.lighting.quickstart.DataClass.dummy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.quickstart.Fragment.ListOfLightsFragment;
import com.philips.lighting.quickstart.R;

import java.util.List;


public class MyListOfLightsRecyclerViewAdapter extends RecyclerView.Adapter<MyListOfLightsRecyclerViewAdapter.ViewHolder> {


    private PHBridge bridge;
    private List<PHLight> allLights;

    public MyListOfLightsRecyclerViewAdapter(List<PHLight> items) {
        allLights = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_listoflights, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
       // holder.mItem = mValues.get(position);
        holder.mIdView.setText(allLights.get(position).getIdentifier());
        holder.mContentView.setText(allLights.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return allLights.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
