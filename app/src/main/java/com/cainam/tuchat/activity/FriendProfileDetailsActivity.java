package com.cainam.tuchat.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cainam.tuchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.database.ServerValue.*;

public class FriendProfileDetailsActivity extends AppCompatActivity {

    private String name;
    private String username;
    private String status;
    private String phonecall;
    private String image;
    private String website;
    private String description;
    private String gender;
    private String email;
    private String thumbnail;

    private TextView mUsername;
    private TextView mEmail;
    private TextView mStatus;
    private ImageView mProfile;
    private ProgressBar mProfileImageProgressbar;
    private ProgressBar mProfileDataProgressbar;
    private ProgressBar mFollowUnfollowProgressbar;
    private Button mFollowButton;
    private Button mFollowButtonOther;
    private Button mRejectButton;
    private Toolbar mToolbar;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mUserFollowRequestDB;
    private DatabaseReference mFriendshipDatabase;
    private DatabaseReference mFollowNotificationDB;
    private DatabaseReference mOnlineDatabase;



    private FirebaseUser mUser;
    private FirebaseAuth mFirebaseAuth;
    private Context mContext;
    private String friendUID;
    private String currentUID;
    private int mState;
    private String mFollowRequestType = "type";
    private String meRequestType = "sent";
    private String yourRequestType = "received";

    private int not_friend = 0;
    private int request_sent = 2;
    private int request_received = 3;
    private int already_friend = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile_details);

        mState = not_friend;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();
        currentUID = mUser.getUid();

        mContext = FriendProfileDetailsActivity.this;
        mUsername = (TextView) findViewById(R.id.friend_profile_username);
        mEmail = (TextView) findViewById(R.id.friend_profile_email);
        mStatus = (TextView) findViewById(R.id.friend_profile_status);
        mProfile = (ImageView) findViewById(R.id.friend_profile_image);
        mFollowButton = (Button) findViewById(R.id.friend_profile_follow_request);
        mFollowButtonOther = (Button) findViewById(R.id.friend_profile_follow_request_other);
        mRejectButton = (Button) findViewById(R.id.friend_profile_reject_request);
        mProfileImageProgressbar = (ProgressBar) findViewById(R.id.friend_image_progressbar);
        mProfileDataProgressbar = (ProgressBar) findViewById(R.id.friend_data_progressbar);
        mFollowUnfollowProgressbar = (ProgressBar) findViewById(R.id.friend_follow_unfollow_progressbar);
        mFollowUnfollowProgressbar.setVisibility(View.INVISIBLE);
        mProfileImageProgressbar.setVisibility(View.VISIBLE);
        mProfileDataProgressbar.setVisibility(View.VISIBLE);

        mToolbar = (Toolbar) findViewById(R.id.friend_profile_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(username);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();

        mRejectButton.setEnabled(false);

        friendUID = getIntent().getStringExtra("userID");
        mOnlineDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);

        mUserDatabase = mDatabaseRef.child("Users").child(friendUID);
        mFollowNotificationDB = mDatabaseRef.child("notifications");
        mUserDatabase.keepSynced(true);
        mUserFollowRequestDB = mDatabaseRef.child("Follow_Request");
        mUserFollowRequestDB.keepSynced(true);
        mFriendshipDatabase = mDatabaseRef.child("Friends");
        mFriendshipDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null){

                    mProfileDataProgressbar.setVisibility(View.INVISIBLE);

                    if (dataSnapshot.hasChild("username")){
                        username = dataSnapshot.child("username").getValue().toString();
                        getSupportActionBar().setTitle(username);
                        mUsername.setText(username);
                    }else {

                    }

                    if (dataSnapshot.hasChild("name")){
                        name = dataSnapshot.child("name").getValue().toString();
                    }else {

                    }

                    if (dataSnapshot.hasChild("status")){
                        status = dataSnapshot.child("status").getValue().toString();
                        mStatus.setText(status);
                    }else {

                    }

                    if (dataSnapshot.hasChild("website")){
                        website = dataSnapshot.child("website").getValue().toString();
                    }else {

                    }

                    if (dataSnapshot.hasChild("phonecall")){
                        phonecall = dataSnapshot.child("phonecall").getValue().toString();
                    }else {

                    }

                    if (dataSnapshot.hasChild("gender")){
                        gender = dataSnapshot.child("gender").getValue().toString();
                    }else {

                    }

                    if (dataSnapshot.hasChild("description")){
                        description = dataSnapshot.child("description").getValue().toString();
                    }else {

                    }

                    if (dataSnapshot.hasChild("email")){
                        email = dataSnapshot.child("email").getValue().toString();
                        mEmail.setText(email);
                    }else {

                    }

                    if (dataSnapshot.hasChild("image")){
                        image = dataSnapshot.child("image").getValue().toString();

                        if (image.equals("avatar")) {
                            mProfileImageProgressbar.setVisibility(View.INVISIBLE);
                        } else {
                            mProfileImageProgressbar.setVisibility(View.INVISIBLE);
                            Picasso.with(mContext).load(image).placeholder(R.drawable.man_profile).fit().into(mProfile);
                        }
                    }else {

                    }

                    if (dataSnapshot.hasChild("thumbnail")){
                        thumbnail = dataSnapshot.child("thumbnail").getValue().toString();
                    }else {

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFollowUnfollowProgressbar.setVisibility(View.VISIBLE);

                switch (mState) {
                    case 0:
                        SendFollowRequestToCurrentFriend();
                        break;
                    case 1:
                        mRejectButton.setEnabled(false);
                        UnfriendFriendShipToCurrentUser(currentUID, friendUID);
                        break;
                    case 2:
                        CancelFollowRequestToCurrentFriend();
                        break;
                    case 3:
                        AcceptFollowRequestToCurrentFriend();
                        break;
                }
            }
        });

        mFollowButtonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFollowUnfollowProgressbar.setVisibility(View.VISIBLE);

                switch (mState) {
                    case 0:
                        SendFollowRequestToCurrentFriend();
                        break;
                    case 1:
                        mRejectButton.setEnabled(false);
                        UnfriendFriendShipToCurrentUser(currentUID, friendUID);
                        break;
                    case 2:
                        CancelFollowRequestToCurrentFriend();
                        break;
                    case 3:
                        AcceptFollowRequestToCurrentFriend();
                        break;
                }
            }
        });


        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFollowUnfollowProgressbar.setVisibility(View.VISIBLE);
                RemoveFollowRequestToCurrentFriend();
                mFollowButtonOther.setText("UnFriend");
            }
        });

        CheckForTheUsersFriendRequest();
    }

    public void UnfriendFriendShipToCurrentUser(String myUID, String toUID) {

        this.currentUID = myUID;
        this.friendUID = toUID;

        String current = currentUID + "/" + friendUID;
        String friend = friendUID + "/" + currentUID;

        Map unfriendAction = new HashMap();
        unfriendAction.put("Friends/" + currentUID + "/" + friendUID, null);
        unfriendAction.put("Friends/" + friendUID + "/" + currentUID, null);
        unfriendAction.put("Chat/" + current, null);
        unfriendAction.put("Chat/" + friend, null);
        unfriendAction.put("Messages/" + current, null);
        unfriendAction.put("Messages/" + friend, null);


        mDatabaseRef.updateChildren(unfriendAction, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null) {
                    mFollowUnfollowProgressbar.setVisibility(View.INVISIBLE);
                    mState = not_friend;
                    mFollowButton.setVisibility(View.INVISIBLE);
                    mRejectButton.setVisibility(View.INVISIBLE);
                    mFollowButton.setEnabled(false);
                    mRejectButton.setEnabled(false);
                    mFollowButtonOther.setText("follow");
                    mFollowButtonOther.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext, "Unfriended", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Unable to unfriend", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CheckForTheUsersFriendRequest() {
        mUserFollowRequestDB.child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(friendUID)) {
                    mState = request_sent;
                    mFollowButton.setVisibility(View.INVISIBLE);
                    mRejectButton.setVisibility(View.INVISIBLE);
                    mFollowButton.setEnabled(false);
                    mRejectButton.setEnabled(false);
                    mFollowButtonOther.setEnabled(true);
                    mFollowButtonOther.setText("Unfollow");

                    String followRequest = dataSnapshot.child(friendUID).child(mFollowRequestType).getValue().toString();
                    if (followRequest.equals("received")) {
                        mState = request_received;
                        mFollowButton.setVisibility(View.VISIBLE);
                        mRejectButton.setVisibility(View.VISIBLE);
                        mFollowButton.setEnabled(true);
                        mRejectButton.setEnabled(true);
                        mFollowButtonOther.setVisibility(View.INVISIBLE);
                        mFollowButtonOther.setEnabled(false);
                        mFollowButton.setText("Accept");
                    } else if (followRequest.equals("sent")) {
                        mState = request_sent;
                        mFollowButton.setVisibility(View.INVISIBLE);
                        mRejectButton.setVisibility(View.INVISIBLE);
                        mFollowButton.setEnabled(false);
                        mRejectButton.setEnabled(false);
                        mFollowButtonOther.setEnabled(true);
                        mFollowButtonOther.setText("Unfollow");
                    }
                } else if (!dataSnapshot.hasChild(friendUID)) {
                    mFollowButtonOther.setVisibility(View.VISIBLE);
                    mFollowButtonOther.setEnabled(true);
                    CheckForTheUsersFriendship();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void CheckForTheUsersFriendship() {

        mFriendshipDatabase.child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(friendUID)) {
                    mFollowButton.setVisibility(View.INVISIBLE);
                    mRejectButton.setVisibility(View.INVISIBLE);
                    mFollowButton.setEnabled(false);
                    mRejectButton.setEnabled(false);
                    mFollowButton.setText("UnFriend");
                    mFollowButtonOther.setText("UnFriend");
                    mFollowButtonOther.setEnabled(true);
                    mState = already_friend;
                } else if (!dataSnapshot.hasChild(friendUID)) {
                    mUserFollowRequestDB.child(currentUID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(friendUID)) {
                                mFollowButton.setVisibility(View.INVISIBLE);
                                mRejectButton.setVisibility(View.INVISIBLE);
                                mFollowButton.setEnabled(false);
                                mRejectButton.setEnabled(false);
                                mFollowButton.setText("follow");
                                mFollowButtonOther.setText("follow");
                                mFollowButtonOther.setEnabled(true);
                                mState = not_friend;
                            } else if (dataSnapshot.hasChild(friendUID)) {

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void AcceptFollowRequestToCurrentFriend() {

        String timestamp = java.text.DateFormat.getDateTimeInstance().format(new Date());

        Map friendsMap = new HashMap();
        friendsMap.put("Friends/" + currentUID + "/" + friendUID + "/" + "date", timestamp);
        friendsMap.put("Friends/" + friendUID + "/" + currentUID + "/" + "date", timestamp);
        friendsMap.put("notifications/" + currentUID + "/" + friendUID, null);

        mDatabaseRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null) {
                    RemoveFollowRequestToCurrentFriend();
                    mFollowUnfollowProgressbar.setVisibility(View.INVISIBLE);
                    mState = already_friend;
                    mFollowButton.setVisibility(View.INVISIBLE);
                    mRejectButton.setVisibility(View.INVISIBLE);
                    mFollowButtonOther.setVisibility(View.VISIBLE);
                    mFollowButton.setEnabled(false);
                    mRejectButton.setEnabled(false);
                    mFollowButtonOther.setEnabled(true);
                    mFollowButtonOther.setText("UnFriend");
                } else {

                }
                mFollowButtonOther.setEnabled(true);
                mFollowButtonOther.setText("UnFriend");

            }
        });
    }

    private void RemoveFollowRequestToCurrentFriend() {

        Map unfollowActionMap = new HashMap();
        unfollowActionMap.put("Follow_Request/" + currentUID + "/" + friendUID + "/" + mFollowRequestType, null);
        unfollowActionMap.put("Follow_Request/" + friendUID + "/" + currentUID + "/" + mFollowRequestType, null);
        unfollowActionMap.put("notifications/" + currentUID + "/" + friendUID, null);

        mDatabaseRef.updateChildren(unfollowActionMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    mFollowUnfollowProgressbar.setVisibility(View.INVISIBLE);
                    mState = not_friend;
                    mFollowButton.setVisibility(View.INVISIBLE);
                    mRejectButton.setVisibility(View.INVISIBLE);
                    mFollowButton.setEnabled(false);
                    mRejectButton.setEnabled(false);
                    mFollowButtonOther.setVisibility(View.VISIBLE);
                    mFollowButtonOther.setText("follow");
                    mFollowButtonOther.setEnabled(true);
                }
            }
        });
    }

    private void CancelFollowRequestToCurrentFriend() {

        Map unfollowActionMap = new HashMap();
        unfollowActionMap.put("Follow_Request/" + currentUID + "/" + friendUID + "/" + mFollowRequestType, null);
        unfollowActionMap.put("Follow_Request/" + friendUID + "/" + currentUID + "/" + mFollowRequestType, null);
        unfollowActionMap.put("notifications/" + friendUID + "/" + currentUID, null);

        mDatabaseRef.updateChildren(unfollowActionMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null) {
                    mFollowUnfollowProgressbar.setVisibility(View.INVISIBLE);
                    mState = not_friend;
                    mFollowButton.setVisibility(View.INVISIBLE);
                    mRejectButton.setVisibility(View.INVISIBLE);
                    mFollowButton.setEnabled(false);
                    mRejectButton.setEnabled(false);
                    mFollowButtonOther.setText("Follow");
                    mFollowButtonOther.setEnabled(true);
                    Toast.makeText(mContext, "Unfollowed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Unable to Unfollow", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendFollowRequestToCurrentFriend() {

        HashMap<String, String> notificationMap = new HashMap<String, String>();
        notificationMap.put("from", currentUID);
        notificationMap.put("type", "follow_request");

        Map followRequestMap = new HashMap();
        followRequestMap.put("Follow_Request/" + currentUID + "/" + friendUID + "/" + mFollowRequestType, meRequestType);
        followRequestMap.put("Follow_Request/" + friendUID + "/" + currentUID + "/" + mFollowRequestType, yourRequestType);
        followRequestMap.put("notifications/" + friendUID + "/" + currentUID, notificationMap);

        mDatabaseRef.updateChildren(followRequestMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null) {
                    mFollowUnfollowProgressbar.setVisibility(View.INVISIBLE);
                    mState = request_sent;
                    mFollowButton.setVisibility(View.INVISIBLE);
                    mRejectButton.setVisibility(View.INVISIBLE);
                    mFollowButton.setEnabled(false);
                    mRejectButton.setEnabled(false);
                    mFollowButtonOther.setText("Unfollow");
                    mRejectButton.setEnabled(false);
                    Toast.makeText(mContext, "Followed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Unable to Follow", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mUser != null) {
            mOnlineDatabase.child("online").setValue("true");
        }

    }

    /*
    @Override
    protected void onStop() {
        super.onStop();

        if (mUser != null) {
            mOnlineDatabase.child("online").setValue(TIMESTAMP);
        }

    }
    */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.friend_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        final int itemID = item.getItemId();

        DatabaseReference phonecallDB = mDatabaseRef.child("Users").child(friendUID);
        phonecallDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null){

                    phonecall = (String) dataSnapshot.child("phonecall").getValue();

                    switch (itemID) {
                        case R.id.action_call:

                            if (phonecall.equals("default")){
                                Toast.makeText(mContext, "No Phone Number", Toast.LENGTH_SHORT).show();
                            }else {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:"+Uri.encode(phonecall.trim())));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            }
                            break;

                        case R.id.action_sendsms:

                            if (phonecall.equals("default")){
                                Toast.makeText(mContext, "No Phone Number", Toast.LENGTH_SHORT).show();
                            }else {
                                Uri smsUri = Uri.parse("tel:" + phonecall);
                                Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                                intent.putExtra("address", phonecall);
                                intent.putExtra("sms_body", "From: TuChat APP");
                                intent.setType("vnd.android-dir/mms-sms");
                                startActivity(intent);
                            }
                            break;

                        case R.id.action_save_image_to_gallery:
                            Bitmap bitmap = StringToBitMap(image);
                            final String s = SaveProfileImageToGallery(bitmap);

                            Toast.makeText(mContext, "bitmap" + image, Toast.LENGTH_SHORT).show();
                            break;
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return true;
    }

    private String SaveProfileImageToGallery(Bitmap bitmapImage){

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("TuChatProfile", Context.MODE_PRIVATE);
        File mypath = new File(directory, username + ".jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(getApplicationContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
