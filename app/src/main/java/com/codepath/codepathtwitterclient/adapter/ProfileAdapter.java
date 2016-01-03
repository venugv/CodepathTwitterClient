package com.codepath.codepathtwitterclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.codepath.codepathtwitterclient.model.Tweet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public abstract class ProfileAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private ArrayList<Tweet> items = new ArrayList<>();
    private ArrayList<Tweet> curatedItems = new ArrayList<>();
    protected int selectedTab = 0;

    public ProfileAdapter() {
        setHasStableIds(true);
    }

    public void add(Tweet object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, Tweet object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(int selectedTab, Collection<? extends Tweet> collection) {
        this.selectedTab = selectedTab;
        int originalCount = originalCount = items.size();
        if (selectedTab == 1) {
            originalCount = curatedItems.size();
        }
        if (collection != null && collection.size() > 0) {
            items.addAll(collection);
            if (originalCount == 0) {
                curatedItems.add(items.get(0));
            }
            for (Tweet tweet: collection) {
                if (!TextUtils.isEmpty(tweet.getMediaURL())) {
                    curatedItems.add(tweet);
                }
            }
            if (originalCount == 0) {
                notifyDataSetChanged();
            } else {
                notifyItemInserted(originalCount + 1);
            }
        }
    }

    public void addAll(int selectedTab, Tweet... items) {
        List<Tweet> tweetList = Arrays.asList(items);
        addAll(selectedTab, Arrays.asList(items));
    }

    public void clear(boolean refresh) {
        items.clear();
        curatedItems.clear();
        if (refresh) {
            notifyDataSetChanged();
        }
    }

    public void remove(int position, boolean refresh) {
        Tweet tweet;
        if (selectedTab == 0) {
            tweet = items.get(position);
        } else {
            tweet = curatedItems.get(position);
        }
        tweet.setMediaURL(null);
        if (tweet != null) {
            curatedItems.remove(position);
        }
        if (refresh) {
            notifyDataSetChanged();
        }
    }

    public Tweet getItem(int position) {
        if (selectedTab == 0) {
            return items.get(position);
        } else {
            return curatedItems.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        if (selectedTab == 0) {
            return items.size();
        } else {
            return curatedItems.size();
        }
    }

    public void setSelectedTab(int selectedTab) {
        this.selectedTab = selectedTab;
    }
}
