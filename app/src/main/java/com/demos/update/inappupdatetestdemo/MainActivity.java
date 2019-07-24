package com.demos.update.inappupdatetestdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;
import static com.google.android.play.core.install.model.UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS;
import static com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_FLEXIBLE_UPDATE = 17362;

    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appUpdateManager = AppUpdateManagerFactory.create(this);

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE)) {   //  check for the type of update flow you want
                    requestUpdate(appUpdateInfo);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                MainActivity.this,
                                REQUEST_CODE_FLEXIBLE_UPDATE
                        );
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    FLEXIBLE, //  HERE specify the type of update flow you want
                    this,   //  the instance of an activity
                    REQUEST_CODE_FLEXIBLE_UPDATE
            );
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FLEXIBLE_UPDATE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // User approved the update
                    break;

                case Activity.RESULT_CANCELED:
                    // User denied the update
                    break;

                case ActivityResult.RESULT_IN_APP_UPDATE_FAILED:
                    // Update failed
                    break;
            }
        }
    }
}
