package com.codepath.codepathtwitterclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.activity.TweetDetailActivity;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;
import com.codepath.codepathtwitterclient.model.Tweet;
import com.codepath.codepathtwitterclient.model.User;
import com.codepath.codepathtwitterclient.utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vvenkatraman on 1/2/16.
 */
public class TweetDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Tweet> tweetList = new ArrayList<>();
    WeakReference<TweetDetailActivity> activityWeakReference;
    LayoutInflater inflater;
    JsonHttpResponseHandler tweetJsonResponseHandler;

    public TweetDetailAdapter (TweetDetailActivity activity, JsonHttpResponseHandler tweetJsonResponseHandler) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.inflater = LayoutInflater.from(activity);
        this.tweetJsonResponseHandler = tweetJsonResponseHandler;
    }

    private enum ViewType {
        DETAIL(0), REPLY(1);
        private static final SparseArray<ViewType> viewTypeSparseArray = new SparseArray<>();

        static {
            for (ViewType viewType : ViewType.values()) {
                viewTypeSparseArray.put(viewType.type, viewType);
            }
        }

        private int type;

        private ViewType(int type) {
            this.type = type;
        }

        private static ViewType fromType(int type) {
            return viewTypeSparseArray.get(type, ViewType.REPLY);
        }

        public int getType() {
            return type;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewType viewTypeObj = ViewType.fromType(viewType);
        if (viewTypeObj == ViewType.DETAIL) {
            View itemView = inflater.inflate(R.layout.list_item_tweet_detail, parent, false);
            return new TweetDetailViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.list_item_tweet, parent, false);
            return new TweetsViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (activityWeakReference.get() == null) {
            return;
        }
        if (position == 0) {
            TweetDetailViewHolder viewHolder = (TweetDetailViewHolder) holder;
            bindTweetDetailViews(position, viewHolder);
        } else {
            TweetsViewHolder viewHolder = (TweetsViewHolder) holder;
            bindTweetViews(position, viewHolder);
        }
    }

    private void bindTweetDetailViews(int position, TweetDetailViewHolder viewHolder) {
        Tweet tweet = tweetList.get(position);
        viewHolder.tweet = tweet;
        viewHolder.activityWeakReference = activityWeakReference;
        viewHolder.tweetJSONReponseHandler = this.tweetJsonResponseHandler;
        if (tweet.hasRetweetedUsername()) {
            viewHolder.ivRetweetIcon.setVisibility(View.VISIBLE);
            viewHolder.tvUserRetweetName.setVisibility(View.VISIBLE);
            viewHolder.tvUserRetweetName.setText(activityWeakReference.get().getString(R.string.retweeted_user_name
                    , tweet.getRetweetedUserName()));
        } else {
            viewHolder.ivRetweetIcon.setVisibility(View.GONE);
            viewHolder.tvUserRetweetName.setVisibility(View.GONE);
        }

        if (tweet.getUser().isFollowing()) {
            viewHolder.btnFollowing.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.btnFollowing.setVisibility(View.VISIBLE);
        }

        if (tweet.isFavorited()) {
            viewHolder.btnFavorite.setImageResource(R.drawable.ic_fave_on);
        } else {
            viewHolder.btnFavorite.setImageResource(R.drawable.ic_fave_off);
        }

        if (tweet.isRetweeted()) {
            viewHolder.btnRetweet.setImageResource(R.drawable.ic_retweet_on);
        } else {
            viewHolder.btnRetweet.setImageResource(R.drawable.ic_retweet_off);
        }

        viewHolder.tvUserName.setText(tweet.getUser().getName());
        viewHolder.tvUserNameHandle.setText(tweet.getUser().getScreenName());
        viewHolder.tvCreatedAtTime.setText(tweet.getTimeStamp());
        viewHolder.tvBodyText.setText(Html.fromHtml(tweet.getBody()));

        String rtCount = tweet.getRetweetCount();
        viewHolder.tvRetweets.setText(Html.fromHtml(rtCount.equals("1") ? "<b>" + rtCount + "</b> RETWEET" : "<b>" + rtCount + "</b> RETWEETS"));
        String rtFav = tweet.getFavoriteCount();
        viewHolder.tvFavorites.setText(Html.fromHtml(rtFav.equals("1") ? "<b>" + rtFav + "</b> FAVORITE" : "<b>" + rtFav + "</b> FAVORITES"));

        Glide.with(activityWeakReference.get()).load(tweet.getUser().getUrl())
                .thumbnail(1.0f)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.ivProfilePic);

        if (tweet.getUser().isVerified()) {
            viewHolder.ivVerified.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivVerified.setVisibility(View.GONE);
        }

        User user = User.getCurrentUser();
        if (user != null && user.getUserId() == tweet.getUser().getUserId()) {
            viewHolder.btnDelete.setVisibility(View.VISIBLE);
            viewHolder.btnFollowing.setVisibility(View.GONE);
        } else {
            viewHolder.btnDelete.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(tweet.getMediaURL())) {
            viewHolder.ivMedia.setVisibility(View.GONE);
        } else {
            viewHolder.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(activityWeakReference.get()).load(tweet.mediaURL)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.ivMedia);
        }

    }

    private void bindTweetViews(final int position, final TweetsViewHolder holder) {
        holder.setFragmentWeakReference(new WeakReference<TweetFragment>(null));
        final Tweet tweet = getItem(position);
        holder.tweet = tweet;
        if (tweet.hasRetweetedUsername()) {
            holder.ivRetweetUserIcon.setVisibility(View.VISIBLE);
            holder.tvUserRetweetName.setVisibility(View.VISIBLE);
            holder.tvUserRetweetName.setText(activityWeakReference.get().getString(R.string.retweeted_user_name,
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
            holder.btnFavorite.setCompoundDrawablesWithIntrinsicBounds(activityWeakReference.get()
                    .getResources().getDrawable(R.drawable.ic_fave_on), null, null, null);
        } else {
            holder.btnFavorite.setCompoundDrawablesWithIntrinsicBounds(activityWeakReference.get()
                    .getResources().getDrawable(R.drawable.ic_fave_off), null, null, null);
        }

        if (tweet.isRetweeted()) {
            holder.btnRetweet.setCompoundDrawablesWithIntrinsicBounds(activityWeakReference.get()
                    .getResources().getDrawable(R.drawable.ic_retweet_on), null, null, null);
        } else {
            holder.btnRetweet.setCompoundDrawablesWithIntrinsicBounds(activityWeakReference.get()
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

        Glide.with(activityWeakReference.get()).load(tweet.getUser().getUrl())
                .thumbnail(1.0f)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivProfilePic);

        if (TextUtils.isEmpty(tweet.getMediaURL()) || !Utils.isNetworkAvailable(activityWeakReference.get())) {
            holder.ivMedia.setVisibility(View.GONE);
        } else {
            holder.ivMedia.setImageResource(R.mipmap.ic_launcher);
            holder.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(activityWeakReference.get()).load(tweet.mediaURL)
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivMedia);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ViewType.DETAIL.getType();
        } else {
            return ViewType.REPLY.getType();
        }
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    public void add(Tweet tweet, boolean refresh) {
        tweetList.add(tweet);
        if (refresh) {
            notifyDataSetChanged();
        }
    }

    public void clear() {
        tweetList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweetList, boolean refresh) {
        if (tweetList == null || tweetList.size() == 0) {
            return;
        }

        int oldSize = getItemCount();
        this.tweetList.addAll(tweetList);
        if (refresh) {
            if (oldSize == 1) {
                notifyDataSetChanged();
            } else {
                notifyItemRangeInserted(oldSize + 1, tweetList.size());
            }
        }
    }

    public Tweet getItem(int position) {
        return tweetList.get(position);
    }
}
