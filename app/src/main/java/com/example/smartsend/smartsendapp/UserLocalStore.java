package com.example.smartsend.smartsendapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by AGM TAZIM on 12/29/2015.
 */
public class UserLocalStore {

    //Declaration
    public SharedPreferences userDB;
    public static final String userDBName = "userData";

    //Constructor
    public  UserLocalStore(Context ctx){

        userDB = ctx.getSharedPreferences(userDBName, 0);
    }

    //Store rider data in sharedpreference
    public void storeRiderData(Rider rider){
        SharedPreferences.Editor riderSpEditor = userDB.edit();
        riderSpEditor.putInt("id", rider.getId());
        riderSpEditor.putInt("status", rider.getStatus());
        riderSpEditor.putString("email", rider.getEmail());
        riderSpEditor.putString("password", rider.getPassword());
        riderSpEditor.putString("name", rider.getName());
        riderSpEditor.putString("bikeNmuber", rider.getBikeNumber());
        riderSpEditor.putString("contactNumber", rider.getContactNumber());
        riderSpEditor.putString("profilePicture", rider.getContactNumber());
        riderSpEditor.commit();
    }

    //Store client  data in sharedpreference
    public void storeClientData(Client client){
        SharedPreferences.Editor clientSpEditor = userDB.edit();
        clientSpEditor.putInt("id", client.getId());
        clientSpEditor.putString("companyName", client.getCompanyName());
        clientSpEditor.putString("email", client.getEmail());
        clientSpEditor.putString("password", client.getPassword());
        clientSpEditor.putString("companyPostalCode", client.getCompanyPostalCode());
        clientSpEditor.putString("companyUnitNumber", client.getCompanyUnitNumber());
        clientSpEditor.putString("location", client.getLocation());
        clientSpEditor.putString("contactNumber", client.getContactNumber());
        clientSpEditor.putString("billingAddress", client.getBillingAddress());
        clientSpEditor.putString("contactPersonName", client.getContactPersonName());
        clientSpEditor.putString("contactPersonNumber", client.getContactPersonNumber());
        clientSpEditor.putString("contactPersonEmail", client.getContactPersonEmail());
        clientSpEditor.putString("createdDate", client.getCreatedDate());
        clientSpEditor.putString("clientType", client.getClientType());
        clientSpEditor.commit();
    }

    //Get rider data
    public Rider getLogedInRider (){
        String email = userDB.getString("email", "");
        String password = userDB.getString("password", "");
        int id = userDB.getInt("id", 0);
        int status = userDB.getInt("status", 0);
        String name = userDB.getString("name", "");
        String bikeNumber = userDB.getString("bikeNumber", "");
        String contactNumber = userDB.getString("contactNumber", "");
        String profilePicture = userDB.getString("profilePicture", "");

        Rider rider = new Rider(email, password);
        rider.setId(id);
        rider.setName(name);
        rider.setBikeNumber(bikeNumber);
        rider.setContactNumber(contactNumber);
        rider.setProfilePicture(profilePicture);
        rider.setStatus(status);

        return rider;
    }

    //Get client data
    public Client getLogedInClient (){
        int id = userDB.getInt("id", 0);
        String email = userDB.getString("email", "");
        String password = userDB.getString("password", "");
        String comapnyName = userDB.getString("comapnyName", "");
        String companyPostalCode = userDB.getString("companyPostalCode", "");
        String companyUnitNumber = userDB.getString("companyUnitNumber", "");
        String location = userDB.getString("location", "");
        String contactNumber = userDB.getString("contactNumber", "");
        String billingAddress = userDB.getString("billingAddress", "");
        String contactPersonName = userDB.getString("contactPersonName", "");
        String contactPersonNumber = userDB.getString("contactPersonNumber", "");
        String contactPersonEmail = userDB.getString("contactPersonEmail", "");
        String createdDate = userDB.getString("createdDate", "");
        String clientType = userDB.getString("clientType", "");

        //Create Client and put client data
        Client client = new Client();

        client.setId(id);
        client.setEmail(email);
        client.setPassword(password);
        client.setCompanyName(comapnyName);
        client.setCompanyPostalCode(companyPostalCode);
        client.setCompanyUnitNumber(companyUnitNumber);
        client.setLocation(location);
        client.setBillingAddress(billingAddress);
        client.setContactNumber(contactNumber);
        client.setContactPersonName(contactPersonName);
        client.setContactPersonEmail(contactPersonEmail);
        client.setContactPersonNumber(contactPersonNumber);
        client.setCreatedDate(createdDate);
        client.setClientType(clientType);

        return client;
    }

    //Set Login Rider
    public void setRiderLoggedIn(boolean loggedIn){
        SharedPreferences.Editor riderSpEditor = userDB.edit();
        riderSpEditor.putBoolean("loggedIn", loggedIn);
        riderSpEditor.putString("user", "rider");
        riderSpEditor.commit();
    }

    //Set Login Client
    public void setClientLoggedIn(boolean loggedIn){
        SharedPreferences.Editor clientSpEditor = userDB.edit();
        clientSpEditor.putBoolean("loggedIn", loggedIn);
        clientSpEditor.putString("user", "client");
        clientSpEditor.commit();
    }

    //get rider is logged in or not
    public boolean isRiderLoggedIn(){
        if(userDB.getBoolean("loggedIn", false)){
            return true;
        }else{
            return false;
        }
    }

    //get client is logged in or not
    public boolean isClientLoggedIn(){
        if(userDB.getBoolean("loggedIn", false)){
            return true;
        }else{
            return false;
        }
    }

    //Clear rider data
    public void clearRiderData(){
        SharedPreferences.Editor riderSpEditor = userDB.edit();
        riderSpEditor.clear();
        riderSpEditor.commit();
    }

    //Clear client data
    public void clearClientData(){
        SharedPreferences.Editor clientSpEditor = userDB.edit();
        clientSpEditor.clear();
        clientSpEditor.commit();
    }

    //Set Login Rider
    public String loggedInUser(){
        String user = userDB.getString("user", "");
        return  user;
    }
}
