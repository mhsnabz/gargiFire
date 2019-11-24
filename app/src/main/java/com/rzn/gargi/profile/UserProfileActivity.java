package com.rzn.gargi.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.Rate;
import com.rzn.gargi.helper.RatingDialog;
import com.rzn.gargi.helper.UserInfo;
import com.rzn.gargi.helper.sliderAdapter;
import com.rzn.gargi.home.HomeActivity;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import im.delight.android.location.SimpleLocation;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private  TextView [] mDots;
    private LinearLayout dots;
    ArrayList<String> _images;
    private ImageView horoscope;
    private  ViewPager pager_image;
    private Dialog dialogReport,report_dialog;
    private Dialog dialog;
    private String userId ;
    private long _oldRate,_totalRate;
    private String currentUser = auth.getUid();
    private FloatingActionButton back;
    private CircleImageView insta,facebook,twitter,snap;
    private RelativeLayout rel_rate,rel_school_job,rel_about,rel_social;
    private TextView name ,age,point,school,job,about;
    private ScaleRatingBar ratingBar;
    private String facebookUrl ="http://www.facebook.com/";
    private String instaUrl="http://www.instagram.com/";
    private String twitterUrl="http://www.twitter.com/";
    private String snapchatUrl="http://www.snapchat.com/add/";
    private TextView location_tv;
    private SimpleLocation location;
    RelativeLayout relSocial,relAbout,relJobSchool;
    long old_rate,total_rate;
    List<com.rzn.gargi.helper.Rate> rates;
    Rate rateModel;
    String gender ;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        dialog= new Dialog(this);
        userId= getIntent().getStringExtra("userId");
         gender= getIntent().getStringExtra("gender");
        relAbout =(RelativeLayout)findViewById(R.id.relLay_about);
        relJobSchool=(RelativeLayout)findViewById(R.id.relLay_job_school);
        relSocial=(RelativeLayout)findViewById(R.id.relLay_social);
        location_tv=(TextView)findViewById(R.id.location);
       // rates = new ArrayList<>();
        dots =(LinearLayout)findViewById(R.id.dots);
        pager_image=(ViewPager)findViewById(R.id.pager_image);
        location = new SimpleLocation(this);
        _images= new ArrayList<>();
        age=(TextView)findViewById(R.id.age);
        name=(TextView)findViewById(R.id.name);
        point=(TextView)findViewById(R.id.point);
        job=(TextView)findViewById(R.id.job);
        school=(TextView)findViewById(R.id.school);
        about=(TextView)findViewById(R.id.about);
        insta=(CircleImageView)findViewById(R.id.instagram);
        facebook=(CircleImageView)findViewById(R.id.facebook);
        snap=(CircleImageView)findViewById(R.id.snapchat);
        twitter=(CircleImageView)findViewById(R.id.twit);
        ratingBar=(ScaleRatingBar)findViewById(R.id.rate);
        getInfo(getIntent().getStringExtra("userId"),getIntent().getStringExtra("gender"));
        ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, final float rating, boolean fromUser) {
                if (fromUser){
                    if (rating>0){
                        Map<String,Object> crrntUser =new HashMap<>();
                        final Map<String,Long> rate = new HashMap<>();
                        rate.put("rate",(long)rating);
                        crrntUser.put(auth.getUid(),rate);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("rate")
                                .document(userId)
                                .collection(userId)
                                .document(auth.getUid())
                                .set(rate, SetOptions.merge())
                                .addOnCompleteListener(UserProfileActivity.this
                                        , new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    setRate(old_rate,(long)rating,toplamOy,oySayisi,getIntent().getStringExtra("gender"),getIntent().getStringExtra("userId"));
                                                    sendNotification(getIntent().getStringExtra("userId"),String.valueOf(rating));
                                                }
                                            }
                                        });
                    }


                }
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(facebookUrl));
                startActivity(intent);
            }
        });
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(instaUrl));
                startActivity(intent);
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(twitterUrl));
                startActivity(intent);
            }
        });
        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(snapchatUrl));
                startActivity(intent);
            }
        });
        about=(TextView)findViewById(R.id.about);
        point=(TextView)findViewById(R.id.point);
        pager_image.addOnPageChangeListener(pagerListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listener.remove();
       // oldRateListener.remove();
    }


    ListenerRegistration listener;
    private void loadProfileInfo(String gender,
                                 final CircleImageView insta,
                                 final CircleImageView facebook,
                                 final CircleImageView twitter,
                                 final CircleImageView snap,
                                 final TextView name,
                                 final TextView age,
                                 final TextView job,
                                 final TextView school,
                                 final TextView about,
                                 final ImageView burc,
                                 final TextView locatipn_tv
                                 )
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.document(gender+"/"+userId);
        listener = user.addSnapshotListener(UserProfileActivity.this, MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (documentSnapshot != null) {
                        UserInfo info = documentSnapshot.toObject(UserInfo.class);
                        calculateRate(info.getCount(),info.getTotalRate());

                        if (info.getName() != null) {
                            name.setText(info.getName());
                        }
                        /*if (info.getInsta() != null) {
                            insta.setVisibility(View.VISIBLE);
                            instaUrl = instaUrl + info.getInsta();
                        } else insta.setVisibility(View.GONE);
                        if (info.getTwitter() != null) {
                            twitter.setVisibility(View.VISIBLE);
                            twitterUrl = twitterUrl + info.getTwitter();
                        } else twitter.setVisibility(View.GONE);
                        if (info.getFace() != null) {
                            facebook.setVisibility(View.VISIBLE);
                            facebookUrl = facebookUrl + info.getFace();
                        } else facebook.setVisibility(View.GONE);
                        if (info.getSnap() != null) {
                            snap.setVisibility(View.VISIBLE);
                            snapchatUrl = snapchatUrl + info.getSnap();
                        } else snap.setVisibility(View.GONE);*/
                        if (info.getJob() != null) {
                            job.setText(info.getJob());
                        }
                        if (info.getSchool() != null) {
                            school.setText(info.getSchool());

                        }
                        if (info.getAbout() != null) {
                            about.setText(info.getAbout());
                        }
                        age.setText(convert(info.getAge()));
                        if (info.getBurc()!=null){
                            getHoroscope(burc,info.getBurc());
                        }else burc.setVisibility(View.GONE);

                    }
                }
            }
        });


    }

    private void getInfo(String userId,String gender){
        getLocation();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(gender)
                .document(userId).get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult().getString("about").isEmpty()){
                            Log.d("userInfo", "about: "+"isEmpty");
                            relAbout.setVisibility(View.GONE);
                        }else  relAbout.setVisibility(View.VISIBLE);

                        if (task.getResult().getString("job").isEmpty()&&task.getResult().getString("school").isEmpty()){
                            relJobSchool.setVisibility(View.GONE);
                        }else relJobSchool.setVisibility(View.VISIBLE);


                        if (task.getResult().getString("face").isEmpty()
                            && task.getResult().getString("insta").isEmpty()&&
                                task.getResult().getString("snap").isEmpty()&&
                                task.getResult().getString("twitter").isEmpty()  ){
                            relSocial.setVisibility(View.GONE);
                        }else{
                            relSocial.setVisibility(View.VISIBLE);

                            if (!task.getResult().getString("face").isEmpty()){
                                facebook.setVisibility(View.VISIBLE);
                                facebookUrl = facebookUrl + task.getResult().getString("face");

                            }else facebook.setVisibility(View.GONE);

                            if (!task.getResult().getString("insta").isEmpty()){
                                insta.setVisibility(View.VISIBLE);
                                instaUrl = instaUrl + task.getResult().getString("insta");
                            }else insta.setVisibility(View.GONE);

                            if (!task.getResult().getString("snap").isEmpty()){
                                snap.setVisibility(View.VISIBLE);
                                snapchatUrl = snapchatUrl + task.getResult().getString("snap");

                            }else snap.setVisibility(View.GONE);

                            if (!task.getResult().getString("twitter").isEmpty()){
                                twitter.setVisibility(View.VISIBLE);
                                twitterUrl = twitterUrl +task.getResult().getString("twitter");

                            }else twitter.setVisibility(View.GONE);
                        }




                    }
            }
        });
    }

    ViewPager.OnPageChangeListener pagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDostsIndicator(position,_images);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private String convert(long milisecond){
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long time= currentDate.getTime().getTime() / 1000 - milisecond / 1000;
        int years = Math.round(time) / 31536000;
        int months = Math.round(time - years * 31536000) / 2628000;
        return " "+String.valueOf(years);
    }

    private void addDostsIndicator(int position, ArrayList<String> images){

        dots.removeAllViews();
        mDots= new TextView[images.size()];

        for (int i =0 ;i<mDots.length;i++){
            mDots[i] = new TextView(this);
            mDots[i].setTextSize(35);
            mDots[i].setText(Html.fromHtml("&#8212;"));
            mDots[i].setTextColor(getResources().getColor(R.color.transparentWhite));
            dots.addView(mDots[i]);
        }
        if (mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }
    private void loadImages(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference images = db.document("images"+"/"+userId);
        Task<DocumentSnapshot> taska = images.get().addOnCompleteListener(UserProfileActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    com.rzn.gargi.helper.images imagess = task.getResult().toObject(com.rzn.gargi.helper.images.class);
                    if (imagess!=null){
                        if (imagess.getBir() != null) {
                            _images.add(imagess.getBir());
                        }

                        if (imagess.getIki() != null) {
                            _images.add(imagess.getIki());
                        }
                        if (imagess.getUc() != null) {
                            _images.add(imagess.getUc());
                        }
                        if (imagess.getDort() != null) {
                            _images.add(imagess.getDort());
                        }
                        if (imagess.getBes() != null) {
                            _images.add(imagess.getBes());
                        }
                        if (imagess.getAlti() != null) {
                            _images.add(imagess.getAlti());
                        }
                        sliderAdapter adapter = new sliderAdapter(UserProfileActivity.this, _images);
                        pager_image.setAdapter(adapter);
                        task.isComplete();
                    }


                }
            }
        });


    }
    private void getLocation(){
        FirebaseFirestore  db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection(gender).document(userId);
        ref.get().addOnCompleteListener(UserProfileActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getGeoPoint("location")!=null){
                        GeoPoint locaiton = task.getResult().getGeoPoint("location");
                        if (locaiton!=null){
                            double lat=locaiton.getLatitude();
                            double longLat=locaiton.getLongitude();

                            Geocoder geocoder = new Geocoder(UserProfileActivity.this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(lat, longLat, 1);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String cityName = addresses.get(0).getAdminArea();
                            String city2 = addresses.get(0).getSubAdminArea();
                            String code = addresses.get(0).getCountryCode();
                            if (city2==null && cityName ==null && code == null){
                                location_tv.setVisibility(View.GONE);

                                location_tv.setText(R.string.konum_bilgisi_yok );
                            }
                            else
                                location_tv.setText(city2+" / "+cityName+" / "+code);
                        }
                    }

                }

            }
        });

    }
    public void getHoroscope(ImageView burc,String _burc){
         burc =(ImageView) findViewById(R.id.horoscope);
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

    ListenerRegistration oldRateListener,totalRate,calculateRate;
    private void getOldRate(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("rate")
                .document(userId).collection(userId)
                .document(auth.getUid());
        ref.get().addOnSuccessListener(UserProfileActivity.this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot!=null)  {
                    if (documentSnapshot.getLong("rate")!=null){
                        ratingBar.setRating((float)documentSnapshot.getLong("rate"));
                        Log.d("oldRate", "onSuccess: "+(float)documentSnapshot.getLong("rate"));
                        old_rate =documentSnapshot.getLong("rate");
                    }
                    else{
                        old_rate =0;
                        Log.d("oldRate", "onSuccess: "+(float)documentSnapshot.getLong("rate"));
                    }
                }
            }
        });

    }

    long toplamOy;
    long oySayisi;
    private  void getRateValues(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getIntent().getStringExtra("gender"))
                .document(getIntent().getStringExtra("userId"))
                .get().addOnCompleteListener(UserProfileActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task!=null){
                        toplamOy = task.getResult().getLong("totalRate");
                        oySayisi = task.getResult().getLong("count");
                        Log.d("oldRate", "onSuccess: "+toplamOy);
                        Log.d("oldRate", "onSuccess: "+oySayisi);

                    }
                }
            }
        }).addOnFailureListener(UserProfileActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });
    }
    private void setRate(long old_rate,long newRate , long totalRate,long count,String gender,String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (old_rate==0){
            count++;
           if (gender.equals("MAN")){

               totalRate = totalRate+newRate;
               Map<String ,Object> map = new HashMap<>();
               map.put("count",count);
               map.put("totalRate",totalRate);
               db.collection("MAN")
                       .document(userId).set(map,SetOptions.merge());


           }else if (gender.equals("WOMAN")){

               totalRate = totalRate+newRate;
               Map<String ,Object> map = new HashMap<>();
               map.put("count",count);
               map.put("totalRate",totalRate);
               db.collection("WOMAN")
                       .document(userId).set(map,SetOptions.merge());


           }
        }else {
            if (gender.equals("MAN")){
                long _value1 = totalRate-old_rate;
                totalRate=_value1+newRate;
                Map<String ,Object> map = new HashMap<>();
                map.put("totalRate",totalRate);
                db.collection("MAN")
                        .document(userId).set(map,SetOptions.merge());
            }else if (gender.equals("WOMAN")){
                long _value1 = totalRate-old_rate;
                totalRate=_value1+newRate;
                Map<String ,Object> map = new HashMap<>();
                map.put("totalRate",totalRate);
                db.collection("WOMAN")
                        .document(userId).set(map,SetOptions.merge());
            }
        }
    }

    private void calculateRate(long count , long totalRate){
        double rating = (double) totalRate/count;
        rating=Math.round(rating*100.0)/100.0;
        point.setText(String.valueOf(rating));

    }
    @Override
    protected void onStart() {
        super.onStart();
        loadProfileInfo(getIntent().getStringExtra("gender"),insta,facebook,twitter,snap,name,age,job,school,about,horoscope,location_tv);
        loadImages();
        getOldRate();
        getRateValues();
        System.gc();
    }

    public void back(View view)
    {
        finish();
    }
    FirebaseFirestore notDb = FirebaseFirestore.getInstance();
    private void sendNotification(final String userId, final String rate){
        notDb.collection("allUser")
                .document(userId)
                .get().addOnCompleteListener(UserProfileActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getString("tokenID")!=null ){

                        Map<String,Object> not=new HashMap<>();
                        not.put("from",auth.getUid());
                        not.put("type","rate");
                        not.put("rate",rate);
                        not.put("getter",userId);
                        not.put("name","");
                        not.put("tokenID",task.getResult().getString("tokenID"));
                        if (getIntent().getStringExtra("gender").equals("MAN")){
                            not.put("gender","WOMAN");
                        }else if (getIntent().getStringExtra("gender").equals("WOMAN"))
                            not.put("gender","MAN");
                        notDb.collection("notification")
                                .document(userId)
                                .collection("notification").add(not).addOnCompleteListener(UserProfileActivity.this, new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    Log.d("sendNotification", "onComplete: "+"task.isSuccessful");
                                }
                            }
                        }).addOnFailureListener(UserProfileActivity.this, new OnFailureListener() {
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
