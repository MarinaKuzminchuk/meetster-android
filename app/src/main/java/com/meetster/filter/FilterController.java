package com.meetster.filter;

import static com.meetster.PreferencesKeys.PREF_FILTERS_SPECIALTY;
import static com.meetster.PreferencesKeys.PREF_FILTERS_TAG;

import android.content.SharedPreferences;

import com.meetster.model.Filters;

public class FilterController {

    private final SharedPreferences sharedPref;

    public FilterController(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    public Filters getFilters() {
        String specialty = sharedPref.getString(PREF_FILTERS_SPECIALTY, "");
        String tag = sharedPref.getString(PREF_FILTERS_TAG, "");
        return new Filters(specialty, tag);
    }

    public void saveFilters(Filters filters) {
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(PREF_FILTERS_SPECIALTY, filters.specialty);
        ed.putString(PREF_FILTERS_TAG, filters.tag);
        ed.commit();
    }
}
