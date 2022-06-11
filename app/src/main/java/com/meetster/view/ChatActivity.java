package com.meetster.view;

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

import com.example.meetster.R;
import com.meetster.controller.ChatController;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatController chatController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final TextView userNameText = findViewById(R.id.chatUserNameText);
        final EditText messageEditText = findViewById(R.id.messageEditText);
        final ImageView sendBtn = findViewById(R.id.sendBtn);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        ChatRecyclerViewAdapter chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(this);
        configureChatRecyclerView(chatRecyclerViewAdapter);

        // Get chat user name passed from the search activity
        // (inside recycler view when we click on the found user)
        String userName = getIntent().getStringExtra("chat-user");
        userNameText.setText(userName);
        SharedPreferences sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);
        chatController = new ChatController(sharedPref, userName, chatRecyclerViewAdapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString();
                chatController.sendMessage(message);
                messageEditText.setText("");
            }
        });
    }

    // Use adapter to display chat messages
    // Separate messages with horizontal divider
    private void configureChatRecyclerView(ChatRecyclerViewAdapter chatRecyclerViewAdapter) {
        chatRecyclerView.setAdapter(chatRecyclerViewAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}