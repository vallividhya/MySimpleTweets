package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.codepath.apps.restclienttemplate.R;


public class ComposeTweetDialogFragment extends DialogFragment   {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private EditText etComposeTweet;
    private Button btnTweet;
    private ImageButton btnCloseDialog;

    public ComposeTweetDialogFragment() {
        // Required empty public constructor
    }

    public interface ComposeTweetDialogListener  {
        void onFinishComposeTweet(String tweetText);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @return A new instance of fragment ComposeTweetDialogFragment.
     */
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
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnTweet = (Button) view.findViewById(R.id.btn_tweet);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
                String text = etComposeTweet.getText().toString();
                listener.onFinishComposeTweet(text);
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
    }
}
