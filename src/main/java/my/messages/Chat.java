package my.messages;

public class Chat {
    private String name;
    private String messageFile;
    private MessageChecker messageChecker;
    private Thread checkingThread;
    private volatile long lastTimeOfMessageGet;

    public Chat(String name, String messageFile, long lastTimeOfMessageGet) {
        this.name = name;
        this.messageFile = messageFile;
        this.lastTimeOfMessageGet = lastTimeOfMessageGet;
    }

    public String getName() {
        return name;
    }

    public String getMessageFile() {
        return messageFile;
    }

    public MessageChecker getMessageChecker() {
        return messageChecker;
    }

    public Thread getCheckingThread() {
        return checkingThread;
    }

    public long getLastTimeOfMessageGet() {
        return lastTimeOfMessageGet;
    }

    public void setMessageChecker(MessageChecker messageChecker) {
        if (messageChecker == null) {
            throw new IllegalArgumentException("Chat " + this.name + ": null is sent as MessageChecker.");
        }
        this.messageChecker = messageChecker;
    }

    public void setCheckingThread(Thread checkingThread) {
        if (checkingThread == null) {
            throw new IllegalArgumentException("Chat " + this.name + ": null is sent as Thread.");
        }
        this.checkingThread = checkingThread;
    }

    public void setLastTimeOfMessageGet(long lastTimeOfMessageGet) {
        this.lastTimeOfMessageGet = lastTimeOfMessageGet;
    }
}
