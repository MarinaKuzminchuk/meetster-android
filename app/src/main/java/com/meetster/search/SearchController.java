package com.meetster.search;

import static com.meetster.PreferencesKeys.PREF_FOUND_USERS;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meetster.model.Filters;
import com.meetster.model.FoundUser;
import com.meetster.model.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchController {

    private final SharedPreferences sharedPref;
    private final Gson gson;
    private int previouslyFoundUsersIndex;
    private User authenticatedUser;
    private Filters filters;

    public SearchController(SharedPreferences sharedPref, User authenticatedUser, Filters filters) {
        this.sharedPref = sharedPref;
        this.authenticatedUser = authenticatedUser;
        this.filters = filters;
        this.gson = new Gson();
        this.previouslyFoundUsersIndex = 0;
    }

    public String getBluetoothName() {
        return "meetster/" + authenticatedUser.name + "/" + filters.specialty + "/" + filters.tag;
    }

    public void addNewlyFoundUser(String btName) {
        FoundUser foundUser = parseFoundUser(btName);
        if (foundUser == null) {
            return;
        }
        if (atLeastOneFilterMatches(foundUser)) {
            List<FoundUser> foundUsers = getFoundUsers();
            for (int i = 0; i < foundUsers.size(); i++) {
                FoundUser previouslyFoundUser = foundUsers.get(i);
                if (previouslyFoundUser.user.name.equals(foundUser.user.name)) {
                    foundUsers.remove(i);
                    if (i < previouslyFoundUsersIndex) {
                        previouslyFoundUsersIndex--;
                    }
                    break;
                }
            }
            foundUsers.add(0, foundUser);
            previouslyFoundUsersIndex++;
            saveFoundUsers(foundUsers);
        }
    }

    private FoundUser parseFoundUser(String btName) {
        if (btName == null || !btName.startsWith("meetster")) {
            return null;
        }
        String[] parts = btName.split("/");
        if (parts.length < 4) {
            return null;
        }
        String name = parts[1];
        String specialty = parts[2];
        String tag = parts[3];
        return new FoundUser(new User(name), new Filters(specialty, tag));
    }

    private boolean atLeastOneFilterMatches(FoundUser foundUser) {
        return filters.specialty.equals(foundUser.filters.specialty)
                || filters.tag.equals(foundUser.filters.tag);
    }

    public List<FoundUser> getPreviouslyFoundUsers() {
        List<FoundUser> foundUsers = getFoundUsers();
        return foundUsers.subList(previouslyFoundUsersIndex, foundUsers.size());
    }

    public List<FoundUser> getNewlyFoundUsers() {
        List<FoundUser> foundUsers = getFoundUsers();
        return foundUsers.subList(0, previouslyFoundUsersIndex);
    }

    // get json containing previously found users from shared preferences
    // parse this json to a list of found users
    public List<FoundUser> getFoundUsers() {
        String json = sharedPref.getString(PREF_FOUND_USERS, "");
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
        ed.putString(PREF_FOUND_USERS, json);
        ed.commit();
    }
}
