package com.meetster.authentication;

import static com.meetster.IntentExtraKeys.AUTHENTICATED_USER;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.meetster.R;
import com.meetster.model.User;
import com.meetster.filter.FilterActivity;

public class AuthenticationActivity extends AppCompatActivity {
    private EditText userNameText;
    private Button confirmBtn;
    private TextView validationErrorText;
    private AuthenticationController authenticationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);
        authenticationController = new AuthenticationController(sharedPref);

        setContentView(R.layout.activity_main);
        userNameText = findViewById(R.id.userNameText);
        confirmBtn = findViewById(R.id.confirmButton);
        validationErrorText = findViewById(R.id.validationError);
        validationErrorText.setText("");
        // Save user name when user clicks confirm button
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = userNameText.getText().toString();
                if (userName.isEmpty()) {
                    validationErrorText.setText("Your name should not be empty");
                } else {
                    User user = new User(userName);
                    authenticationController.saveUser(user);
                    Intent intent = new Intent(AuthenticationActivity.this, FilterActivity.class);
                    intent.putExtra(AUTHENTICATED_USER, user.name);
                    startActivity(intent);
                }
            }
        });
        // Load user name when activity opens
        User user = authenticationController.getUser();
        userNameText.setText(user.name);
    }
}
