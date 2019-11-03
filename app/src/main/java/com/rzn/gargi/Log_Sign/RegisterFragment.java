package com.rzn.gargi.Log_Sign;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.ModelUser;
import com.rzn.gargi.home.HomeActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    Button loginButton ;
    Button signUp;
    public static String TAG = "MainActivity";
    public static String uid ="ml20r64rnmXBpPHNpO8tbSW5Y8v1";
    View view;
    MaterialEditText name , email ,pass ,passAgain,age_et;
    public Button age ;
    RadioGroup gender;
    RadioButton man , woman ;
    Dialog dialogWait;
    long ageinMilis;
    String horocope;
    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference newUserRef = db.document("User/allUser");

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);

        auth=FirebaseAuth.getInstance();
        dialogWait = new Dialog(getContext());
        loginButton = (Button) view.findViewById(R.id.login_click);
        age = (Button) view.findViewById(R.id.age);
        name = (MaterialEditText) view.findViewById(R.id.name);
        email = (MaterialEditText) view.findViewById(R.id.email);
        pass = (MaterialEditText) view.findViewById(R.id.password);
        passAgain = (MaterialEditText) view.findViewById(R.id.passwordAgain);
        age_et = (MaterialEditText) view.findViewById(R.id.age_et);
        gender=(RadioGroup)view.findViewById(R.id.radio);
        man=(RadioButton)view.findViewById(R.id.man);
        woman=(RadioButton) view.findViewById(R.id.woman);
        signUp=(Button)view.findViewById(R.id.register_clik);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegister(name, email ,pass ,passAgain ,age_et , gender  );
            }
        });
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                                cal.set(Calendar.MONTH, datePicker.getMonth());
                                cal.set(Calendar.YEAR, datePicker.getYear());
                                ageinMilis = cal.getTimeInMillis();

                                if ((calgulateAge(ageinMilis)==0)||calgulateAge(ageinMilis)<18){

                                    Toast.makeText(getContext(),getResources().getString(R.string.onsekiz_yasindan_buyuk_olmanız_gerekmeketedir),Toast.LENGTH_LONG).show();
                                    return;
                                }else {
                                    age_et.setText(String.valueOf(calgulateAge(ageinMilis)));
                                    age.setText(String.valueOf(calgulateAge(ageinMilis)));
                                    horocope=calgulateHoroscope(month,dayOfMonth);
                                }




                            }
                        }, year, month, dayOfMonth);datePickerDialog.show();
            }
        });
        return view;
    }

    private void setRegister(MaterialEditText _name, final MaterialEditText _email, MaterialEditText _pass, MaterialEditText _passAgain, MaterialEditText _age, final RadioGroup _gender){
        dialogWait.setContentView(R.layout.wait_dialog);
        dialogWait.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWait.setCanceledOnTouchOutside(false);
        dialogWait.show();
        final String email = _email.getText().toString();
        String pass = _pass.getText().toString();
        String passAgaim = _passAgain.getText().toString();
        final String name = _name.getText().toString();
        final String age = _age.getText().toString();
        if (name.isEmpty())
        {
            _name.setError(getResources().getString(R.string.lutfen_adinizi_giriniz));
            _name.requestFocus();
            dialogWait.dismiss();
            return;
        }
        if (email.isEmpty()){
            _email.setError(getResources().getString(R.string.lutfen_eposta_adresinizi_giriniz));
            _email.requestFocus();
            dialogWait.dismiss();

            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            _email.setError(getResources().getString(R.string.lutfen_gecerli_bir_email_adresi_giriniz));
            _email.requestFocus();
            dialogWait.dismiss();

            return;
        }
        if (pass.isEmpty())
        {
            _pass.setError(getResources().getString(R.string.lutfen_sifreinizi_belirleyin));
            _pass.requestFocus();
            dialogWait.dismiss();

            return;
        }
        if (passAgaim.isEmpty())
        {
            _passAgain.setError(getResources().getString(R.string.sifrenizi_tekrar_giriniz));
            _passAgain.requestFocus();
            dialogWait.dismiss();

            return;
        }
        if (!pass.equals(passAgaim))
        {
            _pass.setError(getResources().getString(R.string.sifreler_ayni_degil));
            _pass.requestFocus();
            _pass.setText("");
            _passAgain.setError(getResources().getString(R.string.sifreler_ayni_degil));
            _passAgain.setText("");
            _passAgain.requestFocus();
            dialogWait.dismiss();

            return;
        }
        if (age.isEmpty()){
            Toast.makeText(getContext(),getResources().getString(R.string.lutfen_yasinizi_giriniz),Toast.LENGTH_LONG).show();
            dialogWait.dismiss();

            return;
        }
        if (_gender.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(getContext(),getResources().getString(R.string.lutfen_cinisinizyetiniz_giriniz),Toast.LENGTH_LONG).show();
            dialogWait.dismiss();

            return;
        }
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                   if (man.isChecked()){
                       final ModelUser user = new ModelUser("",ageinMilis,horocope,0,0,email,"MAN","",0.0,0.0,name,"","","",1,5,5.0,auth.getUid());
                       final String currentUser =auth.getUid();
                        db.collection("allUser").document(currentUser).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d(TAG, "onComplete: "+"task is succeful");
                                    db.collection("MAN").document(currentUser).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                setRate(uid,currentUser,"MAN");

                                                Intent i = new Intent(getContext(), HomeActivity.class);
                                                i.putExtra("gender","MAN");
                                                startActivity(i);
                                                getActivity().finish();
                                            }
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.toString());

                            }
                        });

                   }
                   else if (woman.isChecked()){
                       final ModelUser user = new ModelUser("",ageinMilis,horocope,0,0,email,"WOMAN","",0.0,0.0,name,"","","",1,5,5.0,auth.getUid());
                       final String currentUser =auth.getUid();
                       db.collection("allUser").document(currentUser).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Log.d(TAG, "onComplete: "+"task is succeful");
                                   db.collection("WOMAN").document(currentUser).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()){
                                               setRate(uid,currentUser,"WOMAN");
                                               Intent i = new Intent(getContext(), HomeActivity.class);
                                               i.putExtra("gender","WOMAN");;
                                               startActivity(i);
                                               getActivity().finish();
                                           }
                                       }
                                   });
                               }
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Log.d(TAG, e.toString());

                           }
                       });
                   }
                }
            }
        });


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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    private void setRate(String uid ,String currentUser, String gender){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference ref = db.collection(gender).document(currentUser);
        DocumentReference refRate = db.collection("rate").document(currentUser);
        Map<String ,Object> adminUid = new HashMap<>();
        Map<String ,Object> rate = new HashMap<>();
        rate.put("rate",5);
        adminUid.put(uid,rate);

        refRate.set(adminUid).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                Log.d(TAG, "onComplete: "+"suscces");
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
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
