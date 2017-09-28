package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.util.TimeUtil;

import org.parceler.Parcels;

public class TweetDetailActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvUserName;
    TextView tvBody;
    TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvTime = (TextView) findViewById(R.id.tvTime);

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvUserName.setText(tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvTime.setText(TimeUtil.getRelativeTimeAgo(tweet.getCreatedAt()));
        Glide.with(getApplicationContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

    }
}
