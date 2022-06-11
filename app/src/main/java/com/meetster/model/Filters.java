package com.meetster.model;

public class Filters {
    public final String university;
    public final String specialty;
    public final String subject;
    public final String tag;

    public Filters(String university, String specialty, String subject, String tag) {
        this.university = university;
        this.specialty = specialty;
        this.subject = subject;
        this.tag = tag;
    }
}
