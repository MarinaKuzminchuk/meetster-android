package com.meetster.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.example.meetster.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class ChatActivityTest {

    @Rule
    public ActivityTestRule<ChatActivity> activityRule =
            new ActivityTestRule<>(ChatActivity.class, false, false);

    // https://www.testrisk.com/2019/05/how-to-set-shared-preferences-in.html
    @Before
    public void startActivityWithTestUser() {
        String userName = "testChatUserName-" + UUID.randomUUID();
        Intent startActivityIntent =
                new Intent(ApplicationProvider.getApplicationContext(), ChatActivity.class)
                        .putExtra("chat-user", userName);
        activityRule.launchActivity(startActivityIntent);
    }

    @Test
    public void testSendMessage() {
        String testMessage = "testMessage " + UUID.randomUUID();
        sendTestMessage(testMessage);

        onView(withId(R.id.messageEditText)).check(matches(withText("")));
        onView(withId(R.id.chatRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(testMessage)))));
    }

    @Test
    public void testSendTwoMessagesInTimeOrder() {
        String testMessage1 = "testMessage1 " + UUID.randomUUID();
        String testMessage2 = "testMessage2 " + UUID.randomUUID();
        sendTestMessage(testMessage1);
        sendTestMessage(testMessage2);

        onView(withId(R.id.messageEditText)).check(matches(withText("")));
        onView(withId(R.id.chatRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(testMessage1)))));
        onView(withId(R.id.chatRecyclerView))
                .check(matches(atPosition(1, hasDescendant(withText(testMessage2)))));
    }

    @Test
    public void testEmptyMessageIsNotSent() {
        String testMessage = "";
        sendTestMessage(testMessage);

        onView(withId(R.id.chatRecyclerView)).check(matches(hasChildCount(0)));
        onView(withId(R.id.chatRecyclerView))
                .check(matches(not(atPosition(0, hasDescendant(withText(testMessage))))));
    }

    private void sendTestMessage(String testMessage) {
        onView(withId(R.id.messageEditText))
                .perform(typeText(testMessage))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.sendBtn)).perform(click());
    }

    // https://stackoverflow.com/a/34795431
    public static Matcher<View> atPosition(int position, Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
