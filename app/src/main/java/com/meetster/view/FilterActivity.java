package com.meetster.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.meetster.R;
import com.example.meetster.SearchActivity;
import com.meetster.controller.FilterController;
import com.meetster.model.Filters;

public class FilterActivity extends AppCompatActivity {

    private EditText universityText;
    private EditText specialtyText;
    private EditText subjectText;
    private EditText tagText;
    private Button confirmBtn;
    private SharedPreferences sharedPref;
    private FilterController filterController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);
        filterController = new FilterController(sharedPref);

        universityText = (EditText) findViewById(R.id.universityText);
        specialtyText = (EditText) findViewById(R.id.specialtyText);
        subjectText = (EditText) findViewById(R.id.subjectText);
        tagText = (EditText) findViewById(R.id.tagText);
        confirmBtn = (Button) findViewById(R.id.saveFiltersButton);

        // Save user-set filters after s/he clicks on the button
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filters filters = new Filters(
                        universityText.getText().toString(),
                        specialtyText.getText().toString(),
                        subjectText.getText().toString(),
                        tagText.getText().toString()
                );
                filterController.saveFilters(filters);
                Intent intent = new Intent(FilterActivity.this, SearchActivity.class);
                startActivity(intent);
//                Toast.makeText(FilterActivity.this, "Your preferences are saved", Toast.LENGTH_SHORT).show();
            }
        });

        Filters filters = filterController.getFilters();
        universityText.setText(filters.university);
        specialtyText.setText(filters.specialty);
        subjectText.setText(filters.subject);
        tagText.setText(filters.tag);
    }
}
