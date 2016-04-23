package com.example.smartsend.smartsendapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RiderDashboardActivity extends AppCompatActivity {

    Toolbar toolbar;
    ToggleButton tbRiderStatus;
    GoogleCloudMessaging gcm;
    String projectNumber;
    String deviceRegIdForGCM = null;
    Context ctx = this;
    private ProgressDialog pDialog;
    ConnectivityDetector connectivityDetector;
    UserLocalStore sessionManager;
    Rider loggedInRider;
    TextView tvRiderProfileLoginTime, tvRiderProfileLocation, tvRiderProfileOrder, btnProfileLoginTime,
            btnProfileDuty, btnProfileOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        tbRiderStatus = (ToggleButton) findViewById(R.id.tbRiderStatus);
        tvRiderProfileLoginTime = (TextView) findViewById(R.id.tvProfileLogin);
        tvRiderProfileLocation = (TextView) findViewById(R.id.tvProfileDuty);
        tvRiderProfileOrder = (TextView) findViewById(R.id.tvProfileOrder);
        btnProfileLoginTime = (TextView) findViewById(R.id.btnProfileLoginTime);
        btnProfileDuty = (TextView) findViewById(R.id.btnProfileDuty);
        btnProfileOrder = (TextView) findViewById(R.id.btnProfileOrder);

        sessionManager = new UserLocalStore(ctx);
        loggedInRider = sessionManager.getLogedInRider();
        projectNumber = "1051391508793";

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Connectivity detector
        connectivityDetector = new ConnectivityDetector(getBaseContext());

        //Show Current Location
        SmartSendLocationManager ssLocationManager = new SmartSendLocationManager(getApplicationContext(),60000, 10);
        SmartSendLocationListener ssLocationListener = new SmartSendLocationListener(getApplicationContext());
        ssLocationManager.setLocationManagerAndListener();

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        //Background Task for registering
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {

                try {
                    if(gcm == null){
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }


                    deviceRegIdForGCM = gcm.register(projectNumber);

                    if(!deviceRegIdForGCM.isEmpty()){
                        registerRiderDevice( deviceRegIdForGCM );
                        //Toast.makeText(getApplicationContext(), "Device Registered : "+deviceRegIdForGCM, Toast.LENGTH_LONG).show();
                    }else{
                        // Toast.makeText(getApplicationContext(), "Device Registration Failed: "+deviceRegIdForGCM, Toast.LENGTH_LONG).show();
                    }

                } catch (IOException e) {
                    //Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_LONG).show();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                hideDialog();
            }
        }.execute(null,null,null);
        //End of registering rider device

        //Change rider profile state when toggle button is clicked
        tbRiderStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setRiderStatus(1);
                    activateRiderProfile();
                }else{
                    setRiderStatus(0);
                    inactiveRiderProfile();
                }
            }
        });

    }//End of onCreate

    //Register Device for GCM  service
    public void registerRiderDevice(String projectNumber){
        String MSG;

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/register_rider_device/"+loggedInRider.getId()+"/"+deviceRegIdForGCM;

        Map<String, String> params = new HashMap<String, String>();

        JsonObjectRequest registerRiderDeviceRequest = new JsonObjectRequest(Request.Method.POST,
                serverAddress, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {

                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){

                        String successMessage = jObj.getString("success_message");
                        // return outlets;
                    }else{
                        String errorMessage = jObj.getString("error_message");
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error", "Login Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq);
        Volley.newRequestQueue(ctx).add(registerRiderDeviceRequest);

    }//End of registerRiderDevice

    //Check if rider is active or not
    public void  getRiderStatus(){
        String MSG;
        //final int[] riderStatus = new int[1];

        // Tag used to cancel the request
        final String[] tag_string_req = {"req_login"};

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/get_rider_status/"+loggedInRider.getId();

        Map<String, String> params = new HashMap<String, String>();

        JsonObjectRequest getRiderStatusRequest = new JsonObjectRequest(Request.Method.POST,
                serverAddress, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
                hideDialog();
                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){
                        String successMessage = jObj.getString("success_message");
                         int riderStatus = jObj.getInt("rider_status");

                        //Check if rider is active or inactive
                        if(riderStatus == 0){
                            inactiveRiderProfile();
                        }else if(riderStatus == 1){
                            activateRiderProfile();
                        }

                        Toast.makeText(ctx, "Success Message : "+successMessage, Toast.LENGTH_LONG).show();
                    }else{
                        String errorMessage = jObj.getString("error_message");
                        Toast.makeText(ctx, "Error Message : "+errorMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    hideDialog();
                    e.printStackTrace();
                    Toast.makeText(ctx, "JSON Exception : "+e, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(ctx, "Error Response  : "+error, Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq);
        Volley.newRequestQueue(ctx).add(getRiderStatusRequest);

       // return riderStatus[0];

    }//End of checkRiderStatus


    //Check if rider is active or not
    public void  setRiderStatus(int status){
        String MSG;

        // Tag used to cancel the request
        final String[] tag_string_req = {"req_login"};

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/set_rider_status/"+loggedInRider.getId()+"/"+status;

        Map<String, String> params = new HashMap<String, String>();

        JsonObjectRequest setRiderStatusRequest = new JsonObjectRequest(Request.Method.POST,
                serverAddress, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
                hideDialog();
                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){
                        String successMessage = jObj.getString("success_message");
                        Toast.makeText(ctx, "Success Message : "+successMessage, Toast.LENGTH_LONG).show();
                    }else{
                        String errorMessage = jObj.getString("error_message");
                        Toast.makeText(ctx, "Error Message : "+errorMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    hideDialog();
                    e.printStackTrace();
                    Toast.makeText(ctx, "JSON Exception : "+e, Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(ctx, "Error Response  : "+error, Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq);
        Volley.newRequestQueue(ctx).add(setRiderStatusRequest);

        // return riderStatus[0];

    }//End of checkRiderStatus


    //Inactive rider profile
    public void inactiveRiderProfile(){
        btnProfileLoginTime.setBackgroundResource(R.drawable.bg_profile_item_inactive);
        btnProfileLoginTime.setTextColor(Color.parseColor("#514E4D"));
        btnProfileDuty.setBackgroundResource(R.drawable.bg_profile_item_inactive);
        btnProfileDuty.setTextColor(Color.parseColor("#514E4D"));
        btnProfileOrder.setBackgroundResource(R.drawable.bg_profile_item_inactive);
        btnProfileOrder.setTextColor(Color.parseColor("#514E4D"));
        btnProfileOrder.setWidth(400);
    }

    //Active rider profile
    public void activateRiderProfile(){
        btnProfileLoginTime.setBackgroundResource(R.drawable.bg_profile_item);
        btnProfileLoginTime.setTextColor(Color.parseColor("#ffffff"));
        btnProfileDuty.setBackgroundResource(R.drawable.bg_profile_item);
        btnProfileDuty.setTextColor(Color.parseColor("#ffffff"));
        btnProfileOrder.setBackgroundResource(R.drawable.bg_profile_item);
        btnProfileOrder.setTextColor(Color.parseColor("#ffffff"));
        btnProfileOrder.setWidth(400);
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
