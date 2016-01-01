package com.codepath.codepathtwitterclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;
import com.codepath.codepathtwitterclient.model.Tweet;
import com.codepath.codepathtwitterclient.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by vvenkatraman on 12/16/15.
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsViewHolder> {
    private TweetFragment fragment;
    private List<Tweet> tweetList;
    private LayoutInflater inflater;

    public TweetsAdapter(TweetFragment fragment, List<Tweet> tweetList) {
        this.fragment = fragment;
        this.tweetList = tweetList;
        this.inflater = LayoutInflater.from(this.fragment.getActivity());
    }

    @Override
    public TweetsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_item_tweet, parent, false);
        return new TweetsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TweetsViewHolder holder, int position) {
        holder.setFragmentWeakReference(new WeakReference<>(this.fragment));
        final Tweet tweet = tweetList.get(position);
        holder.tweet = tweet;
        if (tweet.hasRetweetedUsername()) {
            holder.ivRetweetUserIcon.setVisibility(View.VISIBLE);
            holder.tvUserRetweetName.setVisibility(View.VISIBLE);
            holder.tvUserRetweetName.setText(fragment.getString(R.string.retweeted_user_name,
                    tweet.getRetweetedUserName()));
        } else {
            holder.ivRetweetUserIcon.setVisibility(View.GONE);
            holder.tvUserRetweetName.setVisibility(View.GONE);
        }

        if (tweet.getUser().isFollowing() || tweet.getUser().isCurrentUser()) {
            holder.btnFollow.setVisibility(View.INVISIBLE);
        } else {
            holder.btnFollow.setVisibility(View.VISIBLE);
        }

        if (tweet.isFavorited()) {
            holder.btnFavorite.setCompoundDrawablesWithIntrinsicBounds(fragment
                    .getResources().getDrawable(R.drawable.ic_fave_on), null, null, null);
        } else {
            holder.btnFavorite.setCompoundDrawablesWithIntrinsicBounds(fragment
                    .getResources().getDrawable(R.drawable.ic_fave_off), null, null, null);
        }

        if (tweet.isRetweeted()) {
            holder.btnRetweet.setCompoundDrawablesWithIntrinsicBounds(fragment
                    .getResources().getDrawable(R.drawable.ic_retweet_on), null, null, null);
        } else {
            holder.btnRetweet.setCompoundDrawablesWithIntrinsicBounds(fragment
                    .getResources().getDrawable(R.drawable.ic_retweet_off), null, null, null);

            if (tweet.getUser().isCurrentUser()) {
                holder.btnRetweet.setAlpha(0.5f);
                holder.btnRetweet.setEnabled(false);
            } else {
                holder.btnRetweet.setAlpha(1.0f);
                holder.btnRetweet.setEnabled(true);
            }
        }

        holder.tvUserName.setText(tweet.getUser().getName());
        holder.tvUserNameHandle.setText(tweet.getUser().getScreenName());
        holder.tvCreatedAtTime.setText(tweet.getCreatedAt());
        holder.tvBodyText.setText(Html.fromHtml(tweet.getBody()));

        holder.btnRetweet.setText(tweet.getRetweetCount());
        holder.btnFavorite.setText(tweet.getFavoriteCount());

        Glide.with(fragment).load(tweet.getUser().getUrl())
                .thumbnail(1.0f)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivProfilePic);

        if (TextUtils.isEmpty(tweet.getMediaURL()) || !Utils.isNetworkAvailable(fragment.getContext())) {
            holder.ivMedia.setVisibility(View.GONE);
        } else {
            holder.ivMedia.setImageResource(R.mipmap.ic_launcher);
            holder.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(fragment).load(tweet.mediaURL)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivMedia);
        }
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    public List<Tweet> getTweetList() {
        return tweetList;
    }

    public void setTweetList(List<Tweet> tweetList) {
        this.tweetList = tweetList;
    }
}
