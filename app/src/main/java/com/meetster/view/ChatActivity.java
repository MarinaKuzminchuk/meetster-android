package com.meetster.view;

import static com.meetster.view.IntentExtraKeys.AUTHENTICATED_USER;
import static com.meetster.view.IntentExtraKeys.CHAT_USER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meetster.R;
import com.meetster.controller.ChatController;
import com.meetster.controller.ChatMessageListener;
import com.meetster.model.User;

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
        // and authenticated user name
        Intent intent = getIntent();
        User chatUser = new User(intent.getStringExtra(CHAT_USER));
        User authenticatedUser = new User(intent.getStringExtra(AUTHENTICATED_USER));
        userNameText.setText(chatUser.name);

        ChatRecyclerViewAdapter chatRecyclerViewAdapter =
                new ChatRecyclerViewAdapter(this, chatUser);
        configureChatRecyclerView(chatRecyclerViewAdapter);

        ChatMessageListener chatMessageListener = new ChatMessageListener(chatRecyclerViewAdapter);
        chatController = new ChatController(authenticatedUser, chatUser, chatMessageListener);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    chatController.sendMessage(message);
                    messageEditText.setText("");
                }
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
