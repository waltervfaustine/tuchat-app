package com.cainam.tuchat.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cainam.tuchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UpdateProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mName;
    private EditText mUsername;
    private EditText mWebsite;
    private EditText mDesc;
    private EditText mEmail;
    private EditText mPhonecall;
    private CheckBox mFemale;
    private CheckBox mMale;
    private String gender;
    private CircleImageView mProfile;

    private FirebaseAuth mAuthorization;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mRootRef;
    private FirebaseUser mUser;
    private String currentUID;
    private ProgressDialog mProgress;
    private FloatingActionButton mProfileChooser;
    private static final int GALLERY_INTENT_NUM = 2;

    private StorageReference mUserProfileStorage;
    private StorageReference mUserThumbnailImage;
    private StorageReference mStorageReference;
    private Uri mImageUriFinal;

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser != null){
            mRootRef.child(currentUID).child("online").setValue("true");
        }
    }

    /*
    @Override
    protected void onStop() {
        super.onStop();

        if (mUser != null){
            mCurrentDB.child("online").setValue(ServerValue.TIMESTAMP);

            DatabaseReference typingDB = mRootRef.child("Typing").child(friendUID).child(currentUID).child("istyping");
            typingDB.setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        mAuthorization = FirebaseAuth.getInstance();
        mUser = mAuthorization.getCurrentUser();

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(UpdateProfileActivity.this);

        mToolbar = (Toolbar) findViewById(R.id.update_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = (EditText) findViewById(R.id.update_profile_name);
        mUsername = (EditText) findViewById(R.id.update_profile_username);
        mWebsite = (EditText) findViewById(R.id.update_profile_website);
        mDesc = (EditText) findViewById(R.id.update_profile_description);
        mEmail = (EditText) findViewById(R.id.update_profile_email);
        mPhonecall = (EditText) findViewById(R.id.update_profile_phonenumber);
        mProfileChooser = (FloatingActionButton) findViewById(R.id.update_profile_fab);
        mFemale = (CheckBox) findViewById(R.id.update_profile_gender_female);
        mMale = (CheckBox) findViewById(R.id.update_profile_gender_male);
        mProfile = (CircleImageView) findViewById(R.id.update_profile_image);

        mName.setFocusable(false);
        mUsername.setFocusable(false);
        mWebsite.setFocusable(false);
        mDesc.setFocusable(false);
        mEmail.setFocusable(false);
        mPhonecall.setFocusable(false);

        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName.setFocusableInTouchMode(true);
            }
        });

        mUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername.setFocusableInTouchMode(true);
            }
        });

        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebsite.setFocusableInTouchMode(true);
            }
        });

        mDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDesc.setFocusableInTouchMode(true);
            }
        });

        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail.setFocusableInTouchMode(true);
            }
        });

        mPhonecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhonecall.setFocusableInTouchMode(true);
            }
        });


        mProfileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateCurrentProfileImage();
            }
        });


        if (mUser != null){
            currentUID = mUser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
            mRootRef = FirebaseDatabase.getInstance().getReference();
        }

        mFemale.setChecked(false);
        mMale.setChecked(false);
        mFemale.setTextColor(getResources().getColor(R.color.colorBlack));
        mMale.setTextColor(getResources().getColor(R.color.colorBlack));


        mFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    mFemale.setChecked(true);
                    mMale.setChecked(false);
                    gender = "Female";
                }else{
                    mFemale.setChecked(false);
                }
            }
        });

        mMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    mMale.setChecked(true);
                    mFemale.setChecked(false);
                    gender = "Male";
                }else{
                    mMale.setChecked(false);
                }
            }
        });



        RetriveUserInformationFromDatabase();
    }

    public void UpdateCurrentProfileImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_INTENT_NUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT_NUM && resultCode == RESULT_OK){
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

                Uri uri = imageActivityResult.getUri();
                Context context = UpdateProfileActivity.this;
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

                                                }
                                            });
                                        }else {
                                            //Handle some error in uploading profile image and its thumbnail. I'll do it later
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(UpdateProfileActivity.this, "Failed to update Image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = imageActivityResult.getError();
                Toast.makeText(UpdateProfileActivity.this, "Image Crop Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void RetriveUserInformationFromDatabase() {

        if (mUser != null){

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null){

                        if (dataSnapshot.hasChild("image")){
                            String image = dataSnapshot.child("image").getValue().toString();
                            Picasso.with(UpdateProfileActivity.this).load(image).placeholder(R.drawable.profile_icon).into(mProfile);
                        }else {

                        }


                        if (dataSnapshot.hasChild("username")){
                            String username = dataSnapshot.child("username").getValue().toString();
                            mUsername.setText(username);
                        }else {
                            mUsername.setText("");
                        }

                        if (dataSnapshot.hasChild("name")){
                            String name = dataSnapshot.child("name").getValue().toString();
                            mName.setText(name);
                        }else {
                            mName.setText("");
                        }


                        if (dataSnapshot.hasChild("website")){
                            String website = dataSnapshot.child("website").getValue().toString();
                            mWebsite.setText(website);
                        }else {
                            mWebsite.setText("");
                        }


                        if (dataSnapshot.hasChild("phonecall")){
                            String phonecall = dataSnapshot.child("phonecall").getValue().toString();
                            mPhonecall.setText(phonecall);
                        }else {
                            mPhonecall.setText("");
                        }


                        if (dataSnapshot.hasChild("gender")){
                            gender = dataSnapshot.child("gender").getValue().toString();

                            if (gender.equals("Female")){
                                mFemale.setChecked(true);
                                mMale.setChecked(false);
                                mFemale.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }else if (gender.equals("Male")){
                                mFemale.setChecked(false);
                                mMale.setChecked(true);
                                mMale.setTextColor(getResources().getColor(R.color.colorAccent));
                            }

                        }else {
                            mFemale.setChecked(false);
                            mMale.setChecked(false);
                            Toast.makeText(UpdateProfileActivity.this, "Please select gender for you!", Toast.LENGTH_LONG).show();
                        }


                        if (dataSnapshot.hasChild("description")){
                            String description = dataSnapshot.child("description").getValue().toString();
                            mDesc.setText(description);
                        }else {
                            mDesc.setText("");
                        }


                        if (dataSnapshot.hasChild("email")){
                            String email = dataSnapshot.child("email").getValue().toString();
                            mEmail.setText(email);
                        }else {
                            mEmail.setText("");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void UpdateUserInformationsToDatabase() {

        String name = mName.getText().toString();
        String username = mUsername.getText().toString();
        String website = mWebsite.getText().toString();
        String desc = mDesc.getText().toString();
        String email = mEmail.getText().toString();
        String phonecall = mPhonecall.getText().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(website) && !TextUtils.isEmpty(desc)
                && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phonecall) && !TextUtils.isEmpty(gender)){

            if (mUser != null){

                mProgress.setMessage("Stay Calm. Updating...");
                mProgress.show();

                String userInfo = "Users/" + currentUID;

                Map updateProfileMap = new HashMap();
                updateProfileMap.put(userInfo + "/" + "name", name);
                updateProfileMap.put(userInfo + "/" + "username", username);
                updateProfileMap.put(userInfo + "/" + "website", website);
                updateProfileMap.put(userInfo + "/" + "description", desc);
                updateProfileMap.put(userInfo + "/" + "email", email);
                updateProfileMap.put(userInfo + "/" + "phonecall", phonecall);
                updateProfileMap.put(userInfo + "/" + "gender", gender);

                mRootRef.updateChildren(updateProfileMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null){
                            mProgress.dismiss();
                            Toast.makeText(UpdateProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        }else {
                            mProgress.dismiss();
                            Toast.makeText(UpdateProfileActivity.this, "Profile failed to be updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else {
            Toast.makeText(getApplicationContext(), "Please fill all details in the fields", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.update_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        ActionMenuItemView submit = (ActionMenuItemView) mToolbar.findViewById(R.id.action_submit_data);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserInformationsToDatabase();
            }
        });

        return true;
    }
}
