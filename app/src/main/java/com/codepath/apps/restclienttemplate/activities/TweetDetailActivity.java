package com.codepath.apps.restclienttemplate.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

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
    ImageView ivTweetMediaImage;
    VideoView vvMediaVideo;

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

        ivTweetMediaImage = (ImageView) findViewById(R.id.ivMediaImage);
        if (tweet.getMedia() != null) {
            Glide.with(getApplicationContext()).load(tweet.getMedia().getMediaUrl()).into(ivTweetMediaImage);
        }

        vvMediaVideo = (VideoView) findViewById(R.id.vvDetailVideo);
        MediaController mediaController = new MediaController(this);
        vvMediaVideo.setMediaController(mediaController);

        if (tweet.getMedia() != null && tweet.getMedia().getMediaType().equals("video") && tweet.getMedia().getVideoUrl() != null) {
            vvMediaVideo.setVisibility(View.VISIBLE);
            vvMediaVideo.setVideoPath(tweet.getMedia().getVideoUrl());
            vvMediaVideo.requestFocus();
            vvMediaVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    // Play sound in detail view
                    mp.setVolume(1, 1);
                    vvMediaVideo.start();
                }
            });
        } else {
            vvMediaVideo.setVisibility(View.GONE);
        }





    }
}
