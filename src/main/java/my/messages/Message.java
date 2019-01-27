package my.messages;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long user_to;

    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;

    private int line_no;

    private long user_from;

    private String message;

    public Message() {}

    public Message(long user_to, Date datetime, int line_no, long user_from, String message) {
        this.user_to = user_to;
        this.datetime = datetime;
        this.line_no = line_no;
        this.user_from = user_from;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_to() {
        return user_to;
    }

    public void setUser_to(long user_to) {
        this.user_to = user_to;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public int getLine_no() {
        return line_no;
    }

    public void setLine_no(int line_no) {
        this.line_no = line_no;
    }

    public long getUser_from() {
        return user_from;
    }

    public void setUser_from(long user_from) {
        this.user_from = user_from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
