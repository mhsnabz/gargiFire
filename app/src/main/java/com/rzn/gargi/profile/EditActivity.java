package com.rzn.gargi.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.UserInfo;
import com.rzn.gargi.helper.images;
import com.rzn.gargi.helper.sliderAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {
    com.rzn.gargi.helper.sliderAdapter sliderAdapter;
    com.github.clans.fab.FloatingActionButton edit;
    CircleImageView insta,facebook,twitter,snap;
    TextView name , age,school,job,about,point;
    String facebookUrl ="http://www.facebook.com/";
    String instaUrl="http://www.instagram.com/";
    String twitterUrl="http://www.twitter.com/";
    String snapchatUrl="http://www.snapchat.com/add/";
    LinearLayout dots ;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUser = auth.getUid();
    ImageView horoscope;
    String  gender;
    ArrayList<images> img;
    ViewPager viewPager;
    Dialog wating;
    ArrayList<String> _images;
    TextView [] mDots;
    RelativeLayout relSocial,relAbout,relJobSchool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        _images= new ArrayList<>();
        gender=getIntent().getStringExtra("gender");
        dots=(LinearLayout)findViewById(R.id.dots);
        horoscope=(ImageView)findViewById(R.id.horoscope);
        viewPager=(ViewPager)findViewById(R.id.pager_image);
        wating=new Dialog(this);
        insta=(CircleImageView)findViewById(R.id.instagram);
        facebook=(CircleImageView)findViewById(R.id.facebook);
        snap=(CircleImageView)findViewById(R.id.snap);
        twitter=(CircleImageView)findViewById(R.id.twit);
        age=(TextView)findViewById(R.id.age);
        name=(TextView)findViewById(R.id.name);
        job=(TextView)findViewById(R.id.job);
        school=(TextView)findViewById(R.id.school);
        edit=(FloatingActionButton) findViewById(R.id.edit);
        relAbout =(RelativeLayout)findViewById(R.id.relLay_about);
        relJobSchool=(RelativeLayout)findViewById(R.id.relLay_job_school);
        relSocial=(RelativeLayout)findViewById(R.id.relLay_social);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wating .setContentView(R.layout.wait_dialog);

                wating.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                wating.setCanceledOnTouchOutside(false);
                wating.show();
                final Intent i = new Intent(EditActivity.this,EditProfileActivity.class);
                i.putExtra("gender",getIntent().getStringExtra("gender"));
                startActivity(i);
                wating.dismiss();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

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
        viewPager.addOnPageChangeListener(pagerListener);
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
                                   final ImageView burc)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.document(gender+"/"+auth.getUid());
        listener = user.addSnapshotListener(EditActivity.this, MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    if (documentSnapshot != null) {
                        UserInfo info = documentSnapshot.toObject(UserInfo.class);
                        if (info.getName() != null) {
                            name.setText(info.getName());
                        }
                        if (info.getInsta() != null) {
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
                        } else snap.setVisibility(View.GONE);
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

                        if (info.getInsta() != null || info.getTwitter() != null || info.getFace() != null ||info.getSnap() != null){
                            relSocial.setVisibility(View.VISIBLE);
                        }else relSocial.setVisibility(View.GONE);
                        if (info.getAbout() != null){
                            relAbout.setVisibility(View.VISIBLE);
                        }else relAbout.setVisibility(View.GONE);
                        if (info.getJob()!=null || info.getSchool()!=null){
                            relJobSchool.setVisibility(View.VISIBLE);
                        }else {
                            relJobSchool.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });


    }    private void getHoroscope(ImageView burc,String _burc) {
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

    private String convert(long milisecond){
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long time= currentDate.getTime().getTime() / 1000 - milisecond / 1000;
        int years = Math.round(time) / 31536000;
        int months = Math.round(time - years * 31536000) / 2628000;
        return " "+String.valueOf(years);
    }

    private void loadImages(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference images = db.document("images"+"/"+auth.getUid());
        Task<DocumentSnapshot> task = images.get().addOnCompleteListener(EditActivity.this, new OnCompleteListener<DocumentSnapshot>() {
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
                        sliderAdapter adapter = new sliderAdapter(EditActivity.this, _images);
                        viewPager.setAdapter(adapter);
                        task.isComplete();
                    }


                }
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        loadProfileInfo(getIntent().getStringExtra("gender"),insta,facebook,twitter,snap,name,age,job,school,about,horoscope);
        loadImages();
    }
    private void addDostsIndicator(int position,ArrayList<String> images){

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listener.remove();
        System.gc();
    }

    public void back(View view) {
        Intent i = new Intent(EditActivity.this, ProfileActivity.class);
        i.putExtra("gender",gender);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.slide_in_left,0);

    }
}
