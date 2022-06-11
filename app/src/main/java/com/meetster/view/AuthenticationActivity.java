package com.meetster.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.meetster.FilterActivity;
import com.example.meetster.R;
import com.meetster.controller.AuthenticationController;
import com.meetster.model.User;

public class AuthenticationActivity extends AppCompatActivity {
    private EditText userNameText;
    private Button confirmBtn;
    private SharedPreferences sharedPref;
    private AuthenticationController authenticationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);
        authenticationController = new AuthenticationController(sharedPref);

        setContentView(R.layout.activity_main);
        userNameText = (EditText) findViewById(R.id.userNameText);
        confirmBtn = (Button) findViewById(R.id.confirmButton);
        // Save user name when user clicks confirm button
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = userNameText.getText().toString();
                User user = new User(userName);
                authenticationController.saveUser(user);
//                Toast.makeText(AuthenticationActivity.this, "Your name - " + userName + " - is saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AuthenticationActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        });
        // Load user name when activity opens
        User user = authenticationController.getUser();
        userNameText.setText(user.name);
    }
}
