package com.codepath.apps.restclienttemplate.helpers.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by vidhya on 9/28/17.
 */

@Database(name = SimpleTweetsDatabase.NAME, version = SimpleTweetsDatabase.VERSION)
public class SimpleTweetsDatabase {
    public static final int VERSION = 1;

    public static final String NAME = "SimpleTweetsDatabase";

}
