package com.rzn.gargi.topface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.rzn.gargi.R;
import com.rzn.gargi.chat.ChatActivity;
import com.rzn.gargi.helper.TopFaceFragmentPagerAdapter;
import com.rzn.gargi.helper.bottomNavigationHelper;
import com.rzn.gargi.home.HomeActivity;
import com.rzn.gargi.profile.ProfileActivity;

import q.rorbin.badgeview.QBadgeView;

public class TopFaceActivity extends AppCompatActivity {

    ViewPager mainViewPager;
    ImageView man , woman,rates,views;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUser=auth.getUid();
    String gender;
    private TopFaceFragmentPagerAdapter pagerApapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_face);
        gender=getIntent().getStringExtra("gender");
        if (gender==null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("allUser").document(currentUser);
            ref.get().addOnSuccessListener(TopFaceActivity.this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot!=null){
                        String gender = documentSnapshot.getString("gender");
                        setNavigation(gender);

                    }
                }
            });

        }else{
            setNavigation(gender);
        }

        man =(ImageView) findViewById(R.id.man);
        woman =(ImageView) findViewById(R.id.woman);
        rates =(ImageView) findViewById(R.id.rates);
         views =(ImageView) findViewById(R.id.views);

        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewPager.setCurrentItem(1);

            }
        });
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewPager.setCurrentItem(0);

            }
        });
      rates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewPager.setCurrentItem(2);

            }
        });
        views.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewPager.setCurrentItem(3);

            }
        });
        pagerApapter = new TopFaceFragmentPagerAdapter(getSupportFragmentManager());
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



    private void setNavigation(final String gender){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);

        bottomNavigationHelper.enableNavigation(TopFaceActivity.this,view,gender);
        Menu menu = view.getMenu();
        MenuItem item = menu.getItem(1);
        item.setChecked(true);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final View v = bottomNavigationMenuView.getChildAt(2); // number of menu from left

        final View v1 = bottomNavigationMenuView.getChildAt(0); // number of menu from left
        final View v2 = bottomNavigationMenuView.getChildAt(1); // number of menu from left
        final View v3 = bottomNavigationMenuView.getChildAt(2); // number of menu from left
        final View v4 = bottomNavigationMenuView.getChildAt(3); // number of menu from left
        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TopFaceActivity.this,HomeActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);

                overridePendingTransition(0,0);


            }
        });
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TopFaceActivity.this, ChatActivity.class);
                i.putExtra("gender",gender);
                startActivity(i);


                overridePendingTransition(0, 0);

            }
        });
        v4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TopFaceActivity.this, ProfileActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);
                overridePendingTransition(0, 0);


            }
        });


    }
    private void setBadgeCount(){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final QBadgeView badge = new QBadgeView(TopFaceActivity.this);
        final View v = bottomNavigationMenuView.getChildAt(2);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("badgeCount").document(auth.getUid()).addSnapshotListener(TopFaceActivity.this, MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot!=null){
                    if (documentSnapshot.getData()!=null){
                        badge.bindTarget(v).setBadgeTextSize(14,true).setBadgePadding(7,true)
                                .setBadgeBackgroundColor(Color.RED).setBadgeNumber(documentSnapshot.getData().size());
                        Log.d("badgeCount->>", "onEvent: "+documentSnapshot.getData().size());
                    }
                }
                else
                    badge.hide(true);

            }
        });
    }

    private void changeTabs(int i)
    {
        String TAG ="changeTabsFunc";

        if (i==0){
            man.setBackground(getDrawable(R.drawable.tab_selected));
            woman.setBackground(getDrawable(R.drawable.tab_unselected));
            man.setImageResource(R.drawable.man_selected);
            woman.setImageResource(R.drawable.woman_unselected);
              views.setImageResource(R.drawable.seen_white);
             rates.setImageResource(R.drawable.topface_white);


        }
        if (i==1){
            woman.setBackground(getDrawable(R.drawable.tab_selected));
            man.setBackground(getDrawable(R.drawable.tab_unselected));
               views.setBackground(getDrawable(R.drawable.tab_unselected));
            rates.setBackground(getDrawable(R.drawable.tab_unselected));
            man.setImageResource(R.drawable.man_unselected);
            woman.setImageResource(R.drawable.woman_selected);
            views.setImageResource(R.drawable.seen_white);
            rates.setImageResource(R.drawable.topface_white);

        }
           if (i==2)
        {
            woman.setBackground(getDrawable(R.drawable.tab_unselected));
            man.setBackground(getDrawable(R.drawable.tab_unselected));
            views.setBackground(getDrawable(R.drawable.tab_unselected));
            rates.setBackground(getDrawable(R.drawable.tab_selected));
            man.setImageResource(R.drawable.man_unselected);
            woman.setImageResource(R.drawable.woman_unselected);
            rates.setImageResource(R.drawable.topface_blue);
            views.setImageResource(R.drawable.seen_white);
            }
        if (i==3){
            woman.setBackground(getDrawable(R.drawable.tab_unselected));
            man.setBackground(getDrawable(R.drawable.tab_unselected));
            views.setBackground(getDrawable(R.drawable.tab_selected));
            rates.setBackground(getDrawable(R.drawable.tab_unselected));
            man.setImageResource(R.drawable.man_unselected);
            woman.setImageResource(R.drawable.woman_unselected);
            rates.setImageResource(R.drawable.topface_white);
            views.setImageResource(R.drawable.seen_blue);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setBadgeCount();
    }
}
