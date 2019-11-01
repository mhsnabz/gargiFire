package com.rzn.gargi.topface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.rzn.gargi.R;
import com.rzn.gargi.chat.ChatActivity;
import com.rzn.gargi.helper.TopFaceFragmentPagerAdapter;
import com.rzn.gargi.helper.bottomNavigationHelper;
import com.rzn.gargi.home.HomeActivity;

public class TopFaceActivity extends AppCompatActivity {

    ViewPager mainViewPager;
    ImageView man , woman;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUser=auth.getUid();
    String gender;
    private TopFaceFragmentPagerAdapter pagerApapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_face);
        gender=getIntent().getStringExtra("gender");
        if (gender.isEmpty()){
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
        // rates =(ImageView) findViewById(R.id.rates);
        // views =(ImageView) findViewById(R.id.views);

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
       /* rates.setOnClickListener(new View.OnClickListener() {
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
        });*/
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



    private void setNavigation(String gender){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);

        bottomNavigationHelper.enableNavigation(TopFaceActivity.this,view,gender);
        Menu menu = view.getMenu();
        MenuItem item = menu.getItem(1);
        item.setChecked(true);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final View v = bottomNavigationMenuView.getChildAt(2); // number of menu from left


    }
    private void changeTabs(int i)
    {
        String TAG ="changeTabsFunc";

        if (i==0){
            man.setBackground(getDrawable(R.drawable.tab_selected));
            woman.setBackground(getDrawable(R.drawable.tab_unselected));
            man.setImageResource(R.drawable.man_selected);
            woman.setImageResource(R.drawable.woman_unselected);
            //  views.setImageResource(R.drawable.seen_white);
            // rates.setImageResource(R.drawable.topface_white);


        }
        if (i==1){
            woman.setBackground(getDrawable(R.drawable.tab_selected));
            man.setBackground(getDrawable(R.drawable.tab_unselected));
            //   views.setBackground(getDrawable(R.drawable.tab_unselected));
            //rates.setBackground(getDrawable(R.drawable.tab_unselected));
            man.setImageResource(R.drawable.man_unselected);
            woman.setImageResource(R.drawable.woman_selected);
            //views.setImageResource(R.drawable.seen_white);
            //rates.setImageResource(R.drawable.topface_white);

        }


    }


}
