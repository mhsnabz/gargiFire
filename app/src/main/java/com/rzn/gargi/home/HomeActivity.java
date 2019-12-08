package com.rzn.gargi.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.location.LocationListener;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
import com.rzn.gargi.helper.CallBack;
import com.rzn.gargi.helper.ForceUpdateChecker;
import com.rzn.gargi.helper.bottomNavigationHelper;
import com.rzn.gargi.profile.ProfileActivity;
import com.rzn.gargi.topface.TopFaceActivity;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import im.delight.android.location.SimpleLocation;
import q.rorbin.badgeview.QBadgeView;

import static com.rzn.gargi.profile.ProfileActivity.ALLOWED_CHARACTERS;


public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, RewardedVideoAdListener, ForceUpdateChecker.OnUpdateNeededListener {

    private RewardedVideoAd mRewardedVideoAd;
    public static long MAN_LIMIT = 5;
    public static long WOMAN_LIMIT = 35;
    Dialog update;

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
    private Location location;
    private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private CountDownTimer downTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        update=new Dialog(this);
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        loadRewardedVideoAd();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = permissionsToRequest(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
        mLocation = new SimpleLocation(this);

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
                    if (!task.getResult().getString("profileImage").isEmpty()){

                        String url = task.getResult().getString("profileImage");

                        Picasso.get().load(url).resize(256,256).placeholder(R.drawable.looking_for)
                                .into(centerImage);
                    }

                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });

        centerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (gender.equals("MAN")){
                    if (manLimit<MAN_LIMIT){
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
                        if (womanLimit<WOMAN_LIMIT){
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
        mapCurrenUser.put("isTyping", false);

        mapCurrenUser.put("timer",1800000);

        mapUserId.put("isOnline",false);
        mapUserId.put("senderUid",currentUser);
        mapUserId.put("getterUid",userId);
        mapUserId.put("timer",1800000);
        mapUserId.put("isTyping", false);
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
                                        if (womanSize <6 && womanLimit<WOMAN_LIMIT){
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
                                            if (womanSize<6 && womanLimit<WOMAN_LIMIT)
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
        mapCurrenUser.put("isTyping",false);

        mapUserId.put("isOnline",false);
        mapUserId.put("senderUid",currentUser);
        mapUserId.put("getterUid",userId);
        mapUserId.put("timer",1800000);
        mapUserId.put("time",FieldValue.serverTimestamp());
        mapUserId.put("isTyping",false);
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
                                        if (manSize <2 && manLimit<MAN_LIMIT){
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

                                            if (manSize<2 && manLimit<MAN_LIMIT)
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
        final Button ads =(Button) gunlukEslesmeLimit.findViewById(R.id.video);

        if (mRewardedVideoAd.isLoaded()) {
            ads.setVisibility(View.VISIBLE);
        }else {
            ads.setVisibility(View.GONE);
        }
        ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                    gunlukEslesmeLimit.dismiss();
                }
            }
        });
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
                        if (task.getResult().getDate("time")!=null){
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
                }
            });
        }else if (gender.equals("WOMAN")){
            db.collection("WomanSize")
                    .document(currentUser)
                    .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().getDate("time")!=null){
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
        userMap.put(userId,userId);
        db.collection("limit")
                .document(auth.getUid())
                .set(userMap,SetOptions.merge());
    }
    private void addNewMatchOnLimitOther(String userId){
        Map<String ,Object> map = new HashMap<>();
        map.put("user",auth.getUid());
        Map<String,Object> userMap = new HashMap<>();
        userMap.put(auth.getUid(),auth.getUid());
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
                overridePendingTransition(0, 0);

            }
        });
        v3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ChatActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);

                overridePendingTransition(0, 0);

            }
        });
        v4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                i.putExtra("gender",gender);

                startActivity(i);

                overridePendingTransition(0, 0);

            }
        });
    }

    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();

        if (!checkPlayServices()) {
            Toast.makeText(HomeActivity.this,"You need to install Google Play Services to use the App properly",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        mRewardedVideoAd.resume(this);


        super.onPause();
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
            googleApiClient.disconnect();
        }

    }
    FirebaseFirestore notDb=FirebaseFirestore.getInstance();
    private void sendNotification(final String userId){
        notDb.collection("notificationSetting")
                .document(userId)
                .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getBoolean("match")!=null){
                        if (task.getResult().getBoolean("match")==true){
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
                                            not.put("rate","");
                                            not.put("gender","");
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
                    }
                }
            }
        }).addOnFailureListener(HomeActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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


    private void fetchLocation(double latitude, double longitude, final String uid)
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Map<String,Object> location= new HashMap<>();
        GeoPoint point = new GeoPoint(latitude,longitude);
        if (point!=null) {

            Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                String cityName = addresses.get(0).getAdminArea();
                String city2 = addresses.get(0).getSubAdminArea();
                String code = addresses.get(0).getCountryCode();
                if (city2 == null && cityName == null && code == null) {

                    //location.setText(R.string.konum_bilgisi_yok);
                    location.put("cityName","");
                } else
                    location.put("cityName",city2 + "/" + cityName + "/" + code);
                Map<String, Object> map = new HashMap<>();
                map.put("city", cityName);
                map.put("city2", city2);
                map.put("country", code);
                db.collection(gender)
                        .document(uid)
                        .set(map,SetOptions.merge());

            } else {
                location.put("cityName","");
            }
        }

        location.put("location",point);

        final DocumentReference ref = db.collection(gender).document(uid);

        ref.set(location,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DocumentReference reference = db.collection("allUser").document(uid);
                    reference.set(location,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {


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
    protected void onDestroy() {
        mRewardedVideoAd.resume(this);

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
      //  getAvaibleUserCount(getIntent().getStringExtra("gender"),auth.getUid());
        deleteOneLimit();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }

//        setList();
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
              Button cancel =(Button)noOneIsExist.findViewById(R.id.cancel);
              cancel.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      noOneIsExist.dismiss();
                  }
              });
          }

          @Override
          public void returnTrue(Boolean _true) {

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
    private void getAvaibleUserCount(String gender , final String userId){
        final FirebaseFirestore dbAvaible = FirebaseFirestore.getInstance();

        if (gender.equals("MAN")){
            dbAvaible.collection("WOMANmatch")
                    .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult()!=null){
                            final long size = task.getResult().getDocuments().size();
                            Log.d("avaibleSize", "onComplete: "+size);
                            dbAvaible.collection("oldList")
                                    .document(auth.getUid())
                                    .collection(auth.getUid())
                                    .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult()!=null){
                                            long oldSize = task.getResult().size();
                                            long a  = size-(manSize+oldSize);
                                            Log.d("avaibleSize", "onComplete: " +a);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }else if (gender.equals("WOMAN")){
            dbAvaible.collection("MANmatch")
                    .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult()!=null){
                            final long size = task.getResult().getDocuments().size();
                            Log.d("avaibleSize", "onComplete: "+size);
                            dbAvaible.collection("oldList")
                                    .document(auth.getUid())
                                    .collection(auth.getUid())
                                    .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if (task.getResult()!=null){
                                            long oldSize = task.getResult().size();
                                            long a  = size-(womanSize+oldSize);
                                            Log.d("avaibleSize", "onComplete: " +a);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }

    }


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

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            Log.d("location", "onLocationChanged: "+"Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }
    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRewardedVideoAdLoaded()
    {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed()
    {
            loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem)
    {
        removeFromlimit(limitUser);

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
       loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoCompleted()
    {

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
            TextView location = itemView.findViewById(R.id.userLocation);
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



    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            Log.d("locations", "onConnected: "+"Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            fetchLocation(location.getLatitude(),location.getLongitude(),auth.getUid());
        }

        startLocationUpdates();
    }
    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(HomeActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }

    private void setSetting(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("WOMAN")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot ds : task.getResult().getDocuments()){
                        set(ds.getId());
                    }
                }
            }
        });
    }


    private void set(final String id){

       db.collection("WOMAN")
               .document(id)
               .get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getGeoPoint("location")!=null){
                      GeoPoint  _location=task.getResult().getGeoPoint("location");
                        if (_location!=null){
                            Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(_location.getLatitude() ,  _location.getLongitude(), 1);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (addresses!=null) {
                                String cityName = addresses.get(0).getAdminArea();
                                String city2 = addresses.get(0).getSubAdminArea();
                                String code = addresses.get(0).getCountryCode();

                                Map<String, Object> map = new HashMap<>();
                                map.put("city", cityName);
                                map.put("city2", city2);
                                map.put("country", code);
                                db.collection("WOMAN")
                                        .document(id)
                                        .set(map,SetOptions.merge());
                            }



                        }
                    }
                }
           }
       });


    }

    private void loadRewardedVideoAd() {
       if (!mRewardedVideoAd.isLoaded()){
           mRewardedVideoAd.loadAd("ca-app-pub-1362663023819993/9490956372",
                   new AdRequest.Builder().build());
       }

    }
    ArrayList<String> limitUser = new ArrayList<>();
    private void deleteOneLimit() {
        db.collection("limit")
                .document(auth.getUid())
                .get().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if (documentSnapshot.exists()){
                    Map<String, Object>map = new HashMap<>();
                    map = documentSnapshot.getData();
                    //  Log.d("deleteLimit", "onSuccess: "+map.values());
                    for (Map.Entry<String, Object> e : map.entrySet()) {
                        limitUser.add(e.getValue().toString());
                    }
                }

            }
        }).addOnFailureListener(HomeActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void removeFromlimit(ArrayList<String> users){
        if (users!=null){
            Map<String,Object> map = new HashMap<>();
            map.put(users.get(0),FieldValue.delete());
            db.collection("limit")
                    .document(auth.getUid()).set(map,SetOptions.merge());
        }
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
    public void onUpdateNeeded(final String updateUrl) {
        update.setContentView(R.layout.update_dialog);
        update.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        update.setCancelable(false);
        update.setCanceledOnTouchOutside(false);
        update.show();
        Button no =(Button) update.findViewById(R.id.no);
        Button yes =(Button) update.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectStore(updateUrl);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update.dismiss();
                finishAffinity();
                System.exit(0);

            }
        });

    }
    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
