package com.example.meetster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    private TextView chatUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatUser = findViewById(R.id.textViewChatUser);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String chatUserName = extras.getString("chat-user");
            chatUser.setText("Chat with " + chatUserName);
        }
    }
}
