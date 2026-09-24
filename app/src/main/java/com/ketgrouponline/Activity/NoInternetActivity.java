package com.ketgrouponline.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ketgrouponline.R;
import com.ketgrouponline.Utils.MyUtils;

public class NoInternetActivity extends AppCompatActivity {
    TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        titleTv = findViewById(R.id.titleTv);
        titleTv.setText("No Internet Connection");
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.isNetworkAvailable()) {
                    onBackPressed();
                } else {
                    Toast.makeText(NoInternetActivity.this, "Internet Connection not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.tryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.isNetworkAvailable()) {
                    onBackPressed();
                } else {
                    Toast.makeText(NoInternetActivity.this, "Internet Connection not found.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}