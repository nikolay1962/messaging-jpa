package my.messages;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.type.TimestampType;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static my.messages.UserServices.FORMATTER;

public class DBAccess implements DataAccess{

    private final SessionFactory sessionFactory;

    private static final String dbURL = "jdbc:mysql://localhost:3306/messenger";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private  Connection conn;

    private PreparedStatement selectUserByEmailStmt;
    private PreparedStatement updateUserByEmailStmt;

    private PreparedStatement addMessage;
    private PreparedStatement addUser;

    private PreparedStatement selectNewMessagesForUser;


    public DBAccess(SessionFactory sessionFactory) {

            this.sessionFactory = sessionFactory;

    }

//    public Connection getConn() {
//        return conn;
//    }

    public User getUserByEmail(String email) {

        Session session = sessionFactory.openSession();
        Transaction transaction = transaction = session.beginTransaction();

        User user = (User) session.createQuery(
                "select user from User user " +
                        "where user.email = :email")
                .setParameter("email", email)
                .uniqueResult();

        transaction.commit();
        session.close();

        return user;
    }

    public User getUserById(long id) {

        Session session = sessionFactory.openSession();
        Transaction transaction = transaction = session.beginTransaction();

        User user = (User) session.get(User.class, id);

        transaction.commit();
        session.close();

        return user;
    }


    @Override
    public boolean saveProperties(User user) {

        if (user == null || getUserByEmail(user.getEmail()) == null) {
            return addUser(user);
        } else {
            return updateUser(user);
        }

    }

    private boolean addUser(User user) {
        user.setLastLogoutTime(LocalDateTime.now().format(FORMATTER));

        Session session = sessionFactory.openSession();

        session.beginTransaction();
        user.setLastLogoutTime(LocalDateTime.now().format(FORMATTER));
        session.save(user);
        session.getTransaction().commit();
        session.close();

        return true;

    }

    private boolean updateUser(User user) {

        Session session = sessionFactory.openSession();

        session.beginTransaction();
        user.setLastLogoutTime(LocalDateTime.now().format(FORMATTER));
        session.update(user);
        session.getTransaction().commit();
        session.close();

        return true;

    }

    @Override
    public boolean sendMessage(String userFrom, String userTo, String messageText) {
        User tempUser = getUserByEmail(userFrom);
        long from_id = tempUser == null ? -1 : tempUser.getUser_id();

        tempUser = getUserByEmail(userTo);
        long to_id = tempUser == null ? -1 : tempUser.getUser_id();
        if (from_id == -1 || to_id == -1) {
            return false;
        }


        String datetime = LocalDateTime.now().format(FORMATTER);
        try {

            for (int i = 0; i * 80 < messageText.length(); i++) {
                int upperBound = (i + 1) * 80 < messageText.length() ? (i + 1) * 80 : messageText.length();

                Message message = new Message(to_id, new Date(), i, from_id, messageText.substring(i * 80, upperBound));
                saveMessage(message);

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveMessage(Message message) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(message);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public Properties getProperties(String email) {

        Properties config = new Properties();

        try {

//            selectUserByEmailStmt.setString(1, email);
            Session session = sessionFactory.openSession();
            Transaction transaction = transaction = session.beginTransaction();

            User user = (User) session.createQuery(
                    "select user from User user " +
                            "where user.email = :email")
                    .setParameter("email", email)
                    .uniqueResult();

            if (user != null) {

                String s = (user.getlastLogoutTime() == null) ?
                        "" :
                        user.getlastLogoutTime();
                StringBuilder propertiesFromDB = new StringBuilder();
                propertiesFromDB.append("user_id=" + user.getUser_id() + '\n'
                        + "email=" + user.getEmail() + '\n'
                        + "name=" + user.getName() + '\n'
                        + "password=" + user.getPassword() + '\n'
                        + "lastLogoutTime=" + s + '\n'
                        + "lastTimeOfMessageGet=" + user.getLastTimeOfMessageGet() + '\n'
                        + "age=" + user.getAge() + '\n');

                // create a new reader
                StringReader reader = new StringReader(propertiesFromDB.toString());

                transaction.commit();
                session.close();
                try {
                    // load from input stream
                    config.load(reader);
                    return config;

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {

        }


        return null;
    }

    @Override
    public boolean isCreated() {
        return this.conn != null;
    }

    @Override
    public boolean checkIfHaveNewMessages(String incomingMessages, long lastMessageMillis) {

        boolean returnValue = false;

//        String lastMessageTime = Instant.ofEpochMilli(lastMessageMillis).atZone(ZoneId.systemDefault()).toLocalDateTime().format(FORMATTER);

        return getListOfNewMessages(incomingMessages, lastMessageMillis).size() > 0;

    }

    private List<Message> getListOfNewMessages(String incomingMessages, long lastMessageMillis) {
        List<Message> messages = new ArrayList<>();
        Date lastMessageDate = new Date(lastMessageMillis);

        long user_to = Long.parseLong(incomingMessages);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            Query query = session.createQuery("from Message m " +
                    " where m.user_to = :user_to and m.datetime >= :datetime order by datetime", Message.class);

            query.setParameter("user_to", user_to);
            query.setParameter("datetime", lastMessageDate, TimestampType.INSTANCE);

            messages = query.list();

        } catch (Exception e) {
            e.printStackTrace();
        }
        session.close();
        return messages;
    }

    @Override
    public List<String> getChatMessages(Chat chat) {
        // 2018-10-08T18:39:17.602; from first@mail.com; User first@mail.com has left the chat.

        long user_id = Long.parseLong(chat.getMessageFile());

        List<Message> messages = getListOfNewMessages(chat.getMessageFile(), chat.getLastTimeOfMessageGet());

        List<String> stringMessages = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        for (Message message : messages) {
            // process message to string
            sb.setLength(0);
            sb.append(message.getDatetime())
                    .append("; from ")
                    .append(getUserById(user_id).getName())
                    .append("; ")
                    .append(message.getMessage());
            stringMessages.add(sb.toString());
        }
        return stringMessages;
    }

    @Override
    public boolean isUserExists(String email) {

        User user = getUserByEmail(email);
        return user != null;
    }

    @Override
    public String messageFileName(String recepient) {
        User tempUser = getUserByEmail(recepient);
        long user_id = tempUser == null ? -1 : tempUser.getUser_id();
        if (user_id == -1) {
            return null;
        }

        return String.valueOf(user_id);
    }
}
