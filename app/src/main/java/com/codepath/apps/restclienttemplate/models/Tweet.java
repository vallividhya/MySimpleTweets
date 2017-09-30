package com.codepath.apps.restclienttemplate.models;

import android.view.View;

import com.codepath.apps.restclienttemplate.helpers.database.SimpleTweetsDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by vidhya on 9/25/17.
 */


@Table(database = SimpleTweetsDatabase.class)
@Parcel //(analyze = {Tweet.class})
public class Tweet extends BaseModel{

    @PrimaryKey @Column
    long tweetId;

    @Column
    String body;

    @Column
    String createdAt;

    @Column @ForeignKey(saveForeignKeyModel = true)
    User user;

    // empty constructor needed by the Parceler library
    public Tweet() {
    }

    public Tweet(JSONObject jsonObject) {
        super();
        try {
            this.body = jsonObject.getString("text");
            this.createdAt = jsonObject.getString("created_at");
            this.tweetId = jsonObject.getLong("id");
            this.user = new User(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Tweet(View itemView, String body, long uid, String createdAt, User user) {
        this.body = body;
        this.tweetId = uid;
        this.createdAt = createdAt;
        this.user = user;
    }

    public Tweet(View itemView, String body, long uid, String createdAt) {
        this.body = body;
        this.tweetId = uid;
        this.createdAt = createdAt;

    }

    public static Tweet fromJSON (JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet(jsonObject);
        tweet.getUser().save();
        tweet.save();
        return tweet;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
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
