package com.rzn.gargi.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.bottomNavigationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import im.delight.android.location.SimpleLocation;


public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private SimpleLocation mLocation;

    String currentUser=auth.getUid();
    String gender;

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

        if (!mLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }

     //  getUser();
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

    }






        ///TODO: bottom navigiton setter
    private void setNavigation(String  gender){
        BottomNavigationView view;
        view=(BottomNavigationView)findViewById(R.id.navigationController);

        bottomNavigationHelper.enableNavigation(HomeActivity.this,view,gender);
        Menu menu = view.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);

        BottomNavigationMenuView bottomNavigationMenuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        final View v = bottomNavigationMenuView.getChildAt(2); // number of menu from left


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
                    //fetchLocation(latitude, longitude, auth.getCurrentUser().getUid());
                    GeoPoint point = new GeoPoint(latitude,longitude);


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
        final DocumentReference ref = db.collection(gender).document(uid);
        ref.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DocumentReference reference = db.collection("allUser").document(uid);
                    reference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                  //  fetchLocation(latitude, longitude, auth.getCurrentUser().getUid());


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
           // fetchLocation(latitude, longitude, auth.getCurrentUser().getUid());

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
}
