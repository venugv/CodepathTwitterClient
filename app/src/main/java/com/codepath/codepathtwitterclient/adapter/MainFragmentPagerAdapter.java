package com.codepath.codepathtwitterclient.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.codepathtwitterclient.activity.LandingPageActivity;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;

/**
 * Created by vvenkatraman on 12/13/15.
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"Home", "Mentions", "Tweets"};
    private LandingPageActivity landingPageActivity;
    private String userID;

    public MainFragmentPagerAdapter(FragmentManager fm, LandingPageActivity landingPageActivity,
                                    String userID) {
        super(fm);
        this.landingPageActivity = landingPageActivity;
        this.userID = userID;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment =  TweetFragment.newInstance("mentions");
                break;
            case 2:
                fragment =  TweetFragment.newInstance("user");
                break;

            default:
                // The other sections of the app are dummy placeholders.
                fragment = TweetFragment.newInstance("home");
                break;
        }
        fragment.getArguments().putString("user_id", userID);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}