package com.javachat.model;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Table(name = "categories")
@Getter @Setter
public class Category {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(name="url_name")
    private String urlName;
    private String icon;
    private String detail;
    @Column(name="is_deleted")
    private boolean isDeleted;
    private ZonedDateTime updated;
    private ZonedDateTime created;
}
