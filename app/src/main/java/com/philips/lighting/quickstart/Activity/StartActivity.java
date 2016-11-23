package com.philips.lighting.quickstart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.philips.lighting.quickstart.DataClass.Database.DBHelper;
import com.philips.lighting.quickstart.R;

import java.io.File;

public class StartActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        File database = getApplicationContext().getDatabasePath("profileDataBase.db");

        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            final Button SearchForBridge = (Button) findViewById(R.id.StartSearchButton);
            SearchForBridge.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(StartActivity.this, PHHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            //Database found
            Intent intent = new Intent(StartActivity.this, PHHomeActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
