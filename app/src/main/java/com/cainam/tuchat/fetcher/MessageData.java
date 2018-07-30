package com.cainam.tuchat.fetcher;

/**
 * Created by cainam on 9/2/17.
 */

public class MessageData {

    String message;
    String seen;
    String type;
    String sender;
    long timestamp;

    public MessageData() {

    }

    public MessageData(String message, String seen, String type, String sender, long timestamp) {
        this.message = message;
        this.seen = seen;
        this.type = type;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
