package com.cainam.tuchat.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cainam.tuchat.R;

/**
 * Created by cainam on 9/2/17.
 */

public class MessageDataViewHolder extends RecyclerView.ViewHolder {

    public View messageView;
    public TextView mMessage;

    public MessageDataViewHolder(View itemView) {
        super(itemView);
        this.messageView = itemView;
        mMessage = (TextView) messageView.findViewById(R.id.message_text_layout);
    }
}

