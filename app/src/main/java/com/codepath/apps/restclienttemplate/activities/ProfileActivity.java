package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.twitter.TwitterApp;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

/**
 * Activity for showing profile of the user
 *
 * @author Valli Vidhya Venkatesan
 */
public class ProfileActivity extends AppCompatActivity
        implements TweetsListFragment.TweetSelectedListener, TweetsListFragment.ProfileSelectedListener {

    Toolbar toolbar;
    private TwitterClient mClient;
    private ActivityProfileBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content from the mBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        // Find the toolbar view inside the activity layout
        toolbar = mBinding.includedToolBar.toolbar;
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        String screenName = "";
        screenName = getIntent().getStringExtra("screen_name");
        // Create user fragment
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, userTimelineFragment);
        ft.commit();

        mClient = TwitterApp.getRestClient();

        if (screenName == null) {
            mClient.getAccountOwnerInfo(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    getUserFromResponse(response);
                }
            });
        } else {
            mClient.getUserInfo(screenName, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    getUserFromResponse(response);
                }
            });
        }
    }

    private void getUserFromResponse(JSONObject response) {
        try {
            // Deserialize user object
            User user = User.fromJson(response);
            // Display screenName on the action bar
            toolbar.setTitle("@" + user.getScreenName());

            populateUserHeadline(user);
        } catch (JSONException e) {
            Log.e("ERROR", "Failed to parse User response from server", e);
        }
    }

    private void populateUserHeadline(User user) {
        TextView tvName = mBinding.tvName;
        TextView tvTagline = mBinding.tvTagLine;
        TextView tvFollowers = mBinding.tvFollowers;
        TextView tvFollowing = mBinding.tvFollowing;
        ImageView ivProfileImage = mBinding.ivProfileImage;

        tvName.setText(user.getName());
        Glide.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);

        String follow = String.valueOf(user.getFollowers());
        SpannableStringBuilder str = new SpannableStringBuilder(follow);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, follow.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ForegroundColorSpan(Color.BLACK), 0, follow.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.append(" Followers");
        tvFollowers.setText(str, TextView.BufferType.SPANNABLE);

        String follow1 = String.valueOf(user.getFollowing());
        SpannableStringBuilder str1 = new SpannableStringBuilder(follow1);
        str1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, follow1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, follow1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str1.append(" Following");
        tvFollowing.setText(str1, TextView.BufferType.SPANNABLE);
        tvTagline.setText(user.getTagLine());
    }


    @Override
    public void onTweetSelected(Tweet tweet) {
        Intent intent = new Intent(this, TweetDetailActivity.class);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(intent);
    }

    @Override
    public void onProfileSelected(String screenName) {
        // Do nothing. Already in profile activity;
    }
}
