package com.cainam.tuchat.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cainam.tuchat.R;
import com.cainam.tuchat.activity.ConversationActivity;
import com.cainam.tuchat.activity.FriendProfileDetailsActivity;
import com.cainam.tuchat.activity.UserListActivity;
import com.cainam.tuchat.fetcher.FriendsData;
import com.cainam.tuchat.holder.FriendsDataViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.internal.kx;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendFragment extends Fragment {

    public FriendFragment() {

    }

    private RecyclerView mFriendsList;
    private FirebaseAuth mAuthorization;

    private FirebaseUser mUser;
    private View mineView;
    private String currentUID;
    private DatabaseReference mUserDatabase;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRefDB;
    private DatabaseReference friendsDB;
    private FloatingActionButton mFriends;
    private DatabaseReference mUserRef;
    private DatabaseReference mRootRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mineView = inflater.inflate(R.layout.fragment_friend, container, false);

        mAuthorization = FirebaseAuth.getInstance();
        mFriendsList = (RecyclerView) mineView.findViewById(R.id.mine_recycler_view);
        mFriends = (FloatingActionButton) mineView.findViewById(R.id.friends_floating_action_button);

        mDatabase = FirebaseDatabase.getInstance();
        mRefDB = mDatabase.getReference();
        mUserDatabase = mRefDB.child("Users");

        mUser = mAuthorization.getCurrentUser();

        if (mUser == null){

        }else {
            currentUID = mUser.getUid();
            friendsDB = mRefDB.child("Friends").child(currentUID);
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
            mRootRef = FirebaseDatabase.getInstance().getReference();
        }

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendsIntent = new Intent(mineView.getContext(), UserListActivity.class);
                startActivity(friendsIntent);
            }
        });

        return mineView;

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
    public void onStart() {
        super.onStart();

        if (mUser != null){

            mUserRef.child("online").setValue("true");

            FirebaseRecyclerAdapter <FriendsData, FriendsDataViewHolder> friendsAdapter = new FirebaseRecyclerAdapter<FriendsData, FriendsDataViewHolder>(
                    FriendsData.class,
                    R.layout.friends_information_cardview,
                    FriendsDataViewHolder.class,
                    friendsDB
            ) {
                @Override
                protected void populateViewHolder(final FriendsDataViewHolder friendsHolder, final FriendsData model, int position) {

                    friendsHolder.setDate(model.getDate());
                    final String UID = getRef(position).getKey();

                    mUserDatabase.child(UID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot != null){
                                final String username = dataSnapshot.child("username").getValue().toString();
                                final String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();
                                final String status = dataSnapshot.child("status").getValue().toString();
                                final String online = dataSnapshot.child("online").getValue().toString();

                                friendsHolder.setUsername(username);
                                friendsHolder.setStatus(status);
                                friendsHolder.setImageThumbnail(thumbnail, getContext());

                                if (dataSnapshot.hasChild("online")){
                                    friendsHolder.setOnlineStatus(online, getContext());
                                }

                                friendsHolder.itemView.setOnClickListener(new View.OnClickListener() {

                                    String current = currentUID + "/" + UID;
                                    String friend = UID + "/" + currentUID;

                                    @Override
                                    public void onClick(View v) {

                                        CharSequence menuOptions[] = new CharSequence[]{"View Profile", "Send Message", "Unfriend", "Delete Chat History"};

                                        AlertDialog.Builder menuBuilder = new AlertDialog.Builder(getContext());



                                        menuBuilder.setItems(menuOptions, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                switch (which){

                                                    case 0:

                                                        Intent profileIntent = new Intent(getContext(), FriendProfileDetailsActivity.class);
                                                        profileIntent.putExtra("userID", UID);
                                                        profileIntent.putExtra("username", username);
                                                        profileIntent.putExtra("status", status);
                                                        startActivity(profileIntent);
                                                        break;

                                                    case 1:

                                                        DatabaseReference typingDB = mRefDB.child("Typing").child(currentUID).child(UID).child("istyping");
                                                        typingDB.setValue("no").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Intent convIntent = new Intent(getContext(), ConversationActivity.class);
                                                                convIntent.putExtra("username", username);
                                                                convIntent.putExtra("userID", UID);
                                                                convIntent.putExtra("status", status);
                                                                convIntent.putExtra("thumbnail", thumbnail);
                                                                convIntent.putExtra("online", online);
                                                                startActivity(convIntent);
                                                            }
                                                        });
                                                        break;

                                                    case 2:
                                                        Map unfriendAction = new HashMap();
                                                        unfriendAction.put("Friends/" + currentUID + "/" + UID, null);
                                                        unfriendAction.put("Friends/" + UID + "/" + currentUID, null);
                                                        unfriendAction.put("Chat/" + current, null);
                                                        unfriendAction.put("Chat/" + friend, null);
                                                        unfriendAction.put("Messages/" + current, null);
                                                        unfriendAction.put("Messages/" + friend, null);


                                                        mRootRef.updateChildren(unfriendAction, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                            }
                                                        });
                                                        Toast.makeText(getContext(), "You have unfriended " + username, Toast.LENGTH_LONG).show();
                                                        break;

                                                    case 3:

                                                        Map deleteChatMap = new HashMap();

                                                        deleteChatMap.put("Messages/" + current, null);

                                                        mRootRef.updateChildren(deleteChatMap, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                            }
                                                        });
                                                        Toast.makeText(getContext(), "You have deleted " + username +" Chat history", Toast.LENGTH_LONG).show();
                                                        break;
                                                }
                                            }
                                        });
                                        menuBuilder.show();
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
            mFriendsList.setAdapter(friendsAdapter);
        }
    }
}
