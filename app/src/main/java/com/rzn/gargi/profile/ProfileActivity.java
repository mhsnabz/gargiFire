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
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
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
import com.rzn.gargi.helper.ModelUser;
import com.rzn.gargi.helper.Rate;
import com.rzn.gargi.helper.Shard;
import com.rzn.gargi.helper.UserProfileClass;
import com.rzn.gargi.helper.bottomNavigationHelper;
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

import static java.util.Objects.*;

public class ProfileActivity extends AppCompatActivity {
    private static final String ALLOWED_CHARACTERS ="-_0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM+";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        rates = new ArrayList<>();
        gender = getIntent().getStringExtra("gender");
        if (gender.isEmpty()){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("allUser").document(currentUser);
            ref.get().addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<DocumentSnapshot>() {
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

            }
        });
        edit=(FloatingActionButton)findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this,EditActivity.class);
                i.putExtra("gender",gender);
                startActivity(i);

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


    private void setNavigation(String  gender){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);

        bottomNavigationHelper.enableNavigation(ProfileActivity.this,view,gender);
        Menu menu = view.getMenu();
        MenuItem item = menu.getItem(3);
        item.setChecked(true);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final View v = bottomNavigationMenuView.getChildAt(2); // number of menu from left


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
        getViewers();

    }
  ListenerRegistration userListener;

    private void loadProfile() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.document(gender + "/" + currentUser);
        userListener= user.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    UserProfileClass user = documentSnapshot.toObject(UserProfileClass.class);
            String _name=user.getName();
            name.setText(_name);
            String _job = user.getJob();
            job.setText(_job);
            age.setText(convert(user.getAge()));
            school.setText(user.getSchool());
            view_count.setText(String.valueOf(user.getClick()));
            point.setText(String.valueOf(user.getRate()));
            rate_count.setText(String.valueOf(user.getCount()));
            if (!user.getProfileImage().isEmpty()){
                String _profileImage = user.getProfileImage();
                String thum_image = user.getThumb_image();

                Picasso.get().load(_profileImage).centerCrop().resize(256,256).into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setEnabled(false);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }else profileImage.setImageResource(R.drawable.upload_place_holder);

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewersCount.remove();
        userListener.remove();
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
