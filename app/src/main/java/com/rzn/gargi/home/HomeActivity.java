package com.rzn.gargi.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.hash.HashingOutputStream;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rzn.gargi.R;
import com.rzn.gargi.chat.ChatActivity;
import com.rzn.gargi.chat.OneToOneChat;
import com.rzn.gargi.chat.msg_list.Adapter;
import com.rzn.gargi.helper.CallBack;
import com.rzn.gargi.helper.CallBackLimit;
import com.rzn.gargi.helper.bottomNavigationHelper;
import com.rzn.gargi.profile.ProfileActivity;
import com.rzn.gargi.topface.TopFaceActivity;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import im.delight.android.location.SimpleLocation;
import q.rorbin.badgeview.QBadgeView;


public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private SimpleLocation mLocation;
     CoordinatorLayout coordinatorLayoutLay;
    String currentUser=auth.getUid();
    String gender;
     CircleImageView centerImage;
    RippleBackground rippleBackground;
    RecyclerView macth_list;
    private Dialog noOneIsExist,newMatchDialog,yeterinceEslesmenVar,gunlukEslesmeLimit;
    private long manSize,womanSize,manLimit,womanLimit;
    int result=0;
    List<String> userID;
    List<String> userId;
    String userName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    LinearLayoutManager layoutManager;

    private CountDownTimer downTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mLocation = new SimpleLocation(this);
        checkMapServices();
        noOneIsExist=new Dialog(this);
        newMatchDialog= new Dialog(this);
        yeterinceEslesmenVar= new Dialog(this);
        gunlukEslesmeLimit= new Dialog(this);
        coordinatorLayoutLay=(CoordinatorLayout)findViewById(R.id.coordinatorLay);
         rippleBackground=(RippleBackground)findViewById(R.id.content);
        userId=new ArrayList<>();
         macth_list=(RecyclerView)findViewById(R.id.macth_list);

        adapter= new macthList(userId);
        macth_list = (RecyclerView)findViewById(R.id.macth_list) ;
        layoutManager = new LinearLayoutManager(HomeActivity.this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(true);
        macth_list.setLayoutManager(layoutManager);
        macth_list.setHasFixedSize(true);

        macth_list.setAdapter(adapter);
        if (!mLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }




        gender = getIntent().getStringExtra("gender");



        if (gender==null){
            DocumentReference ref = db.collection("allUser").document(currentUser);
            ref.get().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot!=null){
                        String gender = documentSnapshot.getString("gender");
                        setNavigation(gender);
                        if (gender.equals("MAN")){
                            db.collection("ManSize")
                                    .document(auth.getUid()).addSnapshotListener(HomeActivity.this, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if (null != documentSnapshot){
                                        manSize = documentSnapshot.getLong("size");
                                    }
                                }
                            });
                        }else {
                            db.collection("WomanSize")
                                    .document(auth.getUid()).addSnapshotListener(HomeActivity.this, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if (null != documentSnapshot){
                                        womanSize = documentSnapshot.getLong("size");
                                    }
                                }
                            });
                        }


                    }
                }
            });

        }else{
            setNavigation(gender);
            if (gender.equals("MAN")){
                db.collection("ManSize")
                        .document(auth.getUid()).addSnapshotListener(HomeActivity.this,MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (null != documentSnapshot.getLong("size")){
                            manSize = documentSnapshot.getLong("size");

                        }else manSize=0;
                        if (null != documentSnapshot.getLong("limit")){
                            manLimit = documentSnapshot.getLong("limit");

                        }else manLimit=0;
                    }
                });

            }else {

                db.collection("WomanSize")
                        .document(auth.getUid()).addSnapshotListener(HomeActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (null != documentSnapshot){
                            if (documentSnapshot.getLong("size")!=null)
                            womanSize = documentSnapshot.getLong("size");
                            else womanSize=0;


                        }else womanSize =0;
                        if (null != documentSnapshot){
                            if (documentSnapshot.getLong("limit")!=null)
                                womanLimit = documentSnapshot.getLong("limit");
                            else  womanLimit=0;
                        }else womanLimit =0;

                    }
                });
            }
        }

        if (!isNetworkConnected()){
            showSnackBar();
        }




        centerImage =(CircleImageView)findViewById(R.id.centerImage);
        db.collection("allUser")
                .document(auth.getUid())
                .get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String url = task.getResult().getString("profileImage");

                    Picasso.get().load(url).resize(256,256).placeholder(R.drawable.looking_for)
                            .into(centerImage);
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });
      /*  db.collection("MAN"+"match").addSnapshotListener(this,MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e==null){
                    return;
                }else {
                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments() ){
                        if (ds.getId().equals(auth.getUid())){
                            centerImage.setVisibility(View.VISIBLE);
                        }else centerImage.setVisibility(View.GONE);
                    }
                }
            }
        });*/

        centerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (gender.equals("MAN")){
                    if (manLimit<10){
                    if (manSize<2){

                            rippleBackground.startRippleAnimation();
                            lookForNewMatchForMAN();
                        }else{
                        setYeterinceEslesmenVar();
                        rippleBackground.stopRippleAnimation();
                    }
                    }else {
                            setGunlukEslesmeLimit("MAN",auth.getUid());
                    }
                }else if (gender.equals("WOMAN")){
                    if (womanLimit<35){
                        if (womanSize<6){
                            rippleBackground.startRippleAnimation();
                            lookForNewMatchForWOMAN();
                        }else {
                            setYeterinceEslesmenVar();
                            rippleBackground.stopRippleAnimation();
                        }
                    }else {
                            setGunlukEslesmeLimit("WOMAN",auth.getUid());
                    }

                }
            }
        });


    }


    private void lookForNewMatchForWOMAN(){
        Query ref;
        if (result==0)
            ref = db.collection("MANmatch").orderBy("age").limit(10);
        else {
            ref = db.collection("MANmatch").orderBy("chatSize").startAfter(result).limit(10);
        }
        ref.get().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size()>0){
                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                        String id = ds.getId();
                        setNewMatchForWoman(id);
                    }
                }
            }
        });
    }
    private void lookForNewMatchForMAN() {
        Query ref;
        if (result==0)
             ref = db.collection("WOMANmatch").orderBy("age").limit(10);
        else {
            ref = db.collection("WOMANmatch").orderBy("chatSize").startAfter(result).limit(10);
        }
            ref.get().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.getDocuments().size()>0){
                        for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                            String id = ds.getId();
                            setNewMatchForMan(id);
                        }
                    }
                }
            });
    }

    private void setNewMatchForWoman(final String userId){
        final Map<String , Object> mapCurrenUser = new HashMap<>();
        final Map<String , Object> mapUserId = new HashMap<>();

        mapCurrenUser.put("isOnline",false);
        mapCurrenUser.put("senderUid",userId);
        mapCurrenUser.put("getterUid",currentUser);
        mapCurrenUser.put("time", FieldValue.serverTimestamp());
        mapCurrenUser.put("timer",1800000);

        mapUserId.put("isOnline",false);
        mapUserId.put("senderUid",currentUser);
        mapUserId.put("getterUid",userId);
        mapUserId.put("timer",1800000);
        mapUserId.put("time",FieldValue.serverTimestamp());

        final DocumentReference refCurrenUser = db
                .collection("msgList")
                .document(currentUser)
                .collection(currentUser).document(userId);
        ;
        final Query refCurrenUserMsgList = db
                .collection("msgList")
                .document(currentUser)
                .collection(currentUser).whereEqualTo("senderUid",userId);
        ;
        final Query oldList = db
                .collection("oldList")
                .document(currentUser)
                .collection(currentUser).whereEqualTo("senderUid",userId);
        ;
        final DocumentReference refUserId = db.collection("msgList")
                .document(userId)
                .collection(userId)
                .document(currentUser);


        oldList.get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getDocuments().size()>0){
                        Log.d("userIdIsExist", "onComplete: ");
                        result++;
                        lookForNewMatchForWOMAN();
                    }else
                    {
                        refCurrenUserMsgList.get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().getDocuments().size()>0){
                                        result++;
                                        lookForNewMatchForWOMAN();
                                    }else {
                                        if (womanSize <6 && womanLimit<35){
                                            refCurrenUser.set(mapCurrenUser);
                                            refUserId.set(mapUserId);

                                            updateManSize(userId);
                                            updateWomanSize(auth.getUid());
                                            addNewMatchOnLimit(userId);
                                            addNewMatchOnLimitOther(userId);
                                            updateManLimit(userId);
                                            setBadge(userId,auth.getUid());
                                            womanSize++;
                                            womanLimit++;
                                            result++;
                                            sendNotification(userId);

                                            showDialog();
                                            if (womanSize<6 && womanLimit<35)
                                                lookForNewMatchForWOMAN();
                                            else return;
                                        }else {
                                            rippleBackground.stopRippleAnimation();
                                            return;
                                        }
                                    }
                                }
                            }
                        });

                    }
                }
            }
        });
    }
    int count =0;
    @Override
    public void onBackPressed()
    {
        count++;
        if (count==2){
            Toast.makeText(HomeActivity.this,"Çıkmak için Tekrar Dokunun",Toast.LENGTH_SHORT).show();
        }
        if (count == 3){
            finishAffinity();
            System.exit(0);
        }
    }
    private  void setNewMatchForMan( final String userId){
        final long[] size = {manSize};
        final Map<String , Object> mapCurrenUser = new HashMap<>();
        final Map<String , Object> mapUserId = new HashMap<>();

        mapCurrenUser.put("isOnline",false);
        mapCurrenUser.put("senderUid",userId);
        mapCurrenUser.put("getterUid",currentUser);
        mapCurrenUser.put("time", FieldValue.serverTimestamp());
        mapCurrenUser.put("timer",1800000);

        mapUserId.put("isOnline",false);
        mapUserId.put("senderUid",currentUser);
        mapUserId.put("getterUid",userId);
        mapUserId.put("timer",1800000);
        mapUserId.put("time",FieldValue.serverTimestamp());

        final DocumentReference refCurrenUser = db
                .collection("msgList")
                .document(currentUser)
                .collection(currentUser).document(userId);
                ;
        final Query refCurrenUserMsgList = db
                .collection("msgList")
                .document(currentUser)
                .collection(currentUser).whereEqualTo("senderUid",userId);
                ;
        final Query oldList = db
                .collection("oldList")
                .document(currentUser)
                .collection(currentUser).whereEqualTo("senderUid",userId);
               ;
        final DocumentReference refUserId = db.collection("msgList")
                .document(userId)
                .collection(userId)
                .document(currentUser);

      oldList.get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getDocuments().size()>0){
                        Log.d("userIdIsExist", "onComplete: ");
                        result++;
                        lookForNewMatchForMAN();
                    }else
                    {
                        refCurrenUserMsgList.get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().getDocuments().size()>0){
                                        result++;
                                        lookForNewMatchForMAN();
                                    }else {
                                        if (manSize <2 && manLimit<10){
                                            refCurrenUser.set(mapCurrenUser);
                                            refUserId.set(mapUserId);
                                            manSize++;
                                            result++;
                                            manLimit++;
                                            updateManSize(auth.getUid());
                                            updateWomanSize(userId);
                                            addNewMatchOnLimit(userId);
                                            addNewMatchOnLimitOther(userId);
                                            updateWomanLimit(userId);
                                            setBadge(userId,auth.getUid());
                                            sendNotification(userId);
                                            showDialog();

                                            if (manSize<2 && manLimit<10)
                                                lookForNewMatchForMAN();
                                            else return;
                                        }else {
                                            rippleBackground.stopRippleAnimation();
                                            return;
                                        }
                                    }
                                }
                            }
                        });

                    }
                }
          }
      });

    }

    private void removeFromMatch(String gender , String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Task<Void> ref = db.collection(gender+"match")
                .document(userId).delete();


    }

    private void setGunlukEslesmeLimit(String gender,String currentUser){
        gunlukEslesmeLimit.setContentView(R.layout.gunluk_eslesme_limitine_ulastin);
        gunlukEslesmeLimit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gunlukEslesmeLimit.setCanceledOnTouchOutside(false);
        gunlukEslesmeLimit.show();
        TextView timer =(TextView)gunlukEslesmeLimit.findViewById(R.id.timer);
        Button kapat =(Button) gunlukEslesmeLimit.findViewById(R.id.cancel);
        kapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gunlukEslesmeLimit.dismiss();
            }
        });
        setTimer(gender,currentUser,timer);

    }
    private void setTimer(String gender , final String currentUser, final TextView timer){
        if (gender.equals("MAN")){
            db.collection("ManSize")
                    .document(currentUser)
                    .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        Date time = task.getResult().getDate("time");
                        long second =time.getTime();
                        Date date = Timestamp.now().toDate();
                        long currentTime=date.getTime();
                        long value = second+(24*60*60000)-currentTime;
                        if (value<0){
                            deleteLimit(auth.getUid());
                        }
                        startTimer(timer,value);
                    }
                }
            });
        }else if (gender.equals("WOMAN")){
            db.collection("WomanSize")
                    .document(currentUser)
                    .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        Date time = task.getResult().getDate("time");
                        long second =time.getTime();
                        Date date = Timestamp.now().toDate();
                        long currentTime=date.getTime();
                        long value = second+(24*60*60000)-currentTime;
                        if (value<0){
                            deleteLimit(auth.getUid());
                        }
                         startTimer(timer,value);

                    }
                }
            });
        }
    }

    private void startTimer(final TextView timer, final long time){


        downTimer = new CountDownTimer(time
                ,1000) {
            @Override
            public void onTick(long l) {
                if (l<=2000){
                    deleteLimit(auth.getUid());
                }
                updateTimeTV(timer,l);
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    private void deleteLimit(String uid)
    {
        db.collection("limit")
                .document(uid).delete().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("limit refresh", "onComplete: "+"true");
                }
            }
        });
    }


    private void updateTimeTV(TextView _timer,long durationInMillis) {
       // long millis = durationInMillis % 1000;
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        _timer.setText(time);
       /* int hour=(int) _time % 60;
        int min =(int) _time/60000;
        int second = (int) _time % 60000/1000;
        String timeLeftString;
        timeLeftString = ""+hour;
        timeLeftString+=":";
        timeLeftString += ""+min;
        timeLeftString+=":";
        if (second< 10) timeLeftString +=0;
        timeLeftString += second;
        _timer.setText(timeLeftString);*/
        // if (_time<=1500)
        //     removeMatch(_time);

    }
    private void setBadge(String userId, String currentUser){
        Map<String,Object> mapUserId = new HashMap<>();
        Map<String,Object> mapCurrntUser = new HashMap<>();
        mapUserId.put(currentUser,userId);
        mapCurrntUser.put(userId,currentUser);
        db.collection("badgeCount")
                .document(userId).set(mapUserId,SetOptions.merge());
        db.collection("badgeCount")
                .document(currentUser).set(mapCurrntUser,SetOptions.merge());

    }
    private void setYeterinceEslesmenVar(){
        yeterinceEslesmenVar.setContentView(R.layout.yeterince_eslesmen_var);
        yeterinceEslesmenVar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        yeterinceEslesmenVar.setCanceledOnTouchOutside(false);
        yeterinceEslesmenVar.show();
        Button sendMsg=(Button)yeterinceEslesmenVar.findViewById(R.id.sendMsg);
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,ChatActivity.class);
                i.putExtra("gender",gender);
                startActivity(i);
                finish();
            }
        });
    }
    private void showDialog(){
        newMatchDialog.setContentView(R.layout.you_have_new_match);
        newMatchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newMatchDialog.setCanceledOnTouchOutside(false);
        newMatchDialog.show();

        Button devamEt=(Button)newMatchDialog.findViewById(R.id.devamEt);
        Button sendMsg=(Button)newMatchDialog.findViewById(R.id.sendMsg);

        devamEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerImage.performClick();
                newMatchDialog.dismiss();

            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,ChatActivity.class);
                i.putExtra("gender",gender);
                startActivity(i);
                finish();
            }
        });


    }
    private void updateManSize(final  String userId){
        db.collection("msgList")
                .document(userId)
                .collection(userId).get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                long size = task.getResult().getDocuments().size();
                manSize = size;
                Map<String,Object> map = new HashMap<>();
                map.put("size",size);
                map.put("time",FieldValue.serverTimestamp());
                db.collection("ManSize").document(userId).set(map, SetOptions.merge());
                if (manSize>=2){
                    removeFromMatch("MAN",userId);
                    Log.d("remove", "onComplete: ");
                }
            }
        });
    }
    private void addNewMatchOnLimit(String userId){
        Map<String ,Object> map = new HashMap<>();
        map.put("user",userId);
        Map<String,Object> userMap = new HashMap<>();
        userMap.put(userId,map);
        db.collection("limit")
                .document(auth.getUid())
                .set(userMap,SetOptions.merge());
    }
    private void addNewMatchOnLimitOther(String userId){
        Map<String ,Object> map = new HashMap<>();
        map.put("user",auth.getUid());
        Map<String,Object> userMap = new HashMap<>();
        userMap.put(auth.getUid(),map);
        db.collection("limit")
                .document(userId)
                .set(userMap,SetOptions.merge());
    }
    private void updateWomanLimit(final String userId){
        db.collection("limit")
                .document(userId)
                .get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                long size = task.getResult().getData().size();
                Map<String,Object> map = new HashMap<>();
                map.put("limit",size);
                map.put("time",FieldValue.serverTimestamp());
                db.collection("WomanSize").document(userId).set(map, SetOptions.merge());
            }
        });
    }

    private void updateManLimit(final String userId){
        db.collection("limit")
                .document(userId)
                .get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                long size = task.getResult().getData().size();
                Map<String,Object> map = new HashMap<>();
                map.put("limit",size);
                map.put("time",FieldValue.serverTimestamp());
                db.collection("ManSize").document(userId).set(map, SetOptions.merge());
            }
        });
    }
    private void updateWomanSize(final String userId){
        db.collection("msgList")
                .document(userId)
                .collection(userId).get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                long size = task.getResult().getDocuments().size();
                womanSize = size;
                Map<String,Object> map = new HashMap<>();
                map.put("size",size);
                map.put("time",FieldValue.serverTimestamp());
                db.collection("WomanSize").document(userId).set(map, SetOptions.merge());
                if (womanSize>=6){
                    removeFromMatch("WOMAN",userId);
                    Log.d("remove", "onComplete: ");
                }
            }
        });
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void showSnackBar(){
         Snackbar snackbar =  Snackbar.make(coordinatorLayoutLay,getText(R.string.lutfen_internet_baglantinizi_kontrol_edin),Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.tamam), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).setActionTextColor(Color.RED);
        snackbar.show();
    }
        ///TODO: bottom navigiton setter
    private void setNavigation(final String  gender){
        getName();
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);

        bottomNavigationHelper.enableNavigation(HomeActivity.this,view,gender);
        Menu menu = view.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final View v = bottomNavigationMenuView.getChildAt(2); // number of menu from left

        final View v1 = bottomNavigationMenuView.getChildAt(0); // number of menu from left
        final View v2 = bottomNavigationMenuView.getChildAt(1); // number of menu from left
        final View v3 = bottomNavigationMenuView.getChildAt(2); // number of menu from left
        final View v4 = bottomNavigationMenuView.getChildAt(3);
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,TopFaceActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ChatActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        v4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
    }
    ///TODO: Location Stufff
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("tag", "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    final double latitude = mLocation.getLatitude();
                    final double longitude = mLocation.getLongitude();
                    Log.d("lat", "getLocationPermission: "+latitude);
                    Log.d("lat", "getLocationPermission: "+longitude);
                    fetchLocation(latitude, longitude, auth.getCurrentUser().getUid());



                }
                else{
                    getLocationPermission();
                }
            }
        }
    }

    private void fetchLocation(double latitude, double longitude, final String uid)
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Map<String,Object> map = new HashMap<>();
        map.put("lat",latitude);
        map.put("longLat",longitude);
        GeoPoint point = new GeoPoint(latitude,longitude);

        final Map<String,Object> location= new HashMap<>();
        location.put("location",point);

        final DocumentReference ref = db.collection(gender).document(uid);
        ref.update(location).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DocumentReference reference = db.collection("allUser").document(uid);
                    reference.update(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Crashlytics.logException(e);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;

                    final double latitude = mLocation.getLatitude();
                    final double longitude = mLocation.getLongitude();
                   fetchLocation(latitude, longitude, auth.getCurrentUser().getUid());


                }
            }
        }
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            final double latitude = mLocation.getLatitude();
            final double longitude = mLocation.getLongitude();
            Log.d("lat", "getLocationPermission: "+latitude);
            Log.d("lat", "getLocationPermission: "+longitude);
           fetchLocation(latitude, longitude, auth.getCurrentUser().getUid());

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    protected void onResume() {

        super.onResume();

        if(checkMapServices()){
            if(mLocationPermissionGranted){
                mLocation.beginUpdates();
            }
            else{
                getLocationPermission();
            }
        }
        mLocation.beginUpdates();
    }
    @Override
    protected void onPause() {
        mLocation.endUpdates();

        super.onPause();


    }
    FirebaseFirestore notDb=FirebaseFirestore.getInstance();
    private void sendNotification(final String userId){
        notDb.collection("allUser")
                .document(userId)
                .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getString("tokenID")!=null ){
                        Map<String,Object> not=new HashMap<>();
                        not.put("from",auth.getUid());
                        not.put("type","match");
                        not.put("getter",userId);
                        not.put("tokenID",task.getResult().getString("tokenID"));
                        not.put("name",userName);
                        notDb.collection("notification")
                                .document(userId)
                                .collection("notification").add(not).addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    Log.d("sendNotification", "onComplete: "+"task.isSuccessful");
                                }
                            }
                        }).addOnFailureListener(HomeActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Crashlytics.logException(e);
                            }
                        });
                    }
                }
            }
        });


    }
    private void getName(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("allUser")
                .document(auth.getUid())
                .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    userName = task.getResult().getString("name");
                }
            }
        });
    }
    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }
    public boolean isServicesOK(){
        Log.d("tag", "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);
        if(available == ConnectionResult.SUCCESS){
//everything is fine and the user can make map requests
            Log.d("tag", "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
//an error occured but we can resolve it
            Log.d("tag", "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = HomeActivity.this.getAssets().open("woman.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
    private void getBadgeCount(){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);
        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final QBadgeView badge = new QBadgeView(HomeActivity.this);
        final View v = bottomNavigationMenuView.getChildAt(2);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("badgeCount").document(auth.getUid()).addSnapshotListener(HomeActivity.this,MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
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
    @Override
    protected void onStart() {
        super.onStart();
        setList();
        getBadgeCount();
        final RelativeLayout rippleBackground =(RelativeLayout)findViewById(R.id.rippleBackground);
        final RelativeLayout relLayList =(RelativeLayout)findViewById(R.id.listRel);

        gender = getIntent().getStringExtra("gender");
        if (gender == null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("allUser").document(currentUser);
            ref.get().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        setLayouts(auth.getUid(), documentSnapshot.getString("gender"),rippleBackground,relLayList);

                    }
                }
            });

        }else {
            setLayouts(auth.getUid(),gender,rippleBackground,relLayList);
        }

    }
    private void setLayouts(final String currentUser, final String gender, final RelativeLayout rippleBackground, final RelativeLayout relLayList){
      checkAnyIsExist(gender, new CallBack<Boolean>() {
          @Override
          public void returnFalse(Boolean _false) {
              noOneIsExist.setContentView(R.layout.no_one_exist);
              noOneIsExist.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
              noOneIsExist.setCanceledOnTouchOutside(false);
              noOneIsExist.show();
          }

          @Override
          public void returnTrue(Boolean _true) {
              FirebaseFirestore db = FirebaseFirestore.getInstance();
              CollectionReference ref = db.collection("msgList")
                      .document(currentUser).collection(currentUser);
              ref.get().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<QuerySnapshot>() {
                  @Override
                  public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                      long size = queryDocumentSnapshots.getDocuments().size();
                      if (gender.equals("MAN")){
                          if (size>=2){
                              rippleBackground.setVisibility(View.GONE);
                              relLayList.setVisibility(View.VISIBLE);
                          }else {
                              rippleBackground.setVisibility(View.VISIBLE);
                              relLayList.setVisibility(View.GONE);

                          }
                      }
                      else if (gender.equals("WOMAN")){
                          if (size>=6){
                              rippleBackground.setVisibility(View.GONE);
                              relLayList.setVisibility(View.VISIBLE);
                          }else {
                              rippleBackground.setVisibility(View.VISIBLE);
                              relLayList.setVisibility(View.GONE);

                          }
                      }
                  }
              }).addOnFailureListener(HomeActivity.this, new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {

                  }
              });

          }

      });


    }
    private void checkAnyIsExist(String gender, final CallBack<Boolean> isExist){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref ;
        if (gender.equals("MAN")){
            ref=db.collection("WOMANmatch");

        }else   ref=db.collection("MANmatch");

        ref.addSnapshotListener(this, MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    return;
                }
                if (queryDocumentSnapshots.getDocuments().size()>0){
                    isExist.returnTrue(true);
                }else isExist.returnFalse(false);
            }
        });
    }




   macthList adapter ;

   private  void setList()
   {

       db.collection("msgList")
               .document(auth.getUid())
               .collection(auth.getUid()).addSnapshotListener(HomeActivity.this, new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
               if (e!=null){
                   return;
               }else {
                   if (queryDocumentSnapshots!=null){
                      for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                          if (dc.getType()==DocumentChange.Type.ADDED){
                              userId.add(dc.getDocument().getId());
                              adapter.notifyDataSetChanged();
                          }
                      }
                   }
               }
           }
       });

      // adapter= new macthList()

   }

   public class macthList extends RecyclerView.Adapter<HomeActivity.ViewHolder>{

       List<String> userId;

       public macthList(List<String> userId)
       {
           this.userId = userId;
       }

       @NonNull
       @Override
       public HomeActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View itemView = LayoutInflater.from(parent.getContext())
                   .inflate(R.layout.topface_single_layout, parent, false);

           return new ViewHolder(itemView);
       }

       @Override
       public void onBindViewHolder(@NonNull HomeActivity.ViewHolder holder, int position)
       {
            holder.setProfile(userId.get(position));
       }

       @Override
       public int getItemCount() {
           return userId.size();
       }
   }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {

            super(itemView);

        }

        public void setProfile(String userid){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            TextView seen = (TextView)itemView.findViewById(R.id.seen);
            final TextView name = (TextView)itemView.findViewById(R.id.name);
            TextView location = itemView.findViewById(R.id.locaiton);
            final CircleImageView image = (CircleImageView)itemView.findViewById(R.id.profileImage);
            final ProgressBar loading = (ProgressBar)itemView.findViewById(R.id.loading);
            TextView _age = (TextView)itemView.findViewById(R.id.age);
            TextView point =(TextView)itemView.findViewById(R.id.point);

            if (gender.equals("MAN")){
                db.collection("WOMAN")
                        .document(userid)
                        .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            String _name=task.getResult().getString("name");
                            name.setText(_name);

                            String _horoscope = task.getResult().getString("burc");
                            if (task.getResult().getLong("age")!=null){
                                long _age
                                        = task.getResult().getLong("age");
                                getAge(_age);
                            }


                            getBurc(_horoscope);
                            if (task.getResult().getLong("click")!=null){
                                getClick(task.getResult().getLong("click"));
                            }
                            if (task.getResult().getLong("count")!=null&&task.getResult().getLong("totalRate")!=null){
                                calculateRate(task.getResult().getLong("count"),task.getResult().getLong("totalRate"));
                            }
                            if (task.getResult().getString("thumb_image")!=null){
                                String _thumb= task.getResult().getString("thumb_image");
                                if (!_thumb.isEmpty())
                                    Picasso.get().load(_thumb)
                                            .placeholder(R.drawable.upload_place_holder)
                                            .into(image, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    loading.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onError(Exception e) {

                                                }
                                            });
                                else {
                                    image.setImageResource(R.drawable.upload_place_holder);
                                    loading.setVisibility(View.GONE);

                                }
                            }else{
                                image.setImageResource(R.drawable.upload_place_holder);
                                loading.setVisibility(View.GONE);

                            }
                        }
                    }
                });
            }else if (gender.equals("WOMAN")){
                db.collection("MAN")
                        .document(userid)
                        .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            String _name=task.getResult().getString("name");
                            name.setText(_name);
                            if (task.getResult().getLong("click")!=null){
                                getClick(task.getResult().getLong("click"));
                            }
                            if (task.getResult().getLong("count")!=null&&task.getResult().getLong("totalRate")!=null){
                                calculateRate(task.getResult().getLong("count"),task.getResult().getLong("totalRate"));
                            }
                            String _horoscope = task.getResult().getString("burc");
                            long _age
                                    = task.getResult().getLong("age");
                            getAge(_age);

                            getBurc(_horoscope);
                            if (task.getResult().getString("thumb_image")!=null){
                                String _thumb= task.getResult().getString("thumb_image");
                                if (!_thumb.isEmpty())
                                    Picasso.get().load(_thumb)
                                            .placeholder(R.drawable.upload_place_holder)
                                            .into(image, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    loading.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onError(Exception e) {

                                                }
                                            });
                                else {
                                    image.setImageResource(R.drawable.upload_place_holder);
                                    loading.setVisibility(View.GONE);

                                }
                            }else{
                                image.setImageResource(R.drawable.upload_place_holder);
                                loading.setVisibility(View.GONE);

                            }
                        }
                    }
                });
            }
            db.collection("allUser")
                    .document(userid)
                    .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            String _name=task.getResult().getString("name");
                            name.setText(_name);

                            String _horoscope = task.getResult().getString("burc");
                            long _age
                                    = task.getResult().getLong("age");
                            getAge(_age);

                            getBurc(_horoscope);
                            if (task.getResult().getString("thumb_image")!=null){
                                String _thumb= task.getResult().getString("thumb_image");
                                if (!_thumb.isEmpty())
                                Picasso.get().load(_thumb)
                                        .placeholder(R.drawable.upload_place_holder)
                                        .into(image, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                loading.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {

                                            }
                                        });
                                else {
                                    image.setImageResource(R.drawable.upload_place_holder);
                                    loading.setVisibility(View.GONE);

                                }
                            }else{
                                image.setImageResource(R.drawable.upload_place_holder);
                                loading.setVisibility(View.GONE);

                            }
                        }
                }
            });

        }
        private void calculateRate(long count , long totalRate){
            TextView point =(TextView)itemView.findViewById(R.id.point);

            double rating = (double) totalRate/count;
            rating=Math.round(rating*100.0)/100.0;
            point.setText(String.valueOf(rating));

        }
        public void getClick(long _click){
            TextView seen = (TextView)itemView.findViewById(R.id.seen);
            if (_click<=9999999)
            {
                seen.setText(Long.toString(_click));
            }
            else if (_click>=1000000 && _click<=999999999)
                seen.setText(Long.toString(_click/1000000)+"M");
        }
        public void getAge(long age){
            TextView _age = (TextView)itemView.findViewById(R.id.age);

            Calendar currentDate = Calendar.getInstance();
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
            Long time= currentDate.getTime().getTime() / 1000 - age / 1000;
            int years = Math.round(time) / 31536000;
            int months = Math.round(time - years * 31536000) / 2628000;
            _age.setText(String.valueOf(years));
        }
        public void getBurc(String _burc){
            ImageView burc =(ImageView) itemView.findViewById(R.id.horoscope);
            if (_burc.equals("balik"))
                burc.setImageResource(R.drawable.balik);
            else if (_burc.equals("kova"))
                burc.setImageResource(R.drawable.kova);
            else if (_burc.equals("koc"))
                burc.setImageResource(R.drawable.koc);
            else if (_burc.equals("boga"))
                burc.setImageResource(R.drawable.boga);
            else if (_burc.equals("ikizler"))
                burc.setImageResource(R.drawable.ikizler);
            else if (_burc.equals("yengec"))
                burc.setImageResource(R.drawable.yengec);
            else if (_burc.equals("aslan"))
                burc.setImageResource(R.drawable.aslan);
            else if (_burc.equals("basak"))
                burc.setImageResource(R.drawable.aslan);
            else if (_burc.equals("terazi"))
                burc.setImageResource(R.drawable.terazi);
            else if (_burc.equals("akrep"))
                burc.setImageResource(R.drawable.akrep);
            else if (_burc.equals("yay"))
                burc.setImageResource(R.drawable.yay);
            else if (_burc.equals("oglak"))
                burc.setImageResource(R.drawable.oglak);
            else {
                if (_burc.isEmpty())
                    burc.setVisibility(View.GONE);
            }
        }
    }
}
