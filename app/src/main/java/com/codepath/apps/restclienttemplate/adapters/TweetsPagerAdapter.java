package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.HomeTimeLineFragment;
import com.codepath.apps.restclienttemplate.fragments.MentionsTimelineFragment;

/**
 * Created by vidhya on 10/4/17.
 */

public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private String tabTitles[] = new String[] {"Home", "Mentions"};
    private Context context;
    private int tabIcons[] = {R.drawable.ic_home_gray, R.drawable.ic_mentions_gray};

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return HomeTimeLineFragment.newInstance();
        } else if (position == 1) {
            return new MentionsTimelineFragment();
        } else {
            return null;
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 2;
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return tabTitles[position];
//    }


    @Override
    public int getPageIconResId(int position) {
        return tabIcons[position];
    }
}
