package com.demos.update.inappupdatetestdemo;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class FlexibleUpdateActivity extends AppCompatActivity implements InstallStateUpdatedListener {

    private static final int REQUEST_CODE_FLEXI_UPDATE = 17363;
    private AppUpdateManager appUpdateManager;

    private MutableLiveData<Boolean> updateAvailable = new MutableLiveData<>();
    private AppUpdateInfo updateInfo = null;

    private TextView textViewAppVersion;

    private Snackbar snackBarUpdateAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flexible_update);

        setupViews();

        updateAvailable.setValue(false);

        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(this);

        setAppUpdateInfoListener();

        updateAvailable.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                snackBarUpdateAvailable.show();
            } else {
                snackBarUpdateAvailable.dismiss();
            }
        });

        checkForUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showToast("Download complete", Toast.LENGTH_LONG);
                showRestartSnackbar();
            } else {
                showToast("Update not downloaded. Status: " + appUpdateInfo.installStatus(), Toast.LENGTH_LONG);
                checkForUpdate();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appUpdateManager.unregisterListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FLEXI_UPDATE) {
            if (resultCode != Activity.RESULT_OK) {
                showToast("OnActivityResult RESULT NOT OK", Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onStateUpdate(InstallState installState) {
        // After the update is downloaded, show a notification
        // and request user confirmation to restart the app.
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            showToast("Download complete", Toast.LENGTH_LONG);
            showRestartSnackbar();
        }
    }

    private void setupViews() {
        textViewAppVersion = findViewById(R.id.appVersionTextView);
        textViewAppVersion.setText(String.valueOf(BuildConfig.VERSION_CODE));

        /*buttonCheckUpdate = findViewById(R.id.checkUpdateButton);
        buttonCheckUpdate.setOnClickListener(view -> {
            if (updateAvailable.getValue()) {
                startForInAppUpdate(updateInfo);
            }
        });*/

        snackBarUpdateAvailable = Snackbar
                .make(textViewAppVersion, R.string.update_available, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_update, view -> startForInAppUpdate(updateInfo));
    }

    private void setAppUpdateInfoListener() {
        // Add OnComplete Listener
        appUpdateManager.getAppUpdateInfo().addOnCompleteListener(task ->
                showToast("GET Info complete", Toast.LENGTH_SHORT));

        // Add OnFailure Listener
        appUpdateManager.getAppUpdateInfo().addOnFailureListener(e ->
                showToast("GET Info failed" + e.getMessage(), Toast.LENGTH_LONG));
    }

    private void checkForUpdate() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                updateInfo = appUpdateInfo;
                updateAvailable.setValue(true);
                showToast("New version available: " + appUpdateInfo.availableVersionCode(), Toast.LENGTH_LONG);
            } else {
                updateAvailable.setValue(false);
                showToast("Update not available", Toast.LENGTH_LONG);
            }
        });
    }

    private void startForInAppUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo, AppUpdateType.FLEXIBLE, this, REQUEST_CODE_FLEXI_UPDATE
            );
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String message, int length) {
        Toast.makeText(this, message, length).show();
    }

    private void showRestartSnackbar() {
        Snackbar
                .make(textViewAppVersion, R.string.restart_to_update, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_restart, view -> appUpdateManager.completeUpdate())
                .show();
    }
}
