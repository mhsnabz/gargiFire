package com.rzn.gargi.Log_Sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rzn.gargi.R;
import com.rzn.gargi.helper.UserInfo;
import com.rzn.gargi.home.HomeActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FaceOrGoogle extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_or_google);
        launchFirebaseSignInIntent();
    }
    public void launchFirebaseSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(true)
                .setAvailableProviders(providers)
                .setLogo(R.drawable.big_logo)      // Set logo drawable
//                        .setTheme(R.style.MySuperAppTheme)      // Set theme
                .setTosAndPrivacyPolicyUrls(
                        "https://www.google.com/",
                        "https://images.google.com/")
                .build();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                checkForUserIsExistOrNot();
                        // ...
            }  else {
                // Sign in failed
                if (response == null) {
                    Toast.makeText(this, "UnKnown error", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                FirebaseUiException error = response.getError();

                if (error !=null && error.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No Network Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "UnKnown error", Toast.LENGTH_SHORT).show();
                Log.e("singin", "Sign-in error: ", error);
                finish();
            }
        }
    }

    private void checkForUserIsExistOrNot()
    {
        FirebaseAuth instance = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = instance.getCurrentUser();
        db.collection("allUser")
                .document(currentUser.getUid()).get().addOnCompleteListener(FaceOrGoogle.this, new
                OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (task.getResult().getString("name")!=null){

                                Intent i= new Intent(FaceOrGoogle.this, HomeActivity.class);
                                i.putExtra("gender",task.getResult().getString("gender"));
                                startActivity(i);finish();
                            }else {
                                saveUserToDatabase(currentUser);
                            }
                        }
                    }
                }).addOnFailureListener(FaceOrGoogle.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void saveUserToDatabase(FirebaseUser currentUser)
    {
        if (currentUser!=null){
            String email = currentUser.getEmail();
            String name = currentUser.getDisplayName();
            String uid = currentUser.getUid();
            if (email==null || name.isEmpty()){
                Intent i= new Intent(FaceOrGoogle.this,PhoneRegister.class);
                startActivity(i);
            }else {
                Intent i = new Intent(FaceOrGoogle.this,NewUserInfo.class);
                i.putExtra("email",email);
                i.putExtra("name",name);
                i.putExtra("uid",uid);
                startActivity(i);
            }

        }
    }

    public void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        // [START auth_fui_theme_logo]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.logo)      // Set logo drawable
                       // .setTheme(R.style.MySuperAppTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_theme_logo]
    }
    public void privacyAndTerms() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();
        // [START auth_fui_pp_tos]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_pp_tos]
    }
}
