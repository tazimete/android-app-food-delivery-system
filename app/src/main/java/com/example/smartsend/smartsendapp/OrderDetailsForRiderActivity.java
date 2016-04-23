package com.example.smartsend.smartsendapp;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OrderDetailsForRiderActivity extends AppCompatActivity {

    //Declaration
    TextView tvPickupDateTime, tvPickupName, tvPickupAddress, tvDeliveryDateTime, tvDeliveryName, tvDeliveryAddress;
    Button btnAcceptOrder, btnRejectOrder;
    String clientId, outletId, outletName,outletType, pickupDateTime, mobileNumber, deliverDatetime,
        customerName, postalCode, address, unitNumber, unitNumberFirst, unitNumberLast, foodCost, receiptNumber;
    int currentRiderIndex, totalRiderIndex, uniqueNotificationId;
    Context ctx = this;
    private ProgressDialog pDialog;
    ConnectivityDetector connectivityDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_for_rider);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Connectivity detector
        connectivityDetector = new ConnectivityDetector(ctx);

        //Notification Manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        UserLocalStore userLocalStore = new UserLocalStore(this);
        Rider loggedInRider = userLocalStore.getLogedInRider();

        //Initialization
        tvPickupDateTime = (TextView) findViewById(R.id.tvPickupDateTime);
        tvPickupName = (TextView) findViewById(R.id.tvPickupName);
        tvPickupAddress = (TextView) findViewById(R.id.tvPickupAddress);
        tvDeliveryDateTime = (TextView) findViewById(R.id.tvDeliveryDateTime);
        tvDeliveryName = (TextView) findViewById(R.id.tvDeliveryName);
        tvDeliveryAddress = (TextView) findViewById(R.id.tvDeliveryAddress);

        btnAcceptOrder = (Button) findViewById(R.id.btnAcceptOrder);
        btnRejectOrder = (Button) findViewById(R.id.btnRejectOrder);

        //Change button color when click
        btnAcceptOrder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.getBackground().setAlpha(150);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getBackground().setAlpha(255);
                }
                return false;
            }
        });

        btnRejectOrder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.getBackground().setAlpha(150);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.getBackground().setAlpha(255);
                }
                return false;
            }
        });

        //Get data from intent
        Bundle iData = getIntent().getExtras();

        clientId = iData.getString("client_id");
        outletId = iData.getString("outlet_id");
        outletName = iData.getString("outlet_name");
        outletType = iData.getString("outlet_type");
        pickupDateTime = iData.getString("pickup_datetime");
        deliverDatetime = iData.getString("deliver_datetime");
        mobileNumber = iData.getString("mobile_number");
        customerName = iData.getString("customer_name");
        postalCode = iData.getString("postal_code");
        address = iData.getString("address");
        unitNumberFirst = iData.getString("unit_number_first");
        unitNumberLast = iData.getString("unit_number_last");
        unitNumber = "#"+unitNumberFirst+"-"+unitNumberLast;
        foodCost = iData.getString("food_cost");
        receiptNumber = iData.getString("receipt_number");
        currentRiderIndex = iData.getInt("current_rider_index");
        totalRiderIndex = iData.getInt("total_rider_index");
        uniqueNotificationId = iData.getInt("unique_notification_id");

        int[] riderId = new int[totalRiderIndex];
        String[] riderName = new String[totalRiderIndex];
        String[] riderLat = new String[totalRiderIndex];
        String[] riderLng = new String[totalRiderIndex];
        String[] riderGCMRegId = new String[totalRiderIndex];

        for(int i=0; i<totalRiderIndex; i++){
            riderId[i] = iData.getInt("rider_id_" + i, 0);
            riderName[i] = iData.getString("rider_name_" + i);
            riderLat[i] = iData.getString("rider_lat_" + i);
            riderLng[i] = iData.getString("rider_lng_" + i);
            riderGCMRegId[i] = iData.getString("rider_gcm_reg_id_" + i);
        }

        Toast.makeText(this, "currentRiderIndex : "+Integer.toString(currentRiderIndex), Toast.LENGTH_LONG).show();
        Toast.makeText(this, "totalRiderIndex : "+ Integer.toString(totalRiderIndex), Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "Accepted by  : "+riderId[currentRiderIndex], Toast.LENGTH_LONG).show();

        //Making hasmap
        final Map<String, String> dataForStoreOrder = new HashMap<String, String>();
        dataForStoreOrder.put("rider_id", Integer.toString(riderId[currentRiderIndex]));
        dataForStoreOrder.put("client_id", clientId);
        dataForStoreOrder.put("outlet_id", outletId);
        dataForStoreOrder.put("outlet_name", outletName);
        dataForStoreOrder.put("outlet_type", outletType);
        dataForStoreOrder.put("pickup_datetime", pickupDateTime);
        dataForStoreOrder.put("deliver_datetime", deliverDatetime);
        dataForStoreOrder.put("mobile_number", mobileNumber);
        dataForStoreOrder.put("customer_name", customerName);
        dataForStoreOrder.put("postal_code", postalCode);
        dataForStoreOrder.put("address", address);
        dataForStoreOrder.put("unit_number_first", unitNumberFirst);
        dataForStoreOrder.put("unit_number_last", unitNumberLast);
        dataForStoreOrder.put("food_cost", foodCost);
        dataForStoreOrder.put("receipt_number", receiptNumber);
        dataForStoreOrder.put("current_rider_index", Integer.toString(currentRiderIndex));
        dataForStoreOrder.put("total_rider_index", Integer.toString(totalRiderIndex));

        for(int i=0; i<totalRiderIndex; i++){
            dataForStoreOrder.put("rider_id_" + i, Integer.toString(riderId[i]));
            dataForStoreOrder.put("rider_name_" + i, riderName[i]);
            dataForStoreOrder.put("rider_lat_" + i, riderLat[i]);
            dataForStoreOrder.put("rider_lng_" + i, riderLng[i]);
            dataForStoreOrder.put("rider_gcm_reg_id_" + i, riderGCMRegId[i]);
        }


        //Set Text in Textview
        tvPickupDateTime.setText(pickupDateTime);
        tvPickupName.setText(customerName);
        tvPickupAddress.setText(address);
        tvDeliveryDateTime.setText(deliverDatetime);
        tvDeliveryName.setText(customerName);
        tvDeliveryAddress.setText(address);


        //Show message
        Toast.makeText(getApplicationContext(), "New Order From  : " + clientId, Toast.LENGTH_LONG).show();

        //Cancel notification
        notificationManager.cancel(uniqueNotificationId);

        //Store OrderData to Database when Accept button is clicked
        btnAcceptOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeOrderDataAndSendNotificationToClient(dataForStoreOrder);
               // finish();
            }
        });

        //Send notification to next rider when reject button is clicked
        btnRejectOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send no rider found notification if last rider reject the order
                if((currentRiderIndex+1) == totalRiderIndex){
                    sendFailedNotificationToClient(dataForStoreOrder);
                    //finish();
                }else {
                    sendNotificationToNextRider(dataForStoreOrder);
                    //finish();
                }
            }
        });



    } //End of OnCreate()


    //Store Order Data
    private void  storeOrderDataAndSendNotificationToClient(Map<String, String> dataForStoreOrder){
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/store_order_data_and_send_notification_to_client";

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        CustomRequest storeOrederRequest = new CustomRequest(Request.Method.POST,
                serverAddress, dataForStoreOrder , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
                hideDialog();

                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){
                        String successMessage = jObj.getString("success_message");
                        Toast.makeText(ctx, "Success:" + successMessage, Toast.LENGTH_LONG).show();

                    }else{
                        String errorMessage = jObj.getString("error_message");
                        Toast.makeText(ctx, "Error Order: "+errorMessage, Toast.LENGTH_LONG).show();
                        connectivityDetector.showAlertDialog(ctx, "Order Empty", errorMessage);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(ctx, "(SO) Json catch error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(ctx,
                        " (SO) Error Response: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
                connectivityDetector.showAlertDialog(ctx, "Try Again", "Connection Failed");
                finish();
            }
        });

        // Adding request to request queue
        Volley.newRequestQueue(ctx).add(storeOrederRequest);

    } //End of StoreData

    //Send notification to next rider
    public void sendNotificationToNextRider(Map<String, String> dataForStoreOrder){
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/send_notification_to_next_rider";

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        CustomRequest sendNotificationToNextRiderRequest = new CustomRequest(Request.Method.POST,
                serverAddress, dataForStoreOrder , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
                hideDialog();

                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){
                        String successMessage = jObj.getString("success_message");
                        Toast.makeText(ctx, "Success:" + successMessage, Toast.LENGTH_LONG).show();

                    }else{
                        String errorMessage = jObj.getString("error_message");
                        Toast.makeText(ctx, "Error Order: "+errorMessage, Toast.LENGTH_LONG).show();
                        connectivityDetector.showAlertDialog(ctx, "Order Empty", errorMessage);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(ctx, "(2SO) Json catch error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(ctx,
                        " (2SO) Error Response: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
                connectivityDetector.showAlertDialog(ctx, "Try Again", "Connection Failed");
                finish();
            }
        });

        // Adding request to request queue
        Volley.newRequestQueue(ctx).add(sendNotificationToNextRiderRequest);
    }


    //Send no rider found notification to client
    public void sendFailedNotificationToClient(Map<String, String> dataForStoreOrder){
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/send_failed_notification_to_client";

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        CustomRequest sendFailedNotificationToClientRequest = new CustomRequest(Request.Method.POST,
                serverAddress, dataForStoreOrder , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
                hideDialog();

                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){
                        String successMessage = jObj.getString("success_message");
                        Toast.makeText(ctx, "Success:" + successMessage, Toast.LENGTH_LONG).show();

                    }else{
                        String errorMessage = jObj.getString("error_message");
                        Toast.makeText(ctx, "Error Order: "+errorMessage, Toast.LENGTH_LONG).show();
                        connectivityDetector.showAlertDialog(ctx, "Order Empty", errorMessage);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(ctx, "(2SO) Json catch error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(ctx,
                        " (2SO) Error Response: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
                connectivityDetector.showAlertDialog(ctx, "Try Again", "Connection Failed");
                finish();
            }
        });

        // Adding request to request queue
        Volley.newRequestQueue(ctx).add(sendFailedNotificationToClientRequest);
    }

    //Show Diaslog
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //Hide Dialog
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
