package com.codepath.apps.restclienttemplate.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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

    ImageView ivProfileImage;
    TextView tvUserName;
    TextView tvBody;
    TextView tvTime;
    ImageView ivTweetMediaImage;
    VideoView vvMediaVideo;
    Button btnReply;
    Tweet tweet;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        client = TwitterApp.getRestClient();

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvTime = (TextView) findViewById(R.id.tvTime);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

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

        btnReply = (Button) findViewById(R.id.btnReply);
    }


    public void onReplyTweet(View view) {
        // Bring up the modal to reply
        Bundle bundle = new Bundle();
        bundle.putParcelable("tweet", Parcels.wrap(tweet));
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
                client.postTweet(tweetText, inReplyStatusId, new JsonHttpResponseHandler() {
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
