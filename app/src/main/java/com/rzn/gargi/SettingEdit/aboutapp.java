package com.rzn.gargi.SettingEdit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rzn.gargi.R;

public class aboutapp extends AppCompatActivity {
    ImageView logo;
    RelativeLayout rel;
    TextView name , versiyon , website;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutapp);
        logo =(ImageView)findViewById(R.id.logo);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.mytransistion);
        name =(TextView)findViewById(R.id.name);
        versiyon =(TextView)findViewById(R.id.version);
        website =(TextView)findViewById(R.id.website);
        rel=(RelativeLayout)findViewById(R.id.rel);
        rel.startAnimation(animation);
        //   logo.startAnimation(animation);
        Thread thread= new Thread(){
            @Override
            public void run() {
                try{
                    sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

}
