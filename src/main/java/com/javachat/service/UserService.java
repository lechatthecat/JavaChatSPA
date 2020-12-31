package com.javachat.service;

import com.javachat.model.User;

import java.util.List;

public interface UserService {
    boolean createUser(User user);
    boolean reCreateUser(User user);
    boolean save(User user);
    boolean sendForgotPasswordLink(User user);
    boolean deleteUser(User user);
    User findByName(String name);
    User findByEmail(String email);
    User findByEmailIncludesDeleted(String email);
    User findByEmailIncludesUnverified(String email);
    User findById(long id);
    User findByEmailDetached(String email);
    List<User> findUsersByName(String name);
    List<User> findUsersByEmail(String email);
    List<String> getUsersOfTopic();
    List<String> getUsersOfTopic(String boardId);
}