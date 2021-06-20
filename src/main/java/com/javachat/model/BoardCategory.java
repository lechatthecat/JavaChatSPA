package com.javachat.model;

import java.time.ZonedDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "board_categories")
@Getter @Setter
public class BoardCategory {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    private Board board;
    @ManyToOne(cascade = CascadeType.MERGE)
    private Category category;
    @Column(name="is_deleted")
    private boolean isDeleted;
    private ZonedDateTime updated;
    private ZonedDateTime created;
}
