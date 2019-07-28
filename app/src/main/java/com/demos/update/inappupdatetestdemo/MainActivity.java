package com.demos.update.inappupdatetestdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_immediate_update).setOnClickListener(this);
        findViewById(R.id.main_flexi_update).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_immediate_update:
                startActivity(new Intent(this, ImmediateUpdateActivity.class));
                break;

            case R.id.main_flexi_update:
                startActivity(new Intent(this, FlexibleUpdateActivity.class));
                break;

            default:
                // Not used currently
        }
    }
}