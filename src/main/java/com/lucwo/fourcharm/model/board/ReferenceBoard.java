/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model.board;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Mark;

import java.util.Arrays;
import java.util.logging.Logger;

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

    // ------------------ Instance variables ----------------
    // Array with moves since the start of the game
    private int[] moves;
    // Amount of turns since the start of the game
    private int nplies; 
    // Holds bitboard for every color
    private Mark[][] board;

    // --------------------- Constructors -------------------

    /**
     * Creates an new instance of an Board. In its initial state the board is
     * empty
     */
    public ReferenceBoard() {
        super();
        reset();
    }

    private ReferenceBoard(int[] oldMoves, Mark[][] oldBoard, int plieCount) {
        moves = oldMoves;
        board = oldBoard;
        nplies = plieCount;
    }

    // ----------------------- Queries ----------------------

    public boolean columnHasFreeSpace(int col) {

        return board[col][ROWS - 1] == Mark.EMPTY;

    }

    public boolean hasWon(Mark mark) {


        return hasLRDiagonal(mark) || hasHorizontal(mark)
                || hasRLDiagonal(mark) || hasVertical(mark);

    }

    public boolean isFull() {

        return nplies >= SIZE;

    }

    public int getPlieCount() {

        return nplies;

    }

    /**
     * Find columns of the following form:
     * . . . . . . .
     * . . . . . . .
     * . . . @ . . .
     * . . . . @ . .
     * . . . . . @ .
     * . . . . . . @
     */

    private boolean hasLRDiagonal(Mark player) {

        boolean diag = false;

        for (int column = WIN_STREAK; (column < COLUMNS) && !diag; column++) {
            int streak = 0;
            int tColumn = column;
            for (int row = 0; (row < ROWS) && (tColumn >= 0)
                    && (streak < WIN_STREAK); row++, tColumn--) {
                streak = (this.board[tColumn][row] == player) ? (streak + 1) : 0;
            }
            diag = streak == WIN_STREAK;
        }

        return diag;

    }

    /**
     * Find columns of the following form:
     * . . . . . . .
     * . . . . . . .
     * . . . @ . . .
     * . . @ . . . .
     * . @ . . . . .
     * @ . . . . . .
     */

    private boolean hasRLDiagonal(Mark player) {


        boolean diag = false;

        for (int column = COLUMNS - WIN_STREAK; (column >= 0) && !diag; column--) {
            int streak = 0;
            int tColumn = column;
            for (int row = 0; (row < ROWS) && (tColumn < COLUMNS)
                    && (streak < WIN_STREAK); row++, tColumn++) {
                Logger.getGlobal().finest("Checking col: " + tColumn + " row: " + row);
                streak = (this.board[tColumn][row] == player) ? (streak + 1) : 0;
            }
            diag = streak == WIN_STREAK;
        }

        return diag;

    }

    private boolean hasHorizontal(Mark player) {

        boolean horizontal = false;

        for (int row = 0; (row < ROWS) && !horizontal; row++) {
            int streak = 0;
            for (int column = 0; (column < COLUMNS) && (streak < WIN_STREAK); column++) {
                streak = (this.board[column][row] == player) ? (streak + 1) : 0;
            }
            horizontal = streak == WIN_STREAK;

        }

        return horizontal;

    }

    private boolean hasVertical(Mark player) {

        boolean vertical = false;

        for (int column = 0; (column < COLUMNS) && !vertical; column++) {
            int streak = 0;
            for (int row = 0; (row < ROWS) && (streak < WIN_STREAK); row++) {
                streak = (this.board[column][row] == player) ? (streak + 1) : 0;
            }
            Logger.getGlobal().finer("Streak = " + streak);
            vertical = streak == WIN_STREAK;

        }

        return vertical;

    }

    public Mark getMark(int index) {

        return getMark(index / COLUMNS, index % COLUMNS);

    }

    public Mark getMark(int col, int row) {
        return board[col][row];
    }

    public int[] getMoves() {
        return moves;
    }

    public String toString() {
        StringBuilder repr = new StringBuilder();

        for (int i = 0; i < nplies; i++) {
            repr.append(1 + moves[i]);
        }
        repr.append("\n");
        for (int w = 0; w < COLUMNS; w++) {
            repr.append(" ").append(w + 1);
        }
        repr.append("\n");

        for (int row = ROWS - 1; row >= 0; row--) {
            for (int column = 0; column < COLUMNS; column++) {
                Mark player = board[column][row];
                if (player == Mark.P1) {
                    repr.append(" X");
                } else if (player == Mark.P2) {
                    repr.append(" O");
                } else if (player == Mark.EMPTY) {
                    repr.append(" .");
                }
            }
            repr.append("\n");
        }

        return repr.toString();
    }

    public Board deepCopy() {

        Mark[][] newBoard = new Mark[COLUMNS][ROWS];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = Arrays.copyOf(board[i], board[i].length);
        }

        return new ReferenceBoard(Arrays.copyOf(moves, moves.length), newBoard, nplies);
    }

    public long positionCode() {

        return 0;

    }

    // ----------------------- Commands ---------------------

    /**
     *
     * @param col column in which a piece will be placed
     * @requires gets called for the player which current turn it is
     */
    public void makemove(int col, Mark mark) throws InvalidMoveException {

        if (columnHasFreeSpace(col)) {

            moves[nplies] = col;

            // Increment the plie count
            nplies++;

            boolean needPlacement = true;


            for (int i = 0; i < ROWS && needPlacement; i++) {
                if (board[col][i] == Mark.EMPTY) {
                    board[col][i] = mark;
                    needPlacement = false;
                }
            }
        } else {
            throw new InvalidMoveException("This column has no more free space");
        }


    }

    private void reset() {
        moves = new int[SIZE];
        nplies = 0;
        board = new Mark[COLUMNS][ROWS];
        for (int i = 0; i < COLUMNS; i++) {
            Arrays.fill(board[i], Mark.EMPTY);
        }
    }

}
