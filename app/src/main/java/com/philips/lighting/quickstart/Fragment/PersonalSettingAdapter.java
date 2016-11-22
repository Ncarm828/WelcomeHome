package com.philips.lighting.quickstart.Fragment;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.philips.lighting.quickstart.Activity.MyApplicationActivity;
import com.philips.lighting.quickstart.DataClass.DBHelper;
import com.philips.lighting.quickstart.DataClass.PersonalSettings;
import com.philips.lighting.quickstart.R;

import java.util.List;

/**
 * Created by Nicks on 11/9/2016.
 */

public class PersonalSettingAdapter extends RecyclerView.Adapter<PersonalSettingAdapter.MyViewHolder> {

    private Context mContext;
    private List<PersonalSettings> ProfileList;

    private DBHelper mydb;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.active);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }



    public PersonalSettingAdapter(Context mContext,DBHelper mydb) {
        this.mContext = mContext;
        this.mydb = mydb;
        this.ProfileList = this.mydb.getAllProfile();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_settings_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        PersonalSettings page = ProfileList.get(position);
        holder.title.setText(page.getName());
        holder.count.setText("Default Page:" + page.getActive());
        holder.thumbnail.setImageBitmap(BitmapFactory.decodeByteArray(page.getThumbnail(), 0, page.getThumbnail().length));

        // loading album cover using Glide library
        //Glide.with(mContext).load(page.getThumbnail()).into(holder.thumbnail);



        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_personal_settings, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        int position;

        public MyMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Edit", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    mydb.deleteProfile(position);
                    ProfileList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return ProfileList.size();
    }

}
