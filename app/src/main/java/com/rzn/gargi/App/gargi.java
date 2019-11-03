package com.rzn.gargi.App;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.rzn.gargi.helper.CallBack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class gargi extends Application {

    @Override
    public void onCreate() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("allUser")
                    .document(auth.getUid()).addSnapshotListener( new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    String gender = documentSnapshot.getString("gender");
                    if (!gender.isEmpty()){
                        if (gender.equals("MAN")){
                            Log.d("genderApp", "onEvent: "+gender);
                            checkCount(gender, new CallBack<Boolean>() {
                                @Override
                                public void returnFalse(Boolean _false) {
                                    Log.d("genderApp", "onEvent: "+"has not limit");

                                }

                                @Override
                                public void returnTrue(Boolean _true) {
                                    Log.d("genderApp", "onEvent: "+"has  limit");

                                }
                            });
                        }else if (gender.equals("WOMAN")){
                            Log.d("genderApp", "onEvent: "+gender);

                        }
                    }
                }
            });
        }
        super.onCreate();
    }

    private void checkCount(String gender, final CallBack<Boolean> _return ){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (gender.equals("MAN")){
            db.collection("chat")
                    .document("messegeTime")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            if (documentSnapshot.exists()){
                                long count = documentSnapshot.getLong("count");
                                if (count>=2){
                                    _return.returnTrue(true);
                                }else _return.returnFalse(false);
                            }else _return.returnFalse(false);

                        }
                    });
        }else if (gender.equals("WOMAN")){
            db.collection("chat")
                    .document("messegeTime")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            if (documentSnapshot.exists()){
                                long count = documentSnapshot.getLong("count");
                                if (count>=2){
                                    _return.returnTrue(true);
                                }else _return.returnFalse(false);
                            }else _return.returnFalse(false);

                        }
                    });
        }

    }

}
