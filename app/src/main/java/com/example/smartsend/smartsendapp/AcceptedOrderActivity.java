package com.example.smartsend.smartsendapp;

import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AcceptedOrderActivity extends AppCompatActivity {

    String riderName, riderContactNumber, riderId, riderImageSrc;
    int uniqueNotificationId;
    TextView tvRiderName, tvRiderContactNumber;
    ImageView ivRiderPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_rider);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Notification Manager
        NotificationManager rNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        tvRiderName = (TextView) findViewById(R.id.tvAcceptedOrderRiderName);
        tvRiderContactNumber = (TextView) findViewById(R.id.tvAcceptedOrderRiderContactNumber);
        ivRiderPicture = (ImageView) findViewById(R.id.ivAcceptedOrderRider);

        //Fetch data from notification
        Bundle iData = getIntent().getExtras();
        riderId = iData.getString("rider_id");
        riderName = iData.getString("rider_name");
        riderContactNumber = iData.getString("rider_contact_number");
        riderImageSrc = iData.getString("rider_profile_picture");
        uniqueNotificationId = iData.getInt("unique_notification_id_for_client");

        tvRiderName.setText("Rider - "+riderName);
        tvRiderContactNumber.setText("Contact No - " + riderContactNumber);
        //ivRiderPicture.setImageURI(Uri.parse(riderImageSrc));

        Toast.makeText(this, "Image src : "+riderImageSrc, Toast.LENGTH_LONG).show();

        //Cancel Notification
        rNotificationManager.cancel(uniqueNotificationId);

        //Fetch Rider Image from server
        try {

            URL myFileUrl = new URL(riderImageSrc);
            HttpURLConnection conn =
                    (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            ivRiderPicture.setImageBitmap(BitmapFactory.decodeStream(is));


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    } //ENd of onCreate

}
