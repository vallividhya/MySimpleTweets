package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.AccountOwner;
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
 * Created by vidhya on 10/3/17.
 */

public class HomeTimeLineFragment extends TweetsListFragment {
    private TwitterClient mClient;
    final AccountOwner[] mAccountOwner = new AccountOwner[1];
    private static long sMaxId = 1;

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
//        if (snackbar != null && snackbar.isShown()) {
//            snackbar.dismiss();
//        }
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mClient.getHomeTimeLine(sinceId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        //addItems(response);
                        ArrayList<Tweet> list = getTweetsFromJSONResponse(response);
                        addItems(list);
                        //rotateloading.stop();
                        Log.d("DEBUG", "vvv:home timeline response - " + response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.d("ERROR", errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        // Error could be 423. In such a case, display from local DB.
                        //populateTimeLineFromLocalDB();
                        //Log.d("ERROR", errorResponse.toString());
                    }
                });
            }
        };

        //rotateloading.start();
        // This API has rate-limit of 15 requests in a 15 min window. So, staggering the requests
        handler.postDelayed(runnable, 300);
    }

    private void getAccountOwnerInfo() {
        //getAccountOwnerInfo

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mClient.getAccountOwnerInfo(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", "Account owner : " + response.toString());

                        try {
                            mAccountOwner[0] = AccountOwner.fromJSON(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        // Log.d("DEBUG", errorResponse.getString("error"));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        try {
                            Log.d("DEBUG",  "v1:"+ errorResponse.getJSONObject(0).toString());
                            Toast.makeText(getContext(), "Something went wrong. Check back later", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("DEBUG", "v2:" + responseString);
                        Toast.makeText(getContext(), "Something went wrong. Check back later", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        handler.post(runnable);
    }

    public AccountOwner getUser() {
        return mAccountOwner[0];
    }

    @Override
    void loadMore() {
        populateTimeLineFromAPICall(sMaxId);
    }

    private ArrayList<Tweet> getTweetsFromJSONResponse(JSONArray response) {
        ArrayList<Tweet> list = new ArrayList<>(response.length());
        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                Log.d("DEBUG", " since id = " + tweet.getTweetId());
                list.add(tweet);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!list.isEmpty()) {
            sMaxId = list.get(list.size() - 1).getTweetId();
        }
        return list;
    }
}
