/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.exception;

/**
 * Exception that gets thrown when an invalid is attempted to be made.
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class InvalidMoveException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = -148107440122750510L;

    /**
     * InvalidMoveException without an message.
     */
    public InvalidMoveException() {
        super();

    }
        
    /**
     * InvalidMoveException with an message.
     * @param message used for displaying helpful error messages to user
     */
    public InvalidMoveException(String message) {
        super(message); 
    }

}
