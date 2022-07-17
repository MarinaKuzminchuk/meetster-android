package com.meetster.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.meetster.model.ChatMessage;

import java.util.Date;

public class ChatMessageListener implements ChildEventListener {

    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;

    public ChatMessageListener(ChatRecyclerViewAdapter chatRecyclerViewAdapter) {
        this.chatRecyclerViewAdapter = chatRecyclerViewAdapter;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        displayMessage(snapshot, chatRecyclerViewAdapter);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        // do nothing when chat messages change, because they shouldn't change
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
        // do nothing when chat messages are removed, because they shouldn't be removed
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        // do nothing when chat messages are move, because they shouldn't move
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        // do nothing when connection to database cancels just for now
    }

    private void displayMessage(DataSnapshot snapshot, ChatRecyclerViewAdapter chatRecyclerViewAdapter) {
        ChatMessage message = new ChatMessage(
                snapshot.child("sentBy").getValue(String.class),
                snapshot.child("message").getValue(String.class),
                new Date(snapshot.child("timestamp").getValue(Long.class)));
        chatRecyclerViewAdapter.addMessage(message);
    }
}
