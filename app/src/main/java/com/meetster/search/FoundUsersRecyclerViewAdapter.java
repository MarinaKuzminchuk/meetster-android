package com.meetster.search;

import static com.meetster.IntentExtraKeys.EXTRA_AUTHENTICATED_USER;
import static com.meetster.IntentExtraKeys.EXTRA_CHAT_USER;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetster.R;
import com.meetster.chat.ChatActivity;
import com.meetster.model.FoundUser;
import com.meetster.model.User;

import java.util.ArrayList;
import java.util.List;

public class FoundUsersRecyclerViewAdapter extends RecyclerView.Adapter<FoundUsersRecyclerViewAdapter.ViewHolder> {
    private static final int NEWLY_FOUND_VIEW_TYPE = 0;
    private static final int PREVIOUSLY_FOUND_VIEW_TYPE = 1;
    private Activity activity;
    private User authenticatedUser;
    private List<FoundUser> newlyFoundUsers;
    private List<FoundUser> previouslyFoundUsers;

    public FoundUsersRecyclerViewAdapter(Activity activity, User authenticatedUser, List<FoundUser> previouslyFoundUsers) {
        this.activity = activity;
        this.authenticatedUser = authenticatedUser;
        this.newlyFoundUsers = new ArrayList<>();
        this.previouslyFoundUsers = previouslyFoundUsers;
    }

    // displays newly found users on top of previously found users
    // by comparing position to the newly found users count
    @Override
    public int getItemViewType(int position) {
        if (position < newlyFoundUsers.size()) {
            return NEWLY_FOUND_VIEW_TYPE;
        } else {
            return PREVIOUSLY_FOUND_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view;
        if (viewType == NEWLY_FOUND_VIEW_TYPE) {
            view = inflater.inflate(R.layout.newly_found_users_list_item, parent, false);
        } else {
            view = inflater.inflate(R.layout.found_users_list_item, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra(EXTRA_AUTHENTICATED_USER, authenticatedUser.name);
                intent.putExtra(EXTRA_CHAT_USER, viewHolder.foundUserName.getText().toString());
                activity.startActivity(intent);
            }
        });
        return viewHolder;
    }

    // define how display the item with index = position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoundUser foundUser;
        if (position < newlyFoundUsers.size()) {
            foundUser = newlyFoundUsers.get(position);
        } else {
            foundUser = previouslyFoundUsers.get(position - newlyFoundUsers.size());
        }
        holder.foundUserName.setText(foundUser.user.name);
        holder.foundUserFilter.setText("from " + foundUser.filters.specialty + " with tag " + foundUser.filters.tag);
    }

    @Override
    public int getItemCount() {
        return newlyFoundUsers.size() + previouslyFoundUsers.size();
    }

    public void updateData(List<FoundUser> newlyFoundUsers, List<FoundUser> previouslyFoundUsers) {
        this.newlyFoundUsers = newlyFoundUsers;
        this.previouslyFoundUsers = previouslyFoundUsers;
        notifyDataSetChanged();
    }

    // define how the item data will be displayed
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView foundUserName;
        private TextView foundUserFilter;

        public ViewHolder(View view) {
            super(view);
            foundUserName = view.findViewById(R.id.textViewFoundUserName);
            foundUserFilter = view.findViewById(R.id.textViewFoundUserFilter);
        }
    }
}
