package my.messages;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;

    private String name;
    private String email;
    private String password;
    private int age;
    private String lastLogoutTime;

    @Column(name = "lastgetmessagetime")
    private volatile long lastTimeOfMessageGet;

    @Transient
    private String incomingMessages;

    @Transient
    private List<Chat> chats;

    @Transient
    private UserServices userServices;

    public User() {
    }

    public User(String email, String name, int age, String password, long lastTimeOfMessageGet, int user_id, UserServices userServices) {
        this.userServices = userServices;
        this.email = email;
        this.name = name;
        this.age = age;
        this.password = password;
        this.lastTimeOfMessageGet = lastTimeOfMessageGet;
        this.incomingMessages = userServices.messageFileName(email);
        this.chats = new ArrayList<>();
        Chat userMessages = new Chat("Personal for " + name, incomingMessages, lastTimeOfMessageGet);
        chats.add(userMessages);
        this.user_id = user_id;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public String getlastLogoutTime() {
        return lastLogoutTime;
    }

    public long getLastTimeOfMessageGet() {
        return lastTimeOfMessageGet;
    }

    public void setLastLogoutTime(String lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setIncomingMessages(String incomingMessages) {
        this.incomingMessages = incomingMessages;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public void setUserServices(UserServices userServices) {
        this.userServices = userServices;
    }

    public void setLastTimeOfMessageGet(long lastTimeOfMessageGet) {
        this.lastTimeOfMessageGet = lastTimeOfMessageGet;
    }

    public List<Chat> getChats() {
        return chats;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("email='").append(email).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append(", password='").append(password).append('\'');
        sb.append(", lastLogoutTime=").append(lastLogoutTime);
        sb.append('}');
        return sb.toString();
    }

}
