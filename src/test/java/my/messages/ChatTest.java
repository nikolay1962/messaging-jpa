package my.messages;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChatTest {

    private static Chat chat;

    @Before
    public void setUp() throws Exception {
        chat = new Chat("Test Chat", "MessageFile", 1234567890L);
    }

    @Test
    public void getName_IfReturnsCorrectValue() {
        String name = chat.getName();

        assertEquals("Test Chat", name);
    }

    @Test
    public void getMessageFile_IfReturnsCorrectValue() {
        String messageFile = chat.getMessageFile();

        assertEquals("MessageFile", messageFile);
    }

    @Test
    public void getMessageChecker_IfReturnsNullValue() {
        MessageChecker messageChecker = chat.getMessageChecker();

        assertNull(messageChecker);
    }

    @Test
    public void getCheckingThread_IfReturnsCorrectValue() {
        Thread checkinThread = chat.getCheckingThread();

        assertNull(checkinThread);
    }

    @Test
    public void getLastTimeOfMessageGet_IfReturnsCorrectValue() {
        long lastTimeOfMessageGet = chat.getLastTimeOfMessageGet();

        assertEquals(1234567890L, lastTimeOfMessageGet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMessageChecker_IfThrowsIllegalArgumentException() {
        chat.setMessageChecker(null);
    }

    @Test
    public void setCheckingThread_IfThrowsIllegalArgumentException() {
        try {
            chat.setCheckingThread(null);
            fail("setCheckingThread: no Exception was thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Chat Test Chat: null is sent as Thread.", e.getMessage());
        }
    }

    @Test
    public void setLastTimeOfMessageGet_IfAssignsCorrectValue() {

        chat.setLastTimeOfMessageGet(98765432L);
        assertEquals(98765432L, chat.getLastTimeOfMessageGet());
    }

}