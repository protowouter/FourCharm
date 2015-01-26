/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.board;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.player.Mark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Arrays;

/**
 * Class for modeling a board for the game connect four. This class's
 * responsibility is to keep the state of the board. This is the reference
 * implementation implemented using an 2D array. This class makes use of
 * the Board class.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */

public class ReferenceBoard extends Board {

    // ------------------ Class variables ----------------
    /**
     * Amount of spots in the board.
     */
    public static final int SIZE = COLUMNS * ROWS;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceBoard.class);
    private static final Marker BOARD_DEBUG = MarkerFactory.getMarker("BOARD_DEBUG");

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

    @Override
    public boolean columnHasFreeSpace(int col) {

        return board[col][ROWS - 1] == Mark.EMPTY;

    }

    @Override
    public boolean hasWon(Mark mark) {


        return hasLRDiagonal(mark) || hasHorizontal(mark)
                || hasRLDiagonal(mark) || hasVertical(mark);

    }

    @Override
    public boolean isFull() {

        return nplies >= SIZE;

    }

    @Override
    public int getPlieCount() {

        return nplies;
    }

    /**
     * Finds columns of the following form:
     * . . . . . . .
     * . . . . . . .
     * . . . @ . . .
     * . . . . @ . .
     * . . . . . @ .
     * . . . . . . @
     *
     * @param player The (mark of the) current player.
     * @return True if there exists a LRDiagonal, false if not.
     */
    private boolean hasLRDiagonal(Mark player) {

        boolean diag = false;

        for (int column = WIN_STREAK; (column < COLUMNS) && !diag; column++) {
            int streak = 0;
            int tColumn = column;
            for (int row = 0; (row < ROWS) && (tColumn >= 0)
                    && (streak < WIN_STREAK); row++, tColumn--) {
                streak = this.board[tColumn][row] == player ? streak + 1 : 0;
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
     *
     * @param player The (mark of the) current player.
     * @return True if there exists a RLDiagonal, false if not.
     * @ . . . . . .
     */

    private boolean hasRLDiagonal(Mark player) {

        boolean diag = false;

        for (int column = COLUMNS - WIN_STREAK; (column >= 0) && !diag; column--) {
            int streak = 0;
            int tColumn = column;
            for (int row = 0; (row < ROWS) && (tColumn < COLUMNS)
                    && (streak < WIN_STREAK); row++, tColumn++) {
                LOGGER.debug(BOARD_DEBUG, "Checking col: {} row: {}", tColumn, row);
                streak = this.board[tColumn][row] == player ? streak + 1 : 0;
            }
            diag = streak == WIN_STREAK;
        }

        return diag;
    }

    /**
     * Finds the horizontal 'four in a row's of the game.
     *
     * @param player The (mark of the) current player.
     * @return True if there exists a horizontal row, false if not.
     */
    private boolean hasHorizontal(Mark player) {

        boolean horizontal = false;

        for (int row = 0; (row < ROWS) && !horizontal; row++) {
            int streak = 0;
            for (int column = 0; (column < COLUMNS) && (streak < WIN_STREAK); column++) {
                streak = this.board[column][row] == player ? streak + 1 : 0;
            }
            horizontal = streak == WIN_STREAK;
        }
        return horizontal;
    }

    /**
     * Finds the vertical 'four in a row's of the game.
     *
     * @param player The (mark of the) current player.
     * @return True if there exists a vertical row, false if not.
     */
    private boolean hasVertical(Mark player) {

        boolean vertical = false;

        for (int column = 0; (column < COLUMNS) && !vertical; column++) {
            int streak = 0;
            for (int row = 0; (row < ROWS) && (streak < WIN_STREAK); row++) {
                streak = this.board[column][row] == player ? streak + 1 : 0;
            }
            LOGGER.debug(BOARD_DEBUG, "Streak = {}", streak);
            vertical = streak == WIN_STREAK;
        }
        return vertical;
    }

    @Override
    public Mark getMark(int index) {

        return getMark(index / ROWS, index % ROWS);
    }

    @Override
    public Mark getMark(int col, int row) {
        return board[col][row];
    }

    @Override
    public int[] getMoves() {
        return Arrays.copyOf(moves, moves.length);
    }

    @Override
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

    @Override
    public Board deepCopy() {

        Mark[][] newBoard = new Mark[COLUMNS][ROWS];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = Arrays.copyOf(board[i], board[i].length);
        }

        return new ReferenceBoard(Arrays.copyOf(moves, moves.length), newBoard, nplies);
    }

    @Override
    public long positionCode() {

        long p1Sum = 0;
        long p2Sum = 0;

        for (int i = 0; i < SIZE; i++) {
            Mark m = getMark(i);
            long value = 1L << i;
            if (m == Mark.P1) {
                p1Sum += value;
            } else if (m == Mark.P2) {
                p2Sum += value;
            }
        }

        return 2 * p1Sum + p2Sum;


    }

    // ----------------------- Commands ---------------------

    /**
     * Makes a move on the board for the given player.
     *
     * @param col column in which a piece will be placed
     */
    @Override
    public void makemove(int col, Mark mark) throws InvalidMoveException {

        if (col >= 0 && col < COLUMNS && columnHasFreeSpace(col)) {

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

    /**
     * Resets the board.
     */
    private void reset() {
        moves = new int[SIZE];
        nplies = 0;
        board = new Mark[COLUMNS][ROWS];
        for (int i = 0; i < COLUMNS; i++) {
            Arrays.fill(board[i], Mark.EMPTY);
        }
    }

}
