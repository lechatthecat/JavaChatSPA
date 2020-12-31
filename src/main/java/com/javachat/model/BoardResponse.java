package com.javachat.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "board_responses")
public class BoardResponse implements Comparable<BoardResponse> {

    public enum ValidMsgType {
        CHAT,
        JOIN, 
        LEAVE
    }

    @Enumerated(EnumType.STRING)
    private ValidMsgType msg_type;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String response;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    private User user;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    private Board board;
    private boolean is_deleted;
    @JsonIgnore
    @Column(name="ip_address")
    private String ipAddress;
    @Column(name="is_deleted_by_admin")
    private boolean isDeletedByAdmin;
    @Column(name="is_first")
    private boolean isFirst;
    @Transient
    private String sender;
    @Transient
    @JsonIgnore
    private String board_table_url_name;
    @JsonIgnore
    @Transient
    private String category_string_id;
    @Transient
    private String user_image_path;
    @JsonIgnore
    private ZonedDateTime updated;
    @JsonIgnore
    private ZonedDateTime created;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ValidMsgType getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(ValidMsgType msg_type) {
        this.msg_type = msg_type;
    }

    public String getResponse() {
        if (user != null && user.getIsBanned()) {
            return "This user was banned.";
        }
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return this.board;
    }

    public void setIsDeleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public boolean getIsDeleted() {
        return this.is_deleted;
    }

    public void setIsDeletedByAdmin(boolean isDeletedByAdmin) {
        this.isDeletedByAdmin = isDeletedByAdmin;
    }

    public boolean getIsDeletedByAdmin() {
        return this.isDeletedByAdmin;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean getIsFirst() {
        return this.isFirst;
    }

    public void setUserImagePath(String user_image_path) {
        this.user_image_path = user_image_path;
    }

    public String getUserImagePath() {
        return this.user_image_path;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public ZonedDateTime getUpdated() {
        return this.updated;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getCreated() {
        return this.created;
    }

    public String getUSStringUpdated() {
        if (this.updated != null) {
            ZonedDateTime time = this.updated.withZoneSameInstant(ZoneId.of("America/Chicago"));
            String stringTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(time);
            return stringTime;
        }
        return "";
    }

    public String getUSStringCreated() {
        if (this.created != null) {
            ZonedDateTime time = this.created.withZoneSameInstant(ZoneId.of("America/Chicago"));
            String stringTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(time);
            return stringTime;
        }
        return "";
    }

    public String getSender() {
        if ((sender == null || sender.equals("")) && user != null) {
            sender = user.getUsernameNonEmail();
        }
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBoardTable_url_name() {
        if(this.board!=null){
            this.board_table_url_name = this.board.getTable_url_name();
            return this.board_table_url_name;
        }else{
            return this.board_table_url_name;
        }
    }

    public void setBoardTable_url_name(String board_table_url_name) {
        this.board_table_url_name = board_table_url_name;
    }

    public String getCategoryStringId() {
        return this.category_string_id;
    }

    public void setCategoryStringId(String category_string_id) {
        this.category_string_id = category_string_id;
    }

    @Override
    public int compareTo(BoardResponse br){
        if (created == null || br.created == null) {
            return 0;
        }
        return created.compareTo(br.created);
    }
}