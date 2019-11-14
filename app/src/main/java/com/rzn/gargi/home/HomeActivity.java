package com.rzn.gargi.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.rzn.gargi.R;
import com.rzn.gargi.chat.ChatActivity;
import com.rzn.gargi.helper.CallBack;
import com.rzn.gargi.helper.CallBackLimit;
import com.rzn.gargi.helper.bottomNavigationHelper;
import com.rzn.gargi.profile.ProfileActivity;
import com.rzn.gargi.topface.TopFaceActivity;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import im.delight.android.location.SimpleLocation;


public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private SimpleLocation mLocation;
     CoordinatorLayout coordinatorLayoutLay;
    String currentUser=auth.getUid();
    String gender;
    private Dialog noOneIsExist;

    private boolean mLocationPermissionGranted = false;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mLocation = new SimpleLocation(this);
        checkMapServices();
        noOneIsExist=new Dialog(this);
        coordinatorLayoutLay=(CoordinatorLayout)findViewById(R.id.coordinatorLay);
        if (!mLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }


        gender = getIntent().getStringExtra("gender");
        if (gender==null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("allUser").document(currentUser);
            ref.get().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<DocumentSnapshot>() {
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

        if (!isNetworkConnected()){
            showSnackBar();
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    private void setList(){

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

    private void getUser(){
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("WOMAN");
            ArrayList<HashMap<String, Object>> formList = new ArrayList<HashMap<String, Object>>();
            HashMap<String, Object> user;
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject a = m_jArry.getJSONObject(i);
                user = new HashMap<String, Object>();

                final String id,name,profileImage,thumb_image,school,about,job,burc,email,gender;
                long age=0,reg_time=0;
                double lat=0,longLat=0,rate = 0;


                user.put("gender","WOMAN");
                if (a.has("name")){
                    name = a.getString("name");
                }else name="";
                user.put("name",name);

                if (a.has("profileImage")){
                    profileImage = a.getString("profileImage");
                }else profileImage="";
                user.put("profileImage",profileImage);

                if (a.has("thumb_image")){
                    thumb_image = a.getString("thumb_image");
                }else thumb_image="";
                user.put("thumb_image",thumb_image);

                if (a.has("school")){
                    school = a.getString("school");
                }else school="";
                user.put("school",school);


                if (a.has("about")){
                    about = a.getString("about");
                }else about="";
                user.put("about",about);

                if (a.has("job")){
                    job = a.getString("job");
                }else job="";
                user.put("job",job);


                if (a.has("burc")){
                    burc = a.getString("burc");
                }else burc="";
                user.put("burc",burc);


                if (a.has("email")){
                    email = a.getString("email");
                }else email="";
                user.put("email",email);

               int count=0,click=0,totalRate=0;

                if (a.has("count")){
                     count = Integer.parseInt(a.getString("count"));
                }
                user.put("count",count);

                if (a.has("click")){
                    click = Integer.parseInt(a.getString("click"));
                }
                if (a.has("age")){
                    age =a.getLong("age");
                }user.put("age",age);
                if (a.has("reg_time")){
                    reg_time =a.getLong("reg_time");
                }user.put("reg_time",reg_time);
                if (a.has("lat")){
                    lat=a.getDouble("lat");
                    user.put("lat",lat);
                }else user.put("lat",lat);
                if (a.has("longLat")){
                    longLat=a.getDouble("longLat");
                    user.put("longLat",longLat);
                }else user.put("longLat",longLat);
                user.put("click",click);





                if (a.has("id")){
                    id = a.getString("id");
                }else id="";
                user.put("id",id);

                if (a.has("rate")){
                    rate = a.getDouble("totalPoint");
                }
                user.put("rate",rate);

                if (a.has("rate")){
                    totalRate=a.getInt("rate");
                    user.put("totalRate",totalRate);
                }else user.put("totalRate",totalRate);

                Map<String,Object> img = new HashMap<>();
                img.put("bir",profileImage);
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("images")
                        .document(id)
                        .set(img).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.d("task", "onComplete->>>>: "+id);

                    }
                });
                formList.add(user);
            }

        }
      catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        mLocation.endUpdates();

        super.onPause();


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
    protected void onStart() {
        super.onStart();
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

        }else  setLayouts(auth.getUid(),gender,rippleBackground,relLayList);

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
              ref.addSnapshotListener(HomeActivity.this, MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                  @Override
                  public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                      long size = queryDocumentSnapshots.getDocuments().size();
                      if (gender.equals("MAN")){
                          if (size>=2){
                              rippleBackground.setVisibility(View.GONE);
                              relLayList.setVisibility(View.VISIBLE);
                          }else {
                              rippleBackground.setVisibility(View.VISIBLE);
                              relLayList.setVisibility(View.GONE);
                              setCenterImage();
                              startAnimation();

                          }
                      }
                      else if (gender.equals("WOMAN")){
                          if (size>=6){
                              rippleBackground.setVisibility(View.GONE);
                              relLayList.setVisibility(View.VISIBLE);
                          }else {
                              rippleBackground.setVisibility(View.VISIBLE);
                              relLayList.setVisibility(View.GONE);
                              setCenterImage();
                              startAnimation();
                          }
                      }

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
    private void setCenterImage(){
        final CircleImageView centerImage =(CircleImageView)findViewById(R.id.centerImage);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
    }
    private void startAnimation(){
        RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        lookForNewMatch(gender,auth.getUid());


    }

    private DocumentSnapshot lastResult;
    int limit_one = 1;
    int limit_two;
    private void lookForNewMatch(final String gender, final String currentUser ){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        checkLimit(gender, currentUser, new CallBack<Boolean>() {
            @Override
            public void returnFalse(Boolean _false) {
                if (gender.equals("MAN")) {
                    Query ref;

                    if (lastResult==null){
                         ref = db.collection("WOMAN"+"match")
                                .orderBy("chatSize",Query.Direction.ASCENDING).limit(1);
                    }

                    else {
                         ref = db.collection("WOMAN"+"match")
                                .orderBy("chatSize",Query.Direction.ASCENDING).startAfter(lastResult).limit(1);
                    }


                    final Task<QuerySnapshot> task = ref.get().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {

                            for (final DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {
                                chekcAfterMatchLimit(currentUser, new CallBack<Boolean>() {
                                    @Override
                                    public void returnFalse(Boolean _false) {
                                        isExist(currentUser, dc.getId(), new CallBack<Boolean>() {
                                            @Override
                                            public void returnFalse(Boolean _false) {
                                                setMsgList(currentUser, dc.getId());

                                                lastResult = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                            }

                                            @Override
                                            public void returnTrue(Boolean _true) {
                                                return;
                                            }
                                        });


                                    }

                                    @Override
                                    public void returnTrue(Boolean _true) {
                                        return;
                                    }
                                });
                                break;
                            }

                        }
                    }).addOnFailureListener(HomeActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Crashlytics.logException(e);
                        }
                    });

                }
                else if (gender.equals("WOMAN")){
                    Query ref;
                    if (lastResult==null){
                        ref = db.collection("MAN"+"match").limit(3)
                                ;
                    }

                    else {
                        ref = db.collection("MAN"+"match")
                                 .startAfter(lastResult);
                    }
                    final Task<QuerySnapshot> task = ref.get().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {

                            for (final DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()) {
                                chekcAfterMatchLimit(currentUser, new CallBack<Boolean>() {
                                    @Override
                                    public void returnFalse(Boolean _false) {
                                        isExist(currentUser, dc.getId(), new CallBack<Boolean>() {
                                            @Override
                                            public void returnFalse(Boolean _false) {
                                                setMsgList(currentUser, dc.getId());

                                                lastResult = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                            }

                                            @Override
                                            public void returnTrue(Boolean _true) {
                                                return;
                                            }
                                        });


                                    }

                                    @Override
                                    public void returnTrue(Boolean _true) {
                                        return;
                                    }
                                });
                                break;
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

            @Override
            public void returnTrue(Boolean _true) {

            }
        });
    }
    private void isExist(String currentUser, String uid , final CallBack<Boolean> isExist){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("msgList")
                .document(currentUser)
                .collection(currentUser).document(uid);
        ref.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        isExist.returnTrue(true);
                    }else isExist.returnFalse(false);
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });

    }
    private void chekcAfterMatchLimit(String uid , final CallBack<Boolean> hasLimit){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> ref = db.collection("msgList")
                .document(uid)
                .collection(uid).get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            long taskCount = task.getResult().size();
                            if (gender.equals("MAN")){
                                if (taskCount>=2){
                                    hasLimit.returnTrue(true);
                                    return;
                                }else hasLimit.returnFalse(true);

                            }else if (gender.equals("WOMAN")){
                                if (taskCount>=6){
                                    hasLimit.returnTrue(true);
                                    return;
                                }else hasLimit.returnFalse(true);
                            }

                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void checkLimit(final String gender , String uid, final CallBack<Boolean> hasLimit){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference reference = db.collection("msgList")
                .document(uid)
                .collection(uid);
        reference.addSnapshotListener(this, MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                long size = queryDocumentSnapshots.size();
                if (gender.equals("MAN")){
                    if (size>=2){
                        hasLimit.returnTrue(true);
                    }else hasLimit.returnFalse(true);

                }else if (gender.equals("WOMAN")){
                    if (size>=6){
                        hasLimit.returnTrue(true);
                    }else hasLimit.returnFalse(true);
                }
            }
        });
    }

    Task<Void> isFinish;
    private void setMsgList(final String  currentUser , final String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String , Object> mapCurrenUser = new HashMap<>();
        Map<String , Object> mapUserId = new HashMap<>();

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


        DocumentReference refCurrenUser = db
                .collection("msgList")
                .document(currentUser)
                .collection(currentUser)
                .document(userId);

        refCurrenUser.set(mapCurrenUser).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    return;
                }
            }
        });
        DocumentReference refUserId = db.collection("msgList")
                .document(userId)
                .collection(userId)
                .document(currentUser);
         isFinish = refUserId.set(mapUserId).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    return;
                }
            }
        });

    }


}
