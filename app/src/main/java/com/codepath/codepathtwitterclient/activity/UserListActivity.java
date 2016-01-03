package com.codepath.codepathtwitterclient.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.fragment.TweetFragment;

public class UserListActivity extends FragmentActivity {

    private String userID;
    private String type;
    private String screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition fadeTransform = TransitionInflater.from(this).
                    inflateTransition(android.R.transition.fade);
            fadeTransform.setStartDelay(0);
            fadeTransform.setDuration(500);
            getWindow().setEnterTransition(fadeTransform);
            getWindow().setExitTransition(fadeTransform);
        }
        if (getActionBar() != null) {
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayShowHomeEnabled(true);
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        userID = intent.getStringExtra(TweetFragment.USER_ID);
        screenName = intent.getStringExtra(TweetFragment.SCREEN_NAME);

        // Check what fragment is currently shown, replace if needed.
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
            //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
