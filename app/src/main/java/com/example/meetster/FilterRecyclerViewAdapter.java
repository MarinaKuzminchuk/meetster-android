package com.example.meetster;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<FilterRecyclerViewAdapter.ViewHolder> {
    private Activity activity;
    private List<List<String>> foundUsers;

    public FilterRecyclerViewAdapter(Activity activity, List<List<String>> foundUsers) {
        this.activity = activity;
        this.foundUsers = foundUsers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.foundUserName.setText(foundUsers.get(position).get(0));
        viewHolder.foundUserFilters.setText(foundUsers.get(position).get(1));
    }

    @Override
    public int getItemCount() {
        return foundUsers.size();
    }

    public void addFoundUser(String name, String filter) {
        foundUsers.add(Arrays.asList(name, filter));
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView foundUserName;
        private TextView foundUserFilters;

        public ViewHolder(View view) {
            super(view);
            foundUserName = (TextView) view.findViewById(R.id.textViewFoundUserName);
            foundUserFilters = (TextView) view.findViewById(R.id.textViewFoundUserFilters);
        }
    }
}
