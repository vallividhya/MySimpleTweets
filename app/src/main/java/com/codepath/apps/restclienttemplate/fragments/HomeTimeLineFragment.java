package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.twitter.TwitterApp;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Fragment for the home timeline
 *
 * @author Valli Vidhya Venkatesan
 */
public class HomeTimeLineFragment extends TweetsListFragment {
    private static long sMaxId = 1;
    final User[] mAccountOwner = new User[1];
    private TwitterClient mClient;

    public static HomeTimeLineFragment newInstance() {

        Bundle args = new Bundle();

        HomeTimeLineFragment fragment = new HomeTimeLineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = TwitterApp.getRestClient();
        populateTimeLineFromAPICall(1);
        getAccountOwnerInfo();
    }

    private void populateTimeLineFromAPICall(final long sinceId) {
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mClient.getHomeTimeLine(sinceId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d("DEBUG", "Home timeline response: " + response.toString());
                        ArrayList<Tweet> list = getTweetsFromJSONResponse(response);
                        addItems(list);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.e("ERROR", errorResponse.toString(), throwable);
                        Toast.makeText(getContext(), "Something went wrong. Check back later.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                       // Log.e("ERROR", errorResponse.toString(), throwable);
                        // Error could be 423. In such a case, display from local DB.
                        populateTimeLineFromLocalDB();
                    }
                });
            }
        };

        // This API has rate-limit of 15 requests in a 15 min window. So, staggering the requests
        handler.postDelayed(runnable, 300);
    }

    private void getAccountOwnerInfo() {

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mClient.getAccountOwnerInfo(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", "Account owner : " + response.toString());

                        try {
                            mAccountOwner[0] = User.fromJson(response);
                        } catch (JSONException e) {
                            Log.e("ERROR", "JSON Exception", e);
                            Toast.makeText(getContext(), "Something went wrong. Check back later.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("ERROR", errorResponse.toString(), throwable);
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
        handler.post(runnable);
    }

    public User getUser() {
        return mAccountOwner[0];
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
                Log.d("DEBUG", "tweetId= " + tweet.getTweetId() + "Retweeted " + tweet.isReTweeted());
                list.add(tweet);
            } catch (JSONException e) {
                Log.e("ERROR", "Failed to parse timeline tweets", e);
            }
        }
        if (!list.isEmpty()) {
            sMaxId = list.get(list.size() - 1).getTweetId();
        }
        return list;
    }
}
