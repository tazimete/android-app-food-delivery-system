package com.example.smartsend.smartsendapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClientDashboardActivity extends AppCompatActivity {

  Button btnPlaceOrder;
    GoogleCloudMessaging gcm;
    String projectNumber;
    String deviceRegIdForGCM = null;
    Context ctx = this;
    private ProgressDialog pDialog;
    ConnectivityDetector connectivityDetector;
    UserLocalStore sessionManager;
    Client loggedInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_dash_board);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnPlaceOrder= (Button) findViewById(R.id.btnPlaceOrder);

        sessionManager = new UserLocalStore(ctx);
        loggedInClient = sessionManager.getLogedInClient();
        projectNumber = "1051391508793";

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Connectivity detector
        connectivityDetector = new ConnectivityDetector(getBaseContext());

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
                        registerClientDevice(deviceRegIdForGCM);
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
        }.execute(null, null, null);


        //Change button color when click
        btnPlaceOrder.setOnTouchListener(new View.OnTouchListener() {
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


        //Go to Place order activity
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goPlaceOrderActivity = new Intent(ClientDashboardActivity.this, PlaceOrderActivity.class);
                startActivity(goPlaceOrderActivity);
                finish();
            }
        });

    } //End of onCreate


    //Register Device for GCM  service
    public void registerClientDevice(String projectNumber){
        String MSG;

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/register_client_device/"+loggedInClient.getId()+"/"+deviceRegIdForGCM;

        Map<String, String> params = new HashMap<String, String>();

        JsonObjectRequest registerClientDeviceRequest = new JsonObjectRequest(Request.Method.POST,
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
        Volley.newRequestQueue(ctx).add(registerClientDeviceRequest);

    }//End of registerRiderDevice


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
