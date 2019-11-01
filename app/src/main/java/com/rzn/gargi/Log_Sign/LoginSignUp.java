package com.rzn.gargi.Log_Sign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.ViewPagerApapter;
import com.rzn.gargi.home.HomeActivity;

public class LoginSignUp extends AppCompatActivity {
    ViewPager mainViewPager;
    private ViewPagerApapter pagerApapter;

    TextView login , register;
    FragmentManager fragmen;
    String currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        currentUser= FirebaseAuth.getInstance().getUid();

        if(currentUser!=null)
        {
            Intent intent = new Intent(LoginSignUp.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        login =(TextView) findViewById(R.id.login);
        register =(TextView) findViewById(R.id.register);
        pagerApapter = new ViewPagerApapter(getSupportFragmentManager());
        fragmen = getSupportFragmentManager();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewPager.setCurrentItem(1);

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewPager.setCurrentItem(0);
            }
        });
        pagerApapter = new ViewPagerApapter(getSupportFragmentManager());
        mainViewPager = (ViewPager)findViewById(R.id.mainViewPager);
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                changeTabs(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mainViewPager.setAdapter(pagerApapter);
    }
    private void changeTabs(int i)
    {
        String TAG ="changeTabsFunc";

        if (i==0){
            login.setBackground(getDrawable(R.drawable.tab_selected));
            register.setBackground(getDrawable(R.drawable.tab_unselected));
            login.setTextColor(getColor(R.color.colorPrimaryDark));
            login.setTextSize(16);
            register.setTextColor(getColor(R.color.white));
            register.setTextSize(10);
        }
        if (i==1){
            register.setBackground(getDrawable(R.drawable.tab_selected));
            login.setBackground(getDrawable(R.drawable.tab_unselected));
            login.setTextColor(getColor(R.color.white));
            login.setTextSize(10);
            register.setTextColor(getColor(R.color.colorPrimaryDark));
            register.setTextSize(16);
        }



        Log.d(TAG, "changeTabs: ");
    }


}
