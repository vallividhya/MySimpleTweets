package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vidhya on 9/25/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private ArrayList<Tweet> mTweetsList;
    private Context context;
    private TweetAdapterOnItemClickListener onItemClickListener;

    public interface TweetAdapterOnItemClickListener {
        void onItemClick(View itemView, int position);

        void onProfileImageClick(String screenName);

        void onReplyClick(View itemView, int position);
    }

    public interface OnTweetReplyClickListener {
        void onTweetReplyClick (View itemView, int position);
    }

    public TweetAdapter(ArrayList<Tweet> tweetsList, TweetAdapterOnItemClickListener listener) {
        mTweetsList = tweetsList;
        onItemClickListener = listener;
    }

    // Create a new TweetInDB row and cache references into View Holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Get data according to position
        Tweet tweet = mTweetsList.get(position);

        //Populate the views according to position
        holder.tvUserName.setText(tweet.getUser().getName());
        holder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        holder.tvBody.setText(tweet.getBody());
        holder.tvTime.setText(TimeUtil.getRelativeTimeAgo(tweet.getCreatedAt()));
        holder.ivProfileImage.setTag(R.id.ivProfileImage, tweet.getUser().getScreenName());
        // Rounded corners images
        Glide.with(context).load(tweet.getUser().getProfileImageUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivProfileImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.ivProfileImage.setImageDrawable(circularBitmapDrawable);
            }
        });

        if (tweet.getMedia() != null && tweet.getMedia().getMediaType().equals("video") && tweet.getMedia().getVideoUrl() != null) {
            try {
                //holder.vvMediaVideo.setVisibility(View.VISIBLE);
                holder.vvMediaVideo.setVideoPath(tweet.getMedia().getVideoUrl());
                holder.vvMediaVideo.setVisibility(View.VISIBLE);
                holder.vvMediaVideo.requestFocus();
                holder.vvMediaVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        // Do not play sound
                        mp.setVolume(0, 0);
                        holder.vvMediaVideo.start();
                    }
                });
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage());
            }
        } else {
            holder.vvMediaVideo.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mTweetsList.size();
    }

    public void clear() {
        mTweetsList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        mTweetsList.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvScreenName;
        public TextView tvBody;
        public TextView tvTime;
        public VideoView vvMediaVideo;
        public ImageButton iBtnReply;

        public ViewHolder(final View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String screenName = (String) v.getTag(R.id.ivProfileImage);
                    onItemClickListener.onProfileImageClick(screenName);
                }
            });

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            Typeface userNameFont = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeue-Bold.ttf");
            tvUserName.setTypeface(userNameFont);

            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            Typeface bodyFont = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeue-Regular.ttf");
            tvBody.setTypeface(bodyFont);

            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvScreenName.setTypeface(bodyFont);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(itemView, position);
                        }
                    }
                }
            });

            vvMediaVideo = (VideoView) itemView.findViewById(R.id.vvMediaVideo);

            iBtnReply = (ImageButton) itemView.findViewById(R.id.iBtnReply);
            iBtnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            onItemClickListener.onReplyClick(itemView, pos);
                        }
                    }
                }
            });
        }

    }
}
