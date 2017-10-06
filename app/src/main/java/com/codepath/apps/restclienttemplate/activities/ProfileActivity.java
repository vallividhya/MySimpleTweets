package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.twitter.TwitterApp;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private TwitterClient client;
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content from the binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        // Find the toolbar view inside the activity layout
        final Toolbar toolbar = binding.includedToolBar.toolbar;
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        String screenName = getIntent().getStringExtra("screen_name");
        // Create user fragment
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, userTimelineFragment);
        ft.commit();

        client = TwitterApp.getRestClient();
        client.getUserInfo(screenName, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Deserialize user object
                    User user = User.fromJson(response);
                    // Display screenName on the action bar
                    toolbar.setTitle("@" + user.getScreenName());

                    populateUserHeadline(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void populateUserHeadline(User user) {
        TextView tvName = binding.tvName;
        TextView tvTagline = binding.tvTagLine;
        TextView tvFollowers = binding.tvFollowers;
        TextView tvFollowing = binding.tvFollowing;
        ImageView ivProfileImage = binding.ivProfileImage;

        tvName.setText(user.getName());
        Glide.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);

        SpannableStringBuilder str = new SpannableStringBuilder(user.getFollowers() + " Followers");
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        tvFollowers.setText(str);

        SpannableStringBuilder str1 = new SpannableStringBuilder(user.getFollowing() + " Following");
        str1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvFollowing.setText(str1);
        tvTagline.setText(user.getTagLine());
    }


}
