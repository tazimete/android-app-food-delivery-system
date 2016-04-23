package com.example.smartsend.smartsendapp;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by AGM TAZIM on 1/3/2016.
 */
public class SmartSendLocationManager {

    private int minDistanceForUpdateLocation;
    private int minTimeForUpdateLocastion;
    LocationManager locationManager;
    SmartSendLocationListener locationListener;
    Context ctx;
    private boolean isGPSProviderEnable, isNetworkProviderEnable;

    //COnstructor
    public SmartSendLocationManager(Context ctx){
        this.ctx = ctx;
    }

    public SmartSendLocationManager(Context ctx, int minTimeForUpdateLocastion, int minDistanceForUpdateLocation ){
        this.minDistanceForUpdateLocation =minDistanceForUpdateLocation;
        this.minTimeForUpdateLocastion= minTimeForUpdateLocastion;
        this.ctx = ctx;
    }

    //Set Location Manager and Listener
    public void setLocationManagerAndListener(){
        this.locationListener = new SmartSendLocationListener(this.ctx);
        this.locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        isGPSProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkProviderEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(isGPSProviderEnable){
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeForUpdateLocastion, minDistanceForUpdateLocation, locationListener);
        }else if(isNetworkProviderEnable){
            this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeForUpdateLocastion, minDistanceForUpdateLocation, locationListener);
        }

    }

    //Get Minimum Distance for updating location
    public int getMinDistanceForUpdateLocation() {
        return minDistanceForUpdateLocation;
    }


    //Set Minimum Distance for updating location
    public void setMinDistanceForUpdateLocation(int minDistanceForUpdateLocation) {
        this.minDistanceForUpdateLocation = minDistanceForUpdateLocation;
    }

    //Get Minimum Time for updating location
    public int getMinTimeForUpdateLocastion() {
        return minTimeForUpdateLocastion;
    }

    //Set Minimum Time for updating location
    public void setMinTimeForUpdateLocastion(int minTimeForUpdateLocastion) {
        this.minTimeForUpdateLocastion = minTimeForUpdateLocastion;
    }
}
