package com.codepath.codepathtwitterclient.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.activity.ImageActivity;
import com.codepath.codepathtwitterclient.activity.ProfileActivity;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;
import com.codepath.codepathtwitterclient.model.Tweet;
import com.codepath.codepathtwitterclient.model.User;
import com.codepath.codepathtwitterclient.utils.Utils;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.lang.ref.WeakReference;
import java.security.SecureRandom;

/**
 * Created by vvenkatraman on 12/28/15.
 */
public class ProfileHeadersAdapter extends ProfileAdapter<RecyclerView.ViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    WeakReference<ProfileActivity> profileActivityWeakReference;
    LayoutInflater inflater;
    private float pointOfTouchX = 55.0f;
    private User currentUser;

    public ProfileHeadersAdapter(ProfileActivity testActivity, User currentUser) {
        this.profileActivityWeakReference = new WeakReference<>(testActivity);
        inflater = LayoutInflater.from(testActivity);
        this.currentUser = currentUser;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewType viewTypeObj = ViewType.fromType(viewType);
        View view = null;
        if (viewTypeObj == ViewType.BODY) {
            view = inflater.inflate(R.layout.list_item_tweet, parent, false);
            return new TweetsViewHolder(view);
        } else if (viewTypeObj == ViewType.PROFILE) {
            view = inflater.inflate(R.layout.list_item_profile, parent, false);
            return new ProfileViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.list_item_image, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (profileActivityWeakReference.get() == null) {
            return;
        }
        if (position == 0) {
            bindProfileViews(holder);
        } else if (getSelectedTab() == 0) {
            bindTweetViews(position, (TweetsViewHolder) holder);
        } else {
            final Tweet tweet = getItem(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage(tweet);
                }
            });
            Glide.with(profileActivityWeakReference.get()).load(tweet.mediaURL)
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .centerCrop()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            remove(position, false);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) holder.itemView);
        }
    }

    private void viewImage(Tweet tweet) {
        if (tweet != null) {
            Intent imageIntent = new Intent(profileActivityWeakReference.get(), ImageActivity.class);
            imageIntent.putExtra(ImageActivity.ARG_IMAGE_URI, tweet.getMediaURL());
            profileActivityWeakReference.get().startActivity(imageIntent);
        }
    }


    private void bindTweetViews(final int position, final TweetsViewHolder holder) {
        holder.setFragmentWeakReference(new WeakReference<TweetFragment>(null));
        final Tweet tweet = getItem(position);
        holder.tweet = tweet;
        if (tweet.hasRetweetedUsername()) {
            holder.ivRetweetUserIcon.setVisibility(View.VISIBLE);
            holder.tvUserRetweetName.setVisibility(View.VISIBLE);
            holder.tvUserRetweetName.setText(profileActivityWeakReference.get().getString(R.string.retweeted_user_name,
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
            holder.btnFavorite.setCompoundDrawablesWithIntrinsicBounds(profileActivityWeakReference.get()
                    .getResources().getDrawable(R.drawable.ic_fave_on), null, null, null);
        } else {
            holder.btnFavorite.setCompoundDrawablesWithIntrinsicBounds(profileActivityWeakReference.get()
                    .getResources().getDrawable(R.drawable.ic_fave_off), null, null, null);
        }

        if (tweet.isRetweeted()) {
            holder.btnRetweet.setCompoundDrawablesWithIntrinsicBounds(profileActivityWeakReference.get()
                    .getResources().getDrawable(R.drawable.ic_retweet_on), null, null, null);
        } else {
            holder.btnRetweet.setCompoundDrawablesWithIntrinsicBounds(profileActivityWeakReference.get()
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

        Glide.with(profileActivityWeakReference.get()).load(tweet.getUser().getUrl())
                .thumbnail(1.0f)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivProfilePic);

        if (TextUtils.isEmpty(tweet.getMediaURL()) || !Utils.isNetworkAvailable(profileActivityWeakReference.get())) {
            holder.ivMedia.setVisibility(View.GONE);
        } else {
            holder.ivMedia.setImageResource(R.mipmap.ic_launcher);
            holder.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(profileActivityWeakReference.get()).load(tweet.mediaURL)
                    .asBitmap()
                    .error(R.drawable.ic_error)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivMedia);
        }
    }

    private void bindProfileViews(final RecyclerView.ViewHolder holder) {
        ProfileViewHolder profileViewHolder = (ProfileViewHolder) holder;
        profileViewHolder.user = currentUser;
        Glide.with(profileActivityWeakReference.get()).load(currentUser.getUrl())
                .thumbnail(1.0f)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profileViewHolder.ivProfilePic);

        if (!TextUtils.isEmpty(currentUser.getBgURL())) {
            profileViewHolder.ivBackgroundPic.setVisibility(View.VISIBLE);
            Glide.with(profileActivityWeakReference.get()).load(currentUser.getBgURL())
                    .asBitmap()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileViewHolder.ivBackgroundPic);
        }

        profileViewHolder.tvUserName.setText(currentUser.getName());
        if (currentUser.isVerified()) {
            profileViewHolder.ivProfileVerified.setVisibility(View.GONE);
        } else {
            profileViewHolder.ivProfileVerified.setVisibility(View.VISIBLE);
        }

        profileViewHolder.tvScreenName.setText(currentUser.getScreenName());

        if (TextUtils.isEmpty(currentUser.getDescription())) {
            profileViewHolder.tvDescription.setVisibility(View.GONE);
        } else {
            profileViewHolder.tvDescription.setVisibility(View.VISIBLE);
            profileViewHolder.tvDescription.setText(currentUser.getDescription());
        }

//        if (TextUtils.isEmpty(currentUser.getLocation())) {
//            profileViewHolder.tvLocation.setVisibility(View.GONE);
//        } else {
//            profileViewHolder.tvLocation.setVisibility(View.VISIBLE);
//            profileViewHolder.tvLocation.setText(currentUser.getLocation());
//        }
        if (TextUtils.isEmpty(currentUser.getDisplayURL())) {
            profileViewHolder.tvDisplayURL.setVisibility(View.GONE);
        } else {
            profileViewHolder.tvDisplayURL.setVisibility(View.VISIBLE);
            profileViewHolder.tvDisplayURL.setText(currentUser.getDisplayURL());
        }

        Spanned followingText = Html.fromHtml("<b>" + currentUser.getFollowingCount() + "</b> FOLLOWING");
        Spanned followersText = Html.fromHtml("<b>" + currentUser.getFollowersCount() + "</b> FOLLOWERS");

        profileViewHolder.ivFollowing.setText(followingText, TextView.BufferType.SPANNABLE);

        profileViewHolder.ivFollowers.setText(followersText, TextView.BufferType.SPANNABLE);

        if (currentUser.isCurrentUser()) {
            profileViewHolder.btnEditProfile.setVisibility(View.VISIBLE);
            profileViewHolder.btnFollowingIcon.setVisibility(View.GONE);
        } else {
            profileViewHolder.btnEditProfile.setVisibility(View.GONE);

            profileViewHolder.ivFollowing.setVisibility(View.VISIBLE);
            if (currentUser.isFollowing()) {
                profileViewHolder.btnFollowingIcon.setText("Following");
                profileViewHolder.btnFollowingIcon.setBackgroundResource((R.drawable.rounded_tweet_button));
                profileViewHolder.btnFollowingIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_on, 0, 0, 0);
                profileViewHolder.btnFollowingIcon.setTextColor(profileActivityWeakReference.get().getResources().getColor(android.R.color.white));
            } else {
                profileViewHolder.btnFollowingIcon.setText("Follow");
                profileViewHolder.btnFollowingIcon.setBackgroundResource((R.drawable.rounded_blue_edge_button));
                profileViewHolder.btnFollowingIcon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_off, 0, 0, 0);
                profileViewHolder.btnFollowingIcon.setTextColor(profileActivityWeakReference.get().getResources().getColor(R.color.primary_color));
            }
        }
    }

    @Override
    public long getHeaderId(int position) {
        if (position == 0) {
            return -1;
        } else {
            return getItem(position).getTweetId().charAt(0);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header_item_tabs, parent, false);
        TabLayout slidingTabs = (TabLayout) view.findViewById(R.id.slidingTabs);
        slidingTabs.addTab(slidingTabs.newTab().setText("Tweets"));
        slidingTabs.addTab(slidingTabs.newTab().setText("Media"));
        TabLayout.Tab selectedTab = slidingTabs.getTabAt(getSelectedTab());
        if (selectedTab != null) {
            selectedTab.select();
        }
        return new RecyclerView.ViewHolder(view) {
        };
    }

    public int getSelectedTab() {
        int tabWidth = profileActivityWeakReference.get().getRecyclerView().getWidth() / 2;
        if (tabWidth == 0) {
            return 0;
        }
        return ((int) pointOfTouchX) / tabWidth;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                profileActivityWeakReference.get().getRecyclerView().getAdapter().notifyItemRangeChanged(2,
                        profileActivityWeakReference.get().getRecyclerView().getAdapter().getItemCount() - 2);
            }
        }, 50);
    }

    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }

    public float getPointOfTouchX() {
        return pointOfTouchX;
    }

    public void setPointOfTouchX(float pointOfTouchX) {
        this.pointOfTouchX = pointOfTouchX;
        if (getSelectedTab() != super.selectedTab && profileActivityWeakReference.get() != null) {
            profileActivityWeakReference.get().getRecyclerView().getLayoutManager().scrollToPosition(0);
        }
        super.setSelectedTab(getSelectedTab());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ViewType.PROFILE.getType();
        } else if (getSelectedTab() == 0) {
            return ViewType.BODY.getType();
        } else {
            return ViewType.IMAGE.getType();
        }
    }

    private enum ViewType {
        PROFILE(0), BODY(1), IMAGE(2);
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
            return viewTypeSparseArray.get(type, ViewType.BODY);
        }

        public int getType() {
            return type;
        }
    }
}
