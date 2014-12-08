/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model.board;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Mark;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public abstract class Board {
    
    /**
     * Amount of columns of the board.
     */
    static final int COLUMNS = 7;
    /**
     * Amount of rows of the board.
     */
    static final int ROWS = 6;
    /**
     * Amount of players.
     */
    static final int PLAYERS = 2;
    /**
     * Amount of pieces in a row a player has to have to win.
     */
    static final int WIN_STREAK = 4;

    public abstract boolean columnHasFreeSpace(int col);

    public abstract Mark getMark(int index);

    public Mark getMark(int col, int row) {
        return getMark(col * row);
    }

    public abstract boolean hasWon(Mark mark);

    public abstract boolean isFull();

    public abstract int getPlieCount();

    public abstract int[] getMoves();
    
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
    
    public int getSpotCount() {
        return COLUMNS * ROWS;
    }

    /**
     * 
     * @param col
     * @throws InvalidMoveException when an invalid move is entered
     * @requires gets called for the player which current turn it is
     */

    public abstract void makemove(int col, Mark mark) throws InvalidMoveException;

    /**
     * Returns an deepcopy of the board.
     * 
     * @return deepcopy of this board
     */
    public abstract Board deepCopy();

}
