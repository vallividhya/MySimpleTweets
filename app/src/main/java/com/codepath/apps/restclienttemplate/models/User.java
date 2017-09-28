package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.helpers.database.SimpleTweetsDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
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
@Parcel
public class User extends BaseModel {

    @PrimaryKey
    @Column
    long userId;

    @Column
    String name;

    @Column
    String screenName;

    @Column
    String profileImageUrl;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public User(JSONObject jsonObject) {
        super();
        try {
            this.name = jsonObject.getString("name");
            this.userId = jsonObject.getLong("id");
            this.screenName = jsonObject.getString("screen_name");
            this.profileImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Empty constructor for Parceler library
    public User() {}

    public static User fromJson(JSONObject jsonObject) throws JSONException{
        User user = new User();
        user.save();
        return user;
    }

}
