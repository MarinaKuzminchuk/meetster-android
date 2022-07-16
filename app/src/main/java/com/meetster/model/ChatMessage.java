package com.meetster.model;

import java.util.Date;
import java.util.Objects;

public class ChatMessage {
    public final String sentBy;
    public final String text;
    public final Date timestamp;

    public ChatMessage(String sentBy, String text, Date timestamp) {
        this.sentBy = sentBy;
        this.text = text;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(sentBy, that.sentBy) && Objects.equals(text, that.text) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sentBy, text, timestamp);
    }
}
