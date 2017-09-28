package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vidhya on 9/25/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    ArrayList<Tweet> mTweetsList;
    Context context;
    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public TweetAdapter(ArrayList<Tweet> tweetsList) {
        mTweetsList = tweetsList;
    }

    // Create a new TweetInDB row and cache references into View Holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView =  inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get data according to position
        Tweet tweet = mTweetsList.get(position);

        //Populate the views according to position
        holder.tvUserName.setText(tweet.getUser().getScreenName());
        holder.tvBody.setText(tweet.getBody());
        holder.tvTime.setText(TimeUtil.getRelativeTimeAgo(tweet.getCreatedAt()));
        Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweetsList.size();
    }

    public void clear() {
        mTweetsList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet>list) {
        mTweetsList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

        public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvTime;

        public ViewHolder(final View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }

//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition(); // gets item position
//            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
//                Tweet tweet = mTweetsList.get(position);
//                // We can access the data within the views
//               Toast.makeText(v.getContext(), tweet.getBody(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//
//            }
//        }
    }
}
