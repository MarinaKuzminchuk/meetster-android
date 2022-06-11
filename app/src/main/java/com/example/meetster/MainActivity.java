package com.example.meetster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText text;
    Button confirm;
    SharedPreferences sharedPref;

    public static final String EXTRA_MESSAGE = "com.example.meetster.MESSAGE";
    final String SAVED_NAME = "saved_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (EditText) findViewById(R.id.editTextTextPersonName);
        confirm = (Button) findViewById(R.id.button);
        confirm.setOnClickListener(this);
        sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);

        loadText();
    }

    private void loadText() {
        String name = sharedPref.getString(SAVED_NAME, "");
        text.setText(name);
    }

    /**
     * Called when the user taps the confirm button
     */
    public void onClick(View view) {
        saveText();
        Intent intent = new Intent(this, FilterActivity.class);
        String message = text.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void saveText() {
        SharedPreferences.Editor ed = sharedPref.edit();
        String name = text.getText().toString();
        ed.putString(SAVED_NAME, name);
        ed.commit();
        Toast.makeText(MainActivity.this, "Your name - " + name + " - is saved", Toast.LENGTH_SHORT).show();
    }
}
