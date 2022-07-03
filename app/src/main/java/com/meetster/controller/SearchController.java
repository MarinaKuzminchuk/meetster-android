package com.meetster.controller;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.meetster.model.FoundUser;

import java.util.Arrays;
import java.util.List;

public class SearchController {

    public static final String PREVIOUSLY_FOUND_USERS = "previouslyFoundUsers";
    private final SharedPreferences sharedPref;
    private final Gson gson;

    public SearchController(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
        this.gson = new Gson();
    }

    // get json containing previously found users from shared preferences
    // parse this json to a list of found users
    public List<FoundUser> getPreviouslyFoundUsers() {
        String json = sharedPref.getString(PREVIOUSLY_FOUND_USERS, "");
        FoundUser[] foundUsers = gson.fromJson(json, FoundUser[].class);
        return Arrays.asList(foundUsers);
    }

    // convert previously found users to json
    // and save it in shared preferences to save them on device
    public void savePreviouslyFoundUsers(List<FoundUser> previouslyFoundUsers) {
        String json = gson.toJson(previouslyFoundUsers);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(PREVIOUSLY_FOUND_USERS, json);
        ed.commit();
    }
}
