package com.meetster.authentication;

import static com.meetster.PreferencesKeys.AUTHENTICATED_USER_NAME;

import android.content.SharedPreferences;

import com.meetster.model.User;

public class AuthenticationController {

    private final SharedPreferences sharedPref;

    public AuthenticationController(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    // Get user name from shared preferences
    public User getUser() {
        String userName = sharedPref.getString(AUTHENTICATED_USER_NAME, "");
        return new User(userName);
    }

    // Save user name in shared preferences
    public void saveUser(User user) {
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(AUTHENTICATED_USER_NAME, user.name);
        ed.commit();
    }
}
