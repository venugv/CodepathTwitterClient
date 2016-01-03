package com.codepath.codepathtwitterclient.model;

import android.text.TextUtils;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.codepath.codepathtwitterclient.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vvenkatraman on 12/14/15.
 */
@Table(name = "Tweets")
public class Tweet extends Model {

    private static final String TAG = Tweet.class.getName().toString();
    @Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String tweetID;
    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public User user;
    @Column(name = "created_at")
    public String createdAt;
    @Column(name = "body")
    public String body;
    @Column(name = "retweeted_username")
    public String retweetedUserName;
    @Column(name = "retweet_count")
    public String retweetCount;
    @Column(name = "favorite_count")
    public String favoriteCount;
    @Column(name = "favorited")
    public boolean favorited;
    @Column(name = "retweeted")
    public boolean retweeted;
    @Column(name = "media_url")
    public String mediaURL;
    @Column(name = "in_reply_to_status_id_str")
    private String inReplyTo;

    @Column(name = "type")
    public int type;

    public String getTweetID() {
        return tweetID;
    }

    public void setTweetID(String tweetID) {
        this.tweetID = tweetID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    public static Tweet fromJson(JSONObject tweet, int type) {

        Tweet _tweet = new Tweet();
        try {

            _tweet.body = tweet.getString("text").replace("\n", "<br>");
            _tweet.tweetID = tweet.getString("id_str");
            _tweet.createdAt = tweet.getString("created_at");
            _tweet.favorited = tweet.getBoolean("favorited");
            _tweet.retweeted = tweet.getBoolean("retweeted");
            _tweet.inReplyTo = tweet.getString("in_reply_to_status_id_str");

            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            _tweet.favoriteCount = formatter.format(tweet.getLong("favorite_count"));
            _tweet.retweetCount = formatter.format(tweet.getLong("retweet_count"));

            _tweet.user = User.fromJson(tweet.getJSONObject("user"));

            if (!tweet.isNull("retweeted_status")) {
                _tweet.retweetedUserName = _tweet.user.getName();
                JSONObject retweetStatus = tweet.getJSONObject("retweeted_status");
                _tweet.user = User.fromJson(retweetStatus.getJSONObject("user"));
                _tweet.favoriteCount = formatter.format(retweetStatus.getLong("favorite_count"));
            }

            if(!tweet.getJSONObject("entities").isNull("media")){
                JSONArray array = tweet.getJSONObject("entities").getJSONArray("media");
                _tweet.mediaURL = array.getJSONObject(0).getString("media_url");
            }

        } catch (JSONException ex) {
            Log.e("ERR", ex.toString());
            return null;
        }
        _tweet.type = type;

        Tweet dbTweet = Tweet.getTweet(_tweet.getTweetId());
        if(dbTweet != null){
            //TODO: update all fields
            new Update(Tweet.class)
                    .set("type = " + (dbTweet.getType() | type))
                    .where("tweet_id = ?", dbTweet.getTweetId())
                    .execute();
        }else {
            _tweet.save();
        }
        return _tweet;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTweetId() {
        return tweetID;
    }

    public void setTweetId(String id) {
        this.tweetID = id;
    }

    public String getCreatedAt() {
        return Utils.getRelativeTimeAgo(createdAt);
    }

    public String getTimeStamp() {
        return Utils.getTimeStamp(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public String getRetweetedUserName() {
        return retweetedUserName;
    }

    public void setRetweetedUserName(String retweetedUserName) {
        this.retweetedUserName = retweetedUserName;
    }

    public String getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(String retweetCount) {
        this.retweetCount = retweetCount;
    }

    public String getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(String favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public boolean hasRetweetedUsername() {
        return !TextUtils.isEmpty(this.getRetweetedUserName());

    }

    public static Tweet getTweet(String tweetID){
        return new Select()
                .from(Tweet.class)
                .where("tweet_id = ?", tweetID)
                .orderBy("tweet_id DESC")
                .executeSingle();
    }


    public static List<Tweet> getTweetsList(int count, String maxID){
        return getTweetsList(count, 1, maxID, -1);
    }


    public static List<Tweet> getTweetsList(String maxID, int type){
        return getTweetsList(20, 1, maxID, type);
    }

    public static List<Tweet> getTweetsList(String maxID){
        return getTweetsList(maxID, -1);
    }

    public static List<Tweet> getTweetsList(int count){
        return getTweetsList(count, 1, "-1", -1);
    }

    public static List<Tweet> getTweetsList(int count, long sinceID, String maxID, int type){

        if(count <= 0) return new ArrayList<Tweet>();

        String whereClause = String.format("tweet_id >= " + sinceID);
        if(!TextUtils.isEmpty(maxID)){
            whereClause += String.format(" AND tweet_id < "+ maxID);
        }
        whereClause += " AND type & "+ type + " > 0";
        From sql = new Select()
                .from(Tweet.class)
                .where(whereClause)
                .orderBy("tweet_id DESC")
                .limit(count);
        Log.d("Tweet", sql.toSql());
        return sql.execute();
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray, int type) {
        ArrayList<Tweet> tweetArrayList = new ArrayList<Tweet>();
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject tweet = jsonArray.getJSONObject(i);
                    tweetArrayList.add(fromJson(tweet, type));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        return tweetArrayList;
    }

    @Override
    public String toString() {
        return "tweetID=" + super.getId();
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }
}
