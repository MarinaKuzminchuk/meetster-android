package com.meetster.filter;

import static com.meetster.IntentExtraKeys.EXTRA_AUTHENTICATED_USER;
import static com.meetster.IntentExtraKeys.EXTRA_FILTERS_SPECIALTY;
import static com.meetster.IntentExtraKeys.EXTRA_FILTERS_TAG;
import static com.meetster.PreferencesKeys.PREF_AUTHENTICATED_USER;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meetster.R;
import com.meetster.model.Filters;
import com.meetster.model.User;
import com.meetster.search.SearchActivity;

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
        SharedPreferences sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent.getExtras() == null) {
            authenticatedUser = new User(sharedPref.getString(PREF_AUTHENTICATED_USER, ""));
        } else {
            authenticatedUser = new User(intent.getStringExtra(EXTRA_AUTHENTICATED_USER));
        }

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
                    intent.putExtra(EXTRA_AUTHENTICATED_USER, authenticatedUser.name);
                    intent.putExtra(EXTRA_FILTERS_SPECIALTY, filters.specialty);
                    intent.putExtra(EXTRA_FILTERS_TAG, filters.tag);
                    startActivity(intent);
                }
            }
        });

        Filters filters = filterController.getFilters();
        specialtyText.setText(filters.specialty);
        tagText.setText(filters.tag);
    }
}
