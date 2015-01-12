/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
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
     * Amount of pieces in a row a player has to have to win.
     */
    static final int WIN_STREAK = 4;

    public abstract boolean columnHasFreeSpace(int col);

    public abstract Mark getMark(int index);

    public Mark getMark(int col, int row) {
        return getMark(col * getRows() + row);
    }

    public abstract boolean hasWon(Mark mark);

    public abstract boolean isFull();


    //TODO: pliecount belongs in Game
    public abstract int getPlieCount();

    //TODO: pliecount belongs in Game
    public abstract int[] getMoves();


    /**
     * @return The amount of columns of this board.
     * @ensures result > 0 && result >= getWinStreak()
     */
    public int getColumns() {
        return COLUMNS;
    }

    /**
     * @return The amount of rows of this board.
     * @ensures result > 0 && result >= getWinStreak()
     */
    public int getRows() {
        return ROWS;
    }


    /**
     * @return The amount of rows of this board.
     * @ensures result > 0 && result <= getColumns() && result <= getRows()
     */
    public int getWinStreak() {
        return WIN_STREAK;
    }


    /**
     * @return The amount of spots of this board
     * @ensures result > 0 && result == getColumns() * getRows()
     */
    public int getSpotCount() {
        return COLUMNS * ROWS;
    }

    public abstract long positionCode();

    /**
     * 
     * @param col
     * @throws InvalidMoveException when a invalid move is entered
     * @requires gets called for the player which current turn it is
     */

    public abstract void makemove(int col, Mark mark) throws InvalidMoveException;

    /**
     * Returns a deepcopy of the board.
     * 
     * @return deepcopy of this board
     */
    public abstract Board deepCopy();

}
