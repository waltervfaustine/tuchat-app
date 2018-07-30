package com.cainam.tuchat.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cainam.tuchat.R;
import com.cainam.tuchat.activity.FriendProfileDetailsActivity;
import com.cainam.tuchat.activity.UserListActivity;
import com.cainam.tuchat.fetcher.FollowersData;
import com.cainam.tuchat.holder.FollowersDataViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FollowFragment extends Fragment {

    private View mfollowView;
    private RecyclerView mFollowList;
    private Context mContext;
    private DatabaseReference mFollowDatabase;
    private DatabaseReference mUserDatabase;
    private String currentUID;
    private String followersID;
    private FirebaseUser mUser;
    private FirebaseAuth mAuthorization;
    private DatabaseReference mUserRef;

    public FollowFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mfollowView = inflater.inflate(R.layout.fragment_follow, container, false);

        mContext = getContext();

        mFollowList = (RecyclerView) mfollowView.findViewById(R.id.followers_recycler_view);
        mFollowList.setLayoutManager(new LinearLayoutManager(mContext));
        mFollowList.setHasFixedSize(true);

        mAuthorization = FirebaseAuth.getInstance();
        mUser = mAuthorization.getCurrentUser();

        if (mUser != null){
            currentUID = mUser.getUid();
            mFollowDatabase = FirebaseDatabase.getInstance().getReference().child("Follow_Request").child(currentUID);
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);

        }

        return mfollowView;
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

            FirebaseRecyclerAdapter<FollowersData, FollowersDataViewHolder> followAdapter = new FirebaseRecyclerAdapter<FollowersData, FollowersDataViewHolder>(
                    FollowersData.class,
                    R.layout.follow_information_cardview,
                    FollowersDataViewHolder.class,
                    mFollowDatabase
            ) {
                @Override
                protected void populateViewHolder(final FollowersDataViewHolder followHolder, FollowersData model, int position) {

                    followersID = getRef(position).getKey();

                    if (mUser.getUid() != followersID){
                        mUserDatabase.child(followersID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot != null){
                                    String username = dataSnapshot.child("username").getValue().toString();
                                    String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();
                                    String status = dataSnapshot.child("status").getValue().toString();

                                    followHolder.setUsername(username);
                                    followHolder.setImageIcon(thumbnail, getContext());
                                }

                                followHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent userIntent = new Intent(getContext(), FriendProfileDetailsActivity.class);
                                        userIntent.putExtra("userID", followersID);
                                        startActivity(userIntent);
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            };
            mFollowList.setAdapter(followAdapter);
        }
    }
}
