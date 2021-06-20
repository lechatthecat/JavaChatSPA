package com.javachat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Email;

@Entity
@Table(name = "users")
@Getter @Setter
public class User implements UserDetails {
    private static final long serialVersionUID = 5978279130943396080L;

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Email
    private String email;
    @ManyToOne
    private Role role;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<UserImage> userImages;
    private String password;
    @Transient
    private UserImage userMainImage;
    private ZonedDateTime updated;
    private ZonedDateTime created;
    @Transient
    private String passwordConfirm;
    @Column(name="is_deleted")
    private boolean isDeleted;
    @Column(name="fail_times")
    private int failTimes;
    @Column(name="is_verified")
    private boolean isVerified;
    @Column(name="is_banned")
    private boolean isBanned;
    @Transient
    private boolean agreesTerm = false;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BoardResponse> boardResponses;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Board> boards;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserConfirmation> userConfirmations;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserForgotemail> userForgotemails;
    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    public String getUsernameNonEmail() {
        if (this.isDeleted) {
            return "Deleted user";
        } else if (this.isBanned) {
            return "Banned user";
        }
        return this.name;
    }

    public void setUsernameNonEmail(String name) {
        this.name = name;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public void setUsername(String email) {
        this.email = email;
    }
    
    @Override
    public String getPassword() {
        return this.password;
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
    
    public UserImage getUserMainImage() {
        if (this.userMainImage == null) {
            this.userMainImage = new UserImage();
        } else {
            for (UserImage image : userImages) {
                if (image.isMain()) {
                    this.userMainImage = image;
                    break;
                }
            }
        }
        return this.userMainImage;
    };

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role.getName()));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}