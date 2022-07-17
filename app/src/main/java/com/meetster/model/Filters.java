package com.meetster.model;

import java.io.Serializable;
import java.util.Objects;

public class Filters implements Serializable {
    public final String specialty;
    public final String tag;

    public Filters(String specialty, String tag) {
        this.specialty = specialty;
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filters filters = (Filters) o;
        return Objects.equals(specialty, filters.specialty) && Objects.equals(tag, filters.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specialty, tag);
    }

    @Override
    public String toString() {
        return "{" +
                "specialty='" + specialty + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
