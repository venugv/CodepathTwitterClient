package com.codepath.codepathtwitterclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.codepathtwitterclient.R;

/**
 * Created by vvenkatraman on 12/17/15.
 */
public class UsersViewHolder extends RecyclerView.ViewHolder {
    ImageView ivProfilePic;
    TextView tvUserName;
    TextView tvUserNameHandle;
    TextView tvBodyText;
    ImageView btnFollowing;

    public UsersViewHolder(View itemView) {
        super(itemView);
        this.ivProfilePic = (ImageView) itemView.findViewById(R.id.ivProfilePic);
        this.tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
        this.tvUserNameHandle = (TextView) itemView.findViewById(R.id.tvUserNameHandle);
        this.tvBodyText = (TextView) itemView.findViewById(R.id.tvBodyText);
        this.btnFollowing = (ImageView) itemView.findViewById(R.id.ivFollowing);
    }
}
