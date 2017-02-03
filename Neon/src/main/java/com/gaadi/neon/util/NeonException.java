package com.gaadi.neon.util;

/**
 * @author princebatra
 * @version 1.0
 * @since 27/1/17
 */
public class NeonException extends Exception {

    String msg;

    public NeonException(String message){
        msg = message;
    }

    @Override
    public String toString() {
        return msg;
    }
}
