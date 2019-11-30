package com.rzn.gargi.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rzn.gargi.Log_Sign.FaceOrGoogle;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.Shard;
import com.rzn.gargi.home.HomeActivity;

public class SplashScreen extends AppCompatActivity {
    String currentUser;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo =(ImageView)findViewById(R.id.logo);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.mytransistion);

        logo.startAnimation(animation);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {

                    currentUser = FirebaseAuth.getInstance().getUid();


                    if (currentUser==null){
                        Intent intent = new Intent(SplashScreen.this, FaceOrGoogle.class);
                        startActivity(intent);
                        finish();
                    }else {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                       DocumentReference ref = db.collection("allUser").document(currentUser);
                       ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               if (task.isSuccessful()){
                                   DocumentSnapshot doc = task.getResult();
                                   if (doc!=null){
                                       String gender = doc.getString("gender");
                                       final Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                                       i.putExtra("gender",gender);
                                        startActivity(i);
                                        finish();
                                   }
                               }
                           }
                       });

                    }
                }
            }
        };
        thread.start();

    }

    public Task<Integer> getCount(final DocumentReference ref) {
        // Sum the count of each shard in the subcollection
        return ref.collection("shards").get()
                .continueWith(new Continuation<QuerySnapshot, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        int count = 0;
                        for (DocumentSnapshot snap : task.getResult()) {
                            Shard shard = snap.toObject(Shard.class);
                            count += shard.count;
                        }
                        return count;
                    }
                });
    }
}

