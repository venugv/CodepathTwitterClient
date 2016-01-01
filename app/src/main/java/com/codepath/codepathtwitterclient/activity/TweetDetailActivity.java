package com.codepath.codepathtwitterclient.activity;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.codepathtwitterclient.CodePathTwitterClientApp;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.fragment.CreateTweetFragment;
import com.codepath.codepathtwitterclient.fragment.ImageDialogFragment;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;
import com.codepath.codepathtwitterclient.model.Tweet;
import com.codepath.codepathtwitterclient.model.User;
import com.codepath.codepathtwitterclient.restclient.RestClient;
import com.codepath.codepathtwitterclient.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class TweetDetailActivity extends AppCompatActivity implements CreateTweetFragment.onTweetListener,
    ImageDialogFragment.OnFragmentInteractionListener {

    private static final String TAG = TweetDetailActivity.class.getName().toString();
    private ImageView ivRetweetIcon;
    private ImageView ivProfilePic;
    private TextView tvUserRetweetName;
    private TextView tvUserName;
    private TextView tvUserNameHandle;
    private TextView tvBodyText;
    private TextView tvCreatedAtTime;
    private ImageButton btnReply;
    private ImageButton btnRetweet;
    private ImageButton btnFavorite;
    private ImageButton btnFollowing;
    private TextView tvRetweets;
    private TextView tvFavorites;
    private ImageView ivVerified;
    private ImageButton btnShare;
    private ImageButton btnDelete;
    private User loggedUser;
    private ImageView ivMedia;
    private Tweet tweet;
    private RecyclerView recyclerView;

    public JsonHttpResponseHandler tweetJSONReponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            try {
                new AlertDialog.Builder(TweetDetailActivity.this).setTitle("Error").setMessage(errorResponse.getString("errors")).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "Error loading tweet details." + throwable.getMessage());
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            tweet = Tweet.fromJson(response, -1);
            populateViews(tweet);
        }
    };
    private RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition fadeTransform = TransitionInflater.from(this).
                    inflateTransition(android.R.transition.fade);
            fadeTransform.setStartDelay(0);
            fadeTransform.setDuration(500);
            getWindow().setEnterTransition(fadeTransform);
            getWindow().setExitTransition(fadeTransform);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setIcon(R.drawable.ic_tweet_white);
        }

        setupViews();

        restClient = CodePathTwitterClientApp.getRestClient();

        Intent intent = getIntent();
        String tweetID = intent.getStringExtra("tweet_id");
        if (tweetID == null || tweetID.isEmpty()) {
            Toast.makeText(this, "Error downloading tweet details. Please try again later.", Toast.LENGTH_LONG).show();
            finish();
        }

        loggedUser = User.getCurrentUser();

        if (!Utils.isNetworkAvailable(this)) {
            tweet = Tweet.getTweet(tweetID);
            populateViews(tweet);
        } else {
            restClient.getTweetDetails(tweetID, tweetJSONReponseHandler);
        }

        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(tweet.getUser().getUserId());
            }
        });
    }

    private void startProfileActivity(String userID) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user_id", userID);
        startActivity(intent);

    }

    private void populateViews(Tweet tweet) {
        if (tweet.hasRetweetedUsername()) {
            ivRetweetIcon.setVisibility(View.VISIBLE);
            tvUserRetweetName.setVisibility(View.VISIBLE);
            tvUserRetweetName.setText(getString(R.string.retweeted_user_name
                    , tweet.getRetweetedUserName()));
        } else {
            ivRetweetIcon.setVisibility(View.GONE);
            tvUserRetweetName.setVisibility(View.GONE);
        }

        if (tweet.getUser().isFollowing()) {
            btnFollowing.setVisibility(View.INVISIBLE);
        } else {
            btnFollowing.setVisibility(View.VISIBLE);
        }

        if (tweet.isFavorited()) {
            btnFavorite.setImageResource(R.drawable.ic_fave_on);
        } else {
            btnFavorite.setImageResource(R.drawable.ic_fave_off);
        }

        if (tweet.isRetweeted()) {
            btnRetweet.setImageResource(R.drawable.ic_retweet_on);
        } else {
            btnRetweet.setImageResource(R.drawable.ic_retweet_off);
        }

        tvUserName.setText(tweet.getUser().getName());
        tvUserNameHandle.setText(tweet.getUser().getScreenName());
        tvCreatedAtTime.setText(tweet.getTimeStamp());
        tvBodyText.setText(Html.fromHtml(tweet.getBody()));

        String rtCount = tweet.getRetweetCount();
        tvRetweets.setText(Html.fromHtml(rtCount.equals("1") ? "<b>" + rtCount + "</b> RETWEET" : "<b>" + rtCount + "</b> RETWEETS"));
        String rtFav = tweet.getFavoriteCount();
        tvFavorites.setText(Html.fromHtml(rtFav.equals("1") ? "<b>" + rtFav + "</b> FAVORITE" : "<b>" + rtFav + "</b> FAVORITES"));

        Glide.with(this).load(tweet.getUser().getUrl())
                .thumbnail(1.0f)
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivProfilePic);

        if (tweet.getUser().isVerified()) {
            ivVerified.setVisibility(View.VISIBLE);
        } else {
            ivVerified.setVisibility(View.GONE);
        }

        if (loggedUser != null && loggedUser.getUserId() == tweet.getUser().getUserId()) {
            btnDelete.setVisibility(View.VISIBLE);
            btnFollowing.setVisibility(View.GONE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(tweet.getMediaURL())) {
            ivMedia.setVisibility(View.GONE);
        } else {
            ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.mediaURL)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivMedia);
        }

    }

    private void setupViews() {

        ivRetweetIcon = (ImageView) findViewById(R.id.ivRetweetUserIcon);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        tvUserRetweetName = (TextView) findViewById(R.id.tvUserRetweetName);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserNameHandle = (TextView) findViewById(R.id.tvUserNameHandle);
        tvBodyText = (TextView) findViewById(R.id.tvBodyText);
        tvCreatedAtTime = (TextView) findViewById(R.id.tvTimeStamp);
        btnReply = (ImageButton) findViewById(R.id.btnReply);
        btnRetweet = (ImageButton) findViewById(R.id.btnRetweet);
        btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(TweetDetailActivity.this);
                dialog.setTitle("Retweet").setMessage("Retweet this to you followers?")
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
                                fragment.show(getFragmentManager(), "dialog");
                            }
                        }).show();
            }
        });
        btnFavorite = (ImageButton) findViewById(R.id.btnFavorite);
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet != null) {
                    if (tweet.isFavorited()) {
                        restClient.postUnFavoriteTweetUpdate(tweet.getTweetId(), tweetJSONReponseHandler);
                    } else {
                        restClient.postFavoriteTweetUpdate(tweet.getTweetId(), tweetJSONReponseHandler);
                    }
                }
            }
        });
        btnShare = (ImageButton) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareItem(tweet);
            }
        });
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet != null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TweetDetailActivity.this);
                    dialog.setTitle("Delete").setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    restClient.deleteTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            Tweet.delete(Tweet.class, tweet.getId());
                                            onBackPressed();
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
            }
        });
        tvRetweets = (TextView) findViewById(R.id.tvRetweets);
        tvFavorites = (TextView) findViewById(R.id.tvFavorites);
        ivVerified = (ImageView) findViewById(R.id.ivVerified);
        btnFollowing = (ImageButton) findViewById(R.id.btnFollow);
        ivMedia = (ImageView) findViewById(R.id.ivMedia);

        ivMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet != null) {
                    Intent imageIntent = new Intent(TweetDetailActivity.this, ImageActivity.class);
                    imageIntent.putExtra(ImageActivity.ARG_IMAGE_URI, tweet.getMediaURL());
                    startActivity(imageIntent);
                }
            }
        });

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = tvUserNameHandle.getText().toString();
                DialogFragment fragment = CreateTweetFragment.getInstance(status);
                fragment.show(getFragmentManager(), "dialog");
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTweetSubmit() {
        //DO NOTHING
    }

    // Can be triggered by a view event such as a button press
    public void onShareItem(Tweet tweet) {
        if (tweet != null) {
            String text = "Check out " + tweet.getUser().getScreenName() + "'s Tweet: https://twitter.com/" + tweet.getUser().getScreenName().substring(1) + "/status/" + tweet.getTweetId();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(sendIntent, "Share Tweet"));
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
