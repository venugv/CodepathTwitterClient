package com.codepath.codepathtwitterclient.adapter;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.codepathtwitterclient.CodePathTwitterClientApp;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.activity.ImageActivity;
import com.codepath.codepathtwitterclient.activity.ProfileActivity;
import com.codepath.codepathtwitterclient.activity.TweetDetailActivity;
import com.codepath.codepathtwitterclient.fragment.CreateTweetFragment;
import com.codepath.codepathtwitterclient.model.Tweet;
import com.codepath.codepathtwitterclient.model.User;
import com.codepath.codepathtwitterclient.restclient.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by vvenkatraman on 1/2/16.
 */
public class TweetDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView ivRetweetIcon;
    public ImageView ivProfilePic;
    public TextView tvUserRetweetName;
    public TextView tvUserName;
    public TextView tvUserNameHandle;
    public TextView tvBodyText;
    public TextView tvCreatedAtTime;
    public ImageButton btnReply;
    public ImageButton btnRetweet;
    public ImageButton btnFavorite;
    public ImageButton btnFollowing;
    public TextView tvRetweets;
    public TextView tvFavorites;
    public ImageView ivVerified;
    public ImageButton btnShare;
    public ImageButton btnDelete;
    public User loggedUser;
    public ImageView ivMedia;
    public Tweet tweet;
    private RestClient restClient;
    public JsonHttpResponseHandler tweetJSONReponseHandler;
    public WeakReference<TweetDetailActivity> activityWeakReference;
    public TweetDetailViewHolder(final View itemView) {
        super(itemView);
        restClient = CodePathTwitterClientApp.getRestClient();
        ivRetweetIcon = (ImageView) itemView.findViewById(R.id.ivRetweetUserIcon);
        ivProfilePic = (ImageView) itemView.findViewById(R.id.ivProfilePic);
        ivProfilePic.setOnClickListener(this);
        tvUserRetweetName = (TextView) itemView.findViewById(R.id.tvUserRetweetName);
        tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
        tvUserNameHandle = (TextView) itemView.findViewById(R.id.tvUserNameHandle);
        tvBodyText = (TextView) itemView.findViewById(R.id.tvBodyText);
        tvCreatedAtTime = (TextView) itemView.findViewById(R.id.tvTimeStamp);
        btnReply = (ImageButton) itemView.findViewById(R.id.btnReply);
        btnRetweet = (ImageButton) itemView.findViewById(R.id.btnRetweet);
        btnRetweet.setOnClickListener(this);
        btnFavorite = (ImageButton) itemView.findViewById(R.id.btnFavorite);
        btnFavorite.setOnClickListener(this);
        btnShare = (ImageButton) itemView.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);
        btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        tvRetweets = (TextView) itemView.findViewById(R.id.tvRetweets);
        tvFavorites = (TextView) itemView.findViewById(R.id.tvFavorites);
        ivVerified = (ImageView) itemView.findViewById(R.id.ivVerified);
        btnFollowing = (ImageButton) itemView.findViewById(R.id.btnFollow);
        ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);
        ivMedia.setOnClickListener(this);
        btnReply.setOnClickListener(this);
    }

    public void onShareItem(TweetDetailActivity activity) {
        if (tweet != null) {
            String text = "Check out " + tweet.getUser().getScreenName() + "'s Tweet: https://twitter.com/" + tweet.getUser().getScreenName().substring(1) + "/status/" + tweet.getTweetId();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            // Launch sharing dialog for image
            activity.startActivity(Intent.createChooser(sendIntent, "Share Tweet"));
        }
    }

    private void retweet(final TweetDetailActivity activity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("Retweet").setMessage("Retweet this to your followers?")
                .setPositiveButton("Retweet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restClient.postReTweet(tweet.getTweetId(), tweetJSONReponseHandler);
                    }
                })
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Quote", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String status = "\"" + tvUserNameHandle.getText().toString() + ": " + tvBodyText.getText().toString() + "\"";
                        DialogFragment fragment = CreateTweetFragment.getInstance(status);
                        fragment.show(activity.getFragmentManager(), "dialog");
                    }
                }).show();
    }

    private void deleteTweet(final TweetDetailActivity activity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("Delete").setMessage("Are you sure you want to delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restClient.deleteTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Tweet.delete(Tweet.class, tweet.getId());
                                activity.onBackPressed();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                                super.onFailure(statusCode, headers, throwable, jsonObject);
                                Snackbar.make(btnDelete, "Failed to delete tweet. Please try again later", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", null).show();
    }

    private void favoriteTweet() {
        if (tweet.isFavorited()) {
            restClient.postUnFavoriteTweetUpdate(tweet.getTweetId(), tweetJSONReponseHandler);
        } else {
            restClient.postFavoriteTweetUpdate(tweet.getTweetId(), tweetJSONReponseHandler);
        }
    }

    private void displayImage(TweetDetailActivity activity) {
        Intent imageIntent = new Intent(itemView.getContext(), ImageActivity.class);
        imageIntent.putExtra(ImageActivity.ARG_IMAGE_URI, tweet.getMediaURL());
        activity.startActivity(imageIntent);
    }

    private void replyTweet(TweetDetailActivity activity) {
        String status = tvUserNameHandle.getText().toString();
        DialogFragment fragment = CreateTweetFragment.getInstance(status);
        fragment.show(activity.getFragmentManager(), "dialog");
    }

    private void startProfileActivity(TweetDetailActivity activity, String userID) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra("user_id", userID);
        activity.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        if (activityWeakReference.get() == null || tweet == null) {
            return;
        }
        TweetDetailActivity activity = activityWeakReference.get();
        switch (v.getId()) {
            case R.id.btnRetweet:
                retweet(activity);
                break;
            case R.id.btnDelete:
                deleteTweet(activity);
                break;
            case R.id.btnFavorite:
                favoriteTweet();
                break;
            case R.id.ivMedia:
                displayImage(activity);
                break;
            case R.id.btnReply:
                replyTweet(activity);
                break;
            case R.id.btnShare:
                onShareItem(activity);
                break;
            case R.id.ivProfilePic:
                startProfileActivity(activity, tweet.getUser().getUserId());
                break;
        }
    }
}
