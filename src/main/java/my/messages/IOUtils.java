package my.messages;

import org.hibernate.SessionFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class IOUtils {

    Scanner scanner;

    DataAccess dataAccess;

    public IOUtils(SessionFactory sessionFactory) {
        this.scanner = new Scanner(System.in);
        if (sessionFactory == null) {
            dataAccess = new FileAccess();
        } else {
            this.dataAccess = new DBAccess(sessionFactory);
        }
    }


    public void writeMessage(String message) {
        System.out.println(message);
    }

    public String getInputFromUser() {
        return scanner.nextLine();
    }

    public int getPositiveInteger(String request) {
        int integer = 0;
        while (integer < 1 || integer > 100) {
            writeMessage(request);
            try {
                integer = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                integer = 0;
            }
        }
        return integer;
    }

    public int getIntegerWithinBounds(String request, int lower, int upper) {
        int integer = lower - 1;
        while (integer < lower || integer > upper) {
            writeMessage(request);
            try {
                integer = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                integer = lower - 1;
            }
        }
        return integer;
    }

    public String getNotEmptyString(String request) {
        String input = "";
        while (input.isEmpty()) {
            System.out.print(request);
            input = scanner.nextLine();
        }
        return input;
    }

    public String getValidEmail(String message) {

        while (true) {
            writeMessage(message);

            String email = getInputFromUser();
            if (email == null || email.isEmpty()) {
                return null;
            }
            if (!email.contains("@")) {
                continue;
            }

            return email;
        }
    }

    public boolean sendMessage(String userFrom, String userTo, String message) {
        return dataAccess.sendMessage(userFrom, userTo, message);
    }


    public Properties getProperties(String email) {

        return dataAccess.getProperties(email);

    }


    public boolean saveUserData(User user) {

        return dataAccess.saveProperties(user);

    }

    public boolean isUserExists(String email) {
        return dataAccess.isUserExists(email);
    }


    public boolean checkIfHaveNewMessages(String incomingMessages, long lastMessageMillis) {
        return dataAccess.checkIfHaveNewMessages(incomingMessages, lastMessageMillis);
    }

    public void displayToUser(Chat chat) {

        // variable to compare times;

        String timeOfLastPrintedMessage = Instant.ofEpochMilli(chat.getLastTimeOfMessageGet()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString();

            List<String> lines = dataAccess.getChatMessages(chat);
        if (lines == null) {
            writeMessage("NULL was returned from dataAccess.getChatMessages(chat)");
            return;
        }

            boolean printChatName = true;

            for (String line : lines) {
                /*if (line.indexOf(';') > 0) {
                    String timeOfMessage = line.substring(0, line.indexOf(';'));
                    if (timeOfLastPrintedMessage.compareTo(timeOfMessage) < 0) {
                        //display to user.
                        if (printChatName) {
                            printChatName = false;
                            writeMessage("<++++++++++ " + chat.getName() + " ++++++++++>");
                        }
                        writeMessage('\t' + line);
                        timeOfLastPrintedMessage = timeOfMessage;
                    }
                }*/

                //filtered messages were returned, we just print.
                if (printChatName) {
                    printChatName = false;
                    writeMessage("<++++++++++ " + chat.getName() + " ++++++++++>");
                }
                writeMessage('\t' + line);
            }

        //change lastTimeOfMessageGet in user data
        chat.setLastTimeOfMessageGet(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

    }

}
