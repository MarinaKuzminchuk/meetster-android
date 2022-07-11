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

    private TextView userNameText;
    private EditText messageEditText;
    private ImageView sendBtn;
    private RecyclerView chatRecyclerView;
    private ChatController chatController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userNameText = findViewById(R.id.chatUserNameText);
        messageEditText = findViewById(R.id.messageEditText);
        sendBtn = findViewById(R.id.sendBtn);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        // Get chat user name passed from the search activity
        // (inside recycler view when we click on the found user)
        String userName = getIntent().getStringExtra("chat-user");
        userNameText.setText(userName);

        ChatRecyclerViewAdapter chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(this, userName);
        SharedPreferences sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);
        configureChatRecyclerView(chatRecyclerViewAdapter);

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
    }
}
