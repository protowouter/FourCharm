/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.exception;


/**
 * Signals that a ServerConnectionException of some sort has occured.
 *
 * @author Luce Sandfort and Wouter Timmermans.
 */
public class ServerConnectionException extends Exception {

// --------------------- Constructors -------------------


    public ServerConnectionException(String message) {
        super(message);
    }

}
