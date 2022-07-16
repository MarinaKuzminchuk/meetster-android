package com.meetster.controller;

import static com.meetster.controller.ChatController.SAVED_USER_NAME;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;

public class ChatControllerTest {
    private SharedPreferences sharedPreferences = mock(SharedPreferences.class);
    private ChatMessageListener chatMessageListener = mock(ChatMessageListener.class);
    private FirebaseDatabase database = mock(FirebaseDatabase.class);
    private DatabaseReference chatsReference = mock(DatabaseReference.class);
    private DatabaseReference chatsByTimeReference = mock(DatabaseReference.class);
    private DatabaseReference chatReference = mock(DatabaseReference.class);
    private DatabaseReference chatMessageReference = mock(DatabaseReference.class);

    @Test
    public void testSendMessage() {
        String userName = "testUserName";
        String myUserName = "testMyUserName";
        String message = "testMessage";
        when(sharedPreferences.getString(SAVED_USER_NAME, "")).thenReturn(myUserName);
        when(database.getReference("chats")).thenReturn(chatsReference);
        when(chatsReference.child(myUserName + "_" + userName)).thenReturn(chatReference);
        when(chatReference.orderByChild("timestamp")).thenReturn(chatsByTimeReference);
        when(chatReference.child(anyString())).thenReturn(chatMessageReference);

        ChatController chatController = new ChatController(database, userName, sharedPreferences, chatMessageListener);
        chatController.sendMessage(message);

        verify(chatsByTimeReference).addChildEventListener(chatMessageListener);
        ArgumentCaptor<Map<String, Object>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(chatMessageReference).updateChildren(mapCaptor.capture());
        Map<String, Object> map = mapCaptor.getValue();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("message", message);
        expectedMap.put("timestamp", ServerValue.TIMESTAMP);
        expectedMap.put("sentBy", myUserName);
        assertEquals(expectedMap, map);
    }
}
