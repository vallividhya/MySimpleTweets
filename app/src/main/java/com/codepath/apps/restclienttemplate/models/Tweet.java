package com.codepath.apps.restclienttemplate.models;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by vidhya on 9/25/17.
 */

@Parcel
public class Tweet {

    String body;
    long uid;
    String createdAt;
    User user;


    // empty constructor needed by the Parceler library
    public Tweet() {
    }

    public Tweet(View itemView, String body, long uid, String createdAt, User user) {
        this.body = body;
        this.uid = uid;
        this.createdAt = createdAt;
        this.user = user;
    }

    public Tweet(View itemView, String body, long uid, String createdAt) {
        this.body = body;
        this.uid = uid;
        this.createdAt = createdAt;

    }

    public static Tweet fromJSON (JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.uid = jsonObject.getLong("id");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        return tweet;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
