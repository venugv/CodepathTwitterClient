package com.codepath.codepathtwitterclient.fragment;

/**
 * Created by vvenkatraman on 12/31/15.
 */

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.codepathtwitterclient.CodePathTwitterClientApp;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.model.User;
import com.codepath.codepathtwitterclient.restclient.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by Sandeep on 9/29/2014.
 */
public class CreateTweetFragment extends DialogFragment {

    private static final String TAG = CreateTweetFragment.class.getName().toString();
    private static String sStatus;
    private onTweetListener callback;
    private ImageView ibClose;
    private ImageView profilePic;
    private Button btnSubmit;
    private TextView tweetCount;
    private EditText etStatus;
    private RestClient client;

    public CreateTweetFragment() {
    }

    public static CreateTweetFragment getInstance(String status) {
        sStatus = status;
        return new CreateTweetFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback = (onTweetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onTweetListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        setupViews(view);
        client = CodePathTwitterClientApp.getRestClient();

        User user = User.getCurrentUser();
        if (user == null) {
            Toast.makeText(getDialog().getContext(), "Failed to get user details", Toast.LENGTH_LONG).show();
            getDialog().dismiss();
            return view;
        } else {
            profilePic.setImageResource(0);
            Glide.with(this).load(user.getUrl())
                    .thumbnail(1.0f)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profilePic);
        }
        return view;
    }

    private void setupViews(View view) {
        ibClose = (ImageView) view.findViewById(R.id.ibClose);
        profilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
        btnSubmit = (Button) view.findViewById(R.id.btnTweet);
        tweetCount = (TextView) view.findViewById(R.id.tvTweetCount);
        etStatus = (EditText) view.findViewById(R.id.etTweet);

        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btnSubmit.setEnabled(false);
        btnSubmit.getBackground().setAlpha(50);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTweet();
            }
        });

        etStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = etStatus.getText().toString();
                tweetCount.setText("" + (140 - text.length()));
                if (text.length() > 0 && text.length() <= 140) {
                    btnSubmit.setEnabled(true);
                    btnSubmit.getBackground().setAlpha(255);
                } else {
                    btnSubmit.setEnabled(false);
                    btnSubmit.getBackground().setAlpha(50);
                }
                if (text.length() > 140) {
                    tweetCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    tweetCount.setTextColor(getResources().getColor(R.color.secondary_text_color));
                }
            }
        });
        if (!TextUtils.isEmpty(sStatus)) {
            etStatus.setText("");
            etStatus.append(sStatus + " ");
        }
    }

    private void submitTweet() {
        client.postTweetUpdate(etStatus.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                callback.onTweetSubmit();
                getDialog().dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getDialog().getContext(), "Failed to submit the tweet. Please try again later.", Toast.LENGTH_LONG).show();
                Log.e(TAG, throwable.toString());
            }
        });
    }

    public interface onTweetListener {
        public void onTweetSubmit();
    }
}
