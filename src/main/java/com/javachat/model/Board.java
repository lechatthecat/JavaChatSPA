package com.javachat.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Transient;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JsonIgnore
    private User user;
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER)
    private List<BoardResponse> boardResponses;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardCategory> boardCategories;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BoardUser> boardUsers;
    private String name;
    private String table_url_name;
    private String detail;
    @JsonIgnore
    private boolean is_private;
    @JsonIgnore
    private boolean is_deleted;
    @JsonIgnore
    @Column(name="is_deleted_by_admin")
    private boolean isDeletedByAdmin;
    private ZonedDateTime updated;
    private ZonedDateTime created;
    @Transient
    private String url_name;
    @Transient
    private boolean agreesTerm = false;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        if (this.isDeletedByAdmin) {
            return "Deleted By admin";
        }
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable_url_name() {
        return this.table_url_name;
    }

    public void setTable_url_name(String table_url_name) {
        this.table_url_name = table_url_name;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<BoardResponse> getBoardResponses() {
        return this.boardResponses;
    }

    public void setBoardResponses(List<BoardResponse> boardResponses) {
        this.boardResponses = boardResponses;
    }

    public List<BoardCategory> getBoardCategories() {
        return this.boardCategories;
    }

    public void setBoardCategories(List<BoardCategory> boardCategories) {
        this.boardCategories = boardCategories;
    }

    public void setIsPrivate(boolean is_private) {
        this.is_private = is_private;
    }

    public boolean getIsPrivate() {
        return this.is_private;
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
        ZonedDateTime time = this.updated.withZoneSameInstant(ZoneId.of("America/Chicago"));
        String stringTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(time);
        return stringTime;
    }

    public String getUSStringCreated() {
        ZonedDateTime time = this.created.withZoneSameInstant(ZoneId.of("America/Chicago"));
        String stringTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(time);
        return stringTime;
    }

    public boolean getAgreesTerm() {
        return this.agreesTerm;
    }

    public void setAgreesTerm(boolean agreesTerm) {
        this.agreesTerm = agreesTerm;
    }

    // Spring cannot convert url_name to UrlName.
    // So, getUrl_name sounds a bit weird, but cannot change.
    public String getUrl_name() {
        return this.url_name;
    }

    public void setUrl_name(String url_name) {
        this.url_name = url_name;
    }
}
