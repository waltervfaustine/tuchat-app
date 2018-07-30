package com.cainam.tuchat.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cainam.tuchat.R;
import com.cainam.tuchat.homescreen.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cainam.tuchat.R.id.finishing_account_username;

public class FinishingAccountActivity extends AppCompatActivity {

    private Toolbar mFinishing;
    private TextInputLayout mUsername;
    private Button mFinishingButton;
    private FirebaseAuth mAuthorization;
    private DatabaseReference mUserDatabase;
    private String currentUID;
    private FirebaseUser mUser;
    private FirebaseInstanceId mTockenID;
    private ProgressDialog mProgress;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finishing_account);

        mContext = getApplicationContext();

        mAuthorization =  FirebaseAuth.getInstance();
        mTockenID = FirebaseInstanceId.getInstance();
        mUser = mAuthorization.getCurrentUser();
        mProgress = new ProgressDialog(this);

        mFinishing = (Toolbar) findViewById(R.id.finishing_account_toolbar);
        setSupportActionBar(mFinishing);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (mUser != null){
            currentUID = mUser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        }

        mUsername = (TextInputLayout) findViewById(R.id.finishing_account_username);
        mFinishingButton = (Button) findViewById(R.id.finishing_account_button);

        mFinishingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckUserDataIfExistInUsersDatabase();
            }
        });

    }

    private void CheckUserDataIfExistInUsersDatabase() {

        final String username = mUsername.getEditText().getText().toString();
        final String tocken = mTockenID.getToken();
        final String email = mUser.getEmail();
        final Map<String, String> timestamp = ServerValue.TIMESTAMP;

        if (!TextUtils.isEmpty(username)){
            mProgress.setMessage("Finishing account...");
            mProgress.show();

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null){

                        if (!dataSnapshot.hasChild(currentUID)){

                            Map finishingMap = new HashMap();

                            finishingMap.put("username", username);
                            finishingMap.put("email", email);
                            finishingMap.put("status", "Hey! I'm using TuChat App");
                            finishingMap.put("thumbnail", "avatar_thumbnail");
                            finishingMap.put("image", "avatar");
                            finishingMap.put("tockenid", tocken);
                            finishingMap.put("timestamp", timestamp);
                            finishingMap.put("online", "true");

                            mUserDatabase.child(currentUID).updateChildren(finishingMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if (databaseError == null){
                                        mProgress.dismiss();
                                        Intent registerIntent = new Intent(FinishingAccountActivity.this, MainActivity.class);
                                        registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(registerIntent);
                                        finish();
                                    }

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            View profileSnackbarView = findViewById(R.id.finishing_account_layout);
            Snackbar snackbar = Snackbar.make(profileSnackbarView, "Type your Username before proceeding", Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            snackbar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.finishing_account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int itemID = item.getItemId();
        return true;
    }
}
