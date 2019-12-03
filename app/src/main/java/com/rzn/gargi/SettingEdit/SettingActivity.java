package com.rzn.gargi.SettingEdit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marcoscg.easylicensesdialog.EasyLicensesDialogCompat;
import com.rzn.gargi.R;
import com.rzn.gargi.SplashScreen.SplashScreen;

public class SettingActivity extends AppCompatActivity {
    ImageButton veriftyImage;
    Toolbar toolbar;
    Button logOut;
    TextView headerEmail;
    TextView verfity,emailAdres,password,about;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        headerEmail=(TextView)findViewById(R.id.headerEmail);
        dialog=new Dialog(this);
        emailAdres=(TextView)findViewById(R.id.emailAdres);
        if (firebaseUser.getEmail()!=null){
            emailAdres.setText(firebaseUser.getEmail());
        }else if (firebaseUser.getPhoneNumber()!=null){
            headerEmail.setText(getResources().getString(R.string.telefon_numarasi_ayarlari));
            emailAdres.setText(firebaseUser.getPhoneNumber());
        }

        veriftyImage=(ImageButton)findViewById(R.id.verfityImafge);
        verfity=(TextView)findViewById(R.id.verfity);
        about=(TextView)findViewById(R.id.about);
        password=(TextView)findViewById(R.id.password);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SettingActivity.this, setting_password.class);
                startActivity(intent);
            }
        });
        verfity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.setContentView(R.layout.wait_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                if (firebaseUser.getEmail()!=null)
                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SettingActivity.this,"E Posta Adresinize Doğrulama Linki Gönderildi", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        if (firebaseUser.getPhoneNumber()!=null){
            verfity.setVisibility(View.INVISIBLE);
            veriftyImage.setVisibility(View.VISIBLE);
        }else  if (firebaseUser.getEmail()!=null )
        {
            if (firebaseUser.isEmailVerified()){
                verfity.setVisibility(View.INVISIBLE);
                veriftyImage.setVisibility(View.VISIBLE);
            }

        }



        logOut=(Button)findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(SettingActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(SettingActivity.this, SplashScreen.class);

                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });


            }
        });

    }
    public void showLicensesDialog(View view) {
        new EasyLicensesDialogCompat(this)
                .setTitle("Licenses")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    public void setNotification(View view)
    {
        Intent intent = new Intent(SettingActivity.this, NotificaitonSettingsActivity.class);
        intent.putExtra("currentUser",firebaseUser.getUid());
        startActivity(intent);
    }
    public void back(View view) {


        finish();


    }
    public void aboutApp(View view)
    {
        Intent i = new Intent(SettingActivity.this,aboutapp.class);
        startActivity(i);


    }

    public void helpAndSupport(View view) {
        //  Intent i = new Intent(SettingActivity.this,HelpAndSupport.class);
        //  startActivity(i);
    }

    public void rateUs(View view) {



            final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                    .icon(getDrawable(R.drawable.big_logo))
                    .session(1)
                    .threshold(3)
                    .title("How was your experience with us?")
                    .titleTextColor(R.color.black)
                    .negativeButtonText("Never")
                    .positiveButtonTextColor(R.color.black)
                    .negativeButtonTextColor(R.color.black)
                    .formTitle("Submit Feedback")
                    .formHint("Tell us where we can improve")
                    .formSubmitText("Submit")
                    .formCancelText("Cancel")
                    .ratingBarColor(R.color.colorAccent)
                    .playstoreUrl("https://play.google.com/store/apps/details?id=com.rzn.gargi")
                    .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                        @Override
                        public void onThresholdCleared(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                            //do something
                            ratingDialog.dismiss();
                        }
                    })
                    .onThresholdFailed(new RatingDialog.Builder.RatingThresholdFailedListener() {
                        @Override
                        public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                            //do something
                            ratingDialog.dismiss();
                        }
                    })
                    .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                        @Override
                        public void onRatingSelected(float rating, boolean thresholdCleared) {

                        }
                    })
                    .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                        @Override
                        public void onFormSubmitted(String feedback) {

                        }
                    }).build();

            ratingDialog.show();

    }
}
