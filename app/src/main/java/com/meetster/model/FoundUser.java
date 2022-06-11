package com.meetster.model;

public class FoundUser {
    public final User user;
    public final Filters filters;

    public FoundUser(User user, Filters filters) {
        this.user = user;
        this.filters = filters;
    }
}
