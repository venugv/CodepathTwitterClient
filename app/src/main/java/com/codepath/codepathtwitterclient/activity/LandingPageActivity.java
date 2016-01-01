package com.codepath.codepathtwitterclient.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.codepathtwitterclient.CodePathTwitterClientApp;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.adapter.MainFragmentPagerAdapter;
import com.codepath.codepathtwitterclient.adapter.TweetsAdapter;
import com.codepath.codepathtwitterclient.fragment.CreateTweetFragment;
import com.codepath.codepathtwitterclient.fragment.ImageDialogFragment;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;
import com.codepath.codepathtwitterclient.model.Tweet;
import com.codepath.codepathtwitterclient.model.User;
import com.codepath.codepathtwitterclient.restclient.RestClient;

import java.lang.ref.WeakReference;


public class LandingPageActivity extends AppCompatActivity
        implements CreateTweetFragment.onTweetListener,
        NavigationView.OnNavigationItemSelectedListener,
        ImageDialogFragment.OnFragmentInteractionListener {

    private static final String TAG = LandingPageActivity.class.getName();
    TabLayout tabLayout;
    ViewPager viewPager;
    String searchQuery = null;
    private RestClient restClient;
    private WeakReference<TweetFragment> tweetFragmentWeakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition fadeTransform = TransitionInflater.from(this).
                    inflateTransition(android.R.transition.fade);
            fadeTransform.setStartDelay(0);
            fadeTransform.setDuration(500);
            getWindow().setEnterTransition(fadeTransform);
            getWindow().setExitTransition(fadeTransform);
        }

        initViews();
    }

    private void initViews() {
        restClient = CodePathTwitterClientApp.getRestClient();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        ImageView ivProfilePic = (ImageView) headerView.findViewById(R.id.ivProfilePic);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        User currentUser = User.getCurrentUser();
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUserId();
        }
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(),
                LandingPageActivity.this, userId));
        //viewPager.setPageTransformer(true, new TwitterClientPageTransformer(TwitterClientPageTransformer.TransformType.DEPTH));

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.slidingTabs);
        tabLayout.setupWithViewPager(viewPager);

        Glide.with(this).load(currentUser.getUrl())
                .thumbnail(1.0f)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivProfilePic);
        TextView tvUserName = (TextView) headerView.findViewById(R.id.tvUserName);
        tvUserName.setText(currentUser.getName());
        TextView tvUserNameHandle = (TextView) headerView.findViewById(R.id.tvUserNameHandle);
        tvUserNameHandle.setText(getString(R.string.screen_username,
                currentUser.getScreenName()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_page, menu);
        final MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // handle menu_search
                searchQuery = query;
                Intent intent = new Intent(LandingPageActivity.this, SearchActivity.class);
                intent.putExtra(TweetFragment.SEARCH_STRING, searchQuery);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if (!TextUtils.isEmpty(searchQuery)) {
            search.expandActionView();
            searchView.setQuery(searchQuery, false);
            searchView.clearFocus();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_compose:
                composeTweet();
                return true;
            case R.id.action_mail:
                mailTweet();
                return true;
            case R.id.action_user:
                goToUserFragment();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_compose) {
            composeTweet();
        } else if (id == R.id.action_mail) {
            mailTweet();
        } else if (id == R.id.action_logout) {
            User.getCurrentUser().setCurrentUser(false);
            User.getCurrentUser().save();
            restClient.clearAccessToken();
            startActivity(new Intent(this, LoginActivity.class).
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (id == R.id.action_profile) {
            startProfileActivity(User.getCurrentUser().getUserId());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startProfileActivity(String userID) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user_id", userID);
        startActivity(intent);

    }

    private void composeTweet() {
        DialogFragment fragment = CreateTweetFragment.getInstance(null);
        fragment.show(getFragmentManager(), "Create Tweet");
    }

    private void mailTweet() {
        if (tweetFragmentWeakReference.get() == null) {
            return;
        }
        try {
            LinearLayoutManager layoutManager = (LinearLayoutManager)
                    tweetFragmentWeakReference.get().getRecyclerView().getLayoutManager();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            TweetsAdapter adapter = (TweetsAdapter) tweetFragmentWeakReference.get().getRecyclerView().getAdapter();
            Tweet tweet = adapter.getTweetList().get(firstVisibleItemPosition);
            if (tweet != null) {
                String text = "Check out " + tweet.getUser().getScreenName()
                        + "'s Tweet: https://twitter.com/" + tweet.getUser().getScreenName().substring(1)
                        + "/status/" + tweet.getTweetId();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, "venugv@gmail.com");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                sendIntent.setType("text/plain");
                // Launch sharing dialog for image
                startActivity(Intent.createChooser(sendIntent, "Email Tweet"));
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception!", ex);
        }
    }

    private void goToUserFragment() {
        this.viewPager.setCurrentItem(2, true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void setTweetFragmentWeakReference(WeakReference<TweetFragment> tweetFragmentWeakReference) {
        this.tweetFragmentWeakReference = tweetFragmentWeakReference;
    }

    @Override
    public void onTweetSubmit() {
        if (tweetFragmentWeakReference.get() != null) {
            tweetFragmentWeakReference.get().onRefresh();
        }
    }
}
