package com.codepath.apps.restclienttemplate.helpers.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Database base class
 *
 * @author Valli Vidhya Venkatesan
 */
@Database(name = SimpleTweetsDatabase.NAME, version = SimpleTweetsDatabase.VERSION)
public class SimpleTweetsDatabase {
    public static final int VERSION = 1;

    public static final String NAME = "SimpleTweetsDatabase";

}
