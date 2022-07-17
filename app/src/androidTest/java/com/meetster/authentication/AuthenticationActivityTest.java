package com.meetster.authentication;

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
import com.meetster.authentication.AuthenticationActivity;
import com.meetster.filter.FilterActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuthenticationActivityTest {

    @Rule
    public ActivityScenarioRule<AuthenticationActivity> activityRule =
            new ActivityScenarioRule<>(AuthenticationActivity.class);

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
    public void testUserNameSaved() {
        String userName = "testUserName";
        onView(withId(R.id.userNameText))
                .perform(clearText())
                .perform(typeText(userName))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.confirmButton)).perform(click());

        intended(hasComponent(FilterActivity.class.getName()));

        Espresso.pressBackUnconditionally();

        onView(withId(R.id.userNameText))
                .check(matches(withText(userName)));
    }
    @Test
    public void testUserNameIsNotEmpty() {
        String userName = "";
        onView(withId(R.id.userNameText)).perform(clearText());
        onView(withId(R.id.confirmButton)).perform(click());
        onView(withId(R.id.validationError)).check(matches((withText("Your name should not be empty"))));

        onView(withId(R.id.userNameText))
                .check(matches(withText(userName)));
    }
}
