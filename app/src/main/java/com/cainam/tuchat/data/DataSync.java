package com.cainam.tuchat.data;

import android.app.Application;
import android.content.Context;

import com.firebase.client.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by cainam on 8/18/17.
 */

public class DataSync extends Application {

    Context mContext;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuthorization;
    private FirebaseUser mUser;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthorization = FirebaseAuth.getInstance();
        mContext = getApplicationContext();

        if (!com.google.firebase.FirebaseApp.getApps(mContext).isEmpty()){

            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }

        mUser = mAuthorization.getCurrentUser();

        if (mUser != null){
            String UID = mAuthorization.getCurrentUser().getUid();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null){
                        mUserRef.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}
