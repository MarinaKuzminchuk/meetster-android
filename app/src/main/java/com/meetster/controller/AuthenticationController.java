package com.meetster.controller;

import android.content.SharedPreferences;

import com.meetster.model.User;

public class AuthenticationController {

    static final String SAVED_USER_NAME = "saved_user_name";
    private final SharedPreferences sharedPref;

    public AuthenticationController(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    // Get user name from shared preferences
    public User getUser() {
        String userName = sharedPref.getString(SAVED_USER_NAME, "");
        return new User(userName);
    }

    // Save user name in shared preferences
    public void saveUser(User user) {
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(SAVED_USER_NAME, user.name);
        ed.commit();
    }
}
