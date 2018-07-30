package com.cainam.tuchat.holder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cainam.tuchat.R;
import com.cainam.tuchat.activity.FriendProfileDetailsActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cainam.tuchat.R.drawable.profile_icon;

/**
 * Created by cainam on 8/29/17.
 */

public class FollowersDataViewHolder extends RecyclerView.ViewHolder {

    View mFollowView;

    public FollowersDataViewHolder(View itemView) {
        super(itemView);
        this.mFollowView = itemView;
    }

    public void setUsername(String username) {
        TextView mUsername = (TextView) mFollowView.findViewById(R.id.follow_username);
        mUsername.setText(username);
    }

    public void setImageIcon(String thumbnail, Context context) {

        CircleImageView uThumbnail = (CircleImageView) mFollowView.findViewById(R.id.follow_profile_image);

        if (thumbnail.equals("avatar_thumbnail")){

        }else {
            Picasso.with(context).load(thumbnail).placeholder(profile_icon).into(uThumbnail);
        }

    }
}
