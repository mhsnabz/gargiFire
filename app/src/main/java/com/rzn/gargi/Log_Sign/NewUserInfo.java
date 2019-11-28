package com.rzn.gargi.Log_Sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.ModelUser;
import com.rzn.gargi.home.HomeActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewUserInfo extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{
    RadioGroup gender;
    RadioButton man , woman ;
    String email , name,userId;
    Button signUp,mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Dialog dialogWait;
    long ageinMilis;
    String horocope;
    public static String admin ="ml20r64rnmXBpPHNpO8tbSW5Y8v1";
    MaterialEditText editTextName, editTextEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_info);
        editTextEmail=(MaterialEditText)findViewById(R.id.email);
        editTextName=(MaterialEditText)findViewById(R.id.name);
        dialogWait=new Dialog(this);
        email=getIntent().getStringExtra("email");
        name=getIntent().getStringExtra("name");
        userId=getIntent().getStringExtra("userId");

        if (email.isEmpty()){
            editTextEmail.setVisibility(View.VISIBLE);
        }else editTextEmail.setVisibility(View.GONE);
        if (name.isEmpty()){
            editTextName.setVisibility(View.VISIBLE);
        }else editTextName.setVisibility(View.GONE);
        gender=(RadioGroup)findViewById(R.id.radio);
        man=(RadioButton)findViewById(R.id.man);
        woman=(RadioButton)findViewById(R.id.woman);
        signUp=(Button)findViewById(R.id.register_clik);
        mDisplayDate=(Button)findViewById(R.id.age);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewUserInfo.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,

                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                                cal.set(Calendar.MONTH, datePicker.getMonth());
                                cal.set(Calendar.YEAR, datePicker.getYear());
                                ageinMilis = cal.getTimeInMillis();

                                if ((calgulateAge(ageinMilis)==0)||calgulateAge(ageinMilis)<18){

                                    Toast.makeText(NewUserInfo.this,getResources().getString(R.string.onsekiz_yasindan_buyuk_olmanız_gerekmeketedir),Toast.LENGTH_LONG).show();
                                    return;
                                }else {
                                    mDisplayDate.setText(String.valueOf(calgulateAge(ageinMilis)));
                                    horocope=calgulateHoroscope(month,dayOfMonth);
                                }




                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegister(getIntent().getStringExtra("email"),getIntent().getStringExtra("name"),auth.getUid(),ageinMilis,horocope,gender);
            }
        });
    }
FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private void setRegister(String _email , String   _name , final String _uid, long _ageinMilis, String horoscope, RadioGroup gender  ){

        dialogWait.setContentView(R.layout.wait_dialog);
        dialogWait.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWait.setCanceledOnTouchOutside(false);
        dialogWait.show();


        if (gender.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(NewUserInfo.this,getResources().getString(R.string.lutfen_cinisinizyetiniz_giriniz),Toast.LENGTH_LONG).show();
            dialogWait.dismiss();

            return;
        }
        else if (ageinMilis<=0){
            dialogWait.dismiss();
            return;
        }
        else {
            if (man.isChecked()){
                final ModelUser user = new ModelUser("",_ageinMilis,horoscope,0,0,_email,"MAN","",0.0,0.0,_name,"","","",1,5,5.0,_uid,null);
                final String currentUser =auth.getUid();
                db.collection("allUser").document(currentUser).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("tag", "onComplete: "+"task is succeful");
                            db.collection("MAN").document(currentUser).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        setRate(admin,currentUser,"MAN");

                                        Intent i = new Intent(NewUserInfo.this, HomeActivity.class);
                                        i.putExtra("gender","MAN");
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag", e.toString());

                    }
                });
            }else if (woman.isChecked()){
                final ModelUser user = new ModelUser("",_ageinMilis,horoscope,0,0,_email,"MAN","",0.0,0.0,_name,"","","",1,5,5.0,_uid,null);
                final String currentUser =auth.getUid();
                db.collection("allUser").document(currentUser).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("tag", "onComplete: "+"task is succeful");
                            db.collection("WOMAN").document(currentUser).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        setRate(admin,currentUser,"WOMAN");

                                        Intent i = new Intent(NewUserInfo.this, HomeActivity.class);
                                        i.putExtra("gender","WOMAN");
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag", e.toString());

                    }
                });
            }
        }
    }
    private int calgulateAge(long timeInMillis)
    {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(timeInMillis);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_MONTH ) < dob.get(Calendar.DAY_OF_MONTH))
        {
            age -- ;

        }
        Log.d("age", "calgulateAge: "+age);
        return age;
    }
    private String calgulateHoroscope(int month, int day){

        String zodiacSing = "";
        if (month == 0)
        {
            if (day<=21) zodiacSing="oglak";
            else zodiacSing = "kova";

        }else   if (month == 1)
        {
            if (day<=19) zodiacSing="kova";
            else zodiacSing = "balık";

        }
        else   if (month == 2)
        {
            if (day<=20) zodiacSing="balik";
            else zodiacSing = "koc";

        }
        else   if (month ==3)
        {
            if (day<=20) zodiacSing="koc";
            else zodiacSing = "boga";

        }
        else   if (month == 4)
        {
            if (day<=21) zodiacSing="boga";
            else zodiacSing = "ikizler";

        }
        else   if (month == 5)
        {
            if (day<=22) zodiacSing="ikizler";
            else zodiacSing = "yengec";

        }
        else   if (month == 6)
        {
            if (day<=22) zodiacSing="yengec";
            else zodiacSing = "aslan";

        }
        else   if (month == 7)
        {
            if (day<=22) zodiacSing="aslan";
            else zodiacSing = "basak";

        }else   if (month == 8)
        {
            if (day<=22) zodiacSing="basak";
            else zodiacSing = "terazi";

        }else   if (month == 9)
        {
            if (day<=22) zodiacSing="terazi";
            else zodiacSing = "akrep";

        }else   if (month == 10)
        {
            if (day<=21) zodiacSing="akrep";
            else zodiacSing = "yay";

        }else   if (month == 11)
        {
            if (day<=21) zodiacSing="yay";
            else zodiacSing = "oglak";

        }

        return zodiacSing;
    }
    private void setRate(String uid ,String currentUser, String gender){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference ref = db.collection(gender).document(currentUser);
        DocumentReference refRate = db.collection("rate").document(currentUser).collection(currentUser)
                .document(admin);
        Map<String ,Object> adminUid = new HashMap<>();
        Map<String ,Object> rate = new HashMap<>();
        rate.put("rate",5);
        adminUid.put(uid,rate);

        refRate.set(rate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Map<String ,Object> map = new HashMap<>();
                    map.put("count",1);
                    map.put("totalRate",5);
                    ref.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("rateTag", "onComplete: "+"suscces");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Crashlytics.logException(e);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
