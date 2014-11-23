/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package main.java.model.board;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public interface Board {
    
    /**
     * Amount of columns of the board.
     */
    public static final int COLUMNS = 7;
    /**
     * Amount of rows of the board.
     */
    public static final int ROWS = 6;
    /**
     * Amount of players.
     */
    public static final int PLAYERS = 2;
    
    /**
     * Amount of pieces in a row a player has to have to win.
     */
    public static final int WIN_STREAK = 4;

    public boolean columnHasFreeSpace(int col);

    public boolean lastMoveWon();

    public boolean full();

    public int plieCount();

    /**
     * 
     * @param col
     * @throws InvalidMoveException when an invalid move is entered
     * @requires gets called for the player which current turn it is
     */

    public void makemove(int col) throws InvalidMoveException;

    /**
     * Returns an deepcopy of the board.
     * 
     * @return deepcopy of this board
     */
    public Board deepCopy();

}
