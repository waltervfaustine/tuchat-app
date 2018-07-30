package com.cainam.tuchat.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.cainam.tuchat.R;
import com.cainam.tuchat.fetcher.UserData;
import com.cainam.tuchat.holder.UserDataViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class UserListActivity extends AppCompatActivity {

    public UserListActivity(){

    }

    private RecyclerView mUserAllUserList;
    private Toolbar mAllUserListToolbar;
    private Context mContext;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDBRef;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mUserRef;

    private FirebaseAuth mAuthorization;
    private FirebaseUser mUser;
    private String currentUID;
    private String friendUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        mContext = getApplicationContext();
        mAuthorization = FirebaseAuth.getInstance();
        mUser = mAuthorization.getCurrentUser();

        mUserAllUserList = (RecyclerView) findViewById(R.id.all_users_list_recyclerview);
        mAllUserListToolbar = (Toolbar) findViewById(R.id.all_users_list_toolbar);

        setSupportActionBar(mAllUserListToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Users");

        mUserAllUserList.setHasFixedSize(true);
        mUserAllUserList.setLayoutManager(new LinearLayoutManager(mContext));

        mUser = mAuthorization.getCurrentUser();
        mDBRef = FirebaseDatabase.getInstance().getReference();

        if (mUser != null){
            currentUID = mUser.getUid();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mUser != null){
            mUserRef.child("online").setValue("true");
        }

        mUserDatabase = mDBRef.child("Users");

        FirebaseRecyclerAdapter<UserData, UserDataViewHolder> infoAdapter = new FirebaseRecyclerAdapter<UserData, UserDataViewHolder>(
                UserData.class,
                R.layout.user_information_cardview,
                UserDataViewHolder.class,
                mUserDatabase
        ) {
            @Override
            protected void populateViewHolder(UserDataViewHolder viewHolder, UserData model, int position) {
                viewHolder.setUsername(model.getUsername());
                viewHolder.setUserStatus(model.getStatus());
                viewHolder.setThumbnail(getApplicationContext(), model.getThumbnail());

                final String UID = getRef(position).getKey().toString();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent userIntent = new Intent(UserListActivity.this, FriendProfileDetailsActivity.class);
                        userIntent.putExtra("userID", UID);
                        startActivity(userIntent);
                    }
                });
            }
        };
        mUserAllUserList.setAdapter(infoAdapter);
        infoAdapter.notifyDataSetChanged();

    }

    /*
    @Override
    protected void onStop() {
        super.onStop();
        if (mUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
    */
}
