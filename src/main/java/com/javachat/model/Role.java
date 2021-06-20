package com.javachat.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.Column;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "roles")
@Getter @Setter
public class Role implements GrantedAuthority {
    private static final long serialVersionUID = 5978209130942396080L;
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean is_admin;
    private int power_level;
    private String name;
    @Column(name="is_deleted")
    private boolean isDeleted;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy="role")
    private List<User> users;

    @Override
    public String getAuthority() {
        return this.name;
    }
}
