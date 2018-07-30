package com.cainam.tuchat.fetcher;

/**
 * Created by cainam on 9/5/17.
 */

public class ChatData {

    public long timestamp;

    public ChatData(){

    }

    public ChatData(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
