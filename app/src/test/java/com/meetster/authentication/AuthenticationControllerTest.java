package com.meetster.authentication;

import static com.meetster.PreferencesKeys.PREF_AUTHENTICATED_USER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import com.meetster.model.User;

import org.junit.Test;

public class AuthenticationControllerTest {

    private SharedPreferences sharedPreferences = mock(SharedPreferences.class);
    private AuthenticationController authenticationController =
            new AuthenticationController(sharedPreferences);

    @Test
    public void getUser() {
        String testUserName = "testUser";
        when(sharedPreferences.getString(PREF_AUTHENTICATED_USER, "")).thenReturn(testUserName);

        User user = authenticationController.getUser();

        assertEquals(testUserName, user.name);
    }

    @Test
    public void saveUser() {
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(editor);

        User user = new User("testUser");
        authenticationController.saveUser(user);

        verify(editor).putString(PREF_AUTHENTICATED_USER, user.name);
        verify(editor).commit();
    }
}