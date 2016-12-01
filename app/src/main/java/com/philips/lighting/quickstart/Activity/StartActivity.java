package com.philips.lighting.quickstart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.lighting.quickstart.DataClass.Database.DBHelper;
import com.philips.lighting.quickstart.R;
import com.produvia.sdk.WeaverSdk;

import org.json.JSONObject;

import java.io.File;

import produvia.com.weaverandroidsdk.WeaverSdkApi;

public class StartActivity extends Activity  implements WeaverSdk.WeaverSdkCallback{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //initialize weaver sdk -
        // - enter your Weaver API key and call WeaverSdkApi.init
        // - if you don't have an API key - you can get one at:
        //       http://weavingthings.com
        final String API_KEY = getString(R.string.API_KEY);
        WeaverSdkApi.init(this, API_KEY, getApplicationContext());



        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
        // Listening to activity_register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

        Button login_button = (Button)findViewById(R.id.btnLogin);
        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });
    }


    /****************************************************************************************
     * User login
     ****************************************************************************************/
    public void login() {
        String mUserEmail;
        String mUserPassword;

        EditText userEmailField = (EditText) findViewById(R.id.email);
        mUserEmail = userEmailField.getText().toString();
        EditText userPasswordField = (EditText) findViewById(R.id.password);
        mUserPassword = userPasswordField.getText().toString();

        if (mUserEmail.length() == 0 || mUserPassword.length() == 0) {
            // input fields are empty
            Toast.makeText(StartActivity.this, "Please complete all the fields", Toast.LENGTH_LONG).show();
            return;
        } else {
            WeaverSdkApi.userLogin(this, mUserEmail, mUserPassword);

        }
    }

    @Override
    public void onTaskCompleted(final int flag, final JSONObject data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = data;

                    if (json.getBoolean("success")) {

                        /**************************************************************************
                         * The user has been logged in - let's move on to the lights list activity
                         **************************************************************************/
                        Intent intent = new Intent(StartActivity.this, PHHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        /**************************************************************************
                         * Sing in wasn't successful:
                         **************************************************************************/
                        if(json.has("info"))
                            Toast.makeText(StartActivity.this, json.getString("info"), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onTaskUpdate(int i, JSONObject jsonObject) {

    }

}




       /* File database = getApplicationContext().getDatabasePath("profileDataBase.db");

        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            final Button SearchForBridge = (Button) findViewById(R.id.btnLogin);
            SearchForBridge.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(StartActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            //Database found
            Intent intent = new Intent(StartActivity.this, PHHomeActivity.class);
            startActivity(intent);
            finish();
        }*/