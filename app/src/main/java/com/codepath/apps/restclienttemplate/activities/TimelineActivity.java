package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.restclienttemplate.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.AccountOwner;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.twitter.TwitterApp;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.codepath.apps.restclienttemplate.util.TimeUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.ComposeTweetDialogListener {

    private TwitterClient client;
    private TweetAdapter adapter;
    ArrayList<Tweet> tweetsList;
    RecyclerView rvTweets;
    EndlessRecyclerViewScrollListener scrollListener;
    SwipeRefreshLayout swipeContainer;
    static long sinceId = 1;
    final AccountOwner[] accountOwner = new AccountOwner[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        setupViews();
        populateTimeLine(1);
        getAccountOwnerInfo();
    }

    private void setupViews() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Clear items in the adapter so that the API will load new items.
                adapter.clear();
                populateTimeLine(1);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        client = TwitterApp.getRestClient();
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        tweetsList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new TweetAdapter(tweetsList);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(long page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(sinceId);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
    }

    private void loadNextDataFromApi(long page) {
        populateTimeLine(sinceId);
    }

    private void populateTimeLine(final long since_id) {
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                client.getHomeTimeLine(since_id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        //adapter.clear();

                        Log.d("DEBUG", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                                Log.d("DEBUG", " since id = " + tweet.getUid());
                                tweetsList.add(tweet);
                                // Notify the adapter of the item last inserted
                                adapter.notifyItemInserted(tweetsList.size() - 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // sinceid would be the id of the last item in the array list.
                        sinceId = tweetsList.get(tweetsList.size() - 1).getUid();
                        Log.d("DEBUG", "......... MIN since id = " + sinceId);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.d("ERROR", errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("ERROR", errorResponse.toString());
                    }
                });
            }
        };
        // This API has rate-limit of 15 requests in a 15 min window. So, staggering the requests
        //handler.postDelayed(runnable, 500);
    }

    // Click handler for FAB
    public void onComposeNewTweet(View view) {
        // Compose new tweet in a new activity
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance(getResources().getString(R.string.tweet_dialog_title));
        composeTweetDialogFragment.show(fm, "fragment_compose_tweet");
    }

    // Compose Tweet API Call
    private void postNewTweet(final String tweetText) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                client.postNewTweet(tweetText, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", "post Successful");

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", "post failed " + errorResponse.toString());
                    }
                });
            }
        };
        // This API does not have a rate-limit. So, can just be posted.
        handler.post(runnable);
    }

    @Override
    public void onFinishComposeTweet(String tweetText) {
        postNewTweet(tweetText);
        // Insert the new tweet in the time line before a full refresh
        insertTweetOnTimeLine(tweetText);
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
                        Log.d("DEBUG", errorResponse.toString());
                    }
                });
            }
        };
        handler.post(runnable);
    }

    private void insertTweetOnTimeLine(String tweetText) {
        //Insert this tweet in the timeline to appear first
        Tweet tweet = new Tweet();
        tweet.setCreatedAt(TimeUtil.getTwitterFormatForCurrentTime());
        tweet.setBody(tweetText);
        //Get User details
        User user = new User();
        user.setScreenName(accountOwner[0].getScreenName());
        user.setProfileImageUrl(accountOwner[0].getProfileImageUrl());
        tweet.setUser(user);
        // Insert tweet even before a refresh
        //Insert tweet at adapter position 0
        tweetsList.add(0, tweet);
        adapter.notifyItemInserted(0);
        //To show the latest tweet added by account owner on the top
        rvTweets.smoothScrollToPosition(0);
    }
}
