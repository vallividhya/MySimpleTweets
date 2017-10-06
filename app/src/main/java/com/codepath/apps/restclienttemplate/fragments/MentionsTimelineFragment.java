package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.AccountOwner;
import com.codepath.apps.restclienttemplate.twitter.TwitterApp;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by vidhya on 10/4/17.
 */

public class MentionsTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    final AccountOwner[] accountOwner = new AccountOwner[1];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        populateTimeLineFromAPICall(1);
        getAccountOwnerInfo();
    }

    private void populateTimeLineFromAPICall(final long since_id) {
//        if (snackbar != null && snackbar.isShown()) {
//            snackbar.dismiss();
//        }
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                client.getMentionsTimeLine(since_id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        addItems(response);
                        //rotateloading.stop();
                        Log.d("DEBUG", response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.d("ERROR", errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        // Error could be 423. In such a case, display from local DB.
                        //populateTimeLineFromLocalDB();
                        Log.d("ERROR", errorResponse.toString());
                    }
                });
            }
        };

        // rotateloading.start();
        // This API has rate-limit of 15 requests in a 15 min window. So, staggering the requests
        handler.postDelayed(runnable, 500);
    }

    private void getAccountOwnerInfo() {
        //getAccountOwnerInfo

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                client.getAccountOwnerInfo(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", response.toString());

                        try {
                            accountOwner[0] = AccountOwner.fromJSON(response);
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
                            // Toast.makeText(getApplicationContext(), "Something went wrong. Check back later", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("DEBUG", "v2:" + responseString);
                        //Toast.makeText(getApplicationContext(), "Something went wrong. Check back later", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        handler.post(runnable);
    }

    @Override
    void loadMore(long sinceId) {
        populateTimeLineFromAPICall(sinceId);
    }
}