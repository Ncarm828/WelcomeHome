package com.philips.lighting.quickstart.DataClass.dummy;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.philips.lighting.quickstart.DataClass.Database.DBHelper;
import com.philips.lighting.quickstart.DataClass.Model.PersonalSettings;
import com.philips.lighting.quickstart.DataClass.Model.ProfilesAndHardwareSettings;
import com.philips.lighting.quickstart.DataClass.repo.HardwareSettingRepo;
import com.philips.lighting.quickstart.R;

import java.util.List;


public class PersonalSettingAdapter extends RecyclerView.Adapter<PersonalSettingAdapter.MyViewHolder> {

    private Context mContext;
    private List<ProfilesAndHardwareSettings> ProfileList;
    private HardwareSettingRepo hardwareSettingRepo;
   // private DBHelper mydb; //Leave for now

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



    public PersonalSettingAdapter(Context mContext,HardwareSettingRepo hardwareSettingRepo) {
        this.mContext = mContext;
        this.ProfileList = hardwareSettingRepo.getProfilesAndHardwareSettings();
        this.hardwareSettingRepo = hardwareSettingRepo;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_settings_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ProfilesAndHardwareSettings page = ProfileList.get(position);
        holder.title.setText(page.getPersonalSettingsName());
        holder.count.setText("Default Page: " + page.isPersonalSettingsActive());
        holder.thumbnail.setImageBitmap(BitmapFactory.decodeByteArray(page.getPersonalSettingsThumbnail(), 0, page.getPersonalSettingsThumbnail().length));

        // loading album cover using Glide library
        Glide.with(mContext).load(page.getPersonalSettingsThumbnail()).asBitmap().into(holder.thumbnail);

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
                    hardwareSettingRepo.DeleteProfile(ProfileList.get(position).getPersonalSettingsName());
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
