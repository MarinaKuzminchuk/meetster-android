package com.meetster.filter;

import static com.meetster.PreferencesKeys.FILTERS_SPECIALTY;
import static com.meetster.PreferencesKeys.FILTERS_TAG;

import android.content.SharedPreferences;

import com.meetster.model.Filters;

public class FilterController {

    private final SharedPreferences sharedPref;

    public FilterController(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    public Filters getFilters() {
        String specialty = sharedPref.getString(FILTERS_SPECIALTY, "");
        String tag = sharedPref.getString(FILTERS_TAG, "");
        return new Filters(specialty, tag);
    }

    public void saveFilters(Filters filters) {
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(FILTERS_SPECIALTY, filters.specialty);
        ed.putString(FILTERS_TAG, filters.tag);
        ed.commit();
    }
}
