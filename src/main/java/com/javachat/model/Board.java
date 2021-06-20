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

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Transient;

@Entity
@Table(name = "boards")
@Getter @Setter
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
    @Column(name="table_url_name")
    private String tableUrlName;
    private String detail;
    @JsonIgnore
    @Column(name="is_private")
    private boolean isPrivate;
    @JsonIgnore
    @Column(name="is_deleted")
    private boolean isDeleted;
    @JsonIgnore
    @Column(name="is_deleted_by_admin")
    private boolean isDeletedByAdmin;
    private ZonedDateTime updated;
    private ZonedDateTime created;
    @Transient
    @Column(name="url_name")
    private String urlName;
    @Transient
    private boolean agreesTerm = false;

    public String getName() {
        if (this.isDeletedByAdmin) {
            return "Deleted By admin";
        }
        return this.name;
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
}
