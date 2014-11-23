/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package main.java.model.board;

import main.java.exception.InvalidMoveException;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public abstract class Board {
    
    /**
     * Amount of columns of the board.
     */
    protected static final int COLUMNS = 7;
    /**
     * Amount of rows of the board.
     */
    protected static final int ROWS = 6;
    /**
     * Amount of players.
     */
    protected static final int PLAYERS = 2;
    /**
     * Amount of pieces in a row a player has to have to win.
     */
    protected static final int WIN_STREAK = 4;

    public abstract boolean columnHasFreeSpace(int col);

    public abstract boolean lastMoveWon();

    public abstract boolean isFull();

    public abstract int getPlieCount();
    
    public int getColumns() {
        return COLUMNS;
    }
    
    public int getRows() {
        return ROWS;
    }
    
    public int getPlayers() {
        return PLAYERS;
    }
    
    public int getWinStreak() {
        return WIN_STREAK;
    }

    /**
     * 
     * @param col
     * @throws InvalidMoveException when an invalid move is entered
     * @requires gets called for the player which current turn it is
     */

    public abstract void makemove(int col) throws InvalidMoveException;

    /**
     * Returns an deepcopy of the board.
     * 
     * @return deepcopy of this board
     */
    public abstract Board deepCopy();

}
