package com.cainam.tuchat.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cainam.tuchat.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cainam on 8/19/17.
 */

public class UserDataViewHolder extends RecyclerView.ViewHolder {

    View userDataView;
    TextView uUsename;
    TextView uStatus;
    CircleImageView uThumbnail;

    public UserDataViewHolder(View itemView) {
        super(itemView);

        this.userDataView = itemView;

    }

    public void setUsername(String username) {
        uUsename = (TextView) userDataView.findViewById(R.id.friends_username);
        uUsename.setText((username));

    }

    public void setUserStatus(String status) {
        uStatus = (TextView) userDataView.findViewById(R.id.friends_lastseen);
        uStatus.setText((status));
    }


    public void setThumbnail(Context applicationContext, String thumbnail) {
        uThumbnail = (CircleImageView) userDataView.findViewById(R.id.chat_profile_image);

        Picasso.with(applicationContext).load(thumbnail).placeholder(R.drawable.profile_icon).into(uThumbnail);

    }
}
