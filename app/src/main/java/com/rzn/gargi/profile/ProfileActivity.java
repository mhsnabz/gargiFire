package com.rzn.gargi.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.data.model.User;
import com.github.clans.fab.FloatingActionButton;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rzn.gargi.R;
import com.rzn.gargi.SettingEdit.SettingActivity;
import com.rzn.gargi.chat.ChatActivity;
import com.rzn.gargi.chat.OneToOneChat;
import com.rzn.gargi.helper.ModelUser;
import com.rzn.gargi.helper.Rate;
import com.rzn.gargi.helper.Shard;
import com.rzn.gargi.helper.UserProfileClass;
import com.rzn.gargi.helper.bottomNavigationHelper;
import com.rzn.gargi.home.HomeActivity;
import com.rzn.gargi.topface.TopFaceActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import q.rorbin.badgeview.QBadgeView;

import static java.util.Objects.*;

public class ProfileActivity extends AppCompatActivity {
    public static final String ALLOWED_CHARACTERS ="-_0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM+";

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUser=auth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    FloatingActionButton setting,edit;
    CircleImageView profileImage;
    SpinKitView progressBar;
    TextView name , age,point,school,job;
    Dialog wating;
    String gender;
    TextView tv_2,tv_1,tv_3;
    TextView rate_count,tvviewer,view_count;
    ArrayList<String> _images;
    ArrayList<Rate> rates;
    RelativeLayout relJob  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        relJob=(RelativeLayout)findViewById(R.id.relJob);
        rates = new ArrayList<>();
        gender = getIntent().getStringExtra("gender");
        if (gender==null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("allUser").document(currentUser);
            ref.get().addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot!=null){
                         gender = documentSnapshot.getString("gender");
                        setNavigation(gender);

                    }
                }
            });

        }else{
            setNavigation(gender);
        }

        _images= new ArrayList<>();
        wating= new Dialog(this);
        profileImage=(CircleImageView)findViewById(R.id.profileImage);
        progressBar=(SpinKitView) findViewById(R.id.progres);
        name=(TextView)findViewById(R.id.name);
        job=(TextView)findViewById(R.id.job);
        school=(TextView)findViewById(R.id.school);
        age=(TextView)findViewById(R.id.age);
        point=(TextView)findViewById(R.id.point);
        setting=(FloatingActionButton)findViewById(R.id.setting);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wating .setContentView(R.layout.wait_dialog);

                wating.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                wating.setCanceledOnTouchOutside(false);
                wating.show();
                Intent i = new Intent(ProfileActivity.this, SettingActivity.class);

                startActivity(i);
                wating.dismiss();
                overridePendingTransition(R.anim.slide_in_left,0);


            }
        });
        edit=(FloatingActionButton)findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this,EditActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


            }
        });
        rate_count =(TextView)findViewById(R.id.rate_count);
        view_count=(TextView)findViewById(R.id.viewsCount);
        tvviewer=(TextView)findViewById(R.id.tvviewer);
        tv_1=(TextView)findViewById(R.id.tv_1);
        tv_2=(TextView)findViewById(R.id.tv_2);
        tv_3=(TextView)findViewById(R.id.tv_3);
        tv_1.setText(getResources().getString(R.string.goruntulenme)+" ");
        tv_2.setText(getResources().getString(R.string.oy_verenler)+" ");
        tv_3.setText(getResources().getString(R.string.kisi_goruntuledi)+" ");


    }


    private void setNavigation(final String  gender){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);

        bottomNavigationHelper.enableNavigation(ProfileActivity.this,view,gender);
        Menu menu = view.getMenu();
        MenuItem item = menu.getItem(3);
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
                Intent i = new Intent(ProfileActivity.this, HomeActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);
                overridePendingTransition(0,0);


            }
        });
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, TopFaceActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ChatActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);
                overridePendingTransition(0,0);
            }
        });


    }
    private void getBadgeCount(){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final QBadgeView badge = new QBadgeView(ProfileActivity.this);
        final View v = bottomNavigationMenuView.getChildAt(2);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("badgeCount").document(auth.getUid()).addSnapshotListener(ProfileActivity.this,MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
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

    private  String getRandomString(final int sizeOfRandomString){
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        Log.d("randomStrign", "getRandomString: "+sb.toString());

        return sb.toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadProfile();
        getProfileInfo();
        getViewers();
        getBadgeCount();

    }
  ListenerRegistration userListener;
    int count =0;
    @Override
    public void onBackPressed()
    {
        count++;
        if (count==2){
            Toast.makeText(ProfileActivity.this,"Çıkmak için Tekrar Dokunun",Toast.LENGTH_SHORT).show();
        }
        if (count == 3){
            finishAffinity();
            System.exit(0);
        }
    }
    private void getProfileInfo(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(gender)
                .document(auth.getUid())
                .get().addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful()){
                    String _name = task.getResult().getString("name");
                    name.setText(_name);
                    if (task.getResult().getString("job")!=null && !task.getResult().getString("job").isEmpty()){
                        String _job = task.getResult().getString("job");
                        job.setText(_job);
                    }else {
                        job.setVisibility(View.GONE);
                    }
                    if (task.getResult().getLong("age")!=null)
                    {
                        age.setText(convert(task.getResult().getLong("age")));
                    }
                    if (task.getResult().getString("school")!=null && !task.getResult().getString("school").isEmpty()){
                        school.setText(task.getResult().getString("school"));
                    }else {
                        school.setVisibility(View.GONE);
                    }

                    if (task.getResult().getString("profileImage")!=null && !task.getResult().getString("profileImage").isEmpty()){
                        Picasso.get().load(task.getResult().getString("profileImage"))
                                .placeholder(R.drawable.upload_place_holder)
                                .into(profileImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                })
                        ;
                    }else progressBar.setVisibility(View.GONE);


                }
            }
        }).addOnFailureListener(ProfileActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    Crashlytics.logException(e);
            }
        });
    }
    private void loadProfile() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.document(gender + "/" + currentUser);
        userListener= user.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    UserProfileClass user = documentSnapshot.toObject(UserProfileClass.class);

            view_count.setText(String.valueOf(user.getClick()));
            point.setText(String.valueOf(user.getRate()));
            rate_count.setText(String.valueOf(user.getCount()));

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewersCount.remove();
        userListener.remove();
        System.gc();
    }

    ListenerRegistration viewersCount;
    private void getViewers(){
        FirebaseFirestore viewers =FirebaseFirestore.getInstance();
        CollectionReference reference = viewers.collection(gender).document(currentUser).collection("view");
         viewersCount = reference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        Log.d("viewers", "onEvent: " + queryDocumentSnapshots);
                        int size = queryDocumentSnapshots.size();
                        tvviewer.setText(String.valueOf(queryDocumentSnapshots.size()));
                    }
                }
            }
        });

    }

    private String convert(long milisecond){
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long time= currentDate.getTime().getTime() / 1000 - milisecond / 1000;
        int years = Math.round(time) / 31536000;
        int months = Math.round(time - years * 31536000) / 2628000;
        return " "+String.valueOf(years);
    }
}
