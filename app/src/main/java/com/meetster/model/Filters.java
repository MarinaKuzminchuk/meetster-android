package com.meetster.model;

public class Filters {
    public final String specialty;
    public final String tag;

    public Filters(String specialty, String tag) {
        this.specialty = specialty;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "{" +
                ", specialty='" + specialty + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
