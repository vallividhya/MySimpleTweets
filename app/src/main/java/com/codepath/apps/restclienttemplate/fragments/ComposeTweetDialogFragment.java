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

import com.codepath.apps.restclienttemplate.R;


public class ComposeTweetDialogFragment extends DialogFragment   {

    private EditText etComposeTweet;
    private Button btnTweet;
    private ImageButton btnCloseDialog;
    private TextView tvCharCount;
    SharedPreferences preferences;

    public ComposeTweetDialogFragment() {
        // Required empty public constructor
    }

    public interface ComposeTweetDialogListener  {
        void onFinishComposeTweet(String tweetText);
    }

    public static ComposeTweetDialogFragment newInstance(String title) {
        ComposeTweetDialogFragment fragment = new ComposeTweetDialogFragment();
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
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etComposeTweet = (EditText) view.findViewById(R.id.etNewTweet);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Let's tweet");
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
                listener.onFinishComposeTweet(text);
                // Delete draft if exists
                saveDraft("");
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveDraft(etComposeTweet.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            getDraft();
        }
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
}
