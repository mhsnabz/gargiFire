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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marcoscg.easylicensesdialog.EasyLicensesDialogCompat;
import com.rzn.gargi.Log_Sign.LoginSignUp;
import com.rzn.gargi.R;

public class SettingActivity extends AppCompatActivity {
    ImageButton veriftyImage;
    Toolbar toolbar;
    Button logOut;
    TextView verfity,emailAdres,password,about;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        dialog=new Dialog(this);
        emailAdres=(TextView)findViewById(R.id.emailAdres);
        emailAdres.setText(firebaseUser.getEmail());
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
                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SettingActivity.this,"E Posta Adresinize Doğrulama Linki Gönderildi", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        if (firebaseUser.isEmailVerified())
        {
            verfity.setVisibility(View.INVISIBLE);
            veriftyImage.setVisibility(View.VISIBLE);
        }



        logOut=(Button)findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                GoogleSignIn.getClient(
                        SettingActivity.this,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                ).signOut();
                Intent intent = new Intent(SettingActivity.this, LoginSignUp.class);
                startActivity(intent);
                finish();
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

}
