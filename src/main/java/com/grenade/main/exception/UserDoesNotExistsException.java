package com.grenade.main.exception;

public class UserDoesNotExistsException extends Exception {
    public UserDoesNotExistsException(String message){
        super(message);
    }
}
