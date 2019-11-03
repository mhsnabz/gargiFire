package com.rzn.gargi.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.annotations.SerializedName;
import com.rzn.gargi.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OneToOneChat extends AppCompatActivity {
    TextInputEditText msg;
    ImageButton mikrofon;
    FloatingActionButton send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_chat);
        msg=(TextInputEditText)findViewById(R.id.msg);
        mikrofon=(ImageButton)findViewById(R.id.mikrofon);
        send=(FloatingActionButton)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
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

    }

    private void sendMsg(){
        String msj ="sad";
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Map<String ,Object> map = new HashMap<>();
        map.put("msg",msg.getText().toString());
        map.put("sender","gonderen");
        map.put("getter","alan");
        map.put("time", Calendar.getInstance().getTimeInMillis());
        map.put("data","data");
        Map<String,Object> map1= new HashMap<>();
        map1.put("asdasd",map);
        DocumentReference refSender = db.collection("msg").document("gonderen");
        DocumentReference refGetter = db.collection("msg").document("alici");
        refGetter.set(map1);
        refSender.set(map1);

          msg.setText("");
    }
}
