package com.meetster.chat;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.meetster.model.User;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;

public class ChatControllerTest {
    private ChatMessageListener chatMessageListener = mock(ChatMessageListener.class);
    private FirebaseDatabase database = mock(FirebaseDatabase.class);
    private DatabaseReference chatsReference = mock(DatabaseReference.class);
    private DatabaseReference chatsByTimeReference = mock(DatabaseReference.class);
    private DatabaseReference chatReference = mock(DatabaseReference.class);
    private DatabaseReference chatMessageReference = mock(DatabaseReference.class);

    @Test
    public void testSendMessage() {
        String chatUserName = "testUserName";
        String myUserName = "testMyUserName";
        String message = "testMessage";
        when(database.getReference("chats")).thenReturn(chatsReference);
        when(chatsReference.child(myUserName + "_" + chatUserName)).thenReturn(chatReference);
        when(chatReference.orderByChild("timestamp")).thenReturn(chatsByTimeReference);
        when(chatReference.child(anyString())).thenReturn(chatMessageReference);

        ChatController chatController = new ChatController(
                database, new User(myUserName), new User(chatUserName), chatMessageListener);
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
