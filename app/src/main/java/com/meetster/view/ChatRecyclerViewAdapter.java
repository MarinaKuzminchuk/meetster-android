package com.meetster.view;

import android.app.Activity;
import android.graphics.Color;
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
    private final Activity activity;
    private final List<ChatMessage> messages;
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
//        holder.timestamp.setText(timestampFormatter.format(chatMessage.timestamp));
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
        private final TextView sentBy;
        private final TextView text;
//        private final TextView timestamp;

        public ViewHolder(View view) {
            super(view);
            sentBy = (TextView) view.findViewById(R.id.textViewMessageSentBy);
            text = (TextView) view.findViewById(R.id.textViewMessageText);
//            timestamp = (TextView) view.findViewById(R.id.opoTextViewMessageTimestamp);
            view.setDrawingCacheBackgroundColor(Color.GRAY);
        }
    }
}
