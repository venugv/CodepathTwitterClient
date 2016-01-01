package com.codepath.codepathtwitterclient.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.codepathtwitterclient.activity.LandingPageActivity;
import com.codepath.codepathtwitterclient.activity.SearchActivity;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;

/**
 * Created by vvenkatraman on 12/13/15.
 */
public class SearchFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"Top Tweets", "All Tweets"};
    private SearchActivity searchActivity;
    private String searchString;

    public SearchFragmentPagerAdapter(FragmentManager fm, SearchActivity searchActivity,
                                      String searchString) {
        super(fm);
        this.searchActivity = searchActivity;
        this.searchString = searchString;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                Fragment fragment = TweetFragment.newInstance("search_all");
                fragment.getArguments().putString("search_string", searchString);
                return fragment;

            default:
                // The other sections of the app are dummy placeholders.
                Fragment fragment1 = TweetFragment.newInstance("search_top");
                fragment1.getArguments().putString("search_string", searchString);
                return fragment1;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}