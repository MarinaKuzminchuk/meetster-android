package com.example.meetster;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {
    private Activity activity;
    private List<ChatMessage> messages;
    private static final SimpleDateFormat timestampFormatter =
            new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ChatRecyclerViewAdapter(Activity activity) {
        this.activity = activity;
        this.messages = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.chat_message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = messages.get(position);
        holder.sentBy.setText(chatMessage.sentBy);
        holder.text.setText(chatMessage.text);
        holder.timestamp.setText(timestampFormatter.format(chatMessage.timestamp));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView sentBy;
        private TextView text;
        private TextView timestamp;

        public ViewHolder(View view) {
            super(view);
            sentBy = (TextView) view.findViewById(R.id.textViewMessageSentBy);
            text = (TextView) view.findViewById(R.id.textViewMessageText);
            timestamp = (TextView) view.findViewById(R.id.textViewMessageTimestamp);
        }
    }
}
