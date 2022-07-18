package com.meetster.search;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

import android.Manifest;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.meetster.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class SearchActivityTest {
    @Rule
    public ActivityTestRule<SearchActivity> activityRule =
            new ActivityTestRule<>(SearchActivity.class);
//    @Rule
//    public GrantPermissionRule permissionScan = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_SCAN);
//    @Rule
//    public GrantPermissionRule permissionConnect = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_CONNECT);
//    @Rule
//    public GrantPermissionRule permissionAdvertise = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_ADVERTISE);

    @Rule
    public GrantPermissionRule permissionBt = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH);
    @Rule
    public GrantPermissionRule permissionBtAdmin = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_ADMIN);
    @Rule
    public GrantPermissionRule permissionACL = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION);
    @Rule
    public GrantPermissionRule permissionAFL = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testSearchStarts() {
        onView(withId(R.id.search)).perform(click());

        onView(withId(R.id.search)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageBt)).check(matches(withDrawable(R.drawable.ic_action_on)));
    }

    @Test
    public void testSearchStops() {
        onView(withId(R.id.search)).perform(click());
        onView(withId(R.id.stopSearch)).perform(click());

        onView(withId(R.id.stopSearch)).check(matches(not(isDisplayed())));
        onView(withId(R.id.imageBt)).check(matches(withDrawable(R.drawable.ic_action_off)));
    }

    // https://medium.com/@dbottillo/android-ui-test-espresso-matcher-for-imageview-1a28c832626f
    public static Matcher<View> withDrawable(int resourceId) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View target) {
                if (!(target instanceof ImageView)){
                    return false;
                }
                ImageView imageView = (ImageView) target;
                if (resourceId < 0){
                    return imageView.getDrawable() == null;
                }
                Resources resources = target.getContext().getResources();
                Drawable expectedDrawable = resources.getDrawable(resourceId);
                if (expectedDrawable == null) {
                    return false;
                }
                Bitmap bitmap = getBitmap(imageView.getDrawable());
                Bitmap otherBitmap = getBitmap(expectedDrawable);
                return bitmap.sameAs(otherBitmap);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable from resource id: ");
                description.appendValue(resourceId);
            }

            private Bitmap getBitmap(Drawable drawable){
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }
        };
    }
}