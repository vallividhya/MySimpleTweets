package com.codepath.apps.restclienttemplate.twitter;

import android.content.Context;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "Pb9pssiMa3SsHYfdNJ9jKq1Wc";       // Change this
	public static final String REST_CONSUMER_SECRET = "5lj7FcUmzefXJeA3taIZP9xORDiVq4zvmd7xg869T7577xJV0P"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getHomeTimeLine(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	// GET home timeLine
    public void getHomeTimeLine(long sinceId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("count", 10);

        if (sinceId == 1) {
            // For the first time, get the newest tweets
            params.put("since_id", 1);
        } else {
            // For the other times, get the tweets older than the ones you fetched the previous times.
            params.put("max_id", sinceId - 1);
        }

        params.put("format", "json");
        client.get(apiUrl, params, handler);
    }

    // Get mentions timeline
	public void getMentionsTimeLine(long sinceId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 10);

		if (sinceId == 1) {
			// For the first time, get the newest tweets
			params.put("since_id", 1);
		} else {
			// For the other times, get the tweets older than the ones you fetched the previous times.
			params.put("max_id", sinceId - 1);
		}

		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	//Get user timeline
	public void getUserTimeLine(long sinceId, String screenName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
		params.put("count", 10);
        if (sinceId == 1) {
            // For the first time, get the newest tweets
            params.put("since_id", 1);
        } else {
            // For the other times, get the tweets older than the ones you fetched the previous times.
            params.put("max_id", sinceId - 1);
        }
        client.get(apiUrl, params, handler);
	}


    // POST new tweet
    public void postTweet(String status, long tweetIdForReply, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		params.put("status", status);
		if (tweetIdForReply > 0) {
			params.put("in_reply_to_status_id", tweetIdForReply);
		}
		client.post(apiUrl, params, handler);
	}

	// Get account owner info
	public void getAccountOwnerInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        client.get(apiUrl, handler);
    }

    // Get user info
    public void getUserInfo(String userScreenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        if (userScreenName != null && !userScreenName.isEmpty()) {
            params.put("screen_name", userScreenName);
        }
        client.get(apiUrl, params, handler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
