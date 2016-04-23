package com.example.smartsend.smartsendapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import android.view.View.*;


public class LoginActivity extends AppCompatActivity {

    //Declare variable and object
    EditText etUserEmail;
    EditText etPassword;
    RadioGroup rbgUserType;
    ImageView ivLogo;
    Drawable resizedImage;
    Button btnLoginSubmit;
    private ProgressDialog pDialog;
    String userEmail, userPassword;
    int checkedUserId;
    ConnectivityDetector connectivityDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initilize variable and object
        etUserEmail = (EditText) findViewById(R.id.etLoginUserEmail);
        etPassword = (EditText) findViewById(R.id.etLoginPassword);
        ivLogo = (ImageView) findViewById(R.id.logoLogin);
        btnLoginSubmit = (Button) findViewById(R.id.btnLoginSubmit);
        rbgUserType = (RadioGroup) findViewById(R.id.rbgLoginUserType);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //Check already logged in user
        UserLocalStore sessionManager = new UserLocalStore(this);
        if(sessionManager.loggedInUser() == "rider" && sessionManager.isRiderLoggedIn()){
            Intent goRiderProfileActivity = new Intent(LoginActivity.this, RiderDashboardActivity.class);
            startActivity(goRiderProfileActivity);
            finish();
        }else if(sessionManager.loggedInUser() == "client" && sessionManager.isClientLoggedIn()){
            Intent goClientProfileActivity = new Intent(LoginActivity.this, ClientDashboardActivity.class);
            startActivity(goClientProfileActivity);
            finish();
        }

        //Change button color when click
        btnLoginSubmit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.getBackground().setAlpha(150);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.getBackground().setAlpha(255);
                }
                return false;
            }
        });

        //Submit login form
        btnLoginSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = etUserEmail.getText().toString().trim();
                userPassword = etPassword.getText().toString().trim();

                //Login for validation
                if(userEmail.isEmpty() || userPassword.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter email and password", Toast.LENGTH_SHORT).show();
                }else{
                    //Check user type
                    checkedUserId = rbgUserType.getCheckedRadioButtonId();
                    connectivityDetector = new ConnectivityDetector(getBaseContext());

                    if(checkedUserId == R.id.rbUserRider){
                        if(connectivityDetector.checkConnectivityStatus()){
                            checkRiderLogin(userEmail, userPassword);
                            Toast.makeText(LoginActivity.this, "Rider", Toast.LENGTH_SHORT).show();
                        }else{
                            connectivityDetector.showAlertDialog(LoginActivity.this, "Login Failed","No internet connection");
                        }

                    } else if(checkedUserId == R.id.rbUserClient){
                        if(connectivityDetector.checkConnectivityStatus()){
                            checkClientLogin(userEmail, userPassword);
                            Toast.makeText(LoginActivity.this, "Client", Toast.LENGTH_SHORT).show();
                        }else{
                            connectivityDetector.showAlertDialog(LoginActivity.this, "Login Failed","No internet connection");
                        }
                    }
                } //End of else

            }// End of onClick
        });

    } //End of onCreate


    //Not used
    //Resizeing Image function
    public Drawable ResizeImage (int imageID) {
    //Get device dimensions
        Display display = getWindowManager().getDefaultDisplay();
        double deviceWidth = display.getWidth();

        BitmapDrawable bd=(BitmapDrawable) this.getResources().getDrawable(imageID);
        double imageHeight = bd.getBitmap().getHeight();
        double imageWidth = bd.getBitmap().getWidth();

        double ratio = deviceWidth / imageWidth;
        int newImageHeight = (int) (imageHeight * ratio);

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), imageID);
        Drawable drawable = new BitmapDrawable(this.getResources(),getResizedBitmap(bMap,newImageHeight,(int) deviceWidth));

        return drawable;
    }


    //Not used
    /************************ Resize Bitmap *********************************/
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

// create a matrix for the manipulation
        Matrix matrix = new Matrix();

// resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

// recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }


    //Check Rider Login
    private void checkRiderLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        String eEmail = null;
        String ePassword = null;
        try {
            eEmail = URLEncoder.encode(email, "UTF-8");
            ePassword = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/check_rider/"+eEmail+"/"+ePassword;

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest checkRiderRequest = new JsonObjectRequest(Request.Method.POST,
                serverAddress, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
               // Log.d("Login Response", "Login Response: " + response.toString());
                hideDialog();

                try {

                    //JSONObject jObj = response.getJSONObject(1);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                       // session.setLogin(true);

                        int id = jObj.getInt("id");
                        int status = jObj.getInt("status");
                        String name = jObj.getString("name");
                        String email = jObj.getString("email");
                        String password = jObj.getString("password");
                        String bikeNumber = jObj.getString("bike_number");
                        String contactNumber = jObj.getString("contact_number");
                        String createdDate = jObj.getString("created_date");
                        String profilePicture = jObj.getString("profile_picture");

                        Toast.makeText(getApplicationContext(),
                                "Rider Name : "+name,  Toast.LENGTH_LONG).show();

                        //Entering rider data to rider object
                        Rider loggedInRider = new Rider();
                        loggedInRider.setId(id);
                        loggedInRider.setEmail(email);
                        loggedInRider.setPasword(password);
                        loggedInRider.setName(name);
                        loggedInRider.setBikeNumber(bikeNumber);
                        loggedInRider.setContactNumber(contactNumber);
                        loggedInRider.setProfilePicture(profilePicture);
                        loggedInRider.setStatus(status);

                        UserLocalStore userLocalStore = new UserLocalStore(LoginActivity.this);
                        userLocalStore.storeRiderData(loggedInRider);
                        userLocalStore.setRiderLoggedIn(true);

                        // Launch main activity
                        goRiderDashboardActivity();
                    } else {
                        //int i=0;
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_message");
                        Toast.makeText(getApplicationContext(),
                               "Error Message : "+errorMsg,  Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json catch error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                       " Error Response: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq);
        Volley.newRequestQueue(this).add(checkRiderRequest);

    }


    //Check Client Login
    private void checkClientLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        String eEmail = null;
        String ePassword = null;
        try {
            eEmail = URLEncoder.encode(email, "UTF-8");
            ePassword = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String serverAddress = "http://dev.intaresta.com/smartsend/rest_controller/check_client/"+eEmail+"/"+ePassword;

        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.setCancelable(false);
        showDialog();

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest checkClientRequest = new JsonObjectRequest(Request.Method.POST,
                serverAddress, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {
                // Log.d("Login Response", "Login Response: " + response.toString());
                hideDialog();

                try {

                    //JSONObject jObj = response.getJSONObject(1);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        int id = jObj.getInt("id");
                        String email = jObj.getString("email");
                        String password = jObj.getString("password");
                        String comapnyName = jObj.getString("company_name");
                        String companyPostalCode = jObj.getString("company_postal_code");
                        String companyUnitNumber = jObj.getString("company_unit_number");
                        String location = jObj.getString("location");
                        String contactNumber = jObj.getString("contact_number");
                        String billingAddress = jObj.getString("billing_address");
                        String contactPersonName = jObj.getString("contact_person_name");
                        String contactPersonNumber = jObj.getString("contact_person_number");
                        String contactPersonEmail = jObj.getString("contact_person_email");
                        String createdDate = jObj.getString("created_date");
                        String clientType = jObj.getString("client_type");

                        Toast.makeText(getApplicationContext(),
                                "Client Name : "+comapnyName,  Toast.LENGTH_LONG).show();

                        //Entering rider data to rider object
                        Client loggedInClient = new Client();

                        loggedInClient.setId(id);
                        loggedInClient.setEmail(email);
                        loggedInClient.setPassword(password);
                        loggedInClient.setCompanyName(comapnyName);
                        loggedInClient.setCompanyPostalCode(companyPostalCode);
                        loggedInClient.setCompanyUnitNumber(companyUnitNumber);
                        loggedInClient.setLocation(location);
                        loggedInClient.setBillingAddress(billingAddress);
                        loggedInClient.setContactNumber(contactNumber);
                        loggedInClient.setContactPersonName(contactPersonName);
                        loggedInClient.setContactPersonEmail(contactPersonEmail);
                        loggedInClient.setContactPersonNumber(contactPersonNumber);
                        loggedInClient.setCreatedDate(createdDate);
                        loggedInClient.setClientType(clientType);

                        UserLocalStore userLocalStore = new UserLocalStore(LoginActivity.this);
                        userLocalStore.storeClientData(loggedInClient);
                        userLocalStore.setClientLoggedIn(true);

                        // Launch Client dahsboard  activity
                        goClientDashboardActivity();
                    } else {
                        //int i=0;
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_message");
                        Toast.makeText(getApplicationContext(),
                                "Error Message : "+errorMsg,  Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json catch error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Login Error", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        " Error Response: "+ error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq);
        Volley.newRequestQueue(this).add(checkClientRequest);

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


    //Go Rider dashboard  Activityb
    public void goRiderDashboardActivity(){
        Intent intent = new Intent(LoginActivity.this,
                RiderDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    //Go Rider dashboard  Activityb
    public void goClientDashboardActivity(){
        Intent intent = new Intent(LoginActivity.this,
                ClientDashboardActivity.class);
        startActivity(intent);
        finish();
    }

} //End of activity
