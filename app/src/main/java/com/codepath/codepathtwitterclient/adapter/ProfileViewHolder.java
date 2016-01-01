package com.codepath.codepathtwitterclient.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.activity.ImageActivity;
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
    TextView tvDisplayURL;
    TextView ivFollowing;
    TextView ivFollowers;
    Button btnEditProfile;
    Button btnFollowingIcon;
    ImageView ivBackgroundPic;
    User user;

    public ProfileViewHolder(View itemView) {
        super(itemView);
        ivProfilePic = (ImageView) itemView.findViewById(R.id.ivProfilePic);
        ivBackgroundPic = (ImageView) itemView.findViewById(R.id.ivBackgroundPic);
        ivBackgroundPic.setOnClickListener(this);
        tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
        ivProfileVerified = (ImageView) itemView.findViewById(R.id.ivProfileVerified);
        tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
        tvDescription = (TextView) itemView.findViewById(R.id.tvUserDescription);
        tvDisplayURL = (TextView) itemView.findViewById(R.id.tvDisplayURL);
        ivFollowing = (TextView) itemView.findViewById(R.id.ivFollowing);
        ivFollowing.setOnClickListener(this);
        btnFollowingIcon = (Button) itemView.findViewById(R.id.btnFollowingIcon);
        ivFollowers = (TextView) itemView.findViewById(R.id.ivFollowers);
        ivFollowers.setOnClickListener(this);
        btnEditProfile = (Button) itemView.findViewById(R.id.btnEditProfile);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ivFollowers:
                startDisplayActivity("followers", user.getUserId());
                break;
            case R.id.ivFollowing:
                startDisplayActivity("friends", user.getUserId());
                break;
            case R.id.ivBackgroundPic:
                displayImage();
        }
    }

    private void displayImage() {
        Intent intent = new Intent(itemView.getContext(), ImageActivity.class);
        intent.putExtra(ImageActivity.ARG_IMAGE_URI, user.getBgURL());
        itemView.getContext().startActivity(intent);
    }

    private void startDisplayActivity(String type, String userID) {
        Intent intent = new Intent(itemView.getContext(), UserListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("user_id", userID);
        itemView.getContext().startActivity(intent);
    }

}
