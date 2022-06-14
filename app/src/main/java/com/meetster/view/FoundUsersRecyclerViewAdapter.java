package com.meetster.view;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetster.R;
import com.meetster.model.FoundUser;

import java.util.List;

public class FoundUsersRecyclerViewAdapter extends RecyclerView.Adapter<FoundUsersRecyclerViewAdapter.ViewHolder> {
    private Activity activity;
    private List<FoundUser> foundUsers;

    public FoundUsersRecyclerViewAdapter(Activity activity, List<FoundUser> foundUsers) {
        this.activity = activity;
        this.foundUsers = foundUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.found_users_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("chat-user", viewHolder.foundUserName.getText());
                activity.startActivity(intent);
            }
        });
        return viewHolder;
    }

    // define how display the item with index = position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoundUser foundUser = foundUsers.get(position);
        holder.foundUserName.setText(foundUser.user.name);
        holder.foundUserFilters.setText(foundUser.filters.toString());
    }

    @Override
    public int getItemCount() {
        return foundUsers.size();
    }

    public void addFoundUser(FoundUser foundUser) {
        foundUsers.add(foundUser);
        // notify that new data should be displayed
        notifyDataSetChanged();
    }

    // define how the item data will be displayed
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView foundUserName;
        private TextView foundUserFilters;

        public ViewHolder(View view) {
            super(view);
            foundUserName = view.findViewById(R.id.textViewFoundUserName);
            foundUserFilters = view.findViewById(R.id.textViewFoundUserFilters);
        }
    }
}
