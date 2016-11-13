package com.philips.lighting.quickstart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.philips.lighting.quickstart.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Button SearchForBridge = (Button) findViewById(R.id.StartSearchButton);
        SearchForBridge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, PHHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
