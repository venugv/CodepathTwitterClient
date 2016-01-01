package com.codepath.codepathtwitterclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.codepathtwitterclient.R;
import com.codepath.codepathtwitterclient.model.User;

import java.util.List;

/**
 * Created by vvenkatraman on 12/17/15.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<User> userList;

    public UsersAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item_user, parent, false);
        return new UsersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, int position) {
        final User user = userList.get(position);
        if(user.isFollowing()){
            holder.btnFollowing.setVisibility(View.INVISIBLE);
        }else{
            holder.btnFollowing.setVisibility(View.VISIBLE);
        }

        holder.tvUserName.setText(user.getName());
        holder.tvUserNameHandle.setText(user.getScreenName());
        holder.tvBodyText.setText(Html.fromHtml(user.getDescription()));

        Glide.with(context).load(user.getUrl())
                .thumbnail(1.0f)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivProfilePic);

        holder.ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileActivity(user.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    private void startProfileActivity(String userId) {
        //
    }
}
