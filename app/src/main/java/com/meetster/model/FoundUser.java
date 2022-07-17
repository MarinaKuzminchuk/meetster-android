package com.meetster.model;

import java.util.Objects;

public class FoundUser {
    public final User user;
    public final Filters filters;

    public FoundUser(User user, Filters filters) {
        this.user = user;
        this.filters = filters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoundUser foundUser = (FoundUser) o;
        return Objects.equals(user, foundUser.user) && Objects.equals(filters, foundUser.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, filters);
    }
}
