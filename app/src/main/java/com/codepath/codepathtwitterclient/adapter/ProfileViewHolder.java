package com.codepath.codepathtwitterclient.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.activity.UserListActivity;
import com.codepath.codepathtwitterclient.model.User;

/**
 * Created by vvenkatraman on 12/27/15.
 */
public class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView ivProfilePic;
    TextView tvUserName;
    ImageView ivProfileVerified;
    TextView tvScreenName;
    TextView tvDescription;
    TextView tvLocation;
    ImageView ivGlow;
    TextView tvDisplayURL;
    Button btnFollowing;
    Button btnFollowers;
    Button btnEditProfile;
    Button btnFollowingIcon;
    ImageButton iBtnSettings;
    ImageButton iBtnNotifications;
    ImageView ivBackgroundPic;
    User user;

    public ProfileViewHolder(View itemView) {
        super(itemView);
        ivProfilePic = (ImageView) itemView.findViewById(R.id.ivProfilePic);
        ivBackgroundPic = (ImageView) itemView.findViewById(R.id.ivBackgroundPic);
        tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
        ivProfileVerified = (ImageView) itemView.findViewById(R.id.ivProfileVerified);
        tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
        tvDescription = (TextView) itemView.findViewById(R.id.tvUserDescription);
        tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
        ivGlow = (ImageView) itemView.findViewById(R.id.ivGlow);
        tvDisplayURL = (TextView) itemView.findViewById(R.id.tvDisplayURL);
        btnFollowing = (Button) itemView.findViewById(R.id.btnFollowing);
        btnFollowing.setOnClickListener(this);
        btnFollowingIcon = (Button) itemView.findViewById(R.id.btnFollowingIcon);
        btnFollowers = (Button) itemView.findViewById(R.id.btnFollowers);
        btnFollowers.setOnClickListener(this);
        btnEditProfile = (Button) itemView.findViewById(R.id.btnEditProfile);
        iBtnSettings = (ImageButton) itemView.findViewById(R.id.btnSettings);
        iBtnNotifications = (ImageButton) itemView.findViewById(R.id.btnNotificationsIcon);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnFollowers:
                startDisplayActivity("followers", user.getUserId());
                break;
            case R.id.btnFollowing:
                startDisplayActivity("friends", user.getUserId());
                break;
        }
    }

    private void startDisplayActivity(String type, String userID) {
        Intent intent = new Intent(itemView.getContext(), UserListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("user_id", userID);
        itemView.getContext().startActivity(intent);
    }

}
