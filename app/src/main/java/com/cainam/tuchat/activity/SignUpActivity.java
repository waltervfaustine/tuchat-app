package com.cainam.tuchat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cainam.tuchat.homescreen.MainActivity;
import com.cainam.tuchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout mUsername;
    private TextInputLayout mUsermail;
    private TextInputLayout mUserPasscode;
    private Button mRegButton;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuthorization;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseInstanceId mFirebaseInstanceId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseInstanceId = FirebaseInstanceId.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mProgress = new ProgressDialog(this);
        mAuthorization = FirebaseAuth.getInstance();

        mUsername = (TextInputLayout) findViewById(R.id.register_username);
        mUsermail = (TextInputLayout) findViewById(R.id.register_email);
        mUserPasscode = (TextInputLayout) findViewById(R.id.register_password);
        mRegButton = (Button) findViewById(R.id.register_button);

        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateUserAccount();
            }
        });
    }

    private void CreateUserAccount() {

        final String username = mUsername.getEditText().getText().toString().trim();
        final String email = mUsermail.getEditText().getText().toString().trim();
        String password = mUserPasscode.getEditText().getText().toString().trim();

        if (!TextUtils.isEmpty(username) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

            mProgress.setMessage("Signup. Please wait...");
            mProgress.show();

            mAuthorization.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isComplete() && task.isSuccessful()){

                        final FirebaseUser mUser = mAuthorization.getCurrentUser();
                        final String UID = mUser.getUid();
                        final String UTokenID = mFirebaseInstanceId.getToken();

                        final Map<String, String> timestamp = ServerValue.TIMESTAMP;

                        final DatabaseReference mUserDatabase = mRef.child("Users").child(UID);

                        Map mUserMap = new HashMap();

                        mUserMap.put("username", username);
                        mUserMap.put("email", email);
                        mUserMap.put("status", "Hey! I'm using TuChat App");
                        mUserMap.put("thumbnail", "avatar_thumbnail");
                        mUserMap.put("image", "avatar");
                        mUserMap.put("tockenid", UTokenID);
                        mUserMap.put("timestamp", timestamp);
                        mUserMap.put("online", "true");

                        mUserDatabase.updateChildren(mUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                mProgress.dismiss();

                                if (databaseError == null){

                                    Intent registerIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                    registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(registerIntent);
                                    finish();
                                }
                            }
                        });
                    }else {
                        mProgress.dismiss();
                        Toast.makeText(SignUpActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(SignUpActivity.this, "Please fill necessary credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
