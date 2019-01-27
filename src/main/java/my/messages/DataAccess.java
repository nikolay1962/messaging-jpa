package my.messages;

import java.util.List;
import java.util.Properties;

public interface DataAccess {

    Properties getProperties(String email);

    boolean saveProperties(User user);

    boolean sendMessage(String userFrom, String userTo, String message);

    boolean isCreated();

    boolean checkIfHaveNewMessages(String incomingMessages, long lastMessageMillis);

    List<String> getChatMessages(Chat chat);

    boolean isUserExists(String email);

    String messageFileName(String recepient);
}
