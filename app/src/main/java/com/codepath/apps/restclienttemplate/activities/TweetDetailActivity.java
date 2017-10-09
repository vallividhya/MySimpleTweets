package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.codepath.apps.restclienttemplate.util.NetworkUtil;
import com.codepath.apps.restclienttemplate.util.TimeUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

/**
 * Activity for showing detailed tweet
 *
 * @author Valli Vidhya Venkatesan
 */
public class TweetDetailActivity extends AppCompatActivity
        implements ReplyTweetFragment.ReplyTweetDialogListener {

    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvBody;
    private TextView tvTime;
    private ImageView ivTweetMediaImage;
    private VideoView vvMediaVideo;
    private ImageView ivReply;
    private ImageView ivReTweet;
    private Tweet mTweet;
    private TwitterClient mClient;
    private ActivityTweetDetailBinding mBinding;
    private ReplyTweetFragment replyTweetDialogFragment;

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

        ivTweetMediaImage = mBinding.ivMediaImage;
        if (mTweet.getMedia() != null) {
            Glide.with(getApplicationContext()).load(mTweet.getMedia().getMediaUrl()).into(ivTweetMediaImage);
        }

        vvMediaVideo = mBinding.vvDetailVideo;
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

        ivReply = mBinding.btnReply;
        ivReTweet = mBinding.btnReTweet;

        if (mTweet.isReTweeted()) {
            paintButton(R.color.colorPrimary);
        }

//        ColorStateList colors;
//        if (Build.VERSION.SDK_INT >= 23) {
//            colors = getResources().getColorStateList(R.color.colorPrimary, getTheme());
//        }
//        else {
//            colors = getResources().getColorStateList(R.color.colorPrimary);
//        }
//        if (mTweet.isReTweeted()) {
//            Drawable drawable = getResources().getDrawable(R.drawable.ic_retweet_24px);
//            Drawable icon = DrawableCompat.wrap(drawable);
//            DrawableCompat.setTint(icon.mutate(), getResources().getColor(R.color.colorPrimary));
//            //DrawableCompat.setTintList(icon.mutate(), colors);
//            ivReTweet.setImageDrawable(icon);
//        }
    }

    // Click Handler for Reply button
    public void onReplyTweet(View view) {
        // Bring up the modal to reply
        Bundle bundle = new Bundle();
        bundle.putParcelable("tweet", Parcels.wrap(mTweet));
        FragmentManager fm = getSupportFragmentManager();
        replyTweetDialogFragment = ReplyTweetFragment.newInstance(getResources().getString(R.string.tweet_dialog_title));
        replyTweetDialogFragment.setArguments(bundle);
        replyTweetDialogFragment.show(fm, "fragment_reply_tweet");
    }

    @Override
    public void onFinishComposeTweet() {
        Toast.makeText(this, "Tweet posted", Toast.LENGTH_SHORT).show();
        // Ask the TimeLineActivity to insert tweet on top
    }

    public void onReTweet(View view) {
        postReTweet(mTweet.getTweetId());
    }

    // Click handler for Retweet button
    private void postReTweet(final long reTweetStatusId) {
        if (!mTweet.isReTweeted()) {
            paintButton(R.color.colorPrimary);
            mTweet.setReTweeted(true);
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    mClient.postReTweet(reTweetStatusId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("DEBUG", "Retweet Successful");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e("ERROR", "post failed " + errorResponse.toString());
                        }
                    });
                }
            };
            // This API does not have a rate-limit. So, can just be posted.
            if (NetworkUtil.isNetworkAvailable(this)) {
                handler.post(runnable);
            }

        } else {
            paintButton(R.color.colorDarkGrey);
            mTweet.setReTweeted(false);
            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    mClient.postUnReTweet(reTweetStatusId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("DEBUG", "Unretweet Successful");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e("ERROR", "post failed " + errorResponse.toString());
                        }
                    });
                }
            };
            // This API does not have a rate-limit. So, can just be posted.
            if (NetworkUtil.isNetworkAvailable(this)) {
                handler.post(runnable);
            }
        }
    }

    private void paintButton(int colorId) {
        ivReTweet.setImageDrawable(DrawableCompat.wrap(ivReTweet.getDrawable()));
        DrawableCompat.setTint(
                ivReTweet.getDrawable(),
                getResources().getColor(colorId)
        );
        ivReTweet.invalidate();
    }
}
