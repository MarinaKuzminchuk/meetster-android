package com.meetster.authentication;

import static com.meetster.PreferencesKeys.PREF_AUTHENTICATED_USER;

import android.content.SharedPreferences;

import com.meetster.model.User;

public class AuthenticationController {

    private final SharedPreferences sharedPref;

    public AuthenticationController(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    // Get user name from shared preferences
    public User getUser() {
        String userName = sharedPref.getString(PREF_AUTHENTICATED_USER, "");
        return new User(userName);
    }

    // Save user name in shared preferences
    public void saveUser(User user) {
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(PREF_AUTHENTICATED_USER, user.name);
        ed.commit();
    }
}
