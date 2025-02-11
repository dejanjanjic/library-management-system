package net.etfbl.korisnikbibliotekeaplikacija.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Comparable<Message>, Serializable {
    private String usernameFrom;
    private String usernameTo;
    private String content;
    private Date createDate;


    public Message() {
        super();
    }


    public Message(String usernameFrom, String usernameTo, String content, Date date) {
        super();
        this.usernameFrom = usernameFrom;
        this.usernameTo = usernameTo;
        this.content = content;
        this.createDate = date;
    }

    public String getUsernameFrom() {
        return usernameFrom;
    }


    public void setUsernameFrom(String usernameFrom) {
        this.usernameFrom = usernameFrom;
    }


    public String getUsernameTo() {
        return usernameTo;
    }


    public void setUsernameTo(String usernameTo) {
        this.usernameTo = usernameTo;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public Date getCreateDate() {
        return createDate;
    }


    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    @Override
    public int compareTo(Message other) {
        return this.createDate.compareTo(other.createDate);
    }
}
