package com.meetster.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.meetster.R;
import com.meetster.controller.FilterController;
import com.meetster.model.Filters;

public class FilterActivity extends AppCompatActivity {

    private EditText specialtyText;
    private EditText tagText;
    private Button confirmBtn;
    private FilterController filterController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        SharedPreferences sharedPref = getSharedPreferences("meetster", MODE_PRIVATE);
        filterController = new FilterController(sharedPref);

        specialtyText = (EditText) findViewById(R.id.specialtyText);
        tagText = (EditText) findViewById(R.id.tagText);
        confirmBtn = (Button) findViewById(R.id.saveFiltersButton);

        // Save user-set filters after s/he clicks on the button
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filters filters = new Filters(
                        specialtyText.getText().toString(),
                        tagText.getText().toString()
                );
                filterController.saveFilters(filters);
                Intent intent = new Intent(FilterActivity.this, SearchActivity.class);
                startActivity(intent);
//                Toast.makeText(FilterActivity.this, "Your preferences are saved", Toast.LENGTH_SHORT).show();
            }
        });

        Filters filters = filterController.getFilters();
        specialtyText.setText(filters.specialty);
        tagText.setText(filters.tag);
    }
}
