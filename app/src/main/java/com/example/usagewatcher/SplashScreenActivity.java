package com.example.usagewatcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        boolean has_all_permissions = Permissions.hasDialogPermissions(SplashScreenActivity.this) && Permissions.hasAppUsagePermission(SplashScreenActivity.this);

        // check for permissions
        if (has_all_permissions) {
            callMainActivity();
        } else {
            // if not there, request them (only the popup permissions for now)
            Permissions.requestDialogPermissions(SplashScreenActivity.this);
        }

        Button button_grant_permissions = findViewById(R.id.grant_permissions);
        button_grant_permissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Permissions.requestDialogPermissions(SplashScreenActivity.this);
            }
        });
    }

    public void callMainActivity() {
        Intent main_activity = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(main_activity);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (!Permissions.hasAppUsagePermission(SplashScreenActivity.this)) {
            Permissions.requestAppUsagePermission(SplashScreenActivity.this);

            /* Since this is an asynchronous permission request, code moves ahead and the
            "grant permissions" button is displayed, even if the user gives permission
            and comes back to the screen. Refreshing the screen fixes that problem.
            (But the refresh should happen before the permission is given, so I don't
            really know why this works lol.) */
            finish();
            startActivity(getIntent());
        }

        boolean has_all_permissions = Permissions.hasDialogPermissions(SplashScreenActivity.this) && Permissions.hasAppUsagePermission(SplashScreenActivity.this);

        if (has_all_permissions) {
            callMainActivity();
        } else {
            Button button_grant_permissions = findViewById(R.id.grant_permissions);
            button_grant_permissions.findViewById(R.id.grant_permissions).setVisibility(View.VISIBLE);
        }
    }

}