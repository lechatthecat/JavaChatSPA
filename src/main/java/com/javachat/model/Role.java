package com.javachat.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    private static final long serialVersionUID = 5978209130942396080L;
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean is_admin;
    private int power_level;
    private String name;
    private boolean is_deleted;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy="role")
    private List<User> users;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getIsAdmin() {
        return this.is_admin;
    }

    public void setIsAdmin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    public int getPowerLevel() {
        return this.power_level;
    }

    public void setPowerLevel(int power_level) {
        this.power_level = power_level;
    }

    public String getName() {
        return this.name;
    }

    public String getRoleName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setIsDeleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public boolean getIsDeleted() {
        return this.is_deleted;
    }

    @Override
    public String getAuthority() {
        return this.name;
    }
}
