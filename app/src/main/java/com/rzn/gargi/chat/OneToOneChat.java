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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ServerValue;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OneToOneChat extends AppCompatActivity {
    private static final int camera_request =200;
    TextInputEditText msg;
    ImageButton mikrofon;
    List<MessegesModel> msgges;
    FloatingActionButton send;
    Toolbar toolbar;
    TextView timer;
    RecyclerView msg_list;
    CardView mediaLayout;
    MessegesModel model;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    static String admin ="ml20r64rnmXBpPHNpO8tbSW5Y8v1";
  //  MessegesList adapter;
    LinearLayoutManager layoutManager;
    RecyclerView.Adapter mAdapter;
    FirestoreRecyclerOptions<MessegesModel> options ;
    private CountDownTimer downTimer;
    private long _time;

    Dialog dialog;
    Dialog dialog_options,report_dilaog;
    Dialog time_dialog,dialog_areYouSure ;
    String tokenId,userName;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_chat);
        msg=(TextInputEditText)findViewById(R.id.msg);
        mikrofon=(ImageButton) findViewById(R.id.mikrofon);

        _time=getIntent().getLongExtra("timer",0);
        msgges= new ArrayList<>();
        send=(FloatingActionButton)findViewById(R.id.send);
        mikrofon.setVisibility(View.GONE);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg(auth.getUid(),getIntent().getStringExtra("userId"));
            }
        });
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        FirebaseFirestore dbToken = FirebaseFirestore.getInstance();
        dbToken.collection("allUser")
                .document(getIntent().getStringExtra("userId")).get().addOnSuccessListener(OneToOneChat.this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot!=null){
                        if (documentSnapshot.getString("tokenID")!=null){
                            tokenId=documentSnapshot.getString("tokenID");
                        }

                    }
            }
        });
        dbToken.collection("allUser")
                .document(auth.getUid())
                .get().addOnSuccessListener(OneToOneChat.this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userName=documentSnapshot.getString("name");
            }
        });
        mAdapter= new Adapter(msgges);
        msg_list = (RecyclerView)findViewById(R.id.messeges_list) ;
        layoutManager = new LinearLayoutManager(OneToOneChat.this);
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


         msgListner = refMsg.addSnapshotListener(OneToOneChat.this, MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
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


        timer =(TextView)findViewById(R.id.timer);
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
                .get().addOnSuccessListener(OneToOneChat.this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot!=null){
                    String _name = documentSnapshot.getString("name");
                    name.setText(_name);
                    if (!documentSnapshot.getString("thumb_image").isEmpty()){
                        String url = documentSnapshot.getString("thumb_image");
                        Picasso.get().load(url)
                                .config(Bitmap.Config.RGB_565)
                                .placeholder(R.drawable.upload_place_holder)
                                .into(image, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                    }else image.setImageResource(R.drawable.upload_place_holder);

                }
            }
        }).addOnFailureListener(OneToOneChat.this, new OnFailureListener() {
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
                Intent i = new Intent(OneToOneChat.this,ChatActivity.class);
                i.putExtra("gender",getIntent().getStringExtra("gender"));
                startActivity(i);
                finish();
               setOffline();
            }
        });


        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_options.show();
                Button cancel = (Button)dialog_options.findViewById(R.id.cancel);
                final TextView removeMatch=(TextView)dialog_options.findViewById(R.id.removeMatch);
                TextView report =(TextView)dialog_options.findViewById(R.id.report);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_options.dismiss();
                    }
                });


                removeMatch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickDeleteMatch(_timeLeft);
                    }
                });

                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                        report_dilaog.show();
                        dialog_options.dismiss();

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
            }
        });


        setlock(lock,unlock,getIntent().getLongExtra("timer",0));
    }
    int count =0;
    @Override
    public void onBackPressed()
    {

    }
    private void setlock(final ImageView lock, final ImageView unlock, final long timer) {
        if (timer>=(27*60000)){
            lock.setVisibility(View.VISIBLE);
            unlock.setVisibility(View.GONE);
            downTimer= new CountDownTimer(timer,1000) {
                @Override
                public void onTick(long l) {
                    _time = l;
                    if (27*60000>=l){
                        lock.setVisibility(View.GONE);
                        unlock.setVisibility(View.VISIBLE);
                        return;
                    }

                }

                @Override
                public void onFinish() {

                }
            }.start();
        }else {
            lock.setVisibility(View.GONE);
            unlock.setVisibility(View.VISIBLE);
        }

    }
    private void checkIsOnline(String userID, final CallBack<Boolean> isOnline){
        notDb.collection("isOnline")
                .document(auth.getUid())
                .collection(auth.getUid())
                .document(userID).get().addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getBoolean("isOnline")!=null){
                        if (task.getResult().getBoolean("isOnline")){
                            isOnline.returnTrue(true);
                        }else isOnline.returnFalse(false);
                    }
                }
            }
        });
    }
    FirebaseFirestore notDb = FirebaseFirestore.getInstance();
    private void sendNotification(final String userId, final String tokenId){
        checkIsOnline(userId, new CallBack<Boolean>() {
            @Override
            public void returnFalse(Boolean _false) {
                setNewBadge();
                if (!tokenId.isEmpty()){

                    Map<String,Object> not=new HashMap<>();
                    not.put("from",auth.getUid());
                    not.put("type","msg");
                    not.put("getter",userId);
                    not.put("tokenID",tokenId);
                    not.put("name",userName);
                    not.put("rate","");
                    not.put("gender","");
                    notDb.collection("notification")
                            .document(userId)
                            .collection("notification").add(not).addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Log.d("sendNotification", "onComplete: "+"task.isSuccessful");
                            }
                        }
                    }).addOnFailureListener(OneToOneChat.this, new OnFailureListener() {
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
    private void sendMsg(String currentUser, final String userId){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference refSender = db.collection("msg")
                .document(currentUser)
                .collection(userId).document();
        DocumentReference refGetter = db.collection("msg")
                .document(userId)
                .collection(currentUser).document();
        String _msg = msg.getText().toString();
        final Map<String ,Object> map = new HashMap<>();
        if (!_msg.isEmpty()){

            map.put("msg",msg.getText().toString());
            map.put("sender",currentUser);
            map.put("getter",userId);
            map.put("time", Calendar.getInstance().getTimeInMillis());
            map.put("data","data");
            msg.setText("");
            refGetter.set(map).addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()){

                        refSender.set(map).addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()){

                                    DocumentReference ref = db
                                            .collection("msgList")
                                            .document(auth.getUid())
                                            .collection(auth.getUid())
                                            .document(userId);
                                    Map<String,Object> map1 = new HashMap<>();
                                    map1.put("time", FieldValue.serverTimestamp());
                                    ref.set(map1,SetOptions.merge() ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()){
                                                Log.d("tag", "onComplete: "+task);
                                                DocumentReference ref = db
                                                        .collection("msgList")
                                                        .document(userId)
                                                        .collection(userId)
                                                        .document(auth.getUid());
                                                Map<String,Object> map1 = new HashMap<>();
                                                map1.put("time", FieldValue.serverTimestamp());
                                                ref.set(map1,SetOptions.merge() );
                                                sendNotification(userId,tokenId);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }

    }
    @Override
    protected void onStop() {


        super.onStop();
        stopTimer();
        msgListner.remove();
        setOffline();
    }
    @Override
    protected void onPause() {


        super.onPause();
        stopTimer();
        setOffline();
    }


    @Override
    protected void onStart() {

        super.onStart();
        setToolbar(getIntent().getStringExtra("userId"));
        startTimer(timer);
        setOnline();

      //  removeBadge();
    }
    @Override
    protected void onDestroy() {
        stopTimer();

        super.onDestroy();
        msgListner.remove();
        setOffline();

    }

    @Override
    protected void onResume() {

        super.onResume();
      //  startTimer(timer);
        setOnline();


    }

    private long _timeLeft,currentLeftTime;
    private boolean timerRunning,_lock=true,_unlock=false;

    private void startTimer(final TextView _timer){


        downTimer = new CountDownTimer(getIntent().getLongExtra("timer",0),1000) {
            @Override
            public void onTick(long l) {
                _time = l;
                _timeLeft=_time;
                currentLeftTime=_time;
                if (3*60000>=_time){
                    _lock=false;
                    _unlock=true;
                }
                updateTimeTV(_timer);
                if (l<3000){
                    removeMatch(getIntent().getStringExtra("userId"),auth.getUid());

                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
        timerRunning = true;
    }



    private void updateTimeTV(TextView _timer) {
        int min =(int) _time/60000;
        int second = (int) _time % 60000/1000;
        String timeLeftString;
        timeLeftString = ""+min;
        timeLeftString+=":";
        if (second< 10) timeLeftString +=0;
        timeLeftString += second;
        _timer.setText(timeLeftString);
       // if (_time<=1500)
   //     removeMatch(_time);

    }
    private void stopTimer(){
        downTimer.cancel();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("msgList")
                .document(auth.getUid())
                .collection(auth.getUid())
                .document(getIntent().getStringExtra("userId"));
        ref.get().addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()){
                    if (task.getResult().get("getterUid")!=null){

                        final DocumentReference refCurrentUser =db.collection("msgList")
                                .document(auth.getUid())
                                .collection(auth.getUid())
                                .document(getIntent().getStringExtra("userId"));
                        DocumentReference refUserId =db.collection("msgList")
                                .document(getIntent().getStringExtra("userId"))
                                .collection(getIntent().getStringExtra("userId"))
                                .document(auth.getUid()) ;
                        final Map<String,Object> timer = new HashMap<>();
                        timer.put("timer",_timeLeft);
                        refUserId.set(timer,SetOptions.merge()).addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    refCurrentUser.set(timer, SetOptions.merge()).addOnCompleteListener( new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()){
                                                Log.d("timerTask", "onComplete: "+"complete");
                                            }
                                        }
                                    }).addOnFailureListener(OneToOneChat.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Crashlytics.log(e.toString());
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(OneToOneChat.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Crashlytics.log(e.toString());
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(OneToOneChat.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });


        timerRunning = false;

    }

    private void setOnline(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("isOnline")
                .document(getIntent().getStringExtra("userId"))
                .collection(getIntent().getStringExtra("userId"))
                .document(auth.getUid());
        Map<String,Boolean> map = new HashMap<>();
        map.put("isOnline",true);
        ref.set(map,SetOptions.merge());
    }
    private void setOffline(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("isOnline")
                .document(getIntent().getStringExtra("userId"))
                .collection(getIntent().getStringExtra("userId"))
                .document(auth.getUid());
        Map<String,Boolean> map = new HashMap<>();
        map.put("isOnline",false);
        ref.set(map,SetOptions.merge());
    }

    private void setNewBadge(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> currentUserBadge = new HashMap<>();
        Map<String,Object> hasBadge = new HashMap<>();
        hasBadge.put(auth.getUid(),getIntent().getStringExtra("userId"));
        currentUserBadge.put(auth.getUid(),hasBadge);
        db.collection("badgeCount")
                .document(getIntent().getStringExtra("userId")).set(hasBadge,SetOptions.merge());



    }
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
            final TextView timer = (TextView)time_dialog.findViewById(R.id.leftTime);
            downTimer= new CountDownTimer(_leftTime-(27*60000),1000) {
                @Override
                public void onTick(long l) {
                    _time = l;
                    currentLeftTime=_time;
                    updateTimeTV(timer);
                }

                @Override
                public void onFinish() {

                }
            }.start();
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
                        .addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<Void>() {
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
                                    refUserId.document(currentUser).delete().addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()){
                                                db.collection("msgList")
                                                        .document(userId)
                                                        .collection(userId).get().addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    if (getIntent().getStringExtra("gender").equals("MAN")){
                                                                        if (task.getResult().getDocuments().size()<6){
                                                                            db.collection("limit")
                                                                                    .document(userId)
                                                                                    .get().addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<DocumentSnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> taskLimit) {
                                                                                            if (taskLimit.isSuccessful()){
                                                                                                if (taskLimit.getResult().getData()!=null){
                                                                                                    if (taskLimit.getResult().getData().size()<35){
                                                                                                        getUserInfo(userId, task.getResult().getDocuments().size());
                                                                                                        Intent i = new Intent(OneToOneChat.this, ChatActivity.class);
                                                                                                        i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                                        startActivity(i);
                                                                                                        finish();
                                                                                                        dialog.dismiss();
                                                                                                    }else {
                                                                                                        Intent i = new Intent(OneToOneChat.this, ChatActivity.class);
                                                                                                        i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                                        startActivity(i);
                                                                                                        finish();
                                                                                                        dialog.dismiss();
                                                                                                    }
                                                                                                }
                                                                                                    else {
                                                                                                    getUserInfo(userId, task.getResult().getDocuments().size());

                                                                                                    Intent i = new Intent(OneToOneChat.this, ChatActivity.class);
                                                                                                    i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                                    startActivity(i);
                                                                                                    finish();
                                                                                                    dialog.dismiss();
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }).addOnFailureListener(OneToOneChat.this, new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Crashlytics.logException(e);
                                                                                }
                                                                            });

                                                                        }else {
                                                                            Intent i = new Intent(OneToOneChat.this, ChatActivity.class);
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
                                                                                    .get().addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> taskLimit) {
                                                                                    if (taskLimit.isSuccessful()){
                                                                                        if (taskLimit.getResult().getData()!=null){
                                                                                            if (taskLimit.getResult().getData().size()<10){
                                                                                                getUserInfo(userId,task.getResult().getDocuments().size());
                                                                                                Intent i = new Intent(OneToOneChat.this, ChatActivity.class);
                                                                                                i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                                startActivity(i);
                                                                                                finish();
                                                                                                dialog.dismiss();
                                                                                            }else {
                                                                                                Intent i = new Intent(OneToOneChat.this, ChatActivity.class);
                                                                                                i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                                startActivity(i);
                                                                                                finish();
                                                                                                dialog.dismiss();
                                                                                            }
                                                                                        }else {
                                                                                            getUserInfo(userId,task.getResult().getDocuments().size());

                                                                                            Intent i = new Intent(OneToOneChat.this, ChatActivity.class);
                                                                                            i.putExtra("gender",getIntent().getStringExtra("gender"));
                                                                                            startActivity(i);
                                                                                            finish();
                                                                                            dialog.dismiss();
                                                                                        }

                                                                                    }
                                                                                }
                                                                            }).addOnFailureListener(OneToOneChat.this, new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Crashlytics.logException(e);
                                                                                }
                                                                            });

                                                                        }else {
                                                                            Intent i = new Intent(OneToOneChat.this, ChatActivity.class);
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
                        }).addOnFailureListener(OneToOneChat.this, new OnFailureListener() {
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
                                .addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isComplete()){
                                            Log.d("removeMach", "onComplete: "+"task is succes");
                                        }
                                    }
                                }).addOnFailureListener(OneToOneChat.this, new OnFailureListener() {
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
