package com.example.smartsend.smartsendapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Button btnOrder;
    Spinner spOutlet;
    Context ctx = this;
    private ProgressDialog pDialog;
    ConnectivityDetector connectivityDetector;
    Outlet selectedOutlet;
    EditText etPickupDateTime, etDeliverDateTime, etMobileNumber, etCustomerName, etPostalCode, etAddress, etUnitNumberFirst, etUnitNumberLast, etFoodCost, etReceiptNumber;
    String pickupDateTime, deliverDateTime, mobileNumber, customerName, postalCode, address, unitNumberFirst, unitNumberLast, foodCost, receiptNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        btnOrder = (Button) findViewById(R.id.btnOrder);
        spOutlet = (Spinner) findViewById(R.id.spOutlet);

        etPickupDateTime = (EditText) findViewById(R.id.etPickupDateAndTime);
        etDeliverDateTime = (EditText) findViewById(R.id.etDeliverDateTime);
        etMobileNumber = (EditText) findViewById(R.id.etMobileNumber);
        etCustomerName = (EditText) findViewById(R.id.etCustomerName);
        etPostalCode = (EditText) findViewById(R.id.etPostalCode);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etUnitNumberFirst = (EditText) findViewById(R.id.etUnitNoFirst);
        etUnitNumberLast = (EditText) findViewById(R.id.etUnitNumberLast);
        etFoodCost = (EditText) findViewById(R.id.etFoodCost);
        etReceiptNumber = (EditText) findViewById(R.id.etReceiptNumber);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Connectivity detector
        connectivityDetector = new ConnectivityDetector(ctx);

        UserLocalStore userLocalStore = new UserLocalStore(this);
        final Client loggedInClient = userLocalStore.getLogedInClient();
        final int loggedInClientId = loggedInClient.getId();
        final int maxLengthOfPostCode = 6;

        //Loading outlets
        getOutletsbyClientId(loggedInClientId,  spOutlet);
        spOutlet.setOnItemSelectedListener(this);

        //Change button color when click
        btnOrder.setOnTouchListener(new View.OnTouchListener() {
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

        //Validation after btnOrder is clicked
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialiaze the string
                pickupDateTime = etPickupDateTime.getText().toString().trim();
                deliverDateTime = etDeliverDateTime.getText().toString().trim();
                mobileNumber = etMobileNumber.getText().toString().trim();
                customerName = etCustomerName.getText().toString().trim();
                postalCode = etPostalCode.getText().toString().trim();
                address = etAddress.getText().toString().trim();
                unitNumberFirst = etUnitNumberFirst.getText().toString().trim();
                unitNumberLast = etUnitNumberFirst.getText().toString().trim();
                foodCost = etFoodCost.getText().toString().trim();
                receiptNumber = etReceiptNumber.getText().toString().trim();


                if(pickupDateTime.isEmpty() || deliverDateTime.isEmpty() || mobileNumber.isEmpty() || mobileNumber.isEmpty() || customerName.isEmpty() ||
                        postalCode.isEmpty() || address.isEmpty() || unitNumberFirst.isEmpty() || unitNumberLast.isEmpty() || foodCost.isEmpty()){

                    Toast.makeText(PlaceOrderActivity.this, "Please fill-up  all fields", Toast.LENGTH_SHORT).show();

                }else{
                    sendOrderToRider(loggedInClient, selectedOutlet, pickupDateTime, deliverDateTime, mobileNumber, customerName, postalCode,
                           address, unitNumberFirst, unitNumberLast, foodCost, receiptNumber);
                    //sendOrderToRider();
                }
            }
        });

        //Get address by postcode
        etPostalCode.setFocusable(true);

        //Get address by postcode
        etPostalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence pCode, int start, int before, int count) {
                postalCode = etPostalCode.getText().toString().trim();

                if( pCode.length() == maxLengthOfPostCode){
                    getAddressByPostalCode(postalCode);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Fteching Outlet of this client


    }

    //Get outlets by client id
    private void getOutletsbyClientId(final int id, final Spinner spOutlet) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        UserLocalStore userLocalStore = new UserLocalStore(ctx);
        Client loggedInClient = userLocalStore.getLogedInClient();
        final int loggedInClientId = loggedInClient.getId();
        final String loggedInClientName = loggedInClient.getCompanyName();

        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/get_all_outlet_by_client_id/"+loggedInClientId;

        Toast.makeText(ctx,"Client ID : "+loggedInClientId, Toast.LENGTH_LONG).show();

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        Map<String, String> params = new HashMap<String, String>();

        JsonObjectRequest fetchOutletsRequest = new JsonObjectRequest(Request.Method.POST,
                serverAddress, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
                // Log.d("Login Response", "Login Response: " + response.toString());
                hideDialog();

                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){
                        int JSONLength = jObj.getInt("length");
                       ArrayList<Outlet> outlets = new ArrayList<Outlet>();
                        //ArrayList<String> outlets = new ArrayList<String>();
                       // outlets.add(0, new Outlet(loggedInClientId, loggedInClientName, 1));
                        Toast.makeText(ctx, "Json Length :" + JSONLength, Toast.LENGTH_LONG).show();

                        for (int i=0; i<JSONLength; i++) {
                            outlets.add(i, new Outlet(jObj.getInt("outlet_id_"+i), jObj.getString("outlet_" + i), jObj.getInt("outlet_type_"+i)));
                        }

                        //for (int i=0; i<JSONLength; i++) {
                       //     outlets.add(jObj.getString("outlet_" + i));
                       // }

                        //Binding outlets to spinner
                        ArrayAdapter outletAdapter = new ArrayAdapter(getApplicationContext(), R.layout.layout_outlet_spinner, outlets);
                        spOutlet.setAdapter(outletAdapter);

                        String successMessage = jObj.getString("success_message");
                        Toast.makeText(ctx, "Success:" + successMessage, Toast.LENGTH_LONG).show();
                       // return outlets;
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
                hideDialog();
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq);
        Volley.newRequestQueue(ctx).add(fetchOutletsRequest);

    }

    //Get address by postal code and check distance between outlet and deliver address
    public void getAddressByPostalCode(String postalCode){
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/get_address_by_postal_code/"+postalCode+"/"+selectedOutlet.getId()+"/"+selectedOutlet.getType();

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        Map<String, String> params = new HashMap<String, String>();

        JsonObjectRequest fetchAddressByPostalCodeRequest = new JsonObjectRequest(Request.Method.POST,
                serverAddress, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
                // Log.d("Login Response", "Login Response: " + response.toString());
                hideDialog();

                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){

                        String successMessage = jObj.getString("success_message");
                        Toast.makeText(ctx, "Success:" + successMessage, Toast.LENGTH_LONG).show();

                        String zip_bulding_no = jObj.getString("zip_bulding_no");
                        String zip_bulding_name = jObj.getString("zip_bulding_name");
                        String zip_street_name = jObj.getString("zip_street_name");
                        String zip_code = jObj.getString("zip_code");

                        String address = genrateAddress(zip_bulding_no, zip_bulding_name, zip_street_name, zip_code, "", "");

                        //Set gererated address to address field
                        etAddress.setText(address);

                        // return outlets;
                    }else{
                        String errorMessage = jObj.getString("error_message");
                        Toast.makeText(ctx, "Error Fetching Address: "+errorMessage, Toast.LENGTH_LONG).show();
                        connectivityDetector.showAlertDialog(ctx, "Invalid postcode", errorMessage);
                        etPostalCode.setText("");
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(ctx, "(FA) Json catch error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    etPostalCode.setText("");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(ctx,
                        " (FA) Error Response: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
                connectivityDetector.showAlertDialog(ctx, "Try Again", "Connection Failed");
                etPostalCode.setText("");
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq);
        Volley.newRequestQueue(ctx).add(fetchAddressByPostalCodeRequest);
    }


    //Send order to rider
    //public void sendOrderToRider(){
    public void sendOrderToRider(Client loggedInClient, Outlet outlet, String npickupDateTime, String ndeliverDateTime, String nmobileNumber, String ncustomerName,
                                String npostalCode, String naddress, String nunitNumberFirst, String nunitNumberLast, String nfoodCost, String nreceiptNumber){
        // Tag used to cancel the request
        String tag_string_req = "req_login";

       // String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/send_order_to_rider/"+ outlet.getId()+"/"+outlet.getName()+"/"+outlet.getType()+"/"+npickupDateTime+"/"+ndeliverDateTime+"/"+nmobileNumber+"/"+ncustomerName+"/"+npostalCode+"/"+naddress+"/"+nunitNumberFirst+"/"+nunitNumberLast+"/"+nfoodCost+"/"+nreceiptNumber;
        //String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/send_order_to_rider_2";
        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/send_order_to_rider";

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        Toast.makeText(ctx, "Client ID: "+loggedInClient.getId(), Toast.LENGTH_LONG).show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("client_id", Integer.toString(loggedInClient.getId()));
        params.put("outlet_id", Integer.toString(outlet.getId()));
        params.put("outlet_name", outlet.getName());
        params.put("outlet_type", Integer.toString(outlet.getType()));
        params.put("pickup_datetime", npickupDateTime);
        params.put("deliver_datetime", ndeliverDateTime);
        params.put("mobile_number", nmobileNumber);
        params.put("customer_name", ncustomerName);
        params.put("postal_code", npostalCode);
        params.put("address", naddress);
        params.put("unit_number_first", nunitNumberFirst);
        params.put("unit_number_last", unitNumberLast);
        params.put("food_cost", nfoodCost);
        params.put("receipt_number", nreceiptNumber);

        //String jsonStringValue = params.toString();

        CustomRequest sendOrederToRiderRequest = new CustomRequest(Request.Method.POST,
                serverAddress, params , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
                // Log.d("Login Response", "Login Response: " + response.toString());
                hideDialog();

                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){

                        String successMessage = jObj.getString("success_message");
                        String acceptedRiderName = jObj.getString("rider_name_0");
                        Toast.makeText(ctx, "Success:" + successMessage+"--"+acceptedRiderName, Toast.LENGTH_LONG).show();

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

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(ctx,
                        " (SO) Error Response: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
                connectivityDetector.showAlertDialog(ctx, "Try Again", "Connection Failed");
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq);
        Volley.newRequestQueue(ctx).add(sendOrederToRiderRequest);
    }


    //Generate full address
    public String  genrateAddress(String zipBuldingNo, String zipBuldingName, String zipStreetName, String zipCode,
                                  String unitNoFirst, String unitNoLast){

        String address = zipBuldingNo+" ";
        String unitNumber = "";

        if(!zipBuldingName.isEmpty()){
            address = address + '('+zipBuldingName+')';
        }

        address = address + ", ";
        address = address + zipStreetName +", ";

        if(!unitNoFirst.isEmpty()){
            unitNumber = unitNoFirst;
        }

        if(!unitNoLast.isEmpty()){
            unitNumber = unitNumber+"-"+unitNoLast;
        }

        if(!unitNumber.isEmpty()){
            address = address + '#' + unitNumber +",  ";
        }

        address = address + "Singapore-" + zipCode;
        return address;
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

    //Spinner Listener
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedOutlet = (Outlet) parent.getItemAtPosition(position);
        Toast.makeText(ctx, "Selected Item Name : "+selectedOutlet.getName()+"\n Selected ID : "+selectedOutlet.getId(), Toast.LENGTH_LONG).show();

        etPostalCode.setText("");
       // String updatedPostCode = etPostalCode.getText().toString().trim();
        //if(updatedPostCode.length() == 6){
        //    getAddressByPostalCode(updatedPostCode);
        //}

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
