package com.javachat.model;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javachat.util.Constants;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_images")
@Getter @Setter
public class UserImage {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String detail;
    @ManyToOne
    private User user;
    private String path;
    @Column(name="is_main")
    private boolean isMain;
    private ZonedDateTime updated;
    private ZonedDateTime created;
    @Column(name="is_deleted")
    private boolean isDeleted;

    public String getPath() {
        if (this.path == null || this.path.equals("")) {
            return Constants.BLANK_USERIMAGE_PATH;
        }
        return this.path;
    }

}