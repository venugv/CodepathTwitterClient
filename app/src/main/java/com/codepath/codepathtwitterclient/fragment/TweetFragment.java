package com.codepath.codepathtwitterclient.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.codepathtwitterclient.CodePathTwitterClientApp;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.activity.LandingPageActivity;
import com.codepath.codepathtwitterclient.adapter.TweetsAdapter;
import com.codepath.codepathtwitterclient.adapter.UsersAdapter;
import com.codepath.codepathtwitterclient.model.Tweet;
import com.codepath.codepathtwitterclient.model.User;
import com.codepath.codepathtwitterclient.restclient.RestClient;
import com.codepath.codepathtwitterclient.utils.EndlessRecyclerOnScrollListener;
import com.codepath.codepathtwitterclient.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by vvenkatraman on 12/13/15.
 */
public class TweetFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ARG_PARAM1 = "type";
    public static final int USER_TWEETS_TYPE = 1 << 2;
    public static final String USER_ID = "user_id";
    public static final String SCREEN_NAME = "screen_name";
    public static final String SEARCH_STRING = "search_string";
    public static final int HOME_TWEETS_TYPE = 1 << 0;
    public static final int MENTIONS_TWEETS_TYPE = 1 << 1;
    public static final int REPLIES_TWEETS_TYPE = 1 << 2;
    private static final String TAG = TweetFragment.class.getName();
    private final CountDownLatch networkRequestDoneSignal = new CountDownLatch(1);
    public JsonHttpResponseHandler tweetsJsonResponseHandler;
    public JsonHttpResponseHandler searchJsonResponseHandler;
    public JsonHttpResponseHandler usersJsonResponseHandler;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerOnScrollListener endlessScrollListener;
    private TweetsAdapter tweetsAdapter;
    private UsersAdapter usersAdapter;
    private int type;
    private String param1;
    private String maxID;
    private String userId;
    private String screenName;
    private String searchString;
    private RestClient restClient;

    // TODO: Rename and change types and number of parameters
    public static TweetFragment newInstance(String param1) {
        TweetFragment fragment = new TweetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param1 = getArguments().getString(ARG_PARAM1);
            if (param1 != null) {
                if (param1.equals("home")) {
                    type = HOME_TWEETS_TYPE;
                } else if (param1.equals("mentions")) {
                    type = MENTIONS_TWEETS_TYPE;
                } else if (param1.equals("user")) {
                    type = USER_TWEETS_TYPE;
                }
            }
            userId = getArguments().getString(USER_ID);
            screenName = getArguments().getString(SCREEN_NAME);
            if (TextUtils.isEmpty(userId)) {
                userId = User.getCurrentUser().getUserId();
                screenName = User.getCurrentUser().getScreenName();
            }
            searchString = getArguments().getString(SEARCH_STRING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        this.swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        endlessScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                // start searching
                if (!TextUtils.isEmpty(maxID)) {
                    loadTweetsFromNetwork(maxID);
                }
            }
        };
        recyclerView.addOnScrollListener(endlessScrollListener);
        recyclerView.setAdapter(null);
        restClient = CodePathTwitterClientApp.getRestClient();
        initResponseHandlers();

        view.clearFocus();

        loadTweetsFromNetwork(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LandingPageActivity) {
            LandingPageActivity activity = (LandingPageActivity) context;
            activity.setTweetFragmentWeakReference(new WeakReference<>(this));
        }
    }

    public void initResponseHandlers() {
        tweetsJsonResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                ArrayList<Tweet> list = Tweet.fromJson(response, type);
                updateTweetsAdapter(list);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Snackbar.make(recyclerView, "Failed to load new tweets from network", Snackbar.LENGTH_LONG).show();
                Log.e(TAG, throwable.toString());
//            usersAdapter.setError(true);
                if (tweetsAdapter != null) {
                    tweetsAdapter.notifyDataSetChanged();
                }
                networkRequestDoneSignal.countDown();
            }
        };

        searchJsonResponseHandler = new JsonHttpResponseHandler() {
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
                ArrayList<Tweet> list = Tweet.fromJson(jsonArray, type);
                updateTweetsAdapter(list);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Failed to load new tweets from network", Toast.LENGTH_LONG).show();
                Log.e(TAG, throwable.toString());
                networkRequestDoneSignal.countDown();
//            usersAdapter.setError(true);
                if (tweetsAdapter != null) {
                    tweetsAdapter.notifyDataSetChanged();
                }
            }
        };

        usersJsonResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "Users response callback array received???????");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray jsonArray = null;
                try {
                    maxID = response.getString("next_cursor");
                    jsonArray = response.getJSONArray("users");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList<User> list = User.fromJson(jsonArray);
                if (TextUtils.isEmpty(maxID) || usersAdapter == null) {
                    usersAdapter = new UsersAdapter(getActivity(), list);
                    recyclerView.setAdapter(usersAdapter);
                    usersAdapter.notifyDataSetChanged();
                } else if (list.isEmpty()) {
                    usersAdapter.notifyDataSetChanged();
                } else {
                    int lastItemPos = usersAdapter.getItemCount();
                    usersAdapter.getUserList().addAll(list);
                    usersAdapter.notifyItemRangeInserted(lastItemPos, list.size());
                }
                networkRequestDoneSignal.countDown();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), "Failed to load new tweets from network", Toast.LENGTH_LONG).show();
                Log.e(TAG, throwable.toString());
                networkRequestDoneSignal.countDown();
//            mUsersAdapter.setError(true);
                usersAdapter.notifyDataSetChanged();
            }
        };


    }

    @Override
    public void onResume() {
        super.onResume();
        endlessScrollListener.reset(0, true);
    }

    @Override
    public void onRefresh() {
        maxID = null;
        loadTweetsFromNetwork(null);
        swipeContainer.setRefreshing(false);
    }

    private void updateTweetsAdapter(List<Tweet> tweetArrayList) {
        endlessScrollListener.setLoading(false);
        tweetsAdapter = (TweetsAdapter) recyclerView.getAdapter();
        if (tweetsAdapter == null) {
            tweetsAdapter = new TweetsAdapter(this, tweetArrayList);
            recyclerView.setAdapter(tweetsAdapter);
        }
        if (TextUtils.isEmpty(maxID)) {
            tweetsAdapter.setTweetList(tweetArrayList);
            tweetsAdapter.notifyDataSetChanged();
        } else {
            int oldSize = tweetsAdapter.getItemCount();
            tweetsAdapter.getTweetList().addAll(tweetArrayList);
            tweetsAdapter.notifyItemRangeInserted(oldSize + 1, tweetArrayList.size());
        }
        if (tweetArrayList.size() > 0) {
            maxID = tweetArrayList.get(tweetArrayList.size() - 1).getTweetID();
        }
    }

    private void loadTweetsFromNetwork(String max) {
        loadTweetsFromNetwork(max, false);
    }

    private void loadTweetsFromNetwork(String max, final boolean await) {
        endlessScrollListener.setLoading(true);
        boolean isActiveNetwork = Utils.isNetworkAvailable(getActivity());

        maxID = max;
        if (param1.equals("home")) {
            if (isActiveNetwork) {
                restClient.getHomeTimeLine(max, tweetsJsonResponseHandler);
            } else {
                List<Tweet> list = Tweet.getTweetsList(maxID, HOME_TWEETS_TYPE);
                updateTweetsAdapter(list);
                Snackbar.make(recyclerView, "No Internet connection. In Offline mode.", Snackbar.LENGTH_LONG).show();
            }
        } else if (param1.equals("mentions")) {
            if (isActiveNetwork) {
                restClient.getMentionsTimeLine(max, tweetsJsonResponseHandler);
            } else {
                List<Tweet> list = Tweet.getTweetsList(maxID, MENTIONS_TWEETS_TYPE);
                updateTweetsAdapter(list);
                Snackbar.make(recyclerView, "No Internet connection. In Offline mode.", Snackbar.LENGTH_LONG).show();
            }
        } else if (param1.equals("user")) {
            if (isActiveNetwork) {
                restClient.getUserTimeLine(userId, max, tweetsJsonResponseHandler);
            } else {
                List<Tweet> list = Tweet.getTweetsList(maxID, USER_TWEETS_TYPE);
                updateTweetsAdapter(list);
                Snackbar.make(recyclerView, "No Internet connection. In Offline mode.", Snackbar.LENGTH_LONG).show();
            }
        } else if (param1.equals("favorites")) {
            if (isActiveNetwork) {
                restClient.getFavoritesTimeLine(userId, max, tweetsJsonResponseHandler);
            } else {
                Snackbar.make(recyclerView, "No Internet connection. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        } else if (param1.equals("friends")) {
            if (isActiveNetwork) {
                restClient.getFriendsTimeLine(userId, screenName, max, usersJsonResponseHandler);
            } else {
                Snackbar.make(recyclerView, "No Internet connection. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        } else if (param1.equals("followers")) {
            if (isActiveNetwork) {
                restClient.getFollowersTimeLine(userId, screenName, max, usersJsonResponseHandler);
            } else {
                Snackbar.make(recyclerView, "No Internet connection. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        } else if (param1.equals("search_top")) {
            if (isActiveNetwork) {
                restClient.getSearchTopTweets(searchString, max, searchJsonResponseHandler);
            } else {
                Snackbar.make(recyclerView, "No Internet connection. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        } else if (param1.equals("search_all")) {
            if (isActiveNetwork) {
                restClient.getSearchAllTweets(searchString, max, searchJsonResponseHandler);
            } else {
                Snackbar.make(recyclerView, "No Internet connection. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        }

        try {
            if (await) {
                networkRequestDoneSignal.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
