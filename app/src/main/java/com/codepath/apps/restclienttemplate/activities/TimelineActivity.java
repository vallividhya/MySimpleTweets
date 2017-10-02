package com.codepath.apps.restclienttemplate.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.restclienttemplate.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.AccountOwner;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.twitter.TwitterApp;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.codepath.apps.restclienttemplate.util.NetworkUtil;
import com.codepath.apps.restclienttemplate.util.TimeUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.ComposeTweetDialogListener {

    private ActivityTimelineBinding binding;
    private TwitterClient client;
    private TweetAdapter adapter;
    ArrayList<Tweet> tweetsList;
    RecyclerView rvTweets;
    EndlessRecyclerViewScrollListener scrollListener;
    SwipeRefreshLayout swipeContainer;
    static long sinceId = 1;
    final AccountOwner[] accountOwner = new AccountOwner[1];
    NetworkChangeReceiver networkChangeReceiver;
    com.victor.loading.rotate.RotateLoading rotateloading;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        setupViews();
        populateTimeLineFromAPICall(1);
        getAccountOwnerInfo();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void setupViews() {
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        rotateloading = (com.victor.loading.rotate.RotateLoading) findViewById(R.id.rotateloading);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Clear items in the adapter so that the API will load new items.
                adapter.clear();
                if (NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    populateTimeLineFromAPICall(1);
                } else {
                    populateTimeLineFromLocalDB();
                }
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
        adapter.setOnItemClickListener(new TweetAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View itemView, int position) {
                Intent intent = new Intent(getApplicationContext(), TweetDetailActivity.class);
                Tweet tweet = tweetsList.get(position);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                startActivity(intent);
            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);

        return true;
    }



    private void loadNextDataFromApi(long page) {
        populateTimeLineFromAPICall(sinceId);
    }

    private void populateTimeLineFromAPICall(final long since_id) {
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                client.getHomeTimeLine(since_id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        //adapter.clear();
                        rotateloading.stop();
                        Log.d("DEBUG", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                                Log.d("DEBUG", " since id = " + tweet.getTweetId());
                                tweetsList.add(tweet);
                                // Notify the adapter of the item last inserted
                                adapter.notifyItemInserted(tweetsList.size() - 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // sinceid would be the id of the last item in the array list.
                        sinceId = tweetsList.get(tweetsList.size() - 1).getTweetId();
                        Log.d("DEBUG", "......... MIN since id = " + sinceId);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.d("ERROR", errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        // Error could be 423. In such a case, display from local DB.
                        populateTimeLineFromLocalDB();
                        //Log.d("ERROR", errorResponse.toString());
                    }
                });
            }
        };

        rotateloading.start();
        // This API has rate-limit of 15 requests in a 15 min window. So, staggering the requests
        handler.postDelayed(runnable, 500);
    }

    public void populateTimeLineFromLocalDB() {
        // No Network connection
        snackbar = Snackbar.make(findViewById(R.id.cLayout), R.string.snackbar_text, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        // Read from DB
        tweetsList.addAll((ArrayList<Tweet>) SQLite.select().from(Tweet.class).queryList());
        adapter.notifyDataSetChanged();
    }

    // Click handler for FAB
    public void onComposeNewTweet(View view) {
        // Compose new tweet in a new activity
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance(getResources().getString(R.string.tweet_dialog_title));
        composeTweetDialogFragment.show(fm, "fragment_compose_tweet");
    }

    // Compose TweetInDB API Call
    private void postNewTweet(final String tweetText) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                client.postTweet(tweetText, 0, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", "post Successful");

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("DEBUG", "post failed " + errorResponse.toString());
                        Toast.makeText(getApplicationContext(), "Failed to post", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        // This API does not have a rate-limit. So, can just be posted.
        handler.post(runnable);
    }

    // On click handler for log out menu item
    public void onLogOut(MenuItem item) {
        client.clearAccessToken();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtil.isNetworkAvailable(context)) {
                // Network available now:
                Log.d("DEBUG", "vvv: populating from internet");
                populateTimeLineFromAPICall(sinceId);
            } else {
                // Network disconnected
                Log.d("DEBUG", "vvv: populating from local DB");
                populateTimeLineFromLocalDB();
            }
        }
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
                           // Log.d("DEBUG", errorResponse.getString("error"));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        try {
                            Log.d("DEBUG",  "v1:"+ errorResponse.getJSONObject(0).toString());
                            Toast.makeText(getApplicationContext(), "Something went wrong. Check back later", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("DEBUG", "v2:" + responseString);
                        Toast.makeText(getApplicationContext(), "Something went wrong. Check back later", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkChangeReceiver);
        super.onDestroy();
    }
}
