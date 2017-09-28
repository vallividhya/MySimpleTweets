package com.codepath.apps.restclienttemplate.helpers.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by vidhya on 9/28/17.
 */

@Database(name = SimpleTweetsDatabase.NAME, version = SimpleTweetsDatabase.VERSION)
public class SimpleTweetsDatabase {
    public static final int VERSION = 1;

    public static final String NAME = "SimpleTweetsDatabase";

//    @Table(database = SimpleTweetsDatabase.class)
//    @Parcel(analyze = {TweetInDB.class})
//    public class TweetInDB extends BaseModel {
//
//        @Column
//        @PrimaryKey (autoincrement = true)
//        long tweetId;
//
//        @Column
//        long userId;
//
//        @Column
//        String tweetText;
//
//        @Column
//        String userScreenName;
//
//        @Column
//        String tweetCreateTime;
//    }
//
//    // Save new tweet
//    public void addTweet(Tweet tweet, User user) {
//        TweetInDB tweetInDB = new TweetInDB();
//        tweetInDB.tweetCreateTime = tweet.getCreatedAt();
//        tweetInDB.tweetText = tweet.getBody();
//        tweetInDB.userId = user.getUserId();
//        tweetInDB.userScreenName = user.getScreenName();
//    }

}
