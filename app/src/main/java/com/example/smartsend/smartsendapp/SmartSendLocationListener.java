package com.example.smartsend.smartsendapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AGM TAZIM on 1/3/2016.
 */
public class SmartSendLocationListener implements LocationListener {

    private double lat, lng, alt, acc, time;
    Context ctx;
    ConnectivityDetector connectivityDetector;

    public SmartSendLocationListener(Context ctx){
        this.ctx = ctx;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
        this.alt = location.getAltitude();
        this.acc = location.getAccuracy();
        this.time = location.getTime();

        Toast.makeText(ctx, "Lat : "+this.lat+" \n Lng : "+this.lng+" \nAlt : "+this.alt+" \n Acc : "+this.acc+" \n Time : "+this.time, Toast.LENGTH_LONG).show();

        changeRiderLocation(this.lat, this.lng);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    //Change rider location
    private void changeRiderLocation(final double lat, final double lng) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        UserLocalStore userLocalStore = new UserLocalStore(ctx);
        Rider loggedInRider = userLocalStore.getLogedInRider();
        int loggedInRiderId = loggedInRider.getId();

        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/change_rider_location/"+loggedInRiderId+"/"+lat+"/"+lng;

        Map<String, String> params = new HashMap<String, String>();

        JsonObjectRequest changeRiderLocationRequest = new JsonObjectRequest(Request.Method.POST,
                serverAddress, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
                // Log.d("Login Response", "Login Response: " + response.toString());

                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){
                        String successMessage = jObj.getString("success_message");
                        Toast.makeText(ctx, "Success:"+successMessage, Toast.LENGTH_LONG).show();
                    }else{
                        String errorMessage = jObj.getString("error_message");
                        Toast.makeText(ctx, "Error: "+errorMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(ctx, "(RL) Json catch error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(ctx,
                        " (RL) Error Response: "+ error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq);
        Volley.newRequestQueue(ctx).add(changeRiderLocationRequest);

    }
}
