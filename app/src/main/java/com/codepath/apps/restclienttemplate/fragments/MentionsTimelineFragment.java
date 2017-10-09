package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.twitter.TwitterApp;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.codepath.apps.restclienttemplate.util.NetworkUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Fragment for the mentions's timeline
 *
 * @author Valli Vidhya Venkatesan
 */
public class MentionsTimelineFragment extends TweetsListFragment {

    private static long sMaxId = 1;
    final User[] mAccountOwner = new User[1];
    private TwitterClient mClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = TwitterApp.getRestClient();
        populateTimeLineFromAPICall(1);
        getAccountOwnerInfo();
    }

    private void populateTimeLineFromAPICall(final long since_id) {
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mClient.getMentionsTimeLine(since_id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d("DEBUG", "Mentions timeline" + response.toString());
                        ArrayList<Tweet> list = getTweetsFromJSONResponse(response);
                        addItems(list);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.d("ERROR", errorResponse.toString(), throwable);
                        Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("ERROR", errorResponse.toString(), throwable);
                        Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        if (NetworkUtil.isNetworkAvailable(getContext())) {
            // This API has rate-limit of 15 requests in a 15 min window. So, staggering the requests
            handler.postDelayed(runnable, 500);
        }
    }

    private void getAccountOwnerInfo() {

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mClient.getAccountOwnerInfo(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", response.toString());

                        try {
                            mAccountOwner[0] = User.fromJson(response);
                        } catch (JSONException e) {
                            Log.e("ERROR", "JSON Exception", e);
                            Toast.makeText(getContext(), "Something went wrong. Check back later.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        //Log.d("ERROR", errorResponse.toString(), throwable);
                        Toast.makeText(getContext(), "Something went wrong. Check back later.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.e("ERROR", errorResponse.toString(), throwable);
                        Toast.makeText(getContext(), "Something went wrong. Check back later.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("ERROR", responseString, throwable);
                        Toast.makeText(getContext(), "Something went wrong. Check back later.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            handler.post(runnable);
        }
    }

    @Override
    public void loadMore() {
        populateTimeLineFromAPICall(sMaxId);
    }

    private ArrayList<Tweet> getTweetsFromJSONResponse(JSONArray response) {
        ArrayList<Tweet> list = new ArrayList<>(response.length());
        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJson(response.getJSONObject(i));
                Log.d("DEBUG", " since id = " + tweet.getTweetId());
                list.add(tweet);

            } catch (JSONException e) {
                Log.e("ERROR", "JSON Exception", e);
            }
        }
        if (!list.isEmpty()) {
            sMaxId = list.get(list.size() - 1).getTweetId();
        }

        return list;
    }
}
