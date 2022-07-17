package com.meetster.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.database.DataSnapshot;
import com.meetster.model.ChatMessage;
import com.meetster.view.ChatRecyclerViewAdapter;

import org.junit.Test;

import java.util.Date;

public class ChatMessageListenerTest {
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter = mock(ChatRecyclerViewAdapter.class);
    private ChatMessageListener chatMessageListener = new ChatMessageListener(chatRecyclerViewAdapter);
    private DataSnapshot snapshot = mock(DataSnapshot.class);
    private DataSnapshot sentBySnapshot = mock(DataSnapshot.class);
    private DataSnapshot messageSnapshot = mock(DataSnapshot.class);
    private DataSnapshot timestampSnapshot = mock(DataSnapshot.class);

    @Test
    public void testChatMessageIsDisplayed(){
        String sentBy = "testUserName";
        String message = "testMessage";
        Date timestamp = new Date();
        when(snapshot.child("sentBy")).thenReturn(sentBySnapshot);
        when(snapshot.child("message")).thenReturn(messageSnapshot);
        when(snapshot.child("timestamp")).thenReturn(timestampSnapshot);
        when(sentBySnapshot.getValue(String.class)).thenReturn(sentBy);
        when(messageSnapshot.getValue(String.class)).thenReturn(message);
        when(timestampSnapshot.getValue(Long.class)).thenReturn(timestamp.getTime());

        chatMessageListener.onChildAdded(snapshot, "");

        verify(chatRecyclerViewAdapter).addMessage(new ChatMessage(sentBy, message, timestamp));
    }
}
