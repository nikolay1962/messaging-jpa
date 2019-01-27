package my.messages;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public class FileAccess implements DataAccess {

    final static String[] PREFIX_SUFFIX = {"to_", ".txt"};
    final static String[] CHAT_PREFIX_SUFFIX = {"CHAT_", ".txt"};

    @Override
    public boolean saveProperties(User user) {
        Properties prop = new Properties();

        try(OutputStream output = new FileOutputStream(user.getEmail())) {

            LocalDateTime currentMoment = LocalDateTime.now();

            // set the properties value
            prop.setProperty("email", user.getEmail());
            prop.setProperty("name", user.getName());
            prop.setProperty("password", user.getPassword());
            prop.setProperty("age", String.valueOf(user.getAge()));
            prop.setProperty("lastLogoutTime", currentMoment.format(UserServices.FORMATTER));
            prop.setProperty("lastTimeOfMessageGet", Long.toString(user.getLastTimeOfMessageGet()));

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
            return true;
        }
        return false;
    }

    @Override
    public boolean sendMessage(String userFrom, String userTo, String message) {

        String messageFile = messageFileName(userTo);

        if (!fileExists(messageFile) && !createFile(messageFile)) {
            return false;
        }

        try(FileWriter fw = new FileWriter(messageFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.print(LocalDateTime.now().toString() + "; from " + userFrom + "; ");
            out.println(message);

        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public List<String> getChatMessages(Chat chat) {
        Path messages = Paths.get(chat.getMessageFile());

        try {
            return Files.readAllLines(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isUserExists(String email) {
        Path userFile = Paths.get(email);
        return fileExists(userFile);
    }

    @Override
    public Properties getProperties(String email) {
        Properties config = new Properties();
        Path userData = Paths.get(email);
        try (InputStream stream = Files.newInputStream(userData)) {
            config.load(stream);
        } catch (IOException e) {
            return null;
        }

        return config;
    }

    @Override
    public boolean isCreated() {
        return true;
    }

    @Override
    public boolean checkIfHaveNewMessages(String incomingMessages, long lastMessageMillis) {
        File file = new File(incomingMessages);

        return file.lastModified() > lastMessageMillis;
    }

    @Override
    public String messageFileName(String recepient) {
        return PREFIX_SUFFIX[0] + recepient + PREFIX_SUFFIX[1];
    }

    private boolean createFile(String filename) {
        Path filePath = Paths.get(filename);
        try {
            Files.createFile(filePath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean fileExists(String filename) {
        Path path = Paths.get(filename);
        return fileExists(path);
    }

    public boolean fileExists(Path path) {
        return Files.exists(path);
    }
}
