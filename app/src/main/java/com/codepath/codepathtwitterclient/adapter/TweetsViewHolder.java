package com.codepath.codepathtwitterclient.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.codepathtwitterclient.CodePathTwitterClientApp;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.activity.ImageActivity;
import com.codepath.codepathtwitterclient.activity.ProfileActivity;
import com.codepath.codepathtwitterclient.activity.TweetDetailActivity;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;
import com.codepath.codepathtwitterclient.model.Tweet;
import com.codepath.codepathtwitterclient.restclient.RestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.lang.ref.WeakReference;

/**
 * Created by vvenkatraman on 12/15/15.
 */
public class TweetsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static final String FAVORITE = "favorite";
    private static final String TAG = TweetsViewHolder.class.getName();
    ImageView ivRetweetUserIcon;
    ImageView ivProfilePic;
    TextView tvUserRetweetName;
    TextView tvUserName;
    TextView tvUserNameHandle;
    TextView tvBodyText;
    TextView tvCreatedAtTime;
    Button btnReply;
    Button btnRetweet;
    Button btnFavorite;
    Button btnFollow;
    ImageView ivMedia;
    Tweet tweet;
    AsyncHttpResponseHandler responseHandler;
    String action = null;
    private WeakReference<TweetFragment> fragmentWeakReference;

    public TweetsViewHolder(View itemView) {
        super(itemView);
        this.ivRetweetUserIcon = (ImageView) itemView.findViewById(R.id.ivRetweetUserIcon);
        this.ivProfilePic = (ImageView) itemView.findViewById(R.id.ivProfilePic);
        if (this.ivProfilePic != null) {
            this.ivProfilePic.setOnClickListener(this);
        }
        this.tvUserRetweetName = (TextView) itemView.findViewById(R.id.tvUserRetweetName);
        this.tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
        this.tvUserNameHandle = (TextView) itemView.findViewById(R.id.tvUserNameHandle);
        this.tvBodyText = (TextView) itemView.findViewById(R.id.tvBodyText);
        this.tvCreatedAtTime = (TextView) itemView.findViewById(R.id.tvCreatedAtTime);
        this.btnReply = (Button) itemView.findViewById(R.id.btnReply);
        setOnClickListener(this.btnReply);
        this.btnRetweet = (Button) itemView.findViewById(R.id.btnRetweet);
        setOnClickListener(this.btnRetweet);
        this.btnFavorite = (Button) itemView.findViewById(R.id.btnFavorite);
        setOnClickListener(this.btnFavorite);
        this.btnFollow = (Button) itemView.findViewById(R.id.btnFollow);
        setOnClickListener(this.btnFollow);
        this.ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);
        setOnClickListener(this.ivMedia);

        this.itemView.setOnClickListener(this);
        initResponseHandler();
    }

    void initResponseHandler() {
        this.responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                updateUI();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(itemView, "Failed to update", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, error.toString());
            }
        };
    }

    void setOnClickListener(View v) {
        if (v != null) {
            v.setOnClickListener(this);
        }
    }

    private void updateUI() {
        if (TextUtils.isEmpty(action)) {
            return;
        }
        switch (action) {
            case FAVORITE:
                Drawable img = null;
                if (tweet.favorited) {
                    tweet.favorited = false;
                    img = itemView.getContext().getResources().getDrawable(R.drawable.ic_fave_off);
                } else {
                    tweet.favorited = true;
                    img = itemView.getContext().getResources().getDrawable(R.drawable.ic_fave_off);
                }
                this.btnFavorite.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                break;
            default:
                break;
        }
        if (fragmentWeakReference.get() != null) {
            fragmentWeakReference.get().getRecyclerView().getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivProfilePic:
                startProfileActivity(tweet.getUser().getUserId());
                break;
            case R.id.btnReply:
                break;
            case R.id.btnRetweet:
                break;
            case R.id.btnFavorite:
                action = FAVORITE;
//                toggleFavorite();
                break;
            case R.id.btnFollow:
                break;
            case R.id.ivMedia:
                viewImage();
                break;
            default:
                startTweetDetailsActivity();
                break;
        }
    }

    private void startTweetDetailsActivity() {
        String tweetID = tweet.getTweetID();
        Intent intent = new Intent(itemView.getContext(), TweetDetailActivity.class);
        intent.putExtra("tweet_id", tweetID);
        itemView.getContext().startActivity(intent);
    }

    private void viewImage() {

        if (tweet != null) {
            Intent imageIntent = new Intent(itemView.getContext(), ImageActivity.class);
            imageIntent.putExtra(ImageActivity.ARG_IMAGE_URI, tweet.getMediaURL());
            itemView.getContext().startActivity(imageIntent);
        }
    }

    private void toggleFavorite() {
        RestClient client = CodePathTwitterClientApp.getRestClient();
        if (tweet.favorited) {
            client.postFavoriteTweetUpdate(tweet.getTweetID(), responseHandler);
        } else {
            client.postUnFavoriteTweetUpdate(tweet.getTweetID(), responseHandler);
        }
    }

    private void startProfileActivity(String userID) {
        Intent intent = new Intent(this.itemView.getContext(), ProfileActivity.class);
        intent.putExtra("user_id", userID);
        this.itemView.getContext().startActivity(intent);
    }

    public void setFragmentWeakReference(WeakReference<TweetFragment> fragmentWeakReference) {
        this.fragmentWeakReference = fragmentWeakReference;
    }
}
