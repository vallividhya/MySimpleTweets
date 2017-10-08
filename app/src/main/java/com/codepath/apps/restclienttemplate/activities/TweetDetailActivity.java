package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.fragments.ReplyTweetFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.twitter.TwitterApp;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.codepath.apps.restclienttemplate.util.TimeUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TweetDetailActivity extends AppCompatActivity implements ReplyTweetFragment.ReplyTweetDialogListener {

    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvBody;
    private TextView tvTime;
    private ImageView ivTweetMediaImage;
    private VideoView vvMediaVideo;
    private Button btnReply;
    private Tweet mTweet;
    private TwitterClient mClient;
    private ActivityTweetDetailBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tweet_detail);
        mClient = TwitterApp.getRestClient();


        Toolbar toolbar = mBinding.includedToolBar.toolbar;
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle("Tweet");
        }
        ivProfileImage = mBinding.ivProfileImage;
        tvUserName = mBinding.tvUserName;
        tvBody = mBinding.tvBody;
        tvTime = mBinding.tvTime;

        mTweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvUserName.setText(mTweet.getUser().getScreenName());
        tvBody.setText(mTweet.getBody());
        tvTime.setText(TimeUtil.getRelativeTimeAgo(mTweet.getCreatedAt()));
        Glide.with(getApplicationContext()).load(mTweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        ivTweetMediaImage = mBinding.ivMediaImage; //(ImageView) findViewById(R.id.ivMediaImage);
        if (mTweet.getMedia() != null) {
            Glide.with(getApplicationContext()).load(mTweet.getMedia().getMediaUrl()).into(ivTweetMediaImage);
        }

        vvMediaVideo = mBinding.vvDetailVideo; // (VideoView) findViewById(R.id.vvDetailVideo);
        MediaController mediaController = new MediaController(this);
        vvMediaVideo.setMediaController(mediaController);

        if (mTweet.getMedia() != null && mTweet.getMedia().getMediaType().equals("video") && mTweet.getMedia().getVideoUrl() != null) {
            vvMediaVideo.setVisibility(View.VISIBLE);
            vvMediaVideo.setVideoPath(mTweet.getMedia().getVideoUrl());
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

        btnReply = mBinding.btnReply; //(Button) findViewById(R.id.btnReply);
    }


    public void onReplyTweet(View view) {
        // Bring up the modal to reply
        Bundle bundle = new Bundle();
        bundle.putParcelable("mTweet", Parcels.wrap(mTweet));
        FragmentManager fm = getSupportFragmentManager();
        ReplyTweetFragment replyTweetDialogFragment = ReplyTweetFragment.newInstance(getResources().getString(R.string.tweet_dialog_title));
        replyTweetDialogFragment.setArguments(bundle);
        replyTweetDialogFragment.show(fm, "fragment_reply_tweet");
    }

    @Override
    public void onFinishComposeTweet(final String tweetText, final long inReplyStatusId) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                mClient.postTweet(tweetText, inReplyStatusId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", "vvv: Reply post Successful");

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
}
