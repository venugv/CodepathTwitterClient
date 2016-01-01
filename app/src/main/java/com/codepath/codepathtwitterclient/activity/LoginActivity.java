package com.codepath.codepathtwitterclient.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.codepathtwitterclient.CodePathTwitterClientApp;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.model.User;
import com.codepath.codepathtwitterclient.restclient.RestClient;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends OAuthLoginActionBarActivity<RestClient> {
    private static final String TAG = LoginActivity.class.getName();
    Button btnLogin;
    TextView tvAboutTwitter;
    private ProgressDialog mPRogressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
            getSupportActionBar().setTitle(getString(R.string.title_activity_login));
        }

        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvAboutTwitter = (TextView) findViewById(R.id.tvAboutTwitter);
        tvAboutTwitter.setText(Html.fromHtml(getString(R.string.about_full_text)), TextView.BufferType.SPANNABLE);

        Intent intent = getIntent();
        if(intent.getData() != null && intent.getData().getScheme().equals("oauth")) {
            mPRogressBar = new ProgressDialog(this);
            mPRogressBar.setMessage("Signing in...");
            mPRogressBar.show();
        }

    }

    @Override
    public void onLoginSuccess() {
        // Move to the next activity and finish properly
        RestClient restClient = CodePathTwitterClientApp.getRestClient();
        restClient
                .getUserCredentials(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            response.put("current_user", true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        User.fromJson(response);
                        launchLandingPage();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, throwable.toString());
                    }
                });
    }

    private void launchLandingPage() {
        Intent landingPageIntent = new Intent(this, LandingPageActivity.class);
        startActivity(landingPageIntent);
        LoginActivity.this.finish();
    }

    @Override
    public void onLoginFailure(Exception e) {
        Snackbar.make(btnLogin, R.string.login_failure, Snackbar.LENGTH_LONG).show();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
        CodePathTwitterClientApp.getRestClient().connect();
    }
}
