package com.cainam.tuchat.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cainam.tuchat.R;
import com.cainam.tuchat.activity.ConversationActivity;
import com.cainam.tuchat.fetcher.MessageData;
import com.cainam.tuchat.holder.MessageDataViewHolder;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cainam on 9/2/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageDataViewHolder> {

    private List<MessageData> messageListDatas;
    private FirebaseAuth mAuthorization;
    private FirebaseUser mUser;
    private String currentUID;
    private DatabaseReference mRootRef;

    public MessageAdapter(List<MessageData> messageListDatas) {
        this.messageListDatas = messageListDatas;
    }




    @Override
    public MessageDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_information_cardview, parent, false);
        return new MessageDataViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(final MessageDataViewHolder myViewHolder, final int position) {

        mAuthorization = FirebaseAuth.getInstance();
        mUser = mAuthorization.getCurrentUser();

        if (mUser != null){

            mRootRef = FirebaseDatabase.getInstance().getReference();
            this.currentUID = mAuthorization.getCurrentUser().getUid();

            MessageData messageData = messageListDatas.get(position);
            String sender = messageData.getSender();

            if (sender.equals(currentUID)){

            }else {
                MediaPlayer mpReceiving = MediaPlayer.create(myViewHolder.messageView.getContext(), R.raw.double_pop);
                myViewHolder.mMessage.setBackgroundResource(R.drawable.receiver_message_background);
                RelativeLayout mLayout = (RelativeLayout) myViewHolder.messageView.findViewById(R.id.message_info_cardview);
                mLayout.setGravity(Gravity.LEFT);
                mpReceiving.start();
            }
            myViewHolder.mMessage.setText(messageData.getMessage());
        }


        myViewHolder.messageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                CharSequence messageMenuOption[] = new CharSequence[]{
                        "Copy",
                        "Delete",
                        "Mark as Unread",
                        "Forward"
                };

                AlertDialog.Builder messageDialog = new AlertDialog.Builder(v.getContext());

                messageDialog.setItems(messageMenuOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case 0:

                                break;
                            case 1:

                                break;
                            case 2:

                                break;
                            case 3:

                                break;
                        }
                    }
                });

                messageDialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageListDatas.size();
    }
}
