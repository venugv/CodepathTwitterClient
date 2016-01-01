package com.codepath.codepathtwitterclient.model;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by vvenkatraman on 12/14/15.
 */
@Table(name = "Users")
public class User extends Model {

    private static final String TAG = User.class.getName().toString();
    @Column(name = "user_name")
    public String name;
    @Column(name = "user_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String userID;
    @Column(name = "screen_name")
    public String screenName;
    @Column(name = "url")
    public String url;
    @Column(name = "following")
    public boolean following;
    @Column(name = "current_user")
    public boolean currentUser;
    @Column(name = "verified")
    public boolean verified;
    @Column(name = "location")
    public String location;
    @Column(name = "description")
    public String description;
    @Column(name = "display_url")
    public String displayURL;
    @Column(name = "bg_url")
    public String bgURL;
    @Column(name = "followers_count")
    public String followersCount;
    @Column(name = "friends_count")
    public String followingCount;
    @Column(name = "statuses_count")
    public String tweetsCount;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayURL() {
        return displayURL;
    }

    public void setDisplayURL(String displayURL) {
        this.displayURL = displayURL;
    }

    public String getBgURL() {
        return bgURL;
    }

    public void setBgURL(String bgURL) {
        this.bgURL = bgURL;
    }

    public String getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(String followersCount) {
        this.followersCount = followersCount;
    }

    public String getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(String followingCount) {
        this.followingCount = followingCount;
    }

    public String getTweetsCount() {
        return tweetsCount;
    }

    public void setTweetsCount(String tweetsCount) {
        this.tweetsCount = tweetsCount;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    @Column(name = "notifications")
    public boolean notifications;

    public static User fromJson(JSONObject jsonObject) {
        User user = new User();
        try {

            user.name = jsonObject.getString("name");
            user.userID = jsonObject.getString("id_str");
            user.screenName = "@" + jsonObject.getString("screen_name");
            user.url = jsonObject.getString("profile_image_url");

            if (!jsonObject.isNull("following"))
                user.following = jsonObject.getBoolean("following");
            if (!jsonObject.isNull("verified"))
                user.verified = jsonObject.getBoolean("verified");
            if (!jsonObject.isNull("notifications"))
                user.notifications = jsonObject.getBoolean("notifications");
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            if (!jsonObject.isNull("followers_count"))
                user.followersCount = formatter.format(jsonObject.getLong("followers_count"));
            if (!jsonObject.isNull("friends_count"))
                user.followingCount = formatter.format(jsonObject.getLong("friends_count"));
            if (!jsonObject.isNull("statuses_count"))
                user.tweetsCount = formatter.format(jsonObject.getLong("statuses_count"));

            if (!jsonObject.isNull("location"))
                user.location = jsonObject.getString("location");
            if (!jsonObject.isNull("description"))
                user.description = jsonObject.getString("description");
            if (!jsonObject.isNull("url")) {
                user.displayURL = jsonObject.getString("url");
                if(!jsonObject.isNull("entities")){
                    if(!jsonObject.getJSONObject("entities").isNull("url")){
                        JSONArray urls = jsonObject.getJSONObject("entities").getJSONObject("url")
                                .getJSONArray("urls");
                        for(int i=0;i<urls.length();i++){
                            JSONObject url = urls.getJSONObject(i);
                            if(!url.isNull("url")){
                                String urlValue = url.getString("url");
                                if(user.displayURL.equals(urlValue)){
                                    if(!url.isNull("display_url")){
                                        user.displayURL = url.getString("display_url");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!jsonObject.isNull("profile_banner_url"))
                user.bgURL = jsonObject.getString("profile_banner_url");

            if (!jsonObject.isNull("current_user")) {
                user.currentUser = true;
            } else {
                user.currentUser = false;
            }
        } catch (JSONException ex) {
            Log.e(TAG, ex.toString());
            return null;
        }

        User dbUser = User.getUser(user.getUserId());
        if (dbUser != null) {
            return dbUser;
        }
        user.save();
        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userID;
    }

    public void setUserId(String id) {
        this.userID = id;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public static User getUser(String userID) {
        return new Select()
                .from(User.class)
                .where("user_id = ?", userID)
                .executeSingle();
    }

    public static User getCurrentUser() {
        return new Select()
                .from(User.class)
                .where("current_user = 1")
                .executeSingle();
    }

    public static ArrayList<User> fromJson(JSONArray jsonArray) {
        ArrayList<User> _list = new ArrayList<User>();
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject tweet = jsonArray.getJSONObject(i);
                    _list.add(fromJson(tweet));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }

        return _list;
    }
}

