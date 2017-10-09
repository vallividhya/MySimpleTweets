package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.twitter.TwitterApp;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.codepath.apps.restclienttemplate.util.NetworkUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

/**
 *  Dialog Fragment for replying to a tweet
 *
 * @author Valli Vidhya Venkatesan
 */
public class ReplyTweetFragment extends DialogFragment {

    private EditText etComposeTweet;
    private Button btnTweet;
    private ImageButton btnCloseDialog;
    private TextView tvCharCount;
    private SharedPreferences mPreferences;
    private TwitterClient mClient;
    private Tweet mTweet;
    private ReplyTweetDialogListener listener;

    public ReplyTweetFragment() {
        // Required empty public constructor
    }

    public static ReplyTweetFragment newInstance(String title) {
        ReplyTweetFragment fragment = new ReplyTweetFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = TwitterApp.getRestClient();
        mPreferences = getContext().getSharedPreferences("Drafts", Context.MODE_PRIVATE);
        mTweet = Parcels.unwrap(getArguments().getParcelable("tweet"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ReplyTweetDialogListener) {
            listener = (ReplyTweetDialogListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement ReplyTweetFragment.ReplyTweetDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reply_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        etComposeTweet = (EditText) view.findViewById(R.id.etReplyTweet);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Let's mTweet");
        getDialog().setTitle(title);

        etComposeTweet.append("@" + mTweet.getUser().getScreenName() + " ");
        // Show soft keyboard automatically and request focus to field
        etComposeTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnTweet = (Button) view.findViewById(R.id.btn_reply);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etComposeTweet.getText().toString();
                postReplyToTweet(text, mTweet.getTweetId());
                dismiss();
            }
        });

        btnCloseDialog = (ImageButton) view.findViewById(R.id.btn_close_dialog);
        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);

        etComposeTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText(s.length() + getActivity().getResources().getString(R.string.charCountString));
                if (s.length() > Tweet.TWEET_LENGTH) {
                    // Disable TweetInDB button
                    btnTweet.setEnabled(false);
                } else {
                    btnTweet.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void postReplyToTweet(final String tweetText, final long inReplyStatusId) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                mClient.postTweet(tweetText, inReplyStatusId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", "Reply post successful");
                        listener.onFinishComposeTweet();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("ERROR", "Post reply failed " + errorResponse.toString());
                        Toast.makeText(getContext(), "Failed to post.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        if (NetworkUtil.isNetworkAvailable(getContext())) {
            // This API does not have a rate-limit. So, can just be posted.
            handler.post(runnable);
        }
    }

    public interface ReplyTweetDialogListener {
        void onFinishComposeTweet();
    }
}
