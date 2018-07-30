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
 * Created by cainam on 8/26/17.
 */

public class FriendsDataViewHolder extends RecyclerView.ViewHolder {

    View friendsView;


    public FriendsDataViewHolder(View friendsItem) {
        super(friendsItem);
        this.friendsView = friendsItem;
    }

    public void setDate(String date) {
        TextView mDate = (TextView) friendsView.findViewById(R.id.friends_lastseen);
        mDate.setText(date);
    }

    public void setUsername(String username) {
        TextView mUsername = (TextView) friendsView.findViewById(R.id.friends_username);
        mUsername.setText(username);
    }

    public void setStatus(String status) {
        TextView mStatus = (TextView) friendsView.findViewById(R.id.friends_status);
        mStatus.setText(status);
    }

    public void setImageThumbnail(String thumbnail, Context context) {
        CircleImageView uThumbnail = (CircleImageView) friendsView.findViewById(R.id.friends_profile_image);

        if (thumbnail.equals("avatar_thumbnail")){

        }else {
            Picasso.with(context).load(thumbnail).placeholder(profile_icon).into(uThumbnail);
        }
    }

    public void setOnlineStatus(String online, Context context) {

        TextView mOnline = (TextView) friendsView.findViewById(R.id.friends_online_status);

        if (online.equals("true")){
            mOnline.setVisibility(View.VISIBLE);
        }else {
            LastSeen lastSeen = new LastSeen();
            long onlineLong = Long.parseLong(online);
            String since = lastSeen.checkLastTime(onlineLong, context);
            mOnline.setText(since);
        }
    }
}
