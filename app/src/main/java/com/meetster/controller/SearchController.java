package com.meetster.controller;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetster.model.FoundUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchController {

    public static final String FOUND_USERS = "foundUsers";
    private final SharedPreferences sharedPref;
    private final Gson gson;

    public SearchController(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
        this.gson = new Gson();
    }

    // get json containing previously found users from shared preferences
    // parse this json to a list of found users
    public List<FoundUser> getFoundUsers() {
        String json = sharedPref.getString(FOUND_USERS, "");
        if (json.equals("")) {
            return new ArrayList<>();
        } else {
            // https://stackoverflow.com/questions/5554217/deserialize-a-listt-object-with-gson
            Type foundUserListType = new TypeToken<ArrayList<FoundUser>>(){}.getType();
            return gson.fromJson(json, foundUserListType);
        }
    }

    // convert previously found users to json
    // and save it in shared preferences to save them on device
    public void saveFoundUsers(List<FoundUser> foundUsers) {
        String json = gson.toJson(foundUsers);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(FOUND_USERS, json);
        ed.commit();
    }
}
