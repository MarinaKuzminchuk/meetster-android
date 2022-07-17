package com.meetster.view;

import static com.meetster.view.IntentExtraKeys.AUTHENTICATED_USER;
import static com.meetster.view.IntentExtraKeys.FILTERS_SPECIALTY;
import static com.meetster.view.IntentExtraKeys.FILTERS_TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.meetster.R;
import com.meetster.controller.FilterController;
import com.meetster.model.Filters;
import com.meetster.model.User;

public class FilterActivity extends AppCompatActivity {

    private EditText specialtyText;
    private EditText tagText;
    private Button confirmBtn;
    private FilterController filterController;
    private TextView filtersValidationErrorText;
    private User authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Intent intent = getIntent();
        authenticatedUser = new User(intent.getStringExtra(AUTHENTICATED_USER));

        SharedPreferences sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);
        filterController = new FilterController(sharedPref);

        specialtyText = findViewById(R.id.specialtyText);
        tagText = findViewById(R.id.tagText);
        confirmBtn = findViewById(R.id.saveFiltersButton);
        filtersValidationErrorText = findViewById(R.id.filtersValidationError);
        filtersValidationErrorText.setText("");

        // Save user-set filters after s/he clicks on the button
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filters filters = new Filters(
                        specialtyText.getText().toString().trim(),
                        tagText.getText().toString().trim()
                );
                // checks if at least one of the filters is specified to go to the next page
                if (filters.specialty.isEmpty() || filters.tag.isEmpty()) {
                    filtersValidationErrorText.setText("Both specialty and tag should be specified");
                } else {
                    filterController.saveFilters(filters);
                    Intent intent = new Intent(FilterActivity.this, SearchActivity.class);
                    intent.putExtra(AUTHENTICATED_USER, authenticatedUser.name);
                    intent.putExtra(FILTERS_SPECIALTY, filters.specialty);
                    intent.putExtra(FILTERS_TAG, filters.tag);
                    startActivity(intent);
                }
            }
        });

        Filters filters = filterController.getFilters();
        specialtyText.setText(filters.specialty);
        tagText.setText(filters.tag);
    }
}
