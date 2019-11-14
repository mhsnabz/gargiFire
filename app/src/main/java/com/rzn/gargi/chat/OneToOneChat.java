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
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_chat);
        msg=(TextInputEditText)findViewById(R.id.msg);
        mikrofon=(ImageButton) findViewById(R.id.mikrofon);

        _time=getIntent().getLongExtra("timer",0);
        msgges= new ArrayList<>();
        send=(FloatingActionButton)findViewById(R.id.send);

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
                if (count>0){
                    mikrofon.setVisibility(View.GONE);
                }else mikrofon.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        timer =(TextView)findViewById(R.id.timer);
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
                finish();
               setOffline();
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
                       setNewBadge();
                        refSender.set(map).addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()){

                                    DocumentReference ref = db
                                            .collection("msgList")
                                            .document(auth.getUid())
                                            .collection(auth.getUid())
                                            .document(userId);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

                                    Map<String,Object> map1 = new HashMap<>();
                                    map.put("time", FieldValue.serverTimestamp());
                                    ref.set(map,SetOptions.merge() ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()){
                                                Log.d("tag", "onComplete: "+task);
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
        msgListner.remove();
        stopTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
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
        super.onDestroy();
        msgListner.remove();
        stopTimer();

    }

    @Override
    protected void onResume() {

        super.onResume();
      //  startTimer(timer);


    }

    private long _timeLeft,currentLeftTime;
    private boolean timerRunning;

    private void startTimer(final TextView _timer){
        downTimer = new CountDownTimer(getIntent().getLongExtra("timer",0),1000) {
            @Override
            public void onTick(long l) {
                _time = l;
                _timeLeft=_time;
                currentLeftTime=_time;
                updateTimeTV(_timer);

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
       final DocumentReference refCurrentUser =db.collection("msgList")
               .document(auth.getUid())
               .collection(auth.getUid())
               .document(getIntent().getStringExtra("userId"))
               ;
        DocumentReference refUserId =db.collection("msgList")
                .document(getIntent().getStringExtra("userId"))
                .collection(getIntent().getStringExtra("userId"))
                .document(auth.getUid())
                ;
                final Map<String,Object> timer = new HashMap<>();
                timer.put("timer",_timeLeft);
                refUserId.set(timer,SetOptions.merge()).addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()){
                            refCurrentUser.set(timer, SetOptions.merge()).addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()){

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

    private void isOnline(final CallBack<Boolean> _isOnline){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference ref = db.collection("isOnline")
                .document(auth.getUid())
                .collection(auth.getUid())
                .document(getIntent().getStringExtra("userId"));
            ref.get().addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (task!=null){
                            boolean isOnline = task.getResult().getBoolean("isOnline");
                            if(isOnline){
                                _isOnline.returnTrue(true);
                            }else _isOnline.returnFalse(false);

                        }
                    }
                }
            }).addOnFailureListener(OneToOneChat.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
               Crashlytics.log(e.toString());
                }
            });


    }

    private void setNewBadge(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference ref = db.collection("badgeCount")
                .document(getIntent().getStringExtra("userId"))

                .collection(auth.getUid())
                .document();
        final Map<String,Object> mTrue = new HashMap<>();
        final Map<String,Boolean> mFalse = new HashMap<>();
        mTrue.put("hasBadge",true);
        mFalse.put("hasBadge",false);
        isOnline(new CallBack<Boolean>() {
            @Override
            public void returnFalse(Boolean _false) {
                ref.set(mTrue,SetOptions.merge());
            }

            @Override
            public void returnTrue(Boolean _true) {

            }
        });

    }

    private void removeMatch(long _time){
        if (_time<=1000){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference refCurrentUser = db.collection("msgList")
                    .document(auth.getUid())
                    .collection(auth.getUid())
                    .document(getIntent().getStringExtra("userId"));
            refCurrentUser.delete().addOnCompleteListener(OneToOneChat.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Log.d("deleteMatch", "onComplete: "+"succes");
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
