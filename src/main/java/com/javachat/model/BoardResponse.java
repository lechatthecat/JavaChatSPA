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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "board_responses")
@Getter @Setter
public class BoardResponse implements Comparable<BoardResponse> {

    public enum ValidMsgType {
        CHAT,
        JOIN, 
        LEAVE,
        ERROR
    }

    @Enumerated(EnumType.STRING)
    @Column(name="msg_type")
    private ValidMsgType msgType;

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
    @Column(name="is_deleted")
    private boolean isDeleted;
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(cascade = CascadeType.MERGE)
    private IpString ipString;
    @Column(name="string_id")
    private String stringId;
    @Column(name="ip_address")
    private String ipAddress;
    @Transient
    private String ipStringForView;
    @Column(name="is_deleted_by_admin")
    private boolean isDeletedByAdmin;
    @Column(name="is_first")
    private boolean isFirst;
    @Column(name="res_number")
    private int resNumber;
    @Transient
    private String sender;
    @Transient
    @JsonIgnore
    @Column(name="board_table_url_name")
    private String boardTableUrlName;
    @JsonIgnore
    @Transient
    @Column(name="category_string_id")
    private String categoryStringId;
    @Transient
    private String userImagePath;
    @JsonIgnore
    private ZonedDateTime updated;
    @JsonIgnore
    private ZonedDateTime created;

    public String getResponse() {
        if (user != null && user.isBanned()) {
            return "This user was banned.";
        }
        return this.response;
    }

    public String getIpStringForView() {
        if (this.ipString != null) {
            return this.ipString.getStringId();
        }
        return this.stringId;
    }

    public String getIpAddress() {
        if (this.ipString != null) {
            return this.ipString.getIpAddress();
        }
        return this.ipAddress;
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

    public String getBoardTable_url_name() {
        if(this.board!=null){
            this.boardTableUrlName = this.board.getTableUrlName();
            return this.boardTableUrlName;
        }else{
            return this.boardTableUrlName;
        }
    }

    @Override
    public int compareTo(BoardResponse br){
        if (created == null || br.created == null) {
            return 0;
        }
        return created.compareTo(br.created);
    }
}