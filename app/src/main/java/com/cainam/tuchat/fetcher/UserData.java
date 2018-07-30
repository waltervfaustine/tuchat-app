package com.cainam.tuchat.fetcher;

/**
 * Created by cainam on 8/19/17.
 */

public class UserData {

    String username;
    String email;
    String image;
    String status;
    String thumbnail;

    public UserData(){

    }

    public UserData(String username, String email, String image, String status, String thumbnail) {
        this.username = username;
        this.email = email;
        this.image = image;
        this.status = status;
        this.thumbnail = thumbnail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

}
