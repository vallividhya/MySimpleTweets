package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.FragmentTweetsListBinding;
import com.codepath.apps.restclienttemplate.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.AccountOwner;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.util.TimeUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public abstract class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterOnItemClickListener {

    private TweetAdapter adapter;
    ArrayList<Tweet> tweetsList;
    RecyclerView rvTweets;
    private FragmentTweetsListBinding binding;
    EndlessRecyclerViewScrollListener scrollListener;
    SwipeRefreshLayout swipeContainer;
    Snackbar snackbar;
    LinearLayoutManager layoutManager;
    static long sinceId = 1;
   // com.victor.loading.rotate.RotateLoading rotateloading;

    public interface TweetSelectedListener {
        // Handle tweet selection
        public void onTweetSelected(Tweet tweet);
    }



    public TweetsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = binding.inflate(inflater, container, false);
        setupViews();
        // Inflate the layout for this fragment
        return binding.getRoot();

    }

    private void setupViews() {
        // Find the toolbar view inside the activity layout
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        // Sets the Toolbar to act as the ActionBar for this Activity window.
//        // Make sure the toolbar exists in the activity and is not null
//        setSupportActionBar(toolbar);

        //rotateloading = binding.rotateloading; //(com.victor.loading.rotate.RotateLoading) findViewById(R.id.rotateloading);

        swipeContainer = binding.swipeContainer; //(SwipeRefreshLayout) findViewById(swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Clear items in the adapter so that the API will load new items.
                adapter.clear();
                loadMore(1);
//                if (NetworkUtil.isNetworkAvailable(getApplicationContext())) {
//                    populateTimeLineFromAPICall(1);
//                } else {
//                    populateTimeLineFromLocalDB();
//                }
                binding.swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvTweets = binding.rvTweet; //(RecyclerView) findViewById(R.id.rvTweet);
        tweetsList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new TweetAdapter(tweetsList, this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        // Adding dividers for each tweet in the recycler view
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(long page, int totalItemsCount, RecyclerView view) {
                //loadNextDataFromApi(sinceId);
                loadMore(sinceId);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
    }

    public void addItems(JSONArray response) {
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

    }

    public void insertTweetOnTimeLine(AccountOwner accountOwner, String tweetText) {
        //Insert this tweet in the timeline to appear first
        Tweet tweet = new Tweet();
        tweet.setCreatedAt(TimeUtil.getTwitterFormatForCurrentTime());
        tweet.setBody(tweetText);
        //Get User details
        User user = new User();
        user.setName(accountOwner.getName());
        user.setScreenName(accountOwner.getScreenName());
        user.setProfileImageUrl(accountOwner.getProfileImageUrl());
        tweet.setUser(user);
        // Insert tweet even before a refresh
        //Insert tweet at adapter position 0
         tweetsList.add(0, tweet);
         adapter.notifyItemInserted(0);
        //To show the latest tweet added by account owner on the top
         rvTweets.smoothScrollToPosition(0);
    }

    public void addItemsFromDatabase() {
        tweetsList.addAll((ArrayList<Tweet>) SQLite.select().from(Tweet.class).queryList());
        adapter.notifyDataSetChanged();
    }

    public void populateTimeLineFromLocalDB() {
        // No Network connection
//        snackbar = Snackbar.make(binding., R.string.snackbar_text, Snackbar.LENGTH_LONG);
//        snackbar.show();
        // Read from DB
       addItemsFromDatabase();
    }

    @Override
    public void onItemClick(View itemView, int position) {
        Tweet tweet = tweetsList.get(position);
        ((TweetSelectedListener) getActivity()).onTweetSelected(tweet);
//
    }

    //insertTweetOnTimeLine
    //

    abstract void loadMore(long sinceId);
}
