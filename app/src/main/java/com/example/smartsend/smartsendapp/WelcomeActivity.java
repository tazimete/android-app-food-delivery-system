package com.example.smartsend.smartsendapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    //Decalre variable and object
    ViewPager welcomeViewPager;
    SplashPagerAdapter welcomeViewPagerAdapter;
    Context ctx;
    //TextView tvSkipPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialize variable
       // tvSkipPager = (TextView) findViewById(R.id.vewPagerSkip);
        welcomeViewPager =(ViewPager) findViewById(R.id.welcome_view_pager);
        welcomeViewPagerAdapter = new SplashPagerAdapter(this);
        welcomeViewPager.setAdapter(welcomeViewPagerAdapter);

        //
        ctx = this;
        final SharedPreferences prefs = ctx.getSharedPreferences(
                WelcomeActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString("regId", "");
        Toast.makeText(ctx, "REG ID :"+registrationId, Toast.LENGTH_LONG).show();


        ViewPager.OnPageChangeListener listenerWelcomeViewPager = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int pageNumber) {
                // TODO Auto-generated method stub
               if(pageNumber == 2){
                   goLoginActivity();
               }

            }

            boolean callHappened;
            @Override
            public void onPageScrolled(int pageScrolledOn, float positionOffset, int positionOffsetPixels) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        };
        welcomeViewPager.setOnPageChangeListener(listenerWelcomeViewPager);

        //Set Click listener skip button
    }

    public void goLoginActivity(){
        Intent intent = new Intent(WelcomeActivity.this,
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
