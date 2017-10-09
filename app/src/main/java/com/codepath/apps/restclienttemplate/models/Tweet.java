package com.codepath.apps.restclienttemplate.models;

import android.support.annotation.Nullable;
import android.view.View;

import com.codepath.apps.restclienttemplate.helpers.database.SimpleTweetsDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Model to represent a tweet.
 *
 * @author Valli Vidhya Venkatesan
 */
@Table(database = SimpleTweetsDatabase.class)
@Parcel
public class Tweet extends BaseModel {

    public static final int TWEET_LENGTH = 140;

    @PrimaryKey
    @Column
    long tweetId;

    @Column
    String body;

    @Column
    String createdAt;

    @Column
    boolean reTweeted;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    User user;

    @Column
    @Nullable
    @ForeignKey(saveForeignKeyModel = true)
    Media media;

    // empty constructor needed by the Parceler library
    public Tweet() {
    }

    public Tweet(JSONObject jsonObject) throws JSONException {
        super();

        this.body = jsonObject.getString("text");
        this.createdAt = jsonObject.getString("created_at");
        this.tweetId = jsonObject.getLong("id");
        this.user = new User(jsonObject.getJSONObject("user"));
        this.reTweeted = jsonObject.getBoolean("retweeted");
        JSONObject entityJson = jsonObject.optJSONObject("entities");
        JSONObject mediaJson = null;
        if (entityJson != null) {
            JSONArray mediaJArray = entityJson.optJSONArray("media");
            // This JSON array has only one element, says Twitter API Docs
            if (mediaJArray != null) {
                mediaJson = mediaJArray.getJSONObject(0);
                if (mediaJson != null) {
                    this.media = new Media(mediaJson);
                }
            }
        }
        JSONObject entityExtendedJson = jsonObject.optJSONObject("extended_entities");
        JSONObject mediaVideoJson = null;
        if (entityExtendedJson != null) {
            JSONArray mediaJArray = entityExtendedJson.optJSONArray("media");
            // This JSON array has only one element, says Twitter API Docs
            if (mediaJArray != null) {
                mediaVideoJson = mediaJArray.getJSONObject(0);
                if (mediaVideoJson != null) {
                    if (mediaVideoJson.getString("type").equals("video")) {
                        this.media = new Media(mediaVideoJson);
                    }

                }
            }
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

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet(jsonObject);
        tweet.getUser().save();
        if (tweet.getMedia() != null) {
            tweet.getMedia().save();
        }
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

    @Nullable
    public Media getMedia() {
        return media;
    }

    public void setMedia(@Nullable Media media) {
        this.media = media;
    }

    public boolean isReTweeted() {
        return reTweeted;
    }

    public void setReTweeted(boolean reTweeted) {
        this.reTweeted = reTweeted;
    }
}
