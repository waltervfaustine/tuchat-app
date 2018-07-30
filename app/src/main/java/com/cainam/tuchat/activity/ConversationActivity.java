package com.cainam.tuchat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cainam.tuchat.R;
import com.cainam.tuchat.adapter.MessageAdapter;
import com.cainam.tuchat.fetcher.MessageData;
import com.cainam.tuchat.fragment.FriendFragment;
import com.cainam.tuchat.system.LastSeen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cainam.tuchat.R.*;
import static com.cainam.tuchat.R.drawable.profile_icon;

public class ConversationActivity extends AppCompatActivity {

    private String mUsername;
    private String mThumbnail;
    private String mOnline;
    private String PhoneNum;
    private Toolbar mToolbar;

    private TextView mChatUsername;
    private TextView mChatLastseen;
    private CircleImageView mChatProfile;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mUser;
    private DatabaseReference mCurrentDB;


    private FirebaseAuth mAuthorization;
    private String currentUID;
    public String friendUID;
    private DatabaseReference mRootRef;
    private String typingStatus;
    public MediaPlayer mpSending;
    public MediaPlayer mpReceiving;

    private EditText mMessage;
    private String message;
    private ImageView mSendMessage;
    private ImageView mFileChooser;
    private LinearLayoutManager mLinearLayout;

    public RecyclerView mMessageRecyclerView;
    private final List<MessageData> mMessageListData = new ArrayList<>();
    private MessageAdapter mAdapter;
    private View toolbarLayout;

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser != null){
            mCurrentDB.child("online").setValue("true");
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
        setContentView(layout.activity_conversation);

        mAuthorization = FirebaseAuth.getInstance();
        mUser = mAuthorization.getCurrentUser();

        mpSending = MediaPlayer.create(ConversationActivity.this, raw.single_pop);
        mpReceiving = MediaPlayer.create(ConversationActivity.this, raw.double_pop);

        friendUID = getIntent().getStringExtra("userID");
        mUsername = getIntent().getStringExtra("username");
        mThumbnail = getIntent().getStringExtra("thumbnail");
        mOnline = getIntent().getStringExtra("online");


        mAdapter = new MessageAdapter(mMessageListData);
        mLinearLayout = new LinearLayoutManager(getApplicationContext());
        mMessageRecyclerView = (RecyclerView) findViewById(id.message_recycler_view);
        mMessageRecyclerView.setHasFixedSize(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayout);
        mMessageRecyclerView.setAdapter(mAdapter);
        mToolbar = (Toolbar) findViewById(id.chat_message_toolbar);

        setSupportActionBar(mToolbar);
        ActionBar chatActionBar = getSupportActionBar();
        chatActionBar.setDisplayHomeAsUpEnabled(true);
        chatActionBar.setDisplayShowCustomEnabled(true);
        chatActionBar.setTitle("");

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View chatView = layoutInflater.inflate(layout.chat_customizable_actionbar, null);
        chatActionBar.setCustomView(chatView);


        mChatLastseen = (TextView) findViewById(id.custom_friend_lastseen);
        mChatProfile = (CircleImageView) findViewById(id.custom_friend_profile_image);
        mChatUsername = (TextView) findViewById(id.custom_friend_username);
        toolbarLayout = findViewById(id.chat_customizable_layout);
        mChatUsername.setText(mUsername);

        toolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(ConversationActivity.this, FriendProfileDetailsActivity.class);
                profileIntent.putExtra("userID", friendUID);
                startActivity(profileIntent);
            }
        });

        if (mThumbnail.equals("avatar_thumbnail")){

        }else {
            Picasso.with(getApplicationContext()).load(mThumbnail).placeholder(profile_icon).into(mChatProfile);
        }

        if (mUser != null){
            currentUID = mAuthorization.getCurrentUser().getUid();
            mCurrentDB = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
            mRootRef = FirebaseDatabase.getInstance().getReference();
            CheckIfUserIsTyping();
            RetrieveMessages();
        }

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(friendUID);
        mUserDatabase.keepSynced(true);
        mUserDatabase.keepSynced(true);
        mRootRef.keepSynced(true);
        mCurrentDB.keepSynced(true);



        if (mOnline.equals("true")){
            mChatLastseen.setText("online");
        }else{
            LastSeen lastSeen = new LastSeen();
            long onlineLong = Long.parseLong(mOnline);
            String since = lastSeen.checkLastTime(onlineLong, getApplicationContext());
            mChatLastseen.setText(since);
        }

        mMessage = (EditText) findViewById(id.message_input_text);
        mSendMessage = (ImageView) findViewById(id.message_send_button);
        mFileChooser = (ImageView) findViewById(id.message_file_chooser);
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mpSending.start();
                sendMessageToFriend();
                mMessage.setText("");

                DatabaseReference typingDB = mRootRef.child("Typing").child(friendUID).child(currentUID).child("istyping");
                typingDB.setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });

        mMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mMessage.getText().toString().length() < 1){
                    DatabaseReference typingDB = mRootRef.child("Typing").child(friendUID).child(currentUID).child("istyping");
                    typingDB.setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

                }else if (hasFocus && mMessage.getText().toString().length() > 0){
                    DatabaseReference typingDB = mRootRef.child("Typing").child(friendUID).child(currentUID).child("istyping");
                    typingDB.setValue("yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

                }else if (!hasFocus){
                    DatabaseReference typingDB = mRootRef.child("Typing").child(friendUID).child(currentUID).child("istyping");
                    typingDB.setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }
            }
        });

        mMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (s.length() == 0){
                    DatabaseReference typingDB = mRootRef.child("Typing").child(friendUID).child(currentUID).child("istyping");
                    typingDB.setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() < 1){
                    DatabaseReference typingDB = mRootRef.child("Typing").child(friendUID).child(currentUID).child("istyping");
                    typingDB.setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }else{
                    DatabaseReference typingDB = mRootRef.child("Typing").child(friendUID).child(currentUID).child("istyping");
                    typingDB.setValue("yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() < 1){
                    DatabaseReference typingDB = mRootRef.child("Typing").child(friendUID).child(currentUID).child("istyping");
                    typingDB.setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }
            }
        });
    }

    private void CheckIfUserIsTyping() {

        if (mUser != null){
            DatabaseReference mTypingDB = mRootRef.child("Typing").child(mUser.getUid()).child(friendUID).child("istyping");
            mTypingDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null){

                        typingStatus = dataSnapshot.getValue().toString();

                        if (typingStatus.equals("yes")){
                            mChatLastseen.setText("typing...");
                        }else{
                            if (mOnline.equals("true")){
                                mChatLastseen.setText("online");
                            }else{
                                LastSeen lastSeen = new LastSeen();
                                long onlineLong = Long.parseLong(mOnline);
                                String since = lastSeen.checkLastTime(onlineLong, getApplicationContext());
                                mChatLastseen.setText(since);
                            }
                        }
                    }else {
                        if (mOnline.equals("true")){
                            mChatLastseen.setText("online");
                        }else{
                            LastSeen lastSeen = new LastSeen();
                            long onlineLong = Long.parseLong(mOnline);
                            String since = lastSeen.checkLastTime(onlineLong, getApplicationContext());
                            mChatLastseen.setText(since);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void RetrieveMessages() {

        if (mUser != null){
            DatabaseReference messageReference = FirebaseDatabase.getInstance().getReference().child("Messages").child(mUser.getUid()).child(friendUID);
            messageReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if (dataSnapshot != null){
                        int position = mAdapter.getItemCount();
                        MessageData message = dataSnapshot.getValue(MessageData.class);
                        mMessageListData.add(message);
                        mMessageRecyclerView.smoothScrollToPosition(position);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }



    private void sendMessageToFriend() {

        message = mMessage.getText().toString();

        if (!TextUtils.isEmpty(message)){

            CreateChatSessionForCurrentUser();

            String current = currentUID + "/" + friendUID;
            String friend = friendUID + "/" + currentUID;

            DatabaseReference messagePushID = FirebaseDatabase.getInstance().getReference().child(currentUID).child(friendUID).push();
            String messageID = messagePushID.getKey();

            Map<String, String> timestamp = ServerValue.TIMESTAMP;

            Map chatMessageMap = new HashMap();
            chatMessageMap.put("message", message);
            chatMessageMap.put("seen", "no");
            chatMessageMap.put("type", "text");
            chatMessageMap.put("timestamp", timestamp);
            chatMessageMap.put("sender", currentUID);

            Map messageNotificationMap = new HashMap();
            messageNotificationMap.put("sender", currentUID);
            messageNotificationMap.put("type", "message");

            Map userMessageMap = new HashMap();
            userMessageMap.put("Messages/" + current + "/" + messageID, chatMessageMap);
            userMessageMap.put("Messages/" + friend + "/" + messageID, chatMessageMap);
            userMessageMap.put("NotifyMessage/" + friendUID + "/" + currentUID + "/" + messageID, messageNotificationMap);

            mRootRef.updateChildren(userMessageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null){
                        Log.v("MESSAGE_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }else {
            Toast.makeText(this, "Cant send empty message", Toast.LENGTH_SHORT).show();
        }
    }

    private void CreateChatSessionForCurrentUser() {

        mRootRef.child("Chat").child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(friendUID)){

                    String current = "Chat/" + currentUID + "/" + friendUID;
                    String friend = "Chat/" + friendUID + "/" + currentUID;

                    Map<String, String> createTimestamp = ServerValue.TIMESTAMP;

                    Map newChatMap = new HashMap();
                    newChatMap.put("seen", "false");
                    newChatMap.put("timestamp", createTimestamp);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put(current, newChatMap);
                    chatUserMap.put(friend, newChatMap);


                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null){
                                Log.v("LOG_CAT", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.conversation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        final int ItemID = item.getItemId();
        final String current = currentUID + "/" + friendUID;
        final String friend = friendUID + "/" + currentUID;

        DatabaseReference mPhonecallDB = mRootRef.child("Users").child(friendUID);
        mPhonecallDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null && dataSnapshot.hasChild("phonecall")){

                    PhoneNum = (String) dataSnapshot.child("phonecall").getValue();

                    switch (ItemID){

                        case id.action_conv_call:

                            if (PhoneNum.equals("default")){
                                Toast.makeText(ConversationActivity.this, "No Phone Number", Toast.LENGTH_SHORT).show();
                            }else{
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:"+Uri.encode(PhoneNum.trim())));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            }
                            break;

                        case id.action_conv_sendsms:

                            if (PhoneNum.equals("default")){
                                Toast.makeText(ConversationActivity.this, "No Phone Number", Toast.LENGTH_SHORT).show();
                            }else{
                                Uri smsUri = Uri.parse("tel:" + PhoneNum);
                                Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                                intent.putExtra("address", PhoneNum);
                                intent.putExtra("sms_body", "From: TuChat APP");
                                intent.setType("vnd.android-dir/mms-sms");
                                startActivity(intent);
                            }
                            break;

                        case id.action_conv_profile:
                            Intent profileIntent = new Intent(ConversationActivity.this, FriendProfileDetailsActivity.class);
                            profileIntent.putExtra("userID", friendUID);
                            startActivity(profileIntent);
                            break;

                        case id.action_conv_unfriend:

                            Map unfriendAction = new HashMap();
                            unfriendAction.put("Friends/" + currentUID + "/" + friendUID, null);
                            unfriendAction.put("Friends/" + friendUID + "/" + currentUID, null);
                            unfriendAction.put("Chat/" + current, null);
                            unfriendAction.put("Chat/" + friend, null);
                            unfriendAction.put("Messages/" + current, null);
                            unfriendAction.put("Messages/" + friend, null);


                            mRootRef.updateChildren(unfriendAction, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null){

                                    }
                                }
                            });
                            Toast.makeText(getApplicationContext(), "Unfriended", Toast.LENGTH_LONG).show();
                            break;

                        case id.action_conv_delete:
                            Map deleteChatMap = new HashMap();

                            deleteChatMap.put("Messages/" + current, null);

                            mRootRef.updateChildren(deleteChatMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null){
                                        Intent convIntent = new Intent(ConversationActivity.this, FriendFragment.class);
                                        startActivity(convIntent);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            Toast.makeText(getApplicationContext(), "You have deleted Chat history", Toast.LENGTH_LONG).show();
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
}
