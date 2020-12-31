package com.javachat.error;

public class EmailAddressNotVerifiedException extends Exception {
    private static final long serialVersionUID = -984586779104171565L;

    public EmailAddressNotVerifiedException(String errorMessage) {
        super(errorMessage);
    }
}
