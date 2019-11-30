package com.rzn.gargi.SettingEdit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.rzn.gargi.R;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class NotificaitonSettingsActivity extends AppCompatActivity {
    SwitchCompat messeges,newMatch,newRate;
    TextView header;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaiton_settings);
        header=(TextView)findViewById(R.id.header);
        messeges=(SwitchCompat)findViewById(R.id.messageSwitch);
        newMatch=(SwitchCompat)findViewById(R.id.newMatch);
        newRate=(SwitchCompat)findViewById(R.id.newRate);
        setSetting(messeges,newRate,newMatch);

        messeges.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    Map<String,Object> map = new HashMap<>();
                    map.put("msg",true);
                    notSetting(map);
                }else {
                    Map<String,Object> map = new HashMap<>();
                    map.put("msg",false);
                    notSetting(map);
                }
            }
        });
        newMatch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Map<String,Object> map = new HashMap<>();
                    map.put("match",true);
                    notSetting(map);
                }else {
                    Map<String,Object> map = new HashMap<>();
                    map.put("match",false);
                    notSetting(map);
                }
            }
        });
        newRate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Map<String,Object> map = new HashMap<>();
                    map.put("rate",true);
                    notSetting(map);
                }else {
                    Map<String,Object> map = new HashMap<>();
                    map.put("rate",false);
                    notSetting(map);
                }
            }
        });
        db.collection("allUser")
                .document(auth.getUid()).get().addOnCompleteListener(NotificaitonSettingsActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String gender = task.getResult().getString("gender");
                    if (gender.equals("MAN")){
                        header.setText(getResources().getString(R.string.bir_hanımefendi_size) + " 5.0 "  + getResources().getString(R.string.puan_verdi)) ;
                    }else {
                        header.setText(getResources().getString(R.string.bir_beyefendi_size) + " 5.0 " + getResources().getString(R.string.puan_verdi)) ;

                    }
                }
            }
        }).addOnFailureListener(NotificaitonSettingsActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });
    }

    private void setSetting(final SwitchCompat messeges, final SwitchCompat newRate, final SwitchCompat newMatch){
        db.collection("notificationSetting")
                .document(auth.getUid()).get().addOnCompleteListener(NotificaitonSettingsActivity.this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult()!=null){
                        boolean msg = task.getResult().getBoolean("msg");
                        messeges.setChecked(msg);
                        boolean rate = task.getResult().getBoolean("rate");
                        newRate.setChecked(rate);
                        boolean match = task.getResult().getBoolean("match");
                        newMatch.setChecked(match);
                    }
                }
            }
        }).addOnFailureListener(NotificaitonSettingsActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Crashlytics.logException(e);
            }
        });


    }

    private void notSetting(Map<String,Object> map){
        db.collection("notificationSetting")
                .document(auth.getUid()).set(map, SetOptions.merge());
    }
    public void back(View view) {
        finish();
    }
}
