package com.rzn.gargi.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.ChatFragmentAdapter;
import com.rzn.gargi.helper.bottomNavigationHelper;
import com.rzn.gargi.home.HomeActivity;

public class ChatActivity extends AppCompatActivity {
    private ViewPager mainViewPager;
    private ImageView messeges , oldMesseges;
    private ChatFragmentAdapter pagerApapter;
    private Dialog dialog;
    private String gender;
    private int count=0;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUser=auth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        gender =getIntent().getStringExtra("gender");

        dialog= new Dialog(this);

        if (gender.isEmpty()){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("allUser").document(currentUser);
            ref.get().addOnSuccessListener(ChatActivity.this, new OnSuccessListener<DocumentSnapshot>() {
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



        messeges=(ImageView)findViewById(R.id.messges);
        oldMesseges=(ImageView)findViewById(R.id.oldMesseges);
        messeges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewPager.setCurrentItem(0);

            }
        });

        oldMesseges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewPager.setCurrentItem(1);

            }
        });

        pagerApapter = new ChatFragmentAdapter(getSupportFragmentManager());
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
            messeges.setBackground(getDrawable(R.drawable.tab_selected));
            messeges.setImageResource(R.drawable.msg_blue);
            oldMesseges.setImageResource(R.drawable.old_mgs_white);
            oldMesseges.setBackground(getDrawable(R.drawable.tab_unselected));

        }
        if (i==1){
            oldMesseges.setBackground(getDrawable(R.drawable.tab_selected));
            messeges.setBackground(getDrawable(R.drawable.tab_unselected));
            messeges.setImageResource(R.drawable.msg_white);
            oldMesseges.setImageResource(R.drawable.old_msg_blue);
        }



        Log.d(TAG, "changeTabs: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(ChatActivity.this,OneToOneChat.class);
        startActivity(i);
    }

    private void setNavigation(String gender){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);

        bottomNavigationHelper.enableNavigation(ChatActivity.this,view,gender);
        Menu menu = view.getMenu();
        MenuItem item = menu.getItem(2);
        item.setChecked(true);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final View v = bottomNavigationMenuView.getChildAt(2); // number of menu from left


    }
    @Override
    public void onBackPressed()
    {
        count++;
        if (count==2){
            Toast.makeText(ChatActivity.this,"Çıkmak için Tekrar Dokunun",Toast.LENGTH_SHORT).show();
        }
        if (count == 3){
            finishAffinity();
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
