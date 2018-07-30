package com.cainam.tuchat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cainam.tuchat.homescreen.MainActivity;
import com.cainam.tuchat.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "Google Signing";
    private static final int RC_SIGN_IN = 1;
    private TextInputLayout mUsermail;
    private TextInputLayout mPasscode;
    private Button mLoginButton;
    private Button mNewAccount;
    private SignInButton mGoogleButton;

    private FirebaseAuth mAuthorization;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgress;
    private FirebaseInstanceId mInstanceID;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuthorization = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        mInstanceID = FirebaseInstanceId.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mUsermail = (TextInputLayout) findViewById(R.id.login_email);
        mPasscode = (TextInputLayout) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mNewAccount = (Button) findViewById(R.id.finishing_account_button);
        mGoogleButton = (SignInButton) findViewById(R.id.google_signin_button);

        RedirectToCreateNewAccount();
        SignInToBeAuthenticatedBySystem();
        SignInWithGoogleAccount();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener(){
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SignInActivity.this, "Error in signing", Toast.LENGTH_SHORT).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuthorization.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mProgress.dismiss();
                            Intent signInIntent = new Intent(SignInActivity.this, FinishingAccountActivity.class);
                            signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(signInIntent);
                            finish();

                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuthorization.getCurrentUser();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();;
                        }
                    }
                });
    }

    private void signIn() {

        mProgress.setMessage("Signin. Please wait...");
        mProgress.show();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void SignInWithGoogleAccount() {

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void SignInToBeAuthenticatedBySystem() {

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mUsermail.getEditText().getText().toString().trim();
                String password = mPasscode.getEditText().getText().toString().trim();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mProgress.setMessage("Signing. PLease wait...");
                    mProgress.show();

                    mAuthorization.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isComplete() && task.isSuccessful()){

                                mInstanceID = FirebaseInstanceId.getInstance();
                                mUser = mAuthorization.getCurrentUser();
                                final String UTockenID = mInstanceID.getToken();
                                final String UID = mUser.getUid();
                                mUserDatabase = mRef.child("Users").child(UID);

                                Map userMap = new HashMap<>();
                                userMap.put("tockenid", UTockenID);

                                mUserDatabase.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isComplete() && task.isSuccessful()){
                                            mProgress.dismiss();
                                            Intent signInIntent = new Intent(SignInActivity.this, MainActivity.class);
                                            signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(signInIntent);
                                            finish();
                                        }
                                    }
                                });
                            }else {
                                mProgress.dismiss();
                                Toast.makeText(SignInActivity.this, "Signin Process Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    mProgress.dismiss();
                    Toast.makeText(SignInActivity.this, "Please fill the credentials correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Boolean ValidatePasscode(String password) {

        if (TextUtils.isEmpty(password) || (password.length() < 6)){
            return false;
        }else {
            return true;
        }
    }

    private Boolean ValidateEmail(String email) {

        if (!TextUtils.isEmpty(email)){
            String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(emailPattern);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }else {
            return false;
        }
    }

    private void RedirectToCreateNewAccount() {
        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(signInIntent);
            }
        });
    }
}
