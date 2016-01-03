package com.codepath.codepathtwitterclient.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.codepathtwitterclient.CodePathTwitterClientApp;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.adapter.TweetDetailAdapter;
import com.codepath.codepathtwitterclient.fragment.CreateTweetFragment;
import com.codepath.codepathtwitterclient.fragment.ImageDialogFragment;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;
import com.codepath.codepathtwitterclient.model.Tweet;
import com.codepath.codepathtwitterclient.restclient.RestClient;
import com.codepath.codepathtwitterclient.utils.EndlessRecyclerOnScrollListener;
import com.codepath.codepathtwitterclient.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TweetDetailActivity extends AppCompatActivity implements CreateTweetFragment.onTweetListener,
        ImageDialogFragment.OnFragmentInteractionListener {

    private static final String TAG = TweetDetailActivity.class.getName().toString();
    private Tweet tweet;
    private JsonHttpResponseHandler searchJSONResponseHandler;
    private JsonHttpResponseHandler tweetJSONReponseHandler;
    private RecyclerView recyclerView;
    private EndlessRecyclerOnScrollListener endlessScrollListener;
    private String maxID;
    private RestClient restClient;
    private TweetDetailAdapter tweetDetailAdapter;

    private void initResponseHandlers() {
        tweetJSONReponseHandler = new

                JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                            errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(TweetDetailActivity.this, "Error downloading tweet details. Please try again later.", Toast.LENGTH_LONG).show();
                        finish();
                        Log.e(TAG, "Error loading tweet details." + throwable.getMessage(), throwable);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        tweet = Tweet.fromJson(response, -1);
                        populateViews();
                    }
                };

        searchJSONResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "Search response callback array received???????");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = response.getJSONArray("statuses");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList<Tweet> list = Tweet.fromJson(jsonArray, TweetFragment.REPLIES_TWEETS_TYPE);
                if (list == null || list.size() == 0) {
                    return;
                }
                List<Tweet> replyToList = new ArrayList<>();
                for (Tweet tweetObj: list) {
                    if (!TextUtils.isEmpty(tweetObj.getInReplyTo()) &&
                            tweetObj.getInReplyTo().trim().equalsIgnoreCase(tweet.getTweetID())) {
                        replyToList.add(tweetObj);
                    }
                }
                updateReplies(replyToList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TweetDetailActivity.this, "Failed to load new tweets from network", Toast.LENGTH_LONG).show();
                Log.e(TAG, throwable.toString());
                if (tweetDetailAdapter != null) {
                    tweetDetailAdapter.notifyDataSetChanged();
                }
            }
        };

    }

    private void updateReplies(List<Tweet> tweetList) {
        tweetDetailAdapter.addAll(tweetList, true);
        if (tweetList.size() > 0) {
            maxID = tweetList.get(tweetList.size() - 1).getTweetID();
        }
    }

    private void populateViews() {
        tweetDetailAdapter = new TweetDetailAdapter(this, tweetJSONReponseHandler);
        recyclerView.setAdapter(tweetDetailAdapter);
        tweetDetailAdapter.add(tweet, true);
        loadReplies();
    }

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

        maxID = null;
        restClient = CodePathTwitterClientApp.getRestClient();

        Intent intent = getIntent();
        String tweetID = intent.getStringExtra("tweet_id");
        if (tweetID == null || tweetID.isEmpty()) {
            Toast.makeText(this, "Error downloading tweet details. Please try again later.", Toast.LENGTH_LONG).show();
            finish();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        endlessScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                // start searching
                if (!TextUtils.isEmpty(maxID)) {
                    loadReplies();
                }
            }
        };
        recyclerView.addOnScrollListener(endlessScrollListener);
        recyclerView.setAdapter(null);
        initResponseHandlers();
        if (!Utils.isNetworkAvailable(this)) {
            tweet = Tweet.getTweet(tweetID);
        } else {
            restClient.getTweetDetails(tweetID, tweetJSONReponseHandler);
        }
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void loadReplies() {
        if (Utils.isNetworkAvailable(this)) {
            restClient.getReplyToTweets(tweet.getTweetID(), tweet.getUser().getScreenName(),
                    maxID, searchJSONResponseHandler);
        } else {
            Snackbar.make(recyclerView, "No Internet connection. Please try again later.", Snackbar.LENGTH_LONG).show();
        }
    }
}
