package my.messages;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class UserServices {

    IOUtils ioUtils;

    public static  final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public UserServices(IOUtils ioUtils) {
        this.ioUtils = ioUtils;
    }

    public User getUser(String email, String password) throws IOException {
        User user = null;
        //read file
        Properties config = ioUtils.getProperties(email);

        //get fields from file
        if (config != null && password.equals(config.getProperty("password"))) {
            user = new User(config.getProperty("email"),
                    config.getProperty("name"),
                    Integer.parseInt(config.getProperty("age")),
                    config.getProperty("password"),
                    Long.parseLong(config.getProperty("lastTimeOfMessageGet")),
                    Integer.parseInt(config.getProperty("user_id", "0")), this);
            String lastLogoutTime = (config.getProperty("lastLogoutTime").equals("")) ?
                    LocalDateTime.now().format(FORMATTER) :
                    config.getProperty("lastLogoutTime");
            user.setLastLogoutTime(lastLogoutTime);
        }
        //create new user

        return user;
    }

    public boolean saveData(User user) {
        if (user == null) {
            return false;
        }

        return ioUtils.saveUserData(user);
    }

    public boolean isUserExists(String email) {
        return ioUtils.isUserExists(email);
    }

    public User newUser(String email, String password, String name, int age) {
        User user = new User(email, name, age, password, 0L, 0, this);
        if (saveData(user)) {
            return user;
        } else {

            return null;
        }
    }

    public void addUser() {
        System.out.println("Please, enter your personal data:");
        boolean proceed = false;
        User user = null;
        String email = null;
        while (true) {
            email = ioUtils.getValidEmail("Enter your email:");
            if (email == null) {
                break;
            }
            boolean exists = isUserExists(email);
            if (exists) {
                ioUtils.writeMessage(email + " is already registered in a system.");
            } else {
                proceed = true;
                break;
            }
        }

        if (proceed) {
            String password = ioUtils.getNotEmptyString("Enter password:");
            String name = ioUtils.getNotEmptyString("Enter your name:");
            int age = ioUtils.getPositiveInteger("Enter your age:");

            user = newUser(email, password, name, age);
        }

        if (user != null) {
            runUser(user);
        }
    }

    public void login() throws IOException {
        String email = ioUtils.getValidEmail("Enter your email:");

        if (email == null || email.isEmpty()) {
            return;
        }
        String password = ioUtils.getNotEmptyString("Enter password:");

        User user = getUser(email, password);
        if (user != null) {
            ioUtils.writeMessage("Hello mr." + user.getName());
            ioUtils.writeMessage("You have not been here since " + user.getlastLogoutTime());
            runUser(user);
        } else {
            ioUtils.writeMessage("Wrong username/password for " + email);
        }
    }

    public void logout(User user) {
        for (int i = user.getChats().size() - 1; i >= 0 ; i--) {
            Chat chat = user.getChats().get(i);
            stopCheckingChat(user, i);
        }
        ioUtils.saveUserData(user);
    }

    void startGettingMessages(User user) {
        MessageChecker messageChecker = new MessageChecker();
        messageChecker.setIoUtils(ioUtils);
        int chatsLastIndex = user.getChats().size() - 1;
        Chat lastChat = user.getChats().get(chatsLastIndex);
        messageChecker.setChat(lastChat);

        lastChat.setMessageChecker(messageChecker);

        Thread thread = new Thread(messageChecker);
        thread.start();
        lastChat.setCheckingThread(thread);
    }

    public void sendMessageToUser(User currentUser) {
        int indexOfChat = 0;
        String messageFile = "";
        String recepient = "";
        if (currentUser.getChats().size() > 1) {
            indexOfChat = getChatForAction(currentUser.getChats(), "Enter index of Chat to send message", false);
            if (indexOfChat > 0) {
                messageFile = currentUser.getChats().get(indexOfChat).getMessageFile();
                recepient = currentUser.getChats().get(indexOfChat).getName();
            }
        }
        if (indexOfChat == 0){ //send personal message.
            recepient = getValidUserEmail("Enter recepients email:");

            if (recepient == null) {
                ioUtils.writeMessage("Unable to get Recepient\'s name");
                return;
            }
        }

        String message = ioUtils.getNotEmptyString("Enter your message:");

        if (message == null) {
            ioUtils.writeMessage("Message was NOT sent to " + recepient);
            return;
        }

        boolean result = ioUtils.sendMessage(currentUser.getEmail(), recepient, message);
        if (result) {
            ioUtils.writeMessage("Message was sent to " + recepient);
        } else {
            ioUtils.writeMessage("Message was NOT sent to " + recepient);
        }
    }

    private int getChatForAction(List<Chat> chats, String request, boolean onlyChat) {
        int index = -1;
        int lower = onlyChat ? 1 : 0;
        while (true) {
            ioUtils.writeMessage("index: Chat Name");
            for (int i = lower; i < chats.size(); i++) {
                ioUtils.writeMessage(i + ": \t" + chats.get(i).getName());
            }
            index = ioUtils.getIntegerWithinBounds(request, lower, chats.size() - 1);
            if (index >= 0 && index < chats.size()) {
                break;
            }
        }
        return index;
    }

    private String getValidUserEmail(String currentUserEmail, String message) {

        while (true) {
            String email = ioUtils.getValidEmail(message);
            if (email == null) {
                return null;
            }

            if (currentUserEmail.equals(email)) {
                continue;
            }
            if (!isUserExists(email)) {
                ioUtils.writeMessage("No such user:" + email);
                continue;
            } else {
                return email;
            }
        }
    }

    private String getValidUserEmail(String message) {

        return getValidUserEmail("allowed to yourself", message);
    }

    private void runUser(User user) {

        boolean proceed = true;
        String choice = "";
        startGettingMessages(user);

        while (proceed) {
            printMenu(user);
            choice = ioUtils.getInputFromUser();
            switch (choice) {
                case "1":
                    proceed = false;
                    if (user != null) {
                        logout(user);
                    }
                    user = null;
                    break;
                case "2":
                    if (user == null) {
                        ioUtils.writeMessage("Please, login first.");
                    } else {
                        sendMessageToUser(user);
                    }
                    break;
                case "3":
                    if (user == null) {
                        ioUtils.writeMessage("Please, login first.");
                    } else {
                        ioUtils.saveUserData(user);
                    }
                    break;
                case "4":
                    //join chat
                    joinChat(user);
                    break;
                case "5":
                    //start chat
                    startNewChat(user);
                    break;
                case "6":
                    //invite to chat
                    inviteOtherUserToChat(user);
                    break;
                case "7":
                    //quit chat
                    quitChat(user);
                    break;
            }
        }
    }

    private void joinChat(User user) {
        String chatFileName = ioUtils.getNotEmptyString("Enter Chat Code, received in Invitation:");
//        if (ioUtils.fileExists(chatFileName)) {
//
//            String chatName = ioUtils.getNotEmptyString("Enter Chat name:");
//            Chat newChat = new Chat(chatName, chatFileName, System.currentTimeMillis());
//            user.getChats().add(newChat);
//            startGettingMessages(user);
//
//        } else {
//            ioUtils.writeMessage("No chat with code " + chatFileName);
//        }
    }

    private void inviteOtherUserToChat(User user) {
        if (user.getChats().size() < 2) {
            ioUtils.writeMessage("No chats available.");
            return;
        }
        int indexOfChat = getChatForAction(user.getChats(), "Enter index of CHAT to invite", true);
        String chatMesssages = user.getChats().get(indexOfChat).getMessageFile();

        String otherUser = getValidUserEmail(user.getEmail(), "Enter email of user to invite:");

        if (otherUser == null) {
            ioUtils.writeMessage("No one was invited");
        }

        boolean result = ioUtils.sendMessage(user.getEmail(), otherUser,
                "Mr." + user.getName() + " is inviting you to join his chat. Please, join, using code below:");
        if (result) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = ioUtils.sendMessage(user.getEmail(), otherUser, chatMesssages);
        }
        if (result) {
            ioUtils.writeMessage("Invitation to chat:" + chatMesssages + " was sent to " + otherUser);
        } else {
            ioUtils.writeMessage("Unable to send invitation to " + otherUser);
        }

    }

    private void quitChat(User user) {
        if (user.getChats().size() > 1) {
            int indexOfChat = getChatForAction(user.getChats(), "Enter index of CHAT to quit", true);
            if (indexOfChat > 0) {
                stopCheckingChat(user, indexOfChat);
            } else {
                ioUtils.writeMessage("You stay in a chat.");
                return;
            }
        }
    }

    private void stopCheckingChat(User user, int indexOfChat) {

        Chat chat = user.getChats().get(indexOfChat);

        String messageFile = chat.getMessageFile();
        ioUtils.sendMessage(user.getEmail(), messageFile,
                "User " + user.getEmail() + " has left the chat.");

        chat.getMessageChecker().terminate();
        try {
            chat.getCheckingThread().join();
        } catch (InterruptedException e) {
            ioUtils.writeMessage("Message Checker tread finished with Exception");
        }
        if (indexOfChat == 0) {
            user.setLastTimeOfMessageGet(chat.getLastTimeOfMessageGet());
        }

        user.getChats().remove(indexOfChat);
    }

    private void startNewChat(User user) {
        String chatFileName = null;
        try {
            chatFileName = chatFileName(user.getEmail());
        } catch (NullStringException e) {
            ioUtils.writeMessage("Unable to get current user email.");
            return;
        }
        String chatName = ioUtils.getNotEmptyString("Enter Chat name:");
        // check if file for new CHAT exists and send creation message to it.
//        if (!ioUtils.fileExists(chatFileName)) {
//            boolean result = ioUtils.sendMessage(user.getEmail(), chatFileName, chatName + " was created.");
//            if (result) {
//                ioUtils.writeMessage("Message was sent to " + chatName);
//            } else {
//                ioUtils.writeMessage("Unable to send message to new CHAT " + chatName);
//                return;
//            }
//
//        }
        Chat newChat = new Chat(chatName, chatFileName, System.currentTimeMillis());
        user.getChats().add(newChat);
        startGettingMessages(user);
    }

    private static void printMenu(User currentUser) {
        String whoAmI = currentUser == null ? "Unknown user" : currentUser.getName();
        System.out.println("1 - Logout");
        System.out.println("2 - Send Message");
        System.out.println("3 - Save data");
        System.out.println("4 - Join Chat");
        System.out.println("5 - Start Chat");
        System.out.println("6 - Invite to Chat");
        System.out.println("7 - Quit Chat");
        System.out.println("Please, enter your choice, mr." + whoAmI);
    }



    public String chatFileName(String recepient) throws NullStringException {
        return null;
//        if (recepient == null) {
//            throw new NullStringException("null value was provided to chatFileName.");
//        }
//        return UserServices.CHAT_PREFIX_SUFFIX[0] + recepient + UserServices.CHAT_PREFIX_SUFFIX[1];
    }

//    public void init() {
//
//        LoginView loginView = new LoginView();
//        loginView.show(null);
//    }

    public String messageFileName(String email) {
        return ioUtils.dataAccess.messageFileName(email);
    }
}
