package com.meetster.search;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.not;

import android.Manifest;
import android.os.Build;

import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.meetster.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class SearchActivityTest {
    @Rule
    public ActivityTestRule<SearchActivity> activityRule =
            new ActivityTestRule<>(SearchActivity.class);
    @Rule
    public GrantPermissionRule permissionScan = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_SCAN);
    @Rule
    public GrantPermissionRule permissionConnect = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_CONNECT);
    @Rule
    public GrantPermissionRule permissionAdvertise = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_ADVERTISE);

//    @Rule
//    public GrantPermissionRule permissionBt = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH);
//    @Rule
//    public GrantPermissionRule permissionBtAdmin = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_ADMIN);
//    @Rule
//    public GrantPermissionRule permissionACL = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION);
//    @Rule
//    public GrantPermissionRule permissionAFL = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void grantPhonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.BLUETOOTH_SCAN");
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.BLUETOOTH_CONNECT");
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.BLUETOOTH_ADVERTISE");
        }
    }

    @Test
    public void testSearchStarts() {
        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageBt)).check(matches(not(isDisplayed())));
    }
}