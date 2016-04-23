package com.example.smartsend.smartsendapp;

/**
 * Created by AGM TAZIM on 12/27/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by AGM TAZIM on 12/27/2015.
 */
public class SplashPagerAdapter extends PagerAdapter {

    private int[] images = {R.drawable.splash_image, R.drawable.splash_image, R.drawable.splash_image};
    private Context ctx;
    private LayoutInflater pagerLayoutInflater;
    View splashView;

    public SplashPagerAdapter(Context ctx){
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        pagerLayoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        splashView = pagerLayoutInflater.inflate(R.layout.layout_welcome_viewpager, container, false);
        TextView splashTitle = (TextView) splashView.findViewById(R.id.splash_title);
        TextView splashHeader = (TextView) splashView.findViewById(R.id.splash_heading);
        TextView splashSkip = (TextView) splashView.findViewById(R.id.vewPagerSkip);
        ImageView splashImageView = (ImageView) splashView.findViewById(R.id.splash_image);

        //Go login activitywhen skip is clicked
        splashSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLoginActivity();
            }
        });

        //splashImageView.setImageResource(images[position]);
        container.addView(splashView);

        return splashView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    public void goLoginActivity(){
        Intent intent = new Intent(ctx, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
        ((Activity)ctx).finish();
    }
}
