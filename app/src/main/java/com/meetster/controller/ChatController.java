package com.meetster.controller;

import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatController {

    static final String SAVED_USER_NAME = "saved_user_name";
    public static final String DATABASE_URL = "https://meetster-chat-default-rtdb.europe-west1.firebasedatabase.app/";
    private final String myName;
    private final DatabaseReference chatReference;

    public ChatController(String userName, SharedPreferences sharedPref, ChatMessageListener listener) {
        this(FirebaseDatabase.getInstance(DATABASE_URL), userName, sharedPref, listener);
    }

    // This constructor is used only for testing to pass FirebaseDatabase mock
    ChatController(FirebaseDatabase database, String userName, SharedPreferences sharedPref, ChatMessageListener listener) {
        myName = sharedPref.getString(SAVED_USER_NAME, "");
        String chatName = getChatName(myName, userName);
        chatReference = database.getReference("chats").child(chatName);
        // Subscribe to messages created in a chat to display them in recycler view
        chatReference.orderByChild("timestamp").addChildEventListener(listener);
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
