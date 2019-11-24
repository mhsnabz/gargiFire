package com.rzn.gargi.App;

import android.app.Application;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.rzn.gargi.chat.OneToOneChat;
import com.rzn.gargi.helper.CallBack;
import com.rzn.gargi.helper.Rate;
import com.rzn.gargi.home.HomeActivity;
import com.rzn.gargi.profile.UserProfileActivity;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gargi extends Application {
    String currentUser;
    @Override
    public void onCreate() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();

                    //   getUser();
                    deleteChat("ml20r64rnmXBpPHNpO8tbSW5Y8v1","xdmK5en0hAOU1ZeT8ORvMtEn97i1");
                    currentUser = firebaseAuth.getCurrentUser().getUid();
                    String tokenID = FirebaseInstanceId.getInstance().getToken();
                    Map<String,Object> map=new HashMap<>();
                    map.put("tokenID",tokenID);
                    db.collection("allUser")
                            .document(currentUser)
                            .set(map,SetOptions.merge());
                    db.collection("allUser")
                            .document(currentUser).addSnapshotListener( new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            final String gender = documentSnapshot.getString("gender");
                            if (documentSnapshot.getString("gender")!=null){
                                setChatSize(gender, firebaseAuth.getCurrentUser().getUid());
                                setLimit(gender,currentUser);
                                checkLimit(gender, currentUser, new CallBack<Boolean>() {
                                    @Override
                                    public void returnFalse(Boolean _false) {
                                        checkCount(gender, new CallBack<Boolean>() {
                                            @Override
                                            public void returnFalse(Boolean _false) {
                                                Log.d("LimitAndSize", "returnFalse: "+"has not limit and size");
                                            }

                                            @Override
                                            public void returnTrue(Boolean _true) {
                                                Log.d("LimitAndSize", "returnFalse: "+"has limit and size");
                                                removeFromMatch(gender,currentUser);
                                                Log.d("LimitAndSize", "returnFalse: "+"deleted = " + currentUser);

                                            }
                                        },currentUser);
                                    }

                                    @Override
                                    public void returnTrue(Boolean _true) {

                                    }
                                });
                            }
                        }
                    });


                }
            }
        });

        //set();
        super.onCreate();
    }
    List<Rate> rates;
    Rate rateModel;

    private void getManUser(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("WOMAN")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots!=null){
                    for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                       getTotalRate(ds.getId());
                    }
                }
            }
        });
    }
    private void getTotalRate(final String userId){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("rate")
                .document(userId).collection(userId);

        ref.addSnapshotListener( MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    return;
                }else {
                    rates= new ArrayList<>();
                    if (queryDocumentSnapshots!=null){
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()){
                            if (dc.getType()==DocumentChange.Type.ADDED)
                            {
                                Log.d("userId", "onEvent: "+userId);
                                rateModel =dc.getDocument().toObject(Rate.class);
                                rates.add(rateModel);
                                setRateModel(rates,userId,"WOMAN");
                            }
                        }
                    }
                }
            }
        });




    }



    private void setRateModel(List<Rate> rates,String userId,String gender){
        long toplamPuan = 0;
        for (int i =0;i<rates.size();i++)
        {

            Log.d("rates", "setRateModel: "+rates.get(i).getRate());
            toplamPuan=toplamPuan+rates.get(i).getRate();
            Log.d("rates", "setRateModel: "+toplamPuan);
            Log.d("rates", "setRateModel: "+rates.size());
            Log.d("rates", "setRateModel: "+"totalRate= " + (double)toplamPuan/rates.size()  );

        }
        Map<String ,Object > count = new HashMap<>();
        Map<String ,Object > totalRate = new HashMap<>();
        Map<String ,Object > rate = new HashMap<>();
        count.put("count",rates.size());
        totalRate.put("totalRate",toplamPuan);
        rate.put("rate",(double)toplamPuan/rates.size());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection(gender)
                .document(userId);
        ref.set(count, SetOptions.merge())
        ;
        ref.set(totalRate, SetOptions.merge())
        ;

        ref.set(rate, SetOptions.merge())
        ;
    }

    void  checkLimit(final String gender , final String  currentUser , final CallBack<Boolean> limit){
        final FirebaseFirestore db  = FirebaseFirestore.getInstance();
        if (gender.equals("MAN")){
            db.collection("limit")
                    .document(currentUser).addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot!=null){
                        if (documentSnapshot.getData()!=null){
                            if (documentSnapshot.getData().size()<10){
                                limit.returnFalse(false);
                                Log.d("LimitAndSize", "returnFalse: "+"size = " + documentSnapshot.getData().size());

                            }else {
                                limit.returnTrue(true);
                                removeFromMatch(gender,currentUser);
                                Log.d("LimitAndSize", "returnFalse: "+"deleted = " + currentUser);
                                Log.d("LimitAndSize", "returnFalse: "+"size = " + documentSnapshot.getData().size());


                            }
                        }else limit.returnFalse(false);

                    }else limit.returnFalse(false);
                }
            });
        }else if (gender.equals("WOMAN")){
            db.collection("limit")
                    .document(currentUser).addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot!=null){
                        if (documentSnapshot.getData()!=null){
                            if (documentSnapshot.getData().size()<35){
                                limit.returnFalse(false);
                                Log.d("LimitAndSize", "returnFalse: "+"size = " + documentSnapshot.getData().size());

                            }else {
                                limit.returnTrue(true);
                                removeFromMatch(gender,currentUser);
                                Log.d("LimitAndSize", "returnFalse: "+"deleted = " + currentUser);
                                Log.d("LimitAndSize", "returnFalse: "+"size = " + documentSnapshot.getData().size());


                            }
                        }else  limit.returnFalse(false);

                    }else limit.returnFalse(false);
                }
            });
        }
    }
    void checkCount(@NotNull final String gender, final CallBack<Boolean> _return, final String currentUser){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("msgList")
                .document(currentUser)
                .collection(currentUser);
        chatSize = ref.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (gender.equals("MAN")){
                    if (queryDocumentSnapshots.getDocuments().size()>=2){
                        _return.returnTrue(true);

                    }else{
                        _return.returnFalse(false);
                        getUserInfo(currentUser,queryDocumentSnapshots.getDocuments().size());
                        Log.d("LimitAndSize", "returnFalse: "+"added = " + currentUser);


                    }
                }else if (gender.equals("WOMAN")){
                    if (queryDocumentSnapshots.getDocuments().size()>=6){

                        _return.returnTrue(true);

                    }else {
                        _return.returnFalse(false);
                        getUserInfo(currentUser,queryDocumentSnapshots.getDocuments().size());
                        Log.d("LimitAndSize", "returnFalse: "+"added = " + currentUser);


                    }
                }

            }
        });



    }

    private void setChatSize(final String gender , final String userId){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("msgList")
                .document(userId)
                .collection(userId).addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if ( queryDocumentSnapshots.getDocuments()!=null){
                    long size= queryDocumentSnapshots.getDocuments().size();
                    Map<String,Object> map = new HashMap<>();
                    map.put("size",size);
                    if (gender.equals("MAN")){

                        db.collection("ManSize")
                                .document(userId).set(map,SetOptions.merge());
                    }else if (gender.equals("WOMAN")){

                        db.collection("WomanSize")
                                .document(userId).set(map,SetOptions.merge());
                    }
                }


            }
        });
    }
    private void setLimit(final String gender , final String userId){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("limit")
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot!=null){
                            if (documentSnapshot.getData()!=null){
                                long limit= documentSnapshot.getData().size();
                                Map<String , Object > map = new HashMap<>();
                                map.put("limit",limit);
                                if (gender.equals("MAN")){
                                    db.collection("ManSize")
                                            .document(userId)
                                            .set(map,SetOptions.merge());
                                }else if (gender.equals("WOMAN")){
                                    db.collection("WomanSize")
                                            .document(userId)
                                            .set(map,SetOptions.merge());
                                }
                            }else {
                                if (gender.equals("MAN")){
                                    Map<String , Object > map = new HashMap<>();
                                    map.put("limit",0);
                                    db.collection("ManSize")
                                            .document(userId)
                                            .set(map,SetOptions.merge());

                                }else if (gender.equals("WOMAN")){
                                    Map<String , Object > map = new HashMap<>();
                                    map.put("limit",0);
                                    db.collection("WomanSize")
                                            .document(userId)
                                            .set(map,SetOptions.merge());
                                }
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
       CollectionReference ref = db.collection("WOMAN");
       ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){
               // getUserInfo(dc.getId(),0);
                Log.d("userId", "onSuccess: "+dc.getId());
                setSocial("WOMAN",dc.getId());
            }
           }
       });

   }
   private void removeFromMatch(String gender , String userId){
       FirebaseFirestore db = FirebaseFirestore.getInstance();

       Task<Void> ref = db.collection(gender+"match")
               .document(userId).delete();


   }


   private void setSocial(String gender , String userID){
       FirebaseFirestore db =FirebaseFirestore.getInstance();


       Map<String,Object> map= new HashMap<>();
       map.put("insta","");
       map.put("snap","");
       map.put("face","");
       map.put("twitter","");
       db.collection(gender)
               .document(userID).set(map,SetOptions.merge());
   }


   private void deleteChat(final String id , final String uid){
       final FirebaseFirestore db = FirebaseFirestore.getInstance();
       db.collection("msg")
               .document(uid)
               .collection(id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                   for (DocumentSnapshot dc : task.getResult().getDocuments()){
                       db.collection("msg")
                               .document(uid)
                               .collection(id)
                               .document(dc.getId()).delete();
                   }
                }
           }
       });
   }
}
