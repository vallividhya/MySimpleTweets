package com.codepath.apps.restclienttemplate.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.apps.restclienttemplate.adapters.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.restclienttemplate.fragments.HomeTimeLineFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.util.NetworkUtil;

import org.parceler.Parcels;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.ComposeTweetDialogListener, TweetsListFragment.TweetSelectedListener, TweetsListFragment.ProfileSelectedListener {

    private static long sSinceId = 1;
    private ActivityTimelineBinding mBinding;
    BroadcastReceiver networkChangeReceiver;
    private ViewPager mViewPager;
    private SmartFragmentStatePagerAdapter mAdapterViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = mBinding.includedToolBar.toolbar;
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        mAdapterViewPager = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        mViewPager = mBinding.viewpager;
        mViewPager.setAdapter(mAdapterViewPager);

        PagerSlidingTabStrip tabStrip = mBinding.tabs;
        tabStrip.setShouldExpand(true);
        tabStrip.setViewPager(mViewPager);
        //setUpReceiver();
    }


    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void populateTimeLineFromLocalDB() {
        // No Network connection
//        snackbar = Snackbar.make(findViewById(R.id.cLayout), R.string.snackbar_text, Snackbar.LENGTH_LONG);
//        snackbar.show();
        // Read from DB
       // tweetsList.addAll((ArrayList<Tweet>) SQLite.select().from(Tweet.class).queryList());
       // adapter.notifyDataSetChanged();
    }

    // Click handler for FAB
    public void onComposeNewTweet(View view) {
        // Compose new tweet in a new activity
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance(getResources().getString(R.string.tweet_dialog_title));
        composeTweetDialogFragment.show(fm, "fragment_compose_tweet");
    }

    // On click handler for log out menu item
    public void onLogOut(MenuItem item) {
       // client.clearAccessToken();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    // On click handler for profile menu item
    public void onProfileView(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    // Implementation for interface TweetsListFragment.TweetSelectedListener's onTweetSelected() method
    @Override
    public void onTweetSelected(Tweet tweet) {
        Intent intent = new Intent(this, TweetDetailActivity.class);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(intent);
    }

    // Implementation for interface ComposeTweetDialogFragment.ComposeTweetDialogListener's onFinishComposeTweet() method
    @Override
    public void onFinishComposeTweet(String tweetText) {
       // Get the fragment from the smartFragmentStatePagerAdapter
        HomeTimeLineFragment fragment = (HomeTimeLineFragment) mAdapterViewPager.getRegisteredFragment(0);
        // Call the method in the HomeTimeline fragment to insert to the timeline locally
        fragment.insertTweetOnTimeLine(fragment.getUser(), tweetText);
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(networkChangeReceiver);
        super.onDestroy();
    }

    private void setUpReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (NetworkUtil.isNetworkAvailable(context)) {
                    // Network available now:
                    Log.d("DEBUG", "vvv: populating from internet");
                    TweetsListFragment fragment = (TweetsListFragment) mAdapterViewPager.getRegisteredFragment(mViewPager.getCurrentItem());
                   // fragment.loadMore(1);
                } else {
                    // Network disconnected
                    Log.d("DEBUG", "vvv: populating from local DB");
                    TweetsListFragment fragment = (TweetsListFragment) mAdapterViewPager.getRegisteredFragment(mViewPager.getCurrentItem());
                    fragment.populateTimeLineFromLocalDB();
                }
            }
        };
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onProfileSelected(String screenName) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("screen_name", screenName);
        startActivity(intent);
    }
}
