package com.rzn.gargi.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.CallBack;
import com.rzn.gargi.helper.ChatFragmentAdapter;
import com.rzn.gargi.helper.bottomNavigationHelper;
import com.rzn.gargi.home.HomeActivity;
import com.rzn.gargi.profile.ProfileActivity;
import com.rzn.gargi.topface.TopFaceActivity;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.QBadgeView;

public class ChatActivity extends AppCompatActivity {
    private ViewPager mainViewPager;
    private ImageView messeges , oldMesseges;
    private ChatFragmentAdapter pagerApapter;
    private Dialog dialog;
    private String gender;
    private int count=0;
    CoordinatorLayout coordinatorLay;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUser=auth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        gender =getIntent().getStringExtra("gender");
        coordinatorLay=(CoordinatorLayout)findViewById(R.id.coordinatorLay);
        dialog= new Dialog(this);

        if (gender==null){
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

    @Override
    protected void onResume() {
        super.onResume();

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
    List<String> list ;

    @Override
    protected void onStart() {
        super.onStart();
        //getBadgeCount();
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);
        final QBadgeView view1 = (QBadgeView) new QBadgeView(ChatActivity.this);
        final QBadgeView view2 = (QBadgeView) new QBadgeView(ChatActivity.this);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        Transition slide = new Slide(Gravity.RIGHT);
        slide.excludeTarget(bottomNavigationMenuView, true);
        final View v = bottomNavigationMenuView.getChildAt(2); // number of menu from left

        list= new ArrayList<>();
        final QBadgeView badge = new QBadgeView(ChatActivity.this);
      //  badge.bindTarget(v).setBadgeTextSize(14,true).setBadgePadding(7,true).setBadgeBackgroundColor(Color.RED).setBadgeNumber(documentSnapshot.getData().size());


    }

    private void getBadgeCount(){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final QBadgeView view1 = (QBadgeView) new QBadgeView(ChatActivity.this);
        final QBadgeView view2 = (QBadgeView) new QBadgeView(ChatActivity.this);
        final QBadgeView badge = new QBadgeView(ChatActivity.this);
        final View v = bottomNavigationMenuView.getChildAt(2);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference ref = db.collection("badgeCount")
               .document("badge")
                .collection(auth.getUid())
                .document(auth.getUid());

        ref.addSnapshotListener(ChatActivity.this,MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {


              //  Log.d("badgeCount--->", "onEvent: "+documentSnapshot.getData().size());
                if (documentSnapshot.getData().size()>0)
                    badge.bindTarget(v).setBadgeTextSize(14,true).setBadgePadding(7,true).setBadgeBackgroundColor(Color.RED).setBadgeText("");

                else

                badge.hide(true);

            }
        });
    }
    private void setNavigation(final String gender){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);


        bottomNavigationHelper.enableNavigation(ChatActivity.this,view,gender);
        Menu menu = view.getMenu();
        MenuItem item = menu.getItem(2);
        item.setChecked(true);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        Transition slide = new Slide(Gravity.RIGHT);
        slide.excludeTarget(bottomNavigationMenuView, true);
        getWindow().setEnterTransition(slide);
        final View v1 = bottomNavigationMenuView.getChildAt(0); // number of menu from left
        final View v2 = bottomNavigationMenuView.getChildAt(1); // number of menu from left
        final View v3 = bottomNavigationMenuView.getChildAt(2); // number of menu from left
        final View v4 = bottomNavigationMenuView.getChildAt(3); // number of menu from left

        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this,HomeActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);

                overridePendingTransition(R.anim.slide_in_left,0);

            }
        });
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, TopFaceActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left,0);

            }
        });
        v4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatActivity.this, ProfileActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

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


    private void hasBadge(final CallBack<Boolean> has){

    }
}
