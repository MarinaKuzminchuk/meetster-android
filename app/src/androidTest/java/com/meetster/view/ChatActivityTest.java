package com.meetster.view;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.meetster.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class ChatActivityTest {

    @Test
    public void test1(){
        Intent startActivityIntent =
                new Intent(ApplicationProvider.getApplicationContext(), ChatActivity.class)
                        .putExtra("chat-user", "testChatUserName");
        try (ActivityScenario<ChatActivity> scenario = ActivityScenario.launch(startActivityIntent)) {
            scenario.onActivity(activity -> {
                String testMessage = "testMessage " + UUID.randomUUID();
                onView(withId(R.id.messageEditText))
//                        .perform(clearText())
                        .perform(typeText(testMessage))
                        .perform(closeSoftKeyboard());
                onView(withId(R.id.sendBtn)).perform(click());

                onView(withText(testMessage));
                System.out.println(activity);
            });
        }
    }
    @Test
    public void test2(){

    }
    @Test
    public void test3(){

    }
    @Test
    public void test4(){

    }
}