package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import com.codepath.apps.restclienttemplate.helpers.database.SimpleTweetsDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by vidhya on 9/30/17.
 */
@Table(database = SimpleTweetsDatabase.class)
@Parcel
public class Media extends BaseModel {

    @Column
    String mediaUrl;

    @PrimaryKey
    @Column
    long mediaId;

    public Media() {
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    public Media (JSONObject jsonObject) {
        super();
        try {
            this.mediaUrl = jsonObject.getString("media_url");
            this.mediaId = jsonObject.getLong("id");
            Log.d("DEBUG", "Media URL : " + this.mediaUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static Media fromJson(JSONObject jsonObject) throws JSONException{
        if (jsonObject != null) {
            Media media = new Media(jsonObject);
            media.save();
            return media;
        }
        return null;
    }
}
