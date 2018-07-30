package com.cainam.tuchat.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cainam.tuchat.R;
import com.cainam.tuchat.system.LastSeen;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cainam.tuchat.R.drawable.profile_icon;

/**
 * Created by cainam on 9/5/17.
 */

public class ChatDataViewHolder extends RecyclerView.ViewHolder {

    private View chatView;

    public ChatDataViewHolder(View itemView) {
        super(itemView);
        chatView = itemView;
    }

    public void setUsername(String username) {
        TextView mUsername = (TextView) chatView.findViewById(R.id.chat_username);
        mUsername.setText(username);
    }

    public void setStatus(String status) {
        TextView mStatus = (TextView) chatView.findViewById(R.id.chat_status);
        mStatus.setText(status);
    }

    public void setImageThumbnail(String thumbnail, Context context) {

        CircleImageView uThumbnail = (CircleImageView) chatView.findViewById(R.id.chat_profile_image);

        if (thumbnail.equals("thumbnail")){
            //Do nothing here
        }else {
            Picasso.with(context).load(thumbnail).placeholder(profile_icon).into(uThumbnail);
        }

    }

    public void setOnlineStatus(String online, Context applicationContext) {

        TextView mOnline = (TextView) chatView.findViewById(R.id.chat_online_status);

        if (online.equals("true")){
            mOnline.setVisibility(View.VISIBLE);
        }else {
            LastSeen lastSeen = new LastSeen();
            long onlineLong = Long.parseLong(online);
            String since = lastSeen.checkLastTime(onlineLong, applicationContext);
            mOnline.setText(since);
        }

    }
}
