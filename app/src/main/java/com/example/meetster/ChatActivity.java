package com.example.meetster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private RecyclerView chatRecyclerView;
    private String myName;
    private String userName;
    private String chatName;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://meetsterchat-default-rtdb.europe-west1.firebasedatabase.app/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final TextView nameTV = findViewById(R.id.name);
        final EditText messageEditText = findViewById(R.id.messageEditText);
        final ImageView profilePic = findViewById(R.id.profilePic);
        final ImageView sendBtn = findViewById(R.id.sendBtn);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        ChatRecyclerViewAdapter chatRecyclerViewAdapter =
                new ChatRecyclerViewAdapter(this);
        chatRecyclerView.setAdapter(chatRecyclerViewAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);

        myName = sharedPref.getString("saved_name", "");
        // Get chat user name passed from the search activity
        // (inside recycler view when we click on the found user)
        userName = getIntent().getStringExtra("chat-user");
        nameTV.setText(userName);

        chatName = getChatName();

        DatabaseReference chatReference = database.getReference("chats").child(chatName);
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
                System.out.println("");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                System.out.println("");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                System.out.println("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("");
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString();
                Map<String, Object> map = new HashMap<>();
                map.put("message", message);
                map.put("timestamp", ServerValue.TIMESTAMP);
                map.put("sentBy", myName);
                chatReference.child(UUID.randomUUID().toString()).updateChildren(map);
                messageEditText.setText("");
            }
        });
    }

    // Create unique identifier for the chat between two users
    // The chat name is alphabetically sorted sequence of user names
    private String getChatName() {
        ArrayList<String> namesList = new ArrayList<>();
        namesList.add(myName);
        namesList.add(userName);
        Collections.sort(namesList);
        return namesList.get(0) + "_" + namesList.get(1);
    }
}
