package com.meetster.controller;

import android.content.SharedPreferences;

import com.meetster.model.Filters;

public class FilterController {

    private static final String SAVED_SPECIALITY = "saved_specialty";
    private static final String SAVED_TAG = "saved_tag";
    private final SharedPreferences sharedPref;

    public FilterController(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    public Filters getFilters() {
        String specialty = sharedPref.getString(SAVED_SPECIALITY, "");
        String tag = sharedPref.getString(SAVED_TAG, "");
        return new Filters(specialty, tag);
    }

    public void saveFilters(Filters filters) {
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(SAVED_SPECIALITY, filters.specialty);
        ed.putString(SAVED_TAG, filters.tag);
        ed.commit();
    }
}
