package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.twitter.TwitterApp;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by vidhya on 10/4/17.
 */

public class UserTimelineFragment extends TweetsListFragment {
    private TwitterClient mClient;
    private static long sMaxId = 1;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = TwitterApp.getRestClient();
        populateUserTimeLineFromAPICall(1);
    }

    private void populateUserTimeLineFromAPICall(final long since_id) {

        // Came from the activity
        final String screenName = getArguments().getString("screen_name");
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mClient.getUserTimeLine(since_id, screenName, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        ArrayList<Tweet> list = getTweetsFromJSONResponse(response);
                        addItems(list);
                        Log.d("DEBUG", response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.e("ERROR", errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        // Error could be 423. In such a case, display from local DB.
                        populateTimeLineFromLocalDB();
                        Log.e("ERROR", errorResponse.toString());
                    }
                });
            }
        };

         //rotateloading.start();
        // This API has rate-limit of 15 requests in a 15 min window. So, staggering the requests
        handler.postDelayed(runnable, 500);
    }

    @Override
    public void loadMore() {
        populateUserTimeLineFromAPICall(sMaxId);
    }

    private ArrayList<Tweet> getTweetsFromJSONResponse(JSONArray response) {
        ArrayList<Tweet> list = new ArrayList<>(response.length());
        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                Log.d("DEBUG", " since id = " + tweet.getTweetId());
                list.add(tweet);

            } catch (JSONException e) {
                Log.e("ERROR", "JSON Exception: " + e.getMessage());
            }
        }
        if (!list.isEmpty()) {
            sMaxId = list.get(list.size() - 1).getTweetId();
        }
        return list;
    }
}
