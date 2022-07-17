package com.meetster.controller;

import static com.meetster.controller.PreferencesKeys.FOUND_USERS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.meetster.model.Filters;
import com.meetster.model.FoundUser;
import com.meetster.model.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SearchControllerTest {
    private SharedPreferences sharedPreferences = mock(SharedPreferences.class);
    private User authenticatedUser = new User("testUserName");
    private Filters filters = new Filters("testSpecialty", "testTag");
    private SearchController searchController = new SearchController(sharedPreferences, authenticatedUser, filters);
    private Gson gson = new Gson();

    @Test
    public void getFoundUsers() {
        List<FoundUser> expectedFoundUsers = getTestFoundUsers();
        String testFoundUsers = gson.toJson(expectedFoundUsers);
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn(testFoundUsers);

        List<FoundUser> foundUsers = searchController.getFoundUsers();

        assertEquals(expectedFoundUsers, foundUsers);
    }

    @Test
    public void saveFoundUsers() {
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(editor);

        List<FoundUser> foundUsers = getTestFoundUsers();
        searchController.saveFoundUsers(foundUsers);

        verify(editor).putString(FOUND_USERS, gson.toJson(foundUsers));
        verify(editor).commit();
    }

    private List<FoundUser> getTestFoundUsers() {
        List<FoundUser> expectedFoundUsers = new ArrayList<>();
        expectedFoundUsers.add(new FoundUser(new User("1"), new Filters("s1", "t1")));
        expectedFoundUsers.add(new FoundUser(new User("2"), new Filters("s2", "t2")));
        return expectedFoundUsers;
    }
}