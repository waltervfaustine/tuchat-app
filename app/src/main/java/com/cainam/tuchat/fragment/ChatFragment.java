package com.cainam.tuchat.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cainam.tuchat.R;
import com.cainam.tuchat.activity.ConversationActivity;
import com.cainam.tuchat.fetcher.ChatData;
import com.cainam.tuchat.holder.ChatDataViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ChatFragment extends Fragment {

    private View mChatView;
    private String currentUID;
    private FirebaseAuth mAuthorization;
    private DatabaseReference mChatDatabase;
    private FirebaseUser mUser;
    private RecyclerView mChatList;
    private LinearLayoutManager mLinearLayout;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mUserRef;


    public ChatFragment() {

    }


    /*
    @Override
    public void onStop() {
        super.onStop();

        if (mUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mChatView = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuthorization = FirebaseAuth.getInstance();
        mUser = mAuthorization.getCurrentUser();

        if (mUser != null){
            currentUID = mUser.getUid();
            mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUID);
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
        }

        mChatList = (RecyclerView) mChatView.findViewById(R.id.chats_recycler_view);
        mChatList.setHasFixedSize(true);
        Context mContext = getContext();
        mLinearLayout = new LinearLayoutManager(mContext);
        mChatList.setLayoutManager(mLinearLayout);

        return mChatView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mUser != null){

            mUserRef.child("online").setValue("true");

            FirebaseRecyclerAdapter<ChatData, ChatDataViewHolder> mChatAdapter = new FirebaseRecyclerAdapter<ChatData, ChatDataViewHolder>(
                    ChatData.class,
                    R.layout.chat_information_cardview,
                    ChatDataViewHolder.class,
                    mChatDatabase
            ) {
                @Override
                protected void populateViewHolder(final ChatDataViewHolder chatViewHolder, ChatData model, int position) {

                    final String UID = getRef(position).getKey();

                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot != null){

                                final String username = dataSnapshot.child("username").getValue().toString();
                                final String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();
                                final String status = dataSnapshot.child("status").getValue().toString();
                                final String online =  dataSnapshot.child("online").getValue().toString();

                                chatViewHolder.setUsername(username);
                                chatViewHolder.setStatus(status);
                                chatViewHolder.setImageThumbnail(thumbnail, getContext());

                                if (dataSnapshot.hasChild("online")){
                                    chatViewHolder.setOnlineStatus(online, getContext());
                                }

                                chatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent chatIntent = new Intent(getContext(), ConversationActivity.class);
                                        chatIntent.putExtra("username", username);
                                        chatIntent.putExtra("userID", UID);
                                        chatIntent.putExtra("status", status);
                                        chatIntent.putExtra("thumbnail", thumbnail);
                                        chatIntent.putExtra("online", online);
                                        startActivity(chatIntent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            };
            mChatList.setAdapter(mChatAdapter);
        }



    }
}
