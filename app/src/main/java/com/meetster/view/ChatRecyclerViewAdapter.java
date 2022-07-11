package com.meetster.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetster.R;
import com.meetster.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {
    private static final int MY_MESSAGE = 1;
    private static final int INCOMING_MESSAGE = 2;
    private final Activity activity;
    private final String userName;
    private final List<ChatMessage> messages;
    private static final SimpleDateFormat timestampFormatter =
            new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ChatRecyclerViewAdapter(Activity activity, String userName) {
        this.activity = activity;
        this.userName = userName;
        this.messages = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = messages.get(position);
        if (userName.equals(chatMessage.sentBy)) {
            return INCOMING_MESSAGE;
        } else {
            return MY_MESSAGE;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view;
        if (viewType == MY_MESSAGE) {
            view = inflater.inflate(R.layout.chat_adapter_layout_me, parent, false);
            return new ViewHolder(view, R.id.myMessageText, R.id.myMessageTimestamp);
        } else {
            view = inflater.inflate(R.layout.chat_adapter_layout_incoming, parent, false);
            return new ViewHolder(view, R.id.incomingMessageText, R.id.incomingMessageTimestamp);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = messages.get(position);
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

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView text;
        private final TextView timestamp;

        public ViewHolder(View view, int messageTextId, int messageTimestampId) {
            super(view);
            text = (TextView) view.findViewById(messageTextId);
            timestamp = (TextView) view.findViewById(messageTimestampId);
        }
    }
}
