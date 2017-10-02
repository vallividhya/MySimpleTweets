package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import org.parceler.Parcels;

public class ReplyTweetFragment extends DialogFragment {
    private EditText etComposeTweet;
    private Button btnTweet;
    private ImageButton btnCloseDialog;
    private TextView tvCharCount;
    SharedPreferences preferences;
    Tweet tweet;

    public ReplyTweetFragment() {
        // Required empty public constructor
    }

    public interface ReplyTweetDialogListener {
        void onFinishComposeTweet(String tweetText, long replyId);
    }

    public static ReplyTweetFragment newInstance(String title) {
        ReplyTweetFragment fragment = new ReplyTweetFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        preferences = getActivity().getSharedPreferences("Drafts", Context.MODE_PRIVATE);
        tweet = Parcels.unwrap(getArguments().getParcelable("tweet"));
        return inflater.inflate(R.layout.fragment_reply_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        etComposeTweet = (EditText) view.findViewById(R.id.etReplyTweet);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Let's tweet");
        getDialog().setTitle(title);

        //getDraft();
        etComposeTweet.append("@" + tweet.getUser().getScreenName() + " ");
        // Show soft keyboard automatically and request focus to field
        etComposeTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnTweet = (Button) view.findViewById(R.id.btn_reply);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplyTweetDialogListener listener = (ReplyTweetDialogListener) getActivity();
                String text = etComposeTweet.getText().toString();
                listener.onFinishComposeTweet(text, tweet.getTweetId());
                // Delete draft if exists
                //saveDraft("");
                Toast.makeText(getActivity().getApplicationContext(), "Tweet posted", Toast.LENGTH_SHORT);
                dismiss();
            }
        });

        btnCloseDialog = (ImageButton) view.findViewById(R.id.btn_close_dialog);
        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveDraft(etComposeTweet.getText().toString());
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
                if(s.length() > 140) {
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
}
