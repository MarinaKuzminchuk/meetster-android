package com.meetster.model;

import java.util.Date;

public class ChatMessage {
    public final String sentBy;
    public final String text;
    public final Date timestamp;

    public ChatMessage(String sentBy, String text, Date timestamp) {
        this.sentBy = sentBy;
        this.text = text;
        this.timestamp = timestamp;
    }
}
