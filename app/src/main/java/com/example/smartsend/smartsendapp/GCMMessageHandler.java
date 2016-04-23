package com.example.smartsend.smartsendapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by AGM TAZIM on 1/6/2016.
 */
public class GCMMessageHandler extends IntentService {

    //public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager, cNotificationManager;
    NotificationCompat.Builder builder;


    public GCMMessageHandler() {
        super("GCMMessageHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        String actionIntent = intent.getAction();
        String notificationFor = extras.getString("notification_for");

        //Check if action is receive or not
        if(actionIntent == "com.google.android.c2dm.intent.RECEIVE"){
            if(notificationFor.equals("rider")){
                //Get data from server  and show dialog
                String successMessage = extras.getString("success_message");
                String clientId = extras.getString("client_id");
                String outletId = extras.getString("outlet_id");
                String outletName = extras.getString("outlet_name");
                String outletType = extras.getString("outlet_type");
                String pickupDatetime = extras.getString("pickup_datetime");
                String deliverDatetime = extras.getString("deliver_datetime");
                String mobileNumber = extras.getString("mobile_number");
                String customerName = extras.getString("customer_name");
                String postalCode = extras.getString("postal_code");
                String address = extras.getString("address");
                String unitNumberFirst = extras.getString("unit_number_first");
                String unitNumberLast = extras.getString("unit_number_last");
                String foodCost = extras.getString("food_cost");
                String receiptNumber = extras.getString("receipt_number");

                //Getting current rider and available rider index
                int uniqueNotificationId = (int) (System.currentTimeMillis() & 0xfffffff);
                int totalRiderIndex=0, currentRiderIndex=0;

                try {
                    currentRiderIndex = Integer.parseInt(extras.getString("current_rider_index"));
                    totalRiderIndex = Integer.parseInt(extras.getString("total_rider_index"));

                }catch (Exception e){

                }

                //Getting Available  Rider List
                //  if(totalRiderIndex > 0){
                int[] riderId = new int[totalRiderIndex];
                String[] riderName = new String[totalRiderIndex];
                String[] riderLat = new String[totalRiderIndex];
                String[] riderLng = new String[totalRiderIndex];
                String[] riderGCMRegId = new String[totalRiderIndex];

                try {
                    for(int i=0; i<totalRiderIndex; i++){
                        riderId[i] = Integer.parseInt(extras.getString("rider_id_"+i));
                        riderName[i] = extras.getString("rider_name_" + i);
                        riderLat[i] = extras.getString("rider_lat_" +i);
                        riderLng[i] = extras.getString("rider_lng_" +i);
                        riderGCMRegId[i] = extras.getString("rider_gcm_reg_id_" +i);
                    }

                }catch (Exception e){

                }

                // }


                Toast.makeText(getApplicationContext(), "totalRiderIndex in Rec: "+totalRiderIndex, Toast.LENGTH_LONG).show();
                // Toast.makeText(getApplicationContext(), "New Order : "+riderId[currentRiderIndex], Toast.LENGTH_LONG).show();
                // sendNotification(notifyData);

                //if(actionIntent == "com.google.android.c2dm.intent.RECEIVE"){
                // if(){

                // }
                //Notification
                mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                PendingIntent contentIntent;
                Intent nIntent = new Intent(this, OrderDetailsForRiderActivity.class);
                // nIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                nIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                //Bind intent data
                nIntent.putExtra("success_message", successMessage);
                nIntent.putExtra("client_id", clientId);
                nIntent.putExtra("outlet_id", outletId);
                nIntent.putExtra("outlet_name", outletName);
                nIntent.putExtra("outlet_type", outletType);
                nIntent.putExtra("pickup_datetime", pickupDatetime);
                nIntent.putExtra("deliver_datetime", deliverDatetime);
                nIntent.putExtra("mobile_number", mobileNumber);
                nIntent.putExtra("customer_name", customerName);
                nIntent.putExtra("postal_code", postalCode);
                nIntent.putExtra("address", address);
                nIntent.putExtra("unit_number_first", unitNumberFirst);
                nIntent.putExtra("unit_number_last", unitNumberLast);
                nIntent.putExtra("food_cost", foodCost);
                nIntent.putExtra("receipt_number", receiptNumber);
                nIntent.putExtra("current_rider_index", (int) currentRiderIndex);
                nIntent.putExtra("total_rider_index", (int) totalRiderIndex);
                nIntent.putExtra("unique_notification_id", uniqueNotificationId);

                //Binding Rider Data
                for(int i=0; i<totalRiderIndex; i++){
                    nIntent.putExtra("rider_id_" +i, riderId[i]);
                    nIntent.putExtra("rider_name_" +i, riderName[i]);
                    nIntent.putExtra("rider_lat_" +i, riderLat[i]);
                    nIntent.putExtra("rider_lng_" +i, riderLng[i]);
                    nIntent.putExtra("rider_gcm_reg_id_" +i, riderGCMRegId[i]);
                }

                contentIntent = PendingIntent.getActivity(this, uniqueNotificationId, nIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(
                        this).setSmallIcon(R.drawable.icon_notification)
                        .setContentTitle("SmartSend Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("New Order"))
                        .setContentText("New Order").setWhen(System.currentTimeMillis());

                //Set large Icon
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_notification_white);
                mBuilder.setLargeIcon(largeIcon);

                //Set Sound of notification
                Uri nSound = RingtoneManager.getDefaultUri(Notification.DEFAULT_SOUND);
                mBuilder.setSound(nSound);
                mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000});
                mBuilder.setLights(Color.RED, 3000, 3000);

                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(uniqueNotificationId, mBuilder.build());

                //Check if notification is for client or not
            }else if(notificationFor.equals("client")){
                //Get data from server  and show dialog
                String successMessage = extras.getString("success_message");
                String accreptedRiderId = extras.getString("rider_id");
                String acceptedRiderDeviceRegId = extras.getString("rider_device_reg_id");
                String acceptedRider_name = extras.getString("rider_name");
                String acceptedRiderContactNumber = extras.getString("rider_contact_number");
                String acceptedRiderProfilePicture = extras.getString("rider_profile_picture");

                //Getting current rider and available rider index
                int uniqueNotificationIdForClient = (int) (System.currentTimeMillis() & 0xfffffff);

                Toast.makeText(getApplicationContext(), "Accepted Rider ID : "+accreptedRiderId, Toast.LENGTH_LONG).show();

                //Notification
                cNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                PendingIntent contentIntentForClient;
                Intent cIntent = new Intent(this, AcceptedOrderActivity.class);
                // cIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                cIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                //Bind intent data
                cIntent.putExtra("success_message", successMessage);
                cIntent.putExtra("rider_id", accreptedRiderId);
                cIntent.putExtra("rider_name", acceptedRider_name);
                cIntent.putExtra("rider_contact_number", acceptedRiderContactNumber);
                cIntent.putExtra("rider_profile_picture", acceptedRiderProfilePicture);
                cIntent.putExtra("unique_notification_id_for_client", uniqueNotificationIdForClient);

                contentIntentForClient = PendingIntent.getActivity(this, uniqueNotificationIdForClient, cIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder cBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(
                        this).setSmallIcon(R.drawable.icon_notification)
                        .setContentTitle("SmartSend Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Order Accepted"))
                        .setContentText("Your order has been accepted just now").setWhen(System.currentTimeMillis());

                //Set large Icon
                Bitmap cLargeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_notification_white);
                cBuilder.setLargeIcon(cLargeIcon);

                //Set Sound of notification
                Uri cSound = RingtoneManager.getDefaultUri(Notification.DEFAULT_SOUND);
                cBuilder.setSound(cSound);
                cBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000});
                cBuilder.setLights(Color.RED, 3000, 3000);

                cBuilder.setContentIntent(contentIntentForClient);
                cNotificationManager.notify(uniqueNotificationIdForClient, cBuilder.build());

                //Check if this  for failed notification or not
            }else if(notificationFor.equals("no_rider")){
                String message = extras.getString("message");

                //Getting current rider and available rider index
                int uniqueNotificationIdForClient = (int) (System.currentTimeMillis() & 0xfffffff);

                Toast.makeText(getApplicationContext(), "Message : "+message, Toast.LENGTH_LONG).show();

                //Notification
                cNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                PendingIntent contentIntentForClient;
                Intent cIntent = new Intent(this, OrderFailedActivity.class);
                // cIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                cIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                //Bind intent data
                cIntent.putExtra("message", message);
                cIntent.putExtra("unique_notification_id_for_client", uniqueNotificationIdForClient);

                contentIntentForClient = PendingIntent.getActivity(this, uniqueNotificationIdForClient, cIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder cBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(
                        this).setSmallIcon(R.drawable.icon_notification)
                        .setContentTitle("SmartSend Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("No rider found"))
                        .setContentText("All rider are busy now. Please try again.").setWhen(System.currentTimeMillis());

                //Set large Icon
                Bitmap cLargeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_notification_white);
                cBuilder.setLargeIcon(cLargeIcon);

                //Set Sound of notification
                Uri cSound = RingtoneManager.getDefaultUri(Notification.DEFAULT_SOUND);
                cBuilder.setSound(cSound);
                cBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000});
                cBuilder.setLights(Color.RED, 3000, 3000);

                cBuilder.setContentIntent(contentIntentForClient);
                cNotificationManager.notify(uniqueNotificationIdForClient, cBuilder.build());
            }

            GcmReceiver.completeWakefulIntent(intent);

        } //End of if

       // GcmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {

        Log.d("Notification To Rider", "Notification sent successfully.");
    }
}

