package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.FragmentTweetsListBinding;
import com.codepath.apps.restclienttemplate.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.util.NetworkUtil;
import com.codepath.apps.restclienttemplate.util.TimeUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

/**
 * Base fragment class for displaying tweets in a recycler view
 *
 * @author Valli Vidhya Venkatesan
 */
public abstract class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterOnItemClickListener {

    protected com.victor.loading.rotate.RotateLoading rotateloading;
    private TweetAdapter adapter;
    private ArrayList<Tweet> mTweetsList;
    private RecyclerView rvTweets;
    private FragmentTweetsListBinding mBinding;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private SwipeRefreshLayout swipeContainer;
    private Snackbar snackbar;
    private LinearLayoutManager layoutManager;

    public TweetsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = mBinding.inflate(inflater, container, false);
        setupViews();
        // Inflate the layout for this fragment
        return mBinding.getRoot();

    }

    private void setupViews() {
        rotateloading = mBinding.rotateloading; //(com.victor.loading.rotate.RotateLoading) findViewById(R.id.rotateloading);
        swipeContainer = mBinding.swipeContainer; //(SwipeRefreshLayout) findViewById(swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Clear items in the adapter so that the API will load new items.
                adapter.clear();
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    if (snackbar != null && snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                    rotateloading.start();
                    loadMore();
                    rotateloading.stop();
                } else {
                    snackbar.show();
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

        rvTweets = mBinding.rvTweet; //(RecyclerView) findViewById(R.id.rvTweet);
        mTweetsList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new TweetAdapter(mTweetsList, this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        // Adding dividers for each mTweet in the recycler view
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration);

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(long page, int totalItemsCount, RecyclerView view) {
                rotateloading.start();
                loadMore();
                rotateloading.stop();
            }
        };
        rvTweets.addOnScrollListener(mScrollListener);
        snackbar = Snackbar.make(mBinding.cLayout, R.string.snackbar_text, Snackbar.LENGTH_LONG);
    }

    public void addItems(ArrayList<Tweet> tweetsList) {
        mTweetsList.addAll(tweetsList);
        adapter.notifyItemInserted(mTweetsList.size() - 1);
    }

    public void insertTweetOnTimeLine(User accountOwner, String tweetText) {
        // Insert this mTweet in the timeline to appear first
        Tweet tweet = new Tweet();
        tweet.setCreatedAt(TimeUtil.getTwitterFormatForCurrentTime());
        tweet.setBody(tweetText);
        // Get User details
        User user = new User();
        user.setName(accountOwner.getName());
        user.setScreenName(accountOwner.getScreenName());
        user.setProfileImageUrl(accountOwner.getProfileImageUrl());
        tweet.setUser(user);
        // Insert mTweet even before a refresh
        // Insert mTweet at adapter position 0
        mTweetsList.add(0, tweet);
        adapter.notifyItemInserted(0);
        // To show the latest mTweet added by account owner on the top
        rvTweets.smoothScrollToPosition(0);
    }

    public void addItemsFromDatabase() {
        mTweetsList.addAll((ArrayList<Tweet>) SQLite.select().from(Tweet.class).queryList());
        adapter.notifyDataSetChanged();
    }

    public void populateTimeLineFromLocalDB() {
        // No Network connection
        // Read from DB
        addItemsFromDatabase();
    }

    @Override
    public void onItemClick(View itemView, int position) {
        Tweet tweet = mTweetsList.get(position);
        ((TweetSelectedListener) getActivity()).onTweetSelected(tweet);
    }

    // Might get called when profile image is clicked in the adapter. Pass this upto the activity
    @Override
    public void onProfileImageClick(String screenName) {
        ((ProfileSelectedListener) getActivity()).onProfileSelected(screenName);
    }

    @Override
    public void onReplyClick(View itemView, int position) {
        Tweet tweet = mTweetsList.get(position);
        ((ReplyClickedListener) getActivity()).onReplyTweetClicked(tweet);
    }

    public abstract void loadMore();

    public interface TweetSelectedListener {
        // Handle Tweet selection
        public void onTweetSelected(Tweet tweet);
    }

    public interface ProfileSelectedListener {
        public void onProfileSelected(String screenName);
    }

    public interface ReplyClickedListener {
        public void onReplyTweetClicked(Tweet tweet);
    }
}
