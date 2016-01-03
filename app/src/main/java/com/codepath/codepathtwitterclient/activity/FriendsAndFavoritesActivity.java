package com.codepath.codepathtwitterclient.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.adapter.UsersAdapter;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;
import com.codepath.codepathtwitterclient.model.User;
import com.codepath.codepathtwitterclient.restclient.RestClient;
import com.codepath.codepathtwitterclient.utils.EndlessRecyclerOnScrollListener;
import com.codepath.codepathtwitterclient.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class FriendsAndFavoritesActivity extends AppCompatActivity {
    public static final String TAG = FriendsAndFavoritesActivity.class.getName();
    private final CountDownLatch networkRequestDoneSignal = new CountDownLatch(1);
    private String type;
    private String userID;
    private String screenName;
    private JsonHttpResponseHandler usersJsonResponseHandler;
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private String maxID;
    private EndlessRecyclerOnScrollListener endlessScrollListener;
    private RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_and_favorites);
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

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        userID = intent.getStringExtra(TweetFragment.USER_ID);
        screenName = intent.getStringExtra(TweetFragment.SCREEN_NAME);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(type.toUpperCase());
        }

        TweetFragment details = (TweetFragment)
                getSupportFragmentManager().findFragmentById(R.id.lyFragment);
        if (details == null) {
            // Make new fragment to show this selection.
            details = TweetFragment.newInstance(type);
            Bundle bundle = details.getArguments();
            bundle.putString(TweetFragment.USER_ID, userID);
            bundle.putString(TweetFragment.SCREEN_NAME, screenName);

            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.lyFragment, details);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_and_favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}