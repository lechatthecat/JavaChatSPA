package com.javachat.error;

public class UserIsBannedException extends Exception {
    private static final long serialVersionUID = -78443576504171565L;

    public UserIsBannedException(String errorMessage) {
        super(errorMessage);
    }
}
