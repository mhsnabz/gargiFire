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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    LoginButton fbLoginButton;

    private static final String TAG = "FacebookLogin";
    private CallbackManager mCallbackManager;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
       firebaseFirestore =FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        email=(MaterialEditText)rootView.findViewById(R.id.email);
        password=(MaterialEditText)rootView.findViewById(R.id.password);
        fbLoginButton=(LoginButton)rootView.findViewById(R.id.login_button);
        fbLoginButton.setPermissions("email","public_profile","user_gender"," birthday");
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void updateUI(FirebaseUser user) {
        if (user!=null){
            String name = user.getDisplayName();
            String email = user.getEmail();
        }
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
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
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference ref =
                                db
                                .collection("allUser")
                                .document(auth.getUid());
                        ref.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    String gender = task.getResult().getString("gender");
                                    Intent i = new Intent(getContext(), HomeActivity.class);
                                    i.putExtra("gender",gender);
                                    startActivity(i);
                                    getActivity().finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Crashlytics.log(e.toString());
                            }
                        });
                       // i.putExtra("gender",)

                    }
                }
            });

        }catch (Exception e){
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }
}
