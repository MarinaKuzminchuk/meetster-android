package com.meetster.controller;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.meetster.model.ChatMessage;
import com.meetster.view.ChatRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatController {

    public static final String DATABASE_URL = "https://meetsterchat-default-rtdb.europe-west1.firebasedatabase.app/";
    private final String myName;
    private final DatabaseReference chatReference;

    public ChatController(SharedPreferences sharedPref, String userName,
                          ChatRecyclerViewAdapter chatRecyclerViewAdapter) {
        myName = sharedPref.getString("saved_user_name", "");
        String chatName = getChatName(myName, userName);
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        chatReference = database.getReference("chats").child(chatName);
        // Subscribe to messages created in a chat to display them in recycler view
        chatReference.orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage message = new ChatMessage(
                        snapshot.child("sentBy").getValue(String.class),
                        snapshot.child("message").getValue(String.class),
                        new Date(snapshot.child("timestamp").getValue(Long.class)));
                chatRecyclerViewAdapter.addMessage(message);
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
        });
    }

    // Create message in a chat
    public void sendMessage(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("timestamp", ServerValue.TIMESTAMP);
        map.put("sentBy", myName);
        chatReference.child(UUID.randomUUID().toString()).updateChildren(map);
    }

    // Create unique identifier for the chat between two users
    // The chat name is alphabetically sorted sequence of user names
    private String getChatName(String myName, String userName) {
        ArrayList<String> namesList = new ArrayList<>();
        namesList.add(myName);
        namesList.add(userName);
        Collections.sort(namesList);
        return namesList.get(0) + "_" + namesList.get(1);
    }
}
