package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityLoginBinding;
import com.codepath.apps.restclienttemplate.twitter.TwitterClient;
import com.codepath.oauth.OAuthLoginActionBarActivity;

/**
 * Activity for logging in to the app
 *
 * @author Valli Vidhya Venkatesan
 */
public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public void onLoginSuccess() {
        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
    }

    @Override
    public void onLoginFailure(Exception e) {
        Log.e("ERROR", "Login attempt failed.", e);
        Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_LONG).show();
    }

    public void loginToRest(View view) {
        getClient().connect();
    }
}
