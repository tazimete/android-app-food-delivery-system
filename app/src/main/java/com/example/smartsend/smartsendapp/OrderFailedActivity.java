package com.example.smartsend.smartsendapp;

import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class OrderFailedActivity extends AppCompatActivity {

    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_failed);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnOk = (Button) findViewById(R.id.btnOk);

        //Notification Manager
        NotificationManager rNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Bundle iData = getIntent().getExtras();
        int uniqueNotificationId = iData.getInt("unique_notification_id_for_client");
        //Cancel Notification
        rNotificationManager.cancel(uniqueNotificationId);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
