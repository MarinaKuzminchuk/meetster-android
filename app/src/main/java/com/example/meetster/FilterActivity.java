package com.example.meetster;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText university;
    EditText specialty;
    EditText subject;
    EditText tag;
    Button confirm;
    SharedPreferences sharedPref;
    final String SAVED_UNIVERSITY = "saved_university";
    final String SAVED_SPECIALITY = "saved_specialty";
    final String SAVED_SUBJECT = "saved_subject";
    final String SAVED_TAG = "saved_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        university = (EditText) findViewById(R.id.editTextUniversity);
        specialty = (EditText) findViewById(R.id.editTextSpeciality);
        subject = (EditText) findViewById(R.id.editTextSubject);
        tag = (EditText) findViewById(R.id.editTextTag);

        confirm = (Button) findViewById(R.id.buttonSaveFilters);
        confirm.setOnClickListener(this);
        sharedPref = getPreferences(MODE_PRIVATE);

        loadText();
    }
    private void loadText() {
        String universityName = sharedPref.getString(SAVED_UNIVERSITY, "");
        university.setText(universityName);
        String specialtyName = sharedPref.getString(SAVED_SPECIALITY, "");
        specialty.setText(specialtyName);
        String subjectName = sharedPref.getString(SAVED_SUBJECT, "");
        subject.setText(subjectName);
        String tagName = sharedPref.getString(SAVED_TAG, "");
        tag.setText(tagName);
    }

    /** Called when the user taps the confirm button */
    @Override
    public void onClick(View view) {
        savePref();
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void savePref() {
        SharedPreferences.Editor ed = sharedPref.edit();
        String universityName = university.getText().toString();
        ed.putString(SAVED_UNIVERSITY, universityName);
        String specialtyName = specialty.getText().toString();
        ed.putString(SAVED_SPECIALITY, specialtyName);
        String subjectName = subject.getText().toString();
        ed.putString(SAVED_SUBJECT, subjectName);
        String tagName = tag.getText().toString();
        ed.putString(SAVED_TAG, tagName);
        ed.commit();
        Toast.makeText(FilterActivity.this, "Your preferences are saved", Toast.LENGTH_SHORT).show();
    }

}
