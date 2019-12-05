package com.rzn.gargi.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rzn.gargi.R;
import com.rzn.gargi.chat.msg_list.Adapter;
import com.rzn.gargi.helper.CallBack;
import com.rzn.gargi.helper.MessegesModel;
import com.rzn.gargi.profile.UserProfileActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OldOneToOneChat extends AppCompatActivity {
    List<MessegesModel> msgges;

    Toolbar toolbar;
    RecyclerView msg_list;
    CardView mediaLayout;
    MessegesModel model;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    static String admin ="ml20r64rnmXBpPHNpO8tbSW5Y8v1";
    //  MessegesList adapter;
    LinearLayoutManager layoutManager;
    RecyclerView.Adapter mAdapter;
    FirestoreRecyclerOptions<MessegesModel> options ;
    private long _time;
    long time;
    TextView timer;
    Dialog dialog;
    private CountDownTimer downTimer;
    Dialog dialog_options,report_dilaog;
    Dialog time_dialog,dialog_areYouSure ;
    String tokenId,userName;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_one_to_one_chat);
        timer=(TextView)findViewById(R.id.timer);

        msgges= new ArrayList<>();

        getTime(getIntent().getStringExtra("userId"),timer);
        mAdapter= new Adapter(msgges);
        msg_list = (RecyclerView)findViewById(R.id.messeges_list) ;
        layoutManager = new LinearLayoutManager(OldOneToOneChat.this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(true);
        msg_list.setLayoutManager(layoutManager);
        msg_list.setHasFixedSize(true);

        msg_list.setAdapter(mAdapter);

        getMsg();
    }
    ListenerRegistration msgListner;
    private void getMsg(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("msg");
        Query refMsg = ref.document(auth.getUid())
                .collection(getIntent().getStringExtra("userId"))
                .orderBy("time",Query.Direction.ASCENDING);


        msgListner = refMsg.addSnapshotListener(OldOneToOneChat.this, MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        switch (doc.getType()) {
                            case ADDED:
                                Log.d("tag", "onEvent: " + doc);
                                model = doc.getDocument().toObject(MessegesModel.class);
                                msgges.add(model);
                                msg_list.scrollToPosition(msgges.size() - 1);
                                mAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            }
        });


    }
FirebaseFirestore dbTime =FirebaseFirestore.getInstance();
    private void getTime(String userId, final TextView _timer){
        dbTime.collection("oldList")
                    .document(auth.getUid())
                    .collection(auth.getUid())
                    .document(userId).get().addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() ){
                        Date time = task.getResult().getDate("time");
                        long second =time.getTime();
                        Date date = Timestamp.now().toDate();
                        long currentTime=date.getTime();
                        long value = second+(24*60*60000)-currentTime;
                        if (value<0){
                            deleteChat(getIntent().getStringExtra("userId"),auth.getUid());
                            deleteChat(auth.getUid(),getIntent().getStringExtra("userId"));
                            deleteOnOldList();
                        }else{
                            startTimer(_timer,value);
                        }

                    }
                }
            }).addOnFailureListener(OldOneToOneChat.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Crashlytics.logException(e);
                }
            });
    }
    private void startTimer(final TextView _timer, final long time){


        downTimer = new CountDownTimer(time
                ,1000) {
            @Override
            public void onTick(long l) {
                if (l<=2000){
                    downTimer.cancel();
                    deleteChat(getIntent().getStringExtra("userId"),auth.getUid());
                    deleteChat(auth.getUid(),getIntent().getStringExtra("userId"));
                    deleteOnOldList();
                    finish();

                }

                updateTimeTV(_timer,l);
            }

            @Override
            public void onFinish() {

            }
        }.start();

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
    private void updateTimeTV(TextView _timer,long durationInMillis) {
        // long millis = durationInMillis % 1000;
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        _timer.setText(time);

    }

    private void deleteOnOldList(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("oldList")
                .document(auth.getUid())
                .collection(auth.getUid())
                .document(getIntent().getStringExtra("userId"))
                .delete();
        db.collection("oldList")
                .document(getIntent().getStringExtra("userId"))
                .collection(getIntent().getStringExtra("userId"))
                .document(auth.getUid())
                .delete();
        db.collection("msg")
                .document(auth.getUid())
                .collection(getIntent().getStringExtra("userId"))
                .get().addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() ){
                            if (task.getResult()!=null){
                                for (DocumentSnapshot dc : task.getResult().getDocuments()){
                                    removeChat(dc.getId(),getIntent().getStringExtra("userId"));
                                }
                            }
                        }
                    }
                });
        db.collection("msg")
                .document(getIntent().getStringExtra("userId"))
                .collection(auth.getUid())
                .get().addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() ){
                    if (task.getResult()!=null){
                        for (DocumentSnapshot dc : task.getResult().getDocuments()){
                            removeChatCu(dc.getId(),getIntent().getStringExtra("userId"));
                        }
                    }
                }
            }
        });

    }
    private void removeChat(String id,String userId){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("msg")
                .document(auth.getUid())
                .collection(userId)
                .document(id).delete();

    }
    private void removeChatCu(String id,String userId){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("msg")
                .document(userId)
                .collection(auth.getUid())
                .document(id).delete();

    }
    private void setTimer(long time,TextView _timer){
        Date date = Timestamp.now().toDate();
        long currentTime = date.getTime();
        long value = time+(24*60*60000)-currentTime;
        startTimer(_timer,value);
    }

    @SuppressLint("WrongViewCast")

    private void setToolbar(String userId){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater inLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inLayoutInflater.inflate(R.layout.con_toolbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(action_bar_view);
        final CircleImageView image=(CircleImageView)findViewById(R.id.profileImage);

        final TextView name=(TextView)findViewById(R.id.username);
        final ImageButton options =(ImageButton)findViewById(R.id.options);
        final ImageView verified =(ImageView)findViewById(R.id.verified);
        final ImageView lock =(ImageView)findViewById(R.id.lock);
        final ImageView unlock =(ImageView)findViewById(R.id.unlock);
        lock.setVisibility(View.GONE);
        unlock.setVisibility(View.GONE);
       TextView timer =(TextView)findViewById(R.id.timer);
       timer.setVisibility(View.GONE);
        dialog_options = new Dialog(this);
        report_dilaog = new Dialog(this);
        time_dialog= new Dialog(this);
        dialog_areYouSure=new Dialog(this);

        report_dilaog.setContentView(R.layout.report_dialog);
        report_dilaog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog_options.setContentView(R.layout.options_dialog);
        dialog_options.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        time_dialog.setContentView(R.layout.time_dialog);
        time_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_areYouSure.setContentView(R.layout.are_you_sure_dialog);
        dialog_areYouSure.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog =new Dialog(this);
        dialog.setContentView(R.layout.wait_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("allUser")
                .document(userId)
                .get().addOnSuccessListener(OldOneToOneChat.this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot!=null){
                    String _name = documentSnapshot.getString("name");
                    String currentString =_name;
                    String[] separated = currentString.split(" ");

                    name.setText(separated[0]);
                    if (!documentSnapshot.getString("thumb_image").isEmpty()){
                        String url = documentSnapshot.getString("thumb_image");
                        Picasso.get().load(url)
                                .config(Bitmap.Config.RGB_565)
                                .placeholder(R.drawable.upload_place_holder)
                                .into(image, new Callback() {
                                    @Override
                                    public void onSuccess(){
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                    }else image.setImageResource(R.drawable.upload_place_holder);

                }
            }
        }).addOnFailureListener(OldOneToOneChat.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.log(e.toString());
            }
        });

        if (userId.equals(admin)){
            verified.setVisibility(View.VISIBLE);
            //chatBar.setVisibility(View.GONE);
            options.setVisibility(View.INVISIBLE);
            options.setEnabled(false);
        }else
            verified.setVisibility(View.GONE);
        toolbar.setNavigationIcon(R.drawable.msg_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OldOneToOneChat.this,ChatActivity.class);
                i.putExtra("gender",getIntent().getStringExtra("gender"));
                startActivity(i);
                finish();

            }
        });

        options.setVisibility(View.INVISIBLE);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClick(getIntent().getStringExtra("userId"),getIntent().getStringExtra("gender"));

            }
        });

    }
    private void getClick(final String userID, final String gender){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (gender.equals("MAN")){
            db.collection("WOMAN")
                    .document(userID)
                    .get().addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task!=null){
                        if (task.getResult().getLong("click")!=null){
                            setClik(task.getResult().getLong("click"),userID,gender);

                        }
                    }
                }
            }).addOnFailureListener(OldOneToOneChat.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Crashlytics.logException(e);
                }
            });
        }else if (gender.equals("WOMAN")){
            db.collection("MAN")
                    .document(userID)
                    .get().addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task!=null){
                        if (task.getResult().getLong("click")!=null){
                            setClik(task.getResult().getLong("click"),userID,gender);

                        }
                    }
                }
            }).addOnFailureListener(OldOneToOneChat.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Crashlytics.logException(e);
                }
            });
        }

    }
    public void setClik(final long clik, final String userId, final String gender){
        if (gender.equals("WOMAN")){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Map<String,Object> map = new HashMap<>();
            map.put("clik",1);
            map.put("time", FieldValue.serverTimestamp());
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("MAN")
                    .document(userId)
                    .collection("view")
                    .document(auth.getUid());
            ref.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        ubdateClik(clik,userId,gender);
                    }
                }
            });
        }else if (gender.equals("MAN")){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Map<String,Object> map = new HashMap<>();
            map.put("clik",1);
            map.put("time", FieldValue.serverTimestamp());
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference ref = db.collection("WOMAN")
                    .document(userId)
                    .collection("view")
                    .document(auth.getUid());
            ref.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        ubdateClik(clik,userId,gender);
                    }
                }
            });
        }


    }
    private void ubdateClik(long click, final String userId, String gender){
        if (gender.equals("WOMAN")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String,Object> map = new HashMap<>();
            map.put("click",click+1);
            db.document("MAN"+"/"+userId)
                    .set(map,SetOptions.merge()).addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent i = new Intent(OldOneToOneChat.this, UserProfileActivity.class);
                        i.putExtra("gender","MAN");
                        i.putExtra("userId",userId);
                        i.putExtra("intent","oldOne");

                        startActivity(i);
                        finish();
                    }
                }
            });
        }else if (gender.equals("MAN")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String,Object> map = new HashMap<>();
            map.put("click",click+1);
            db.document("WOMAN"+"/"+userId)
                    .set(map,SetOptions.merge()).addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent i = new Intent(OldOneToOneChat.this, UserProfileActivity.class);
                        i.putExtra("gender","WOMAN");
                        i.putExtra("userId",userId);
                        i.putExtra("intent","oldOne");
                        startActivity(i);
                        finish();
                    }
                }
            });
        }

    }
    int count =0;
    @Override
    public void onBackPressed()
    {

    }

    @Override
    protected void onStop() {


        super.onStop();
        msgListner.remove();
    }
    @Override
    protected void onPause() {


        super.onPause();
    }


    @Override
    protected void onStart() {

        super.onStart();
        setToolbar(getIntent().getStringExtra("userId"));

        //  removeBadge();
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
        msgListner.remove();

    }

    @Override
    protected void onResume() {

        super.onResume();


    }

    private long _timeLeft,currentLeftTime;
    private boolean timerRunning,_lock=true,_unlock=false;






    private void clickDeleteMatch(long _leftTime){
        if (_timeLeft>=(27*60000)){
            time_dialog.show();
            dialog_options.dismiss();
            TextView report = (TextView)time_dialog.findViewById(R.id.report);
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    time_dialog.dismiss();
                    report_dilaog.show();
                    TextView sahteHesap = (TextView)report_dilaog.findViewById(R.id.sahteHesap);
                    TextView kufur = (TextView)report_dilaog.findViewById(R.id.kufur);
                    TextView ciplaklik = (TextView)report_dilaog.findViewById(R.id.ciplaklik);
                    Button cancel = (Button)report_dilaog.findViewById(R.id.cancel);

                    sahteHesap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });


                    kufur.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });


                    ciplaklik.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });


                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            report_dilaog.dismiss();
                            dialog_options.show();
                        }
                    });
                }

            });
            Button cancel = (Button)time_dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    time_dialog.dismiss();
                    dialog_options.show();
                }
            });

        }else {
            dialog_options.dismiss();
            dialog_areYouSure.show();
            Button yes = (Button)dialog_areYouSure.findViewById(R.id.yes);
            Button no = (Button)dialog_areYouSure.findViewById(R.id.no);

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_areYouSure.dismiss();
                    dialog_options.show();
                }
            });

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    removeMatch(getIntent().getStringExtra("userId"),auth.getUid());
                }
            });
        }
    }
    private void removeMatch(final String userId, final String currentUser){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference refCurrent = db.collection("msgList")
                .document(currentUser)
                .collection(currentUser)
                .document(userId);
        DocumentReference refUserId = db.collection("msgList")
                .document(userId)
                .collection(userId)
                .document(currentUser);
        final DocumentReference oldChatCurrentUser = db.collection("oldList")
                .document(currentUser)
                .collection(currentUser)
                .document(userId);

        final DocumentReference oldChatUserId = db.collection("oldList")
                .document(userId)
                .collection(userId)
                .document(currentUser);


        Map<String  , Object> mapDelete = new HashMap<>();
        final Map<String  , Object> currentUserOldChat = new HashMap<>();
        final Map<String  , Object> userIdOldChat = new HashMap<>();
        currentUserOldChat.put("getterUid",currentUser);
        currentUserOldChat.put("senderUid",userId);
        currentUserOldChat.put("time", FieldValue.serverTimestamp());
        currentUserOldChat.put("isOnline",false);


        userIdOldChat.put("getterUid",userId);
        userIdOldChat.put("senderUid",currentUser);
        userIdOldChat.put("time", FieldValue.serverTimestamp());
        userIdOldChat.put("isOnline",false);
        mapDelete.put("getterUid",FieldValue.delete());
        mapDelete.put("isOnline",FieldValue.delete());
        mapDelete.put("senderUid",FieldValue.delete());
        mapDelete.put("time",FieldValue.delete());
        mapDelete.put("timer",FieldValue.delete());
        refCurrent.set(mapDelete,SetOptions.merge()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                oldChatCurrentUser.set(currentUserOldChat,SetOptions.merge())
                        .addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()){
                                    CollectionReference currenUserRef = db.collection("msgList")
                                            .document(currentUser)
                                            .collection(currentUser);
                                    CollectionReference refUserId = db.collection("msgList")
                                            .document(userId)
                                            .collection(userId);
                                    currenUserRef.document(userId).delete();
                                    refUserId.document(currentUser).delete().addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()){
                                                db.collection("msgList")
                                                        .document(userId)
                                                        .collection(userId).get().addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            if (getIntent().getStringExtra("gender").equals("MAN")){
                                                                if (task.getResult().getDocuments().size()<6){
                                                                    db.collection("limit")
                                                                            .document(userId)
                                                                            .get().addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> taskLimit) {
                                                                            if (taskLimit.isSuccessful()){
                                                                                if (taskLimit.getResult().getData()!=null){
                                                                                    if (taskLimit.getResult().getData().size()<35){
                                                                                        getUserInfo(userId, task.getResult().getDocuments().size());
                                                                                        Intent i = new Intent(OldOneToOneChat.this, ChatActivity.class);
                                                                                        i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                        startActivity(i);
                                                                                        finish();
                                                                                        dialog.dismiss();
                                                                                    }else {
                                                                                        Intent i = new Intent(OldOneToOneChat.this, ChatActivity.class);
                                                                                        i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                        startActivity(i);
                                                                                        finish();
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                }
                                                                                else {
                                                                                    getUserInfo(userId, task.getResult().getDocuments().size());

                                                                                    Intent i = new Intent(OldOneToOneChat.this, ChatActivity.class);
                                                                                    i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                    startActivity(i);
                                                                                    finish();
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(OldOneToOneChat.this, new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Crashlytics.logException(e);
                                                                        }
                                                                    });

                                                                }else {
                                                                    Intent i = new Intent(OldOneToOneChat.this, ChatActivity.class);
                                                                    i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                    startActivity(i);
                                                                    finish();
                                                                    dialog.dismiss();
                                                                }
                                                            }
                                                            else if (getIntent().getStringExtra("gender").equals("WOMAN")){
                                                                if (task.getResult().getDocuments().size()<2){
                                                                    db.collection("limit")
                                                                            .document(userId)
                                                                            .get().addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> taskLimit) {
                                                                            if (taskLimit.isSuccessful()){
                                                                                if (taskLimit.getResult().getData()!=null){
                                                                                    if (taskLimit.getResult().getData().size()<10){
                                                                                        getUserInfo(userId,task.getResult().getDocuments().size());
                                                                                        Intent i = new Intent(OldOneToOneChat.this, ChatActivity.class);
                                                                                        i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                        startActivity(i);
                                                                                        finish();
                                                                                        dialog.dismiss();
                                                                                    }else {
                                                                                        Intent i = new Intent(OldOneToOneChat.this, ChatActivity.class);
                                                                                        i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                        startActivity(i);
                                                                                        finish();
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                }else {
                                                                                    getUserInfo(userId,task.getResult().getDocuments().size());

                                                                                    Intent i = new Intent(OldOneToOneChat.this, ChatActivity.class);
                                                                                    i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                    startActivity(i);
                                                                                    finish();
                                                                                    dialog.dismiss();
                                                                                }

                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(OldOneToOneChat.this, new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Crashlytics.logException(e);
                                                                        }
                                                                    });

                                                                }else {
                                                                    Intent i = new Intent(OldOneToOneChat.this, ChatActivity.class);
                                                                    i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                    startActivity(i);
                                                                    finish();
                                                                    dialog.dismiss();
                                                                }
                                                            }


                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    });

                                }
                            }
                        }).addOnFailureListener(OldOneToOneChat.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Crashlytics.logException(e);
                    }
                });
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });


        refUserId.set(mapDelete,SetOptions.merge())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        oldChatUserId.set(userIdOldChat,SetOptions.merge())
                                .addOnCompleteListener(OldOneToOneChat.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isComplete()){
                                            Log.d("removeMach", "onComplete: "+"task is succes");
                                        }
                                    }
                                }).addOnFailureListener(OldOneToOneChat.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Crashlytics.logException(e);
                            }
                        });
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });

    }


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
}
