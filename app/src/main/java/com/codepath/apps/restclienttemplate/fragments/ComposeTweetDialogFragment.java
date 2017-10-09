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

import cz.msebera.android.httpclient.Header;

/**
 * Dialog Fragment for composing new tweet
 *
 * @author Valli Vidhya Venkatesan
 */
public class ComposeTweetDialogFragment extends DialogFragment {

    private TwitterClient client;
    private EditText etComposeTweet;
    private Button btnTweet;
    private ImageButton btnCloseDialog;
    private TextView tvCharCount;
    private SharedPreferences preferences;

    public ComposeTweetDialogFragment() {
        // Required empty public constructor
    }

    public static ComposeTweetDialogFragment newInstance(String title) {
        ComposeTweetDialogFragment fragment = new ComposeTweetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        preferences = getActivity().getSharedPreferences("Drafts", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etComposeTweet = (EditText) view.findViewById(R.id.etNewTweet);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Let's mTweet");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        etComposeTweet.requestFocus();
        getDraft();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnTweet = (Button) view.findViewById(R.id.btn_tweet);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
                String text = etComposeTweet.getText().toString();
                // Post mTweet
                postNewTweet(text);
                listener.onFinishComposeTweet(text);
                // Delete draft if exists
                saveDraft("");
                Toast.makeText(getContext(), "Tweet posted", Toast.LENGTH_SHORT);
                dismiss();
            }
        });

        btnCloseDialog = (ImageButton) view.findViewById(R.id.btn_close_dialog);
        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDraft(etComposeTweet.getText().toString());
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

    // Compose TweetInDB API Call
    private void postNewTweet(final String tweetText) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                client.postTweet(tweetText, 0, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG", "Post Successful");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("ERROR", "Post failed " + errorResponse.toString(), throwable);
                        Toast.makeText(getContext(), "Failed to post", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        // This API does not have a rate-limit. So, can just be posted.
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            handler.post(runnable);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveDraft(etComposeTweet.getText().toString());
    }

    private void saveDraft(String text) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("draft", text);
        edit.commit();
    }

    private void getDraft() {
        SharedPreferences pref = getActivity().getSharedPreferences("Drafts", Context.MODE_PRIVATE);
        String draft = pref.getString("draft", "");
        etComposeTweet.setText(draft);
    }

    public interface ComposeTweetDialogListener {
        void onFinishComposeTweet(String tweetText);
    }
}
