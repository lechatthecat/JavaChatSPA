package com.javachat.service;

public interface SecurityService {
    public String findLoggedInUsername();
    public boolean login(String name, String password);
}