package com.rzn.gargi.SettingEdit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rzn.gargi.R;

public class setting_password extends AppCompatActivity {
    MaterialEditText oldPass,newPass,newPassAgain;
    FirebaseUser user;
    Button change;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);
        user= FirebaseAuth.getInstance().getCurrentUser();
        oldPass=(MaterialEditText)findViewById(R.id.currentPass);
        newPass=(MaterialEditText)findViewById(R.id.newPassword);
        newPassAgain=(MaterialEditText)findViewById(R.id.newPasswordAgain);
        change=(Button)findViewById(R.id.changePassword);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                changePasswordFunk(oldPass.getText().toString(),newPass.getText().toString(),newPassAgain.getText().toString());
            }
        });
    }

    private void changePasswordFunk(String _oldPass, final String _newPass, String _newPassAgain)
    {
        if (_oldPass.isEmpty())
        {
            oldPass.setError("Lütfen Şifrenizi Giriniz ");
            oldPass.requestFocus();
            return;
        }
        if (_newPass.isEmpty())
        {
            newPass.setError("Lütfen Yeni Şifrenizi Giriniz ");
            newPass.requestFocus();
            return;
        }
        if (_newPassAgain.isEmpty())
        {
            newPassAgain.setError("Lütfen Yeni Şifrenizi Tekrar Giriniz ");
            newPassAgain.requestFocus();
            return;
        }
        if (!_newPass.equals(_newPassAgain))
        {
            newPass.setError("Şifreleriniz Aynı Değil");
            newPassAgain.setError("Şifreleriniz Aynı Değil");
            newPass.requestFocus();
            newPassAgain.requestFocus();
            return;
        }
        user=FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,_oldPass);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    user.updatePassword(_newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (!task.isSuccessful())
                            {
                                Toast.makeText(setting_password.this,"Şifreniz Değiştirilemedi,Lütfen Tekrar Deneyiniz ",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(setting_password.this,"Şifreniz Değiştirildi",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
                else
                {
                    oldPass.setError("Şifreniz Doğru Değil");
                    oldPass.requestFocus();
                }

            }
        });
    }

    public void finish(View view)
    {
        finish();
    }
}
