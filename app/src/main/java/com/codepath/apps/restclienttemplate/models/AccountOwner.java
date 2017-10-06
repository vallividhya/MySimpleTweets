package com.codepath.apps.restclienttemplate.models;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by vidhya on 9/27/17.
 */
@Parcel
public class AccountOwner extends BaseModel {

    String name;

    String draftTweet;

    String screenName;

    String profileImageUrl;

    public AccountOwner() {
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

    public void setScreenName(String screen_name) {
        this.screenName = screen_name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDraftTweet() {
        return draftTweet;
    }

    public void setDraftTweet(String draftTweet) {
        this.draftTweet = draftTweet;
    }

    public static AccountOwner fromJSON(JSONObject jsonObject) throws JSONException {
        AccountOwner accountOwner = new AccountOwner();
        accountOwner.setName(jsonObject.getString("name"));
        accountOwner.setScreenName(jsonObject.getString("screen_name"));
        accountOwner.setProfileImageUrl(jsonObject.getString("profile_image_url"));
        return accountOwner;
    }
}
