package com.codepath.apps.restclienttemplate.models;

import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.restclienttemplate.helpers.database.SimpleTweetsDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Model to represent a media inside tweets.
 *
 * @author Valli Vidhya Venkatesan
 */
@Table(database = SimpleTweetsDatabase.class)
@Parcel
public class Media extends BaseModel {

    @Column
    String mediaUrl;

    @PrimaryKey
    @Column
    long mediaId;

    @Column
    String mediaType;

    @Column
    @Nullable
    String videoUrl;

    public Media() {
    }

    public Media(JSONObject jsonObject) throws JSONException {
        super();

        this.mediaUrl = jsonObject.getString("media_url");
        this.mediaId = jsonObject.getLong("id");
        this.mediaType = jsonObject.getString("type");
        if (this.mediaType.equals("video")) {

            JSONArray jArray = jsonObject.getJSONObject("video_info").getJSONArray("variants");
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    Log.d("DEBUG", "vvv: Here ..." + jArray.getJSONObject(i).toString());
                }
                this.videoUrl = jArray.getJSONObject(0).getString("url");

            }
        }
        Log.d("DEBUG", "Media URL : " + this.mediaUrl + " , media type = " + this.mediaType + " , Video URL = " + this.videoUrl);
    }

    public static Media fromJson(JSONObject jsonObject) throws JSONException {
        if (jsonObject != null) {
            Media media = new Media(jsonObject);
            media.save();
            return media;
        }
        return null;
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

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Nullable
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(@Nullable String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
