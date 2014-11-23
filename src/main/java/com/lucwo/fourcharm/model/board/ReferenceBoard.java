/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package com.lucwo.fourcharm.model.board;

import java.util.Arrays;
import java.util.logging.Logger;

import com.lucwo.fourcharm.exception.InvalidMoveException;

/**
 * Class for modeling an board for the game connect four. This class's
 * responsibility is to keep the state of the board. This is the reference
 * implementation implemented using an 2D array
 * 
 * @author Luce Sandfort and Wouter Timmermans
 *
 */

public class ReferenceBoard extends Board {

    // ------------------ Class variables ----------------
    /**
     * Amount of spots in the board.
     */
    public static final int SIZE = COLUMNS * ROWS;
    /**
     * Value assigned to an empty spot.
     */
    public static final int EMPTY = -1;
    /**
     * Value assigned to an spot for player1.
     */
    public static final int PLAYER1 = 0;
    /**
     * Value assigned to an spot for player2.
     */
    public static final int PLAYER2 = 1;

    // ------------------ Instance variables ----------------
    // Array with moves since the start of the game
    private int[] moves; 
    // Amount of turns since the start of the game
    private int nplies; 
    // Holds bitboard for every color
    private int[][] board; 

    // --------------------- Constructors -------------------

    /**
     * Creates an new instance of an Board. In its initial state the board is
     * empty
     */
    public ReferenceBoard() {

        board = new int[COLUMNS][ROWS];
        for (int i = 0; i < COLUMNS; i++) {
            Arrays.fill(board[i], EMPTY);
        }

        moves = new int[SIZE];
        reset();
    }

    // ----------------------- Queries ----------------------

    public boolean columnHasFreeSpace(int col) {

        return board[col][ROWS - 1] == EMPTY;

    }

    public boolean lastMoveWon() {

        int player = nplies & 1;

        return hasLRDiagonal(player) || hasHorizontal(player)
                || hasRLDiagonal(player) || hasVertical(player);

    }

    public boolean isFull() {

        return nplies >= SIZE - 1;

    }

    public int getPlieCount() {

        return nplies;

    }

    private boolean hasLRDiagonal(int player) {

        boolean diag = false;

        for (int column = WIN_STREAK; column < COLUMNS && !diag; column++) {
            int streak = 0;
            int tColumn = column;
            for (int row = 0; row < ROWS && tColumn < COLUMNS; row++, tColumn--) {
                streak = board[column][row] == player ? streak + 1 : 0;
            }
            diag = streak == WIN_STREAK;
        }

        return diag;

    }

    private boolean hasRLDiagonal(int player) {

        boolean diag = false;

        for (int column = COLUMNS - WIN_STREAK; column >= 0 && !diag; column--) {
            int streak = 0;
            int tColumn = column;
            for (int row = 0; row < ROWS && tColumn < COLUMNS; row++, tColumn++) {
                streak = board[column][row] == player ? streak + 1 : 0;
            }
            diag = streak == WIN_STREAK;
        }

        return diag;

    }

    private boolean hasHorizontal(int player) {

        boolean horizontal = false;

        for (int row = 0; row < ROWS && !horizontal; row++) {
            int streak = 0;
            for (int column = 0; column < COLUMNS && streak < WIN_STREAK; column++) {
                streak = board[column][row] == player ? streak + 1 : 0;
            }
            horizontal = streak == WIN_STREAK;

        }

        return horizontal;

    }

    private boolean hasVertical(int player) {

        boolean vertical = false;

        for (int column = 0; column < COLUMNS && !vertical; column++) {
            int streak = 0;
            for (int row = 0; row < ROWS && streak < WIN_STREAK; row++) {
                streak = board[column][row] == player ? streak + 1 : 0;
            }
            vertical = streak == WIN_STREAK;

        }

        return vertical;

    }

    public String toString() {
        StringBuilder repr = new StringBuilder();

        for (int i = 0; i < nplies; i++) {
            repr.append(1 + moves[i]);
        }
        repr.append("\n");
        for (int w = 0; w < COLUMNS; w++) {
            repr.append(" " + (w + 1));
        }
        repr.append("\n");

        for (int row = ROWS - 1; row >= 0; row--) {
            for (int column = 0; column < COLUMNS; column++) {
                int player = board[column][row];
                if (player == PLAYER1) {
                    repr.append(" @");
                } else if (player == PLAYER2) {
                    repr.append(" 0");
                } else if (player == EMPTY) {
                    repr.append(" .");
                }
            }
            repr.append("\n");
        }

        return repr.toString();
    }

    public Board deepCopy() {
        ReferenceBoard boardCopy = new ReferenceBoard();
        boardCopy.reset();
        for (int i = 0; i < nplies; i++) {
            try {
                boardCopy.makemove(moves[i]);
            } catch (InvalidMoveException e) {
                Logger.getGlobal().throwing("ReferenceBoard", "deepCopy", e);
            }
            
        }
        return boardCopy;
    }

    // ----------------------- Commands ---------------------

    /**
     * 
     * @param col
     * @requires gets called for the player which current turn it is
     */
    public void makemove(int col) throws InvalidMoveException {
        
        
        // same as modulo 2 but probably more efficient
        int player = nplies & 1; 

        moves[nplies] = col;

        // Increment the plie count
        nplies++; 

        boolean needPlacement = true;

        for (int i = 0; i < ROWS && needPlacement; i++) {
            if (board[col][i] == EMPTY) {
                board[col][i] = player;
                needPlacement = false;
            }
        }

        if (needPlacement) {
            
            
            throw new InvalidMoveException("This column has no more free space");
        }

    }

    private void reset() {
        nplies = 0;
        board = new int[COLUMNS][ROWS];
        for (int i = 0; i < COLUMNS; i++) {
            Arrays.fill(board[i], EMPTY);
        }
    }

}
