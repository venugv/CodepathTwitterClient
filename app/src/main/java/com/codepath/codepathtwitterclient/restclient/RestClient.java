package com.codepath.codepathtwitterclient.restclient;

/**
 * Created by vvenkatraman on 12/14/15.
 */


import android.content.Context;
import android.text.TextUtils;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import java.util.LinkedList;
import java.util.List;


/*
 *
 * This is the object responsible for communicating with a REST API.
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes:
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 *
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 *
 */
public class RestClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "TWHrjhWZf4NSeq2pvm4j4gf0j";       // Change this
    public static final String REST_CONSUMER_SECRET = "Oa4HmotrNjm83vhHv3tlvFkXD8gpElKpcbH6RG5CrniTGJ6Q9K"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://cptwitterclient"; // Change this (here and in manifest)

    public RestClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    // CHANGE THIS
    // DEFINE METHODS for different API endpoints here
    public void getInterestingnessList(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("format", "json");
        client.get(apiUrl, params, handler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

    public void getHomeTimeLine(String maxID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(maxID)) {
            params.put("max_id", "" + (Long.parseLong(maxID) - 1));
            params.put("count", "80");
        }
        params.put("since_id", "1");

        client.get(apiUrl, params, handler);
    }

    public void getMentionsTimeLine(String maxID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(maxID)) {
            params.put("max_id", "" + (Long.parseLong(maxID) - 1));
            params.put("count", "80");
        }
        params.put("since_id", "1");

        client.get(apiUrl, params, handler);
    }

    public void getUserTimeLine(String userID, String maxID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(maxID)) {
            params.put("max_id", "" + (Long.parseLong(maxID) - 1));
        }
        params.put("since_id", "1");
        params.put("count", "80");
        if (!TextUtils.isEmpty(userID)) {
            params.put("user_id", userID);
        }

        client.get(apiUrl, params, handler);
    }

    public void getFavoritesTimeLine(String userID, String maxID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/favorites/list.json");
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(maxID)) {
            params.put("max_id", "" + (Long.parseLong(maxID) - 1));
            params.put("count", "80");
        }
        params.put("since_id", "1");
        if (!TextUtils.isEmpty(userID)) {
            params.put("user_id", userID);
        }

        client.get(apiUrl, params, handler);
    }

    public void getFriendsTimeLine(String userID, String screenName, String maxID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(maxID)) {
            params.put("cursor", "" + (Long.parseLong(maxID) - 1));
            params.put("count", "80");
        }
        if (!TextUtils.isEmpty(userID)) {
            params.put("user_id", userID);
        }
        if (!TextUtils.isEmpty(screenName)) {
            params.put("screen_name", screenName);
        }

        client.get(apiUrl, params, handler);
    }

    public void getFollowersTimeLine(String userID, String screenName, String maxID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(maxID)) {
            params.put("cursor", "" + (Long.parseLong(maxID) - 1));
            params.put("count", "80");
        }
        if (!TextUtils.isEmpty(userID)) {
            params.put("user_id", userID);
        }
        if (!TextUtils.isEmpty(screenName)) {
            params.put("screen_name", screenName);
        }

        client.get(apiUrl, params, handler);
    }

    public void getSearchTopTweets(String query, String maxID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(maxID)) {
            params.put("max_id", "" + (Long.parseLong(maxID) - 1));
            params.put("count", "80");
        }
        params.put("since_id", "1");
        params.put("result_type", "popular");
        params.put("q", query);

        client.get(apiUrl, params, handler);
    }

    public void getSearchAllTweets(String query, String maxID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(maxID)) {
            params.put("max_id", "" + (Long.parseLong(maxID) - 1));
            params.put("count", "80");
        }
        params.put("since_id", "1");
        params.put("result_type", "recent");
        params.put("q", query);

        client.get(apiUrl, params, handler);
    }

    public void getReplyToTweets(String tweetID, String maxID, AsyncHttpResponseHandler handler) {
        String api = getApiUrl("search/tweets.json");;
        RequestParams params = new RequestParams();
        params.put("q", "in-reply-to-tweet-id:" + tweetID);
        if (!TextUtils.isEmpty(maxID)) {
            params.put("max_id", "" + (Long.parseLong(maxID) - 1));
            params.put("count", "80");
        }
        params.put("since_id", tweetID);
        params.put("include_entities", 1);

        client.get(api, params, handler);
    }

    public void getUserCredentials(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("/account/verify_credentials.json");
        client.get(apiUrl, null, handler);
    }

    public void postTweetUpdate(String tweet, AsyncHttpResponseHandler handler) {
        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("status", tweet));
        String apiUrl = getApiUrl("/statuses/update.json?" + URLEncodedUtils.format(params, "utf-8"));
        client.post(apiUrl, null, handler);
    }

    public void getTweetDetails(String tweetID, AsyncHttpResponseHandler handler) {
        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("id", (tweetID)));
        String apiUrl = getApiUrl("/statuses/show.json?" + URLEncodedUtils.format(params, "utf-8"));
        client.get(apiUrl, null, handler);
    }

    public void deleteTweet(String tweetID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/destroy/" + tweetID + ".json");
        client.post(apiUrl, null, handler);
    }

    public void postReTweet(String tweetID, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/retweet/" + tweetID + ".json");
        client.post(apiUrl, null, handler);
    }

    public void postFavoriteTweetUpdate(String tweetID, AsyncHttpResponseHandler handler) {
        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("id", tweetID));
        String apiUrl = getApiUrl("favorites/create.json?" + URLEncodedUtils.format(params, "utf-8"));
        client.post(apiUrl, null, handler);
    }

    public void postUnFavoriteTweetUpdate(String tweetID, AsyncHttpResponseHandler handler) {
        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("id", tweetID));
        String apiUrl = getApiUrl("favorites/destroy.json?" + URLEncodedUtils.format(params, "utf-8"));
        client.post(apiUrl, null, handler);
    }
}