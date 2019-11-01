package com.rzn.gargi.Log_Sign;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rzn.gargi.R;
import com.rzn.gargi.home.HomeActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    MaterialEditText email,password;
    Button loginButton;
    String _gender;
    View rootView;
    private FirebaseFirestore firebaseFirestore;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
       firebaseFirestore =FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();

        email=(MaterialEditText)rootView.findViewById(R.id.email);
        password=(MaterialEditText)rootView.findViewById(R.id.password);
        loginButton =(Button)rootView.findViewById(R.id.login_click);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {      // test();
                loginFonk(email,password);
            }
        });

        progressDialog = new ProgressDialog(getContext());

        return rootView;
    }

    private void loginFonk(MaterialEditText _email, MaterialEditText _password) {
        String email = _email.getText().toString();
        String password = _password.getText().toString();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            _email.setError(getResources().getString(R.string.lutfen_gecerli_bir_email_adresi_giriniz));
            _email.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            _email.setError(getResources().getString(R.string.lutfen_eposta_adresinizi_giriniz));
            _email.requestFocus();
            return;
        }
        if (password.isEmpty())
        {
            _password.setError(getResources().getString(R.string.lutfen_sifrenizi_giriniz));
            _password.requestFocus();
            return;
        }
        progressDialog.setMessage(getResources().getString(R.string.lutfen_bekleyiniz));
        progressDialog.setTitle(getResources().getString(R.string.giris_yapiliyor));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        _login(email,password);
    }

    private void _login(String _email, String _password) {
        try {
            final String tokenId = FirebaseInstanceId.getInstance().getToken();
            auth.signInWithEmailAndPassword(_email,_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()){
                        progressDialog.setMessage(getResources().getString(R.string.lutfen_bekleyiniz));
                        progressDialog.setTitle(getResources().getString(R.string.giris_yapiliyor));
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        email.setError(getResources().getString(R.string.eposta_yada_sifre_hatali));
                        email.requestFocus();

                        password.setError(getResources().getString(R.string.eposta_yada_sifre_hatali));
                        password.requestFocus();
                        progressDialog.cancel();
                        Log.d("task is succes", "onComplete:  giriş yapılmadı " );
                    }else{
                        Intent i = new Intent(getContext(), HomeActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                       /* HashMap<String , Object > tokenID = new HashMap<>();
                        HashMap<String , Object > userID = new HashMap<>();
                        HashMap<String , Object > node = new HashMap<>();
                        tokenID.put("tokenId",tokenId);
                        userID.put(auth.getUid(),tokenId);
                        node.put("tokenId",userID);
                        firebaseFirestore.collection("user").document("tokenId")
                                .set(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d("", "onComplete: "+"tamama");
                                }
                            }
                        });

                    }*/
                }
            });

        }catch (Exception e){
            Crashlytics.logException(e);
        }
    }

}
