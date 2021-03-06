package com.meetster.filter;

import static com.meetster.PreferencesKeys.PREF_FILTERS_SPECIALTY;
import static com.meetster.PreferencesKeys.PREF_FILTERS_TAG;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import com.meetster.model.Filters;

import org.junit.Test;

public class FilterControllerTest {
    private SharedPreferences sharedPreferences = mock(SharedPreferences.class);
    private FilterController filterController = new FilterController(sharedPreferences);

    @Test
    public void getFilters() {
        String testSpecialty = "specialtyTest";
        String testTag = "tagTest";
        when(sharedPreferences.getString(PREF_FILTERS_SPECIALTY, "")).thenReturn(testSpecialty);
        when(sharedPreferences.getString(PREF_FILTERS_TAG, "")).thenReturn(testTag);

        Filters filters = filterController.getFilters();

        assertEquals(testSpecialty, filters.specialty);
        assertEquals(testTag, filters.tag);
    }

    @Test
    public void saveFilters() {
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(editor);

        Filters filters = new Filters("specialtyTest", "tagTest");
        filterController.saveFilters(filters);

        verify(editor).putString(PREF_FILTERS_SPECIALTY, filters.specialty);
        verify(editor).putString(PREF_FILTERS_TAG, filters.tag);
        verify(editor).commit();
    }
}