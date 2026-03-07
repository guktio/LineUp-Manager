package com.grenade.main.exception;

public class SteamAuthException extends RuntimeException {
    
    public SteamAuthException(String message){
        super(message);
    }
    
    // public SteamAuthException(String message, Throwable cause){
    //     super(message, cause);
    // }
}
