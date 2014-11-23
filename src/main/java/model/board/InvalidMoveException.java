/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package main.java.model.board;

/**
 * Exception that gets thrown when an invalid is attempted to be made.
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class InvalidMoveException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = -6960421680924145617L;
    
    /**
     * InvalidMoveException without an message.
     */
    public InvalidMoveException() {
            
    }
        
    /**
     * InvalidMoveException with an message.
     * @param message used for displaying helpful error messages to user
     */
    public InvalidMoveException(String message) {
        super(message); 
    }

}
