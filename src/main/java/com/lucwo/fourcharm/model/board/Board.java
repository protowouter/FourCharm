/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.board;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.player.Mark;

/**
 * The Board class Models a gameboard for the game of Connect4
 * and provides facilities for making moves and checking
 * if a player has won. BinaryBoard and ReferenceBoard extend the Board class.
 * This class makes use of the InvalidMoveException class which extends Exception.
 * Before making a move, the Board class will check if the move
 * if allowed or not. It will throw the InvalidMoveException if a move is not valid.
 * The board positions are numbered as follows:
 * <pre>
 *       1  2  3  4  5  6  7
 *      _____________________
 *   6 |05|11|17|23|29|35|41|
 *   5 |04|10|16|22|28|34|40|
 *   4 |03|09|15|21|27|33|39|
 *   3 |02|08|14|20|26|32|38|
 *   2 |01|07|13|19|25|31|37|
 *   1 |00|06|12|18|24|30|36|
 * </pre>
 *
 * @author Luce Sandfort and Wouter Timmermans
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


    /**
     * Returns true if the columns has any free space left.
     * The column numbering is as follows (example for 7 columns)
     * |0|1|2|3|4|5|6|
     *
     * @param col the column for which the free space will be checked
     * @return return false if there is no more free space left {@code false}
     * otherwise {@code true}.
     */
    public abstract boolean columnHasFreeSpace(int col);

    /**
     * Returns the mark that is on the position of the board.
     * @param index the index for which the
     * @return the {@link com.lucwo.fourcharm.model.player.Mark} which is on this position.
     */
    public abstract Mark getMark(int index);


    /**
     * @see #getMark
     */
    public Mark getMark(int col, int row) {
        return getMark(col * getRows() + row);
    }


    /**
     * Returns true if the given {@link com.lucwo.fourcharm.model.player.Mark}
     * has won on this board.
     * @param mark The player to check.
     * @return {@code true} if the player has won, otherwise {@code false}.
     */
    public abstract boolean hasWon(Mark mark);


    /**
     * Returns {@code true} if the board is full.
     * @return {@code true} if the board is full, otherwise {@code false}.
     */
    public abstract boolean isFull();

    /**
     * Returns the amount of moves made on the board.
     *
     * @return Amount of moves made.
     */
    public abstract int getPlieCount();

    /**
     * Returns an array of the moves made on the board.
     * @return Array of moves.
     */
    public abstract int[] getMoves();


    /**
     * Returns the amount of columns this {@link Board} has.
     * @return The amount of columns of this board.
     */
    /*@
     * ensures \result > 0 && \result >= getWinStreak()
     */
    public int getColumns() {
        return COLUMNS;
    }

    /**
     * Returns the amount of rows this {@link Board} has.
     * @return The amount of rows of this board.
     */
    /*@
     * ensures \result > 0 && \result >= getWinStreak()
     */
    public int getRows() {
        return ROWS;
    }


    /**
     * Returns the amount of pieces a {@link com.lucwo.fourcharm.model.player.Player} needs
     * to have in a row to win the game.
     * @return The win streak of this {@link Board}.
     */
    /*@
     * ensures \result > 2 && \result <= getColumns() && \result <= getRows()
     */
    public int getWinStreak() {
        return WIN_STREAK;
    }


    /**
     * Returns the amount of spots this board has.
     * @return The amount of spots of this board
     */
    /*@
     * @ensures \result > 0 && \result == getColumns() * getRows()
     */
    public int getSpotCount() {
        return COLUMNS * ROWS;
    }


    /**
     * Returns an unique long encoding of this {@link Board}. This encoding depends only on the
     * spots the players occupy. Not in which order the moves were made.
     * @return an unique long encoding of the {@link Board}.
     */
    public abstract long positionCode();

    /**
     * Makes a move on a given column and fills the lowest spot of that column with the
     * {@link com.lucwo.fourcharm.model.player.Mark}.
     * @param col The column where a move will be made.
     * @throws InvalidMoveException when a invalid move is entered or the column
     * does not exist.
     */
    public abstract void makemove(int col, Mark mark) throws InvalidMoveException;

    /**
     * Returns a deepcopy of the board.
     * @return deepcopy of this board
     */
     /*@
      * ensures \result != \old && \result.equals(\old) && \result instanceof Board
      */
    public abstract Board deepCopy();

    /**
     * @see java.lang.Object#equals
     */
    @Override
    public boolean equals(Object o) {
        boolean equals = true;
        if (o instanceof Board) {
            Board other = (Board) o;
            for (int i = 0; equals && i < getSpotCount(); i++) {
                equals = getMark(i) == other.getMark(i);
            }
        } else {
            equals = false;
        }

        return equals;
    }

    /**
     * @return a hashcode for this object
     * @see java.lang.Object#hashCode
     */
    @Override
    public int hashCode() {
        int p1Sum = 0;
        int p2Sum = 0;

        for (int i = 0; i < getSpotCount(); i++) {
            Mark m = getMark(i);
            long value = 1 << i;
            if (m == Mark.P1) {
                p1Sum += value;
            } else if (m == Mark.P2) {
                p2Sum += value;
            }
        }

        return 2 * p1Sum + p2Sum;
    }

}
