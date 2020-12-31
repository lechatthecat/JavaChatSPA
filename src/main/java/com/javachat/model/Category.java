package com.javachat.model;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String url_name;
    private String icon;
    private String detail;
    private boolean is_deleted;
    private ZonedDateTime updated;
    private ZonedDateTime created;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrlName() {
        return url_name;
    }

    public void setUrlName(String url_name) {
        this.url_name = url_name;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setIsDeleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public boolean getIsDeleted() {
        return this.is_deleted;
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
}
