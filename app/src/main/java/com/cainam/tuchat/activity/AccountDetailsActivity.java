package com.cainam.tuchat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cainam.tuchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class AccountDetailsActivity extends AppCompatActivity {

    private static final int GALLERY_INTENT_NUM = 1;
    private TextView mUsername;
    private TextView mUserStatus;
    private TextView mUsermail;
    private ImageView mDPImage;
    private ImageView mExpandProfileImage;
    private Button mStatusSetting;
    private String currentUID;
    private TextInputLayout mStatusUpdate;
    private Button mStatusUpdateSubmitButton;
    private Toolbar mUserDetailsToolbar;

    private FirebaseAuth mAuthorization;
    private FirebaseUser mUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private StorageReference mUserProfileStorage;
    private StorageReference mUserThumbnailImage;

    private ProgressBar mProgressBar;
    private ProgressBar mProfileImageProgressBar;
    private FloatingActionButton mProfileImageChooser;
    private Uri mImageUriFinal;

    private Snackbar snackbar;
    private String username;
    private String image;

    @Override
    protected void onStart() {
        super.onStart();

        if (mUser != null){
            mUserDatabase.child("online").setValue("true");
        }
    }

    /*
    @Override
    protected void onStop() {
        super.onStop();

        if (mUser != null){
            mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        mAuthorization = FirebaseAuth.getInstance();

        mUserDetailsToolbar = (Toolbar) findViewById(R.id.user_details_toolbar);
        setSupportActionBar(mUserDetailsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");


        mUsername = (TextView) findViewById(R.id.profile_username);
        mUsermail = (TextView) findViewById(R.id.profile_email);
        mUserStatus = (TextView) findViewById(R.id.profile_status);
        mDPImage = (ImageView) findViewById(R.id.chat_profile_image);

        mStatusSetting = (Button) findViewById(R.id.profile_status_setting);
        mProfileImageChooser = (FloatingActionButton) findViewById(R.id.fab_profile_image_chooser);
        mProfileImageProgressBar = (ProgressBar) findViewById(R.id.profile_image_progressbar_circle);
        mProgressBar = (ProgressBar) findViewById(R.id.profile_progress_bar);
        mProfileImageProgressBar.setVisibility(View.VISIBLE);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        mUser = mAuthorization.getCurrentUser();

        mStatusSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateCurrentUserStatus();
            }
        });

        mProfileImageChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserProfileImage();
            }
        });

        mDPImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandedViewOfUserProfileImage();
            }
        });

        if (mUser != null){
            currentUID = mUser.getUid();
            mUserDatabase = mDatabaseReference.child("Users").child(currentUID);
            mUserDatabase.keepSynced(true);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    username = dataSnapshot.child("username").getValue().toString();
                    image = dataSnapshot.child("image").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();

                    if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(status)){
                        mUsername.setText(username);
                        mUserStatus.setText(status);
                        mUsermail.setText(email);

                        if (!TextUtils.isEmpty(image)){

                            if (image.equals("avatar")){
                                mProfileImageProgressBar.setVisibility(View.INVISIBLE);
                            }else{
                                Picasso.with(AccountDetailsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.profile_icon).into(mDPImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        mProfileImageProgressBar.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(AccountDetailsActivity.this).load(image).placeholder(R.drawable.profile_icon).into(mDPImage);
                                        mProfileImageProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void ExpandedViewOfUserProfileImage() {

        AlertDialog.Builder expandImageDialog = new AlertDialog.Builder(AccountDetailsActivity.this);
        View profileView = getLayoutInflater().inflate(R.layout.image_expand_view_dialog, null);
        expandImageDialog.setView(profileView);

        mExpandProfileImage = (ImageView) profileView.findViewById(R.id.profile_image_expand);
        Picasso.with(AccountDetailsActivity.this).load(image)
                .fit()
                .placeholder(R.mipmap.ic_photo_white_24dp)
                .into(mExpandProfileImage);

        expandImageDialog.create();
        expandImageDialog.show();
    }

    public void UpdateUserProfileImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_INTENT_NUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT_NUM && resultCode == RESULT_OK){
//            mProfileImageProgressBar.setVisibility(View.VISIBLE);
//            mProgressBar.setVisibility(View.VISIBLE);
            mImageUriFinal = data.getData();

            CropImage.activity(mImageUriFinal)
                    .setAspectRatio(1,1)
                    .setActivityTitle(getString(R.string.application_name))
                    .setAutoZoomEnabled(true)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult imageActivityResult = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProfileImageProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);

                Uri uri = imageActivityResult.getUri();
                Context context = AccountDetailsActivity.this;
                File imageFile = new File(uri.getPath());
                Bitmap userProfileBitmap;

                try {
                    userProfileBitmap = new Compressor(context)
                            .setMaxWidth(100)
                            .setMaxHeight(100)
                            .setQuality(100)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .compressToBitmap(imageFile);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    userProfileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    final byte[] thumbnailByte = byteArrayOutputStream.toByteArray();

                    mUserProfileStorage = mStorageReference.child("Users_Profile_Image").child(currentUID + ".jpg");
                    mUserThumbnailImage = mStorageReference.child("Users_Profile_Image").child("thumbnails").child(currentUID + ".jpg");

                    mUserProfileStorage.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful() && task.isComplete()){
//                                mProfileImageProgressBar.setVisibility(View.INVISIBLE);

                                final String profileDownloadUrl = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = mUserThumbnailImage.putBytes(thumbnailByte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isComplete() && task.isSuccessful()){

                                            String thumbnailDownloadUrl = task.getResult().getDownloadUrl().toString();

                                            Map imageMap = new HashMap<>();

                                            imageMap.put("image", profileDownloadUrl);
                                            imageMap.put("thumbnail", thumbnailDownloadUrl);

                                            mUserDatabase.updateChildren(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isComplete() && task.isSuccessful()){
                                                        mProfileImageProgressBar.setVisibility(View.GONE);
                                                        mProgressBar.setVisibility(View.GONE);
                                                        View profileSnackbarView = findViewById(R.id.profile_relative_layout);
                                                        snackbar = Snackbar.make(profileSnackbarView, "Hi! " + username + " your image has been updated", Snackbar.LENGTH_LONG);
                                                        View snackbarView = snackbar.getView();
                                                        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        snackbar.show();
                                                    }
                                                }
                                            });
                                        }else {
                                            //Handle some error in uploading profile image and its thumbnail. I'll do it later
                                        }
                                    }
                                });
                            }else {
                                mProfileImageProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(AccountDetailsActivity.this, "Failed to update Image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = imageActivityResult.getError();
                Toast.makeText(AccountDetailsActivity.this, "Image Crop Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void UpdateCurrentUserStatus() {

        final AlertDialog.Builder statusAlertBuilder = new AlertDialog.Builder(AccountDetailsActivity.this);
        View myView = getLayoutInflater().inflate(R.layout.status_alert_dialog, null);
        mStatusUpdate = (TextInputLayout) myView.findViewById(R.id.profile_status_update);
        mStatusUpdateSubmitButton = (Button) myView.findViewById(R.id.profile_update_button);

        mStatusUpdateSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String statusUpdate = mStatusUpdate.getEditText().getText().toString().trim();

                if (!TextUtils.isEmpty(statusUpdate)){
                    mUserDatabase.child("status").setValue(statusUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AccountDetailsActivity.this, "Status Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AccountDetailsActivity.this, "Status update failed", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }else {
                    Toast.makeText(AccountDetailsActivity.this, "Please write status", Toast.LENGTH_SHORT).show();
                }
            }
        });
        statusAlertBuilder.setView(myView);
        statusAlertBuilder.create();
        statusAlertBuilder.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int itemID = item.getItemId();

        switch (itemID){
            case R.id.action_profile_status:
                UpdateCurrentUserStatus();
                break;

            case R.id.action_profile_image:
                UpdateUserProfileImage();
                break;
            case R.id.action_update_profile:

                Intent updateIntent = new Intent(AccountDetailsActivity.this, UpdateProfileActivity.class);
                startActivity(updateIntent);

                break;
        }
        return true;
    }
}
