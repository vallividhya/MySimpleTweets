package com.codepath.apps.restclienttemplate.helpers.database;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Database initialization class
 *
 * @author Valli Vidhya Venkatesan
 */
public class DatabaseHelperApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(new FlowConfig.Builder(this).build());
    }
}
