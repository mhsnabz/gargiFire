package com.rzn.gargi.App;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rzn.gargi.helper.CallBack;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;


import java.util.HashMap;
import java.util.Map;

public class gargi extends Application {

    @Override
    public void onCreate() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
                if (user.getUid()!=null){

                    db.collection("allUser")
                            .document(user.getUid()).addSnapshotListener( new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            final String gender = documentSnapshot.getString("gender");
                            if (!gender.isEmpty()){

                                    Log.d("genderApp", "onEvent: "+gender);
                                    checkCount(gender, new CallBack<Boolean>() {
                                        @Override
                                        public void returnFalse(Boolean _false) {
                                            Log.d("genderApp", "onEvent: "+"has not limit");
                                        }

                                        @Override
                                        public void returnTrue(Boolean _true) {
                                            Log.d("genderApp", "onEvent: "+"has  limit");
                                            removeFromMatch(gender,user.getUid());


                                        }
                                    },user.getUid());

                            }
                        }
                    });
                }

        }
        //set();
        super.onCreate();
    }


    void checkCount(@NotNull final String gender, final CallBack<Boolean> _return, final String currentUser){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("msgList")
                .document(currentUser)
                .collection(currentUser);
        chatSize = ref.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                Log.d("chatSize", "onEvent: " + queryDocumentSnapshots.getDocuments().size());
                if (gender.equals("MAN")){
                    if (queryDocumentSnapshots.getDocuments().size()>=2){
                        _return.returnTrue(true);

                    }else{
                        _return.returnFalse(false);
                        getUserInfo(currentUser,queryDocumentSnapshots.getDocuments().size());

                    }
                }else if (gender.equals("WOMAN")){
                    if (queryDocumentSnapshots.getDocuments().size()>=6){

                        _return.returnTrue(true);

                    }else {
                        _return.returnFalse(false);
                        getUserInfo(currentUser,queryDocumentSnapshots.getDocuments().size());

                    }
                }

            }
        });



    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        chatSize.remove();
    }
    ListenerRegistration chatSize;

   private void getUserInfo(final String userId, final int size){
       final Map<String,Object> map = new HashMap<>();

       FirebaseFirestore db = FirebaseFirestore.getInstance();
      db.collection("allUser")
                .document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                     if (task.isSuccessful()){
                         if (task.getResult().getLong("age")!=null){
                             long age = task.getResult().getLong("age");
                             map.put("age",age);


                         }
                            String gender = task.getResult().getString("gender");
                            String burc= task.getResult().getString("burc");
                            map.put("burc",burc );
                            map.put("chatSize",size);
                            addOnChat(gender,userId,map);
                     }

                 }
             });
   }
    Task<Void> db;
   private void addOnChat(String gender , String userID , Map<String, Object> map){
        db = FirebaseFirestore.getInstance()
               .collection(gender+"match")
               .document(userID)
               .set(map, SetOptions.merge());

   }

   private void getUser(){
       FirebaseFirestore db = FirebaseFirestore.getInstance();
       CollectionReference ref = db.collection("MAN");
       ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){
                getUserInfo(dc.getId(),0);
                Log.d("userId", "onSuccess: "+dc.getId());
            }
           }
       });

   }
   private void removeFromMatch(String gender , String userId){
       FirebaseFirestore db = FirebaseFirestore.getInstance();

       Task<Void> ref = db.collection(gender+"match")
               .document(userId).delete();


   }

}
