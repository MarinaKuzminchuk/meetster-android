package com.meetster.filter;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.meetster.R;
import com.meetster.search.SearchActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class FilterActivityTest {
    @Rule
    public ActivityScenarioRule<FilterActivity> activityRule =
            new ActivityScenarioRule<>(FilterActivity.class);

    @Before
    public void setup() {
        // Initializes Intents and begins recording intents.
        Intents.init();
        clearSharedPreferences();
    }

    private void clearSharedPreferences() {
        Context targetContext = getInstrumentation().getTargetContext();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(targetContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    @After
    public void tearDown() {
        // Clears Intents state.
        Intents.release();
    }

    @Test
    public void testFiltersSaved() {
        String speciality = "testSpecialty";
        String tag = "testTag";
        onView(withId(R.id.specialtyText))
                .perform(clearText())
                .perform(typeText(speciality))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.tagText))
                .perform(clearText())
                .perform(typeText(tag))
                .perform(closeSoftKeyboard());

        onView(withId(R.id.saveFiltersButton)).perform(click());

        intended(hasComponent(SearchActivity.class.getName()));

        Espresso.pressBackUnconditionally();

        onView(withId(R.id.specialtyText))
                .check(matches(withText(speciality)));
        onView(withId(R.id.tagText))
                .check(matches(withText(tag)));
    }

    @Test
    public void testOneFilterIsEmpty() {
        String speciality = "testSpecialty";
        onView(withId(R.id.specialtyText))
                .perform(clearText())
                .perform(typeText(speciality))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.tagText)).perform(clearText());
        onView(withId(R.id.saveFiltersButton)).perform(click());

        onView(withId(R.id.filtersValidationError)).check(matches((withText("Both specialty and tag should be specified"))));
    }

    @Test
    public void testTwoFiltersAreEmpty() {
        onView(withId(R.id.specialtyText))
                .perform(clearText())
                .perform(closeSoftKeyboard());
        onView(withId(R.id.tagText))
                .perform(clearText())
                .perform(closeSoftKeyboard());
        onView(withId(R.id.saveFiltersButton)).perform(click());

        onView(withId(R.id.filtersValidationError)).check(matches((withText("Both specialty and tag should be specified"))));
    }
}