package com.meetster.search;

import static com.meetster.PreferencesKeys.FOUND_USERS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.meetster.model.Filters;
import com.meetster.model.FoundUser;
import com.meetster.model.User;
import com.meetster.search.SearchController;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SearchControllerTest {
    private SharedPreferences sharedPreferences = mock(SharedPreferences.class);
    private String authenticatedUserName = "testUserName";
    private User authenticatedUser = new User(authenticatedUserName);
    private String filterSpecialty = "testSpecialty";
    private String filterTag = "testTag";
    private Filters filters = new Filters(filterSpecialty, filterTag);
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

    @Test
    public void getBluetoothName() {
        String bluetoothName = searchController.getBluetoothName();

        String expectedBluetoothName = "meetster/" + authenticatedUserName
                + "/" + filterSpecialty + "/" + filterTag;
        assertEquals(expectedBluetoothName, bluetoothName);
    }

    @Test
    public void addNewlyFoundUserWithNullBtName() {
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn("");

        searchController.addNewlyFoundUser(null);

        assertEquals(new ArrayList<>(), searchController.getFoundUsers());
    }

    @Test
    public void addNewlyFoundUserWithEmptyBtName() {
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn("");

        searchController.addNewlyFoundUser("");

        assertEquals(new ArrayList<>(), searchController.getFoundUsers());
    }

    @Test
    public void addNewlyFoundUserWithBtNameNotStartingWithMeetster() {
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn("");

        searchController.addNewlyFoundUser("mstr");

        assertEquals(new ArrayList<>(), searchController.getFoundUsers());
    }

    @Test
    public void addNewlyFoundUserWithNotAllComponentsInName() {
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn("");

        searchController.addNewlyFoundUser("meetster/testFoundUserName");

        assertEquals(new ArrayList<>(), searchController.getFoundUsers());
    }

    @Test
    public void addNewlyFoundUserWithNotMatchingFilters() {
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn("");

        searchController.addNewlyFoundUser("meetster/testFoundUserName/a/b");

        assertEquals(new ArrayList<>(), searchController.getFoundUsers());
    }

    @Test
    public void addNewlyFoundUserWithMatchingFilters() {
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn("");

        searchController.addNewlyFoundUser("meetster/testFoundUserName/testSpecialty/b");

        FoundUser foundUser = new FoundUser(new User("testFoundUserName"), new Filters("testSpecialty", "b"));
        List<FoundUser> expectedFoundUsers = new ArrayList<>();
        expectedFoundUsers.add(foundUser);
        verify(editor).putString(FOUND_USERS, gson.toJson(expectedFoundUsers));
        verify(editor).commit();
    }

    @Test
    public void addPreviouslyFoundUserWithMatchingFilters() {
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn("");
        searchController.addNewlyFoundUser("meetster/testFoundUserName/testSpecialty/b");
        FoundUser previouslyFoundUser = new FoundUser(new User("testFoundUserName"), new Filters("a", "testTag"));
        List<FoundUser> savedFoundUsers = new ArrayList<>();
        savedFoundUsers.add(previouslyFoundUser);
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn(gson.toJson(savedFoundUsers));

        searchController.addNewlyFoundUser("meetster/testFoundUserName/a/testTag");

        FoundUser foundUser = new FoundUser(new User("testFoundUserName"), new Filters("a", "testTag"));
        List<FoundUser> expectedFoundUsers = new ArrayList<>();
        expectedFoundUsers.add(foundUser);
        verify(editor).putString(FOUND_USERS, gson.toJson(expectedFoundUsers));
    }

    @Test
    public void getPreviouslyAndNoNewlyFoundUsers() {
        List<FoundUser> foundUsers = getTestFoundUsers();
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn(gson.toJson(foundUsers));

        assertEquals(foundUsers, searchController.getPreviouslyFoundUsers());
        assertEquals(new ArrayList<>(), searchController.getNewlyFoundUsers());
    }

    @Test
    public void getPreviouslyAndNewlyFoundUsers() {
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(sharedPreferences.edit()).thenReturn(editor);
        List<FoundUser> foundUsers = getTestFoundUsers();
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn(gson.toJson(foundUsers));
        searchController.addNewlyFoundUser("meetster/testFoundUserName/a/testTag");
        foundUsers.add(new FoundUser(new User("testFoundUserName"), new Filters("a", "testTag")));
        when(sharedPreferences.getString(FOUND_USERS, "")).thenReturn(gson.toJson(foundUsers));

        assertEquals(foundUsers.subList(1, 3), searchController.getPreviouslyFoundUsers());
        assertEquals(foundUsers.subList(0, 1), searchController.getNewlyFoundUsers());
    }

    private List<FoundUser> getTestFoundUsers() {
        List<FoundUser> expectedFoundUsers = new ArrayList<>();
        expectedFoundUsers.add(new FoundUser(new User("1"), new Filters("s1", "t1")));
        expectedFoundUsers.add(new FoundUser(new User("2"), new Filters("s2", "t2")));
        return expectedFoundUsers;
    }
}