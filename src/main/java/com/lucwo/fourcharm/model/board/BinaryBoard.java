/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model.board;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Mark;

import java.util.Arrays;

/**
 * Class for modeling an board for the game connect four. This class's
 * responsibility is to keep the state of the board. For efficiency reasons the
 * board state is implemented in an array of 2 longs
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class BinaryBoard extends Board {
    /**
     * The multiple of the index for the first row of each column.
     */
    private static final int H1 = ROWS + 1;

    private static final int H2 = ROWS + 2;
    /**
     * Amount of spaces in the board.
     */
    private static final int SIZE = COLUMNS * ROWS;
    /**
     * Amount of spaces in the board plus the top row used by the
     * implementation.
     */
    private static final int SIZE1 = H1 * COLUMNS;
    /**
     * As much one's as there are spots in the grid and the top row.
     */
    private static final long ALL1 = (1L << SIZE1) - 1L;

    private static final int COL1 = (1 << H1) - 1;

    private static final long BOTTOM = ALL1 / COL1;
    /**
     * Bitmask for detecting overflows in columns.
     */
    private static final long TOP = BOTTOM << ROWS;

    // Array with moves since the start of the game
    private final int[] moves;
    // Array with the index of lowest free sqaure
    // for every column; assumes SIZE < 128
    private final byte[] height;
    // Holds bitboard for every color
    private final long[] color;
    // Amount of turns since the start of the game
    private int nplies;

    /**
     * Make an new BinaryBoard and reset it to default settings.
     */
    public BinaryBoard() {
        super();

        color = new long[PLAYERS];
        height = new byte[COLUMNS];
        moves = new int[SIZE];
        reset();
    }

    private BinaryBoard(int[] origMoves, int origNplies, byte[] origHeight, long[] origColor) {
        super();
        moves = origMoves;
        nplies = origNplies;
        height = origHeight;
        color = origColor;
    }

    private void reset() {
        nplies = 0;
        color[0] = 0L;
        color[1] = 0L;

        for (int i = 0; i < COLUMNS; i++) {
            height[i] = (byte) (H1 * i);
        }
    }

    public boolean columnHasFreeSpace(int col) {

        return isLegalBoard(color[nplies & 1] | (1L << height[col]));

    }

    private boolean isLegalBoard(long newboard) {

        // Checks wether no columns have overflown
        return (newboard & TOP) == 0;


    }

    public boolean hasWon(Mark mark) {

        long board = color[getPlayerIndex(mark)];

        return hasLRDiagonal(board) || hasHorizontal(board)
                || hasRLDiagonal(board) || hasVertical(board);

    }

    public boolean isFull() {

        return nplies >= (SIZE - 1);

    }

    public int getPlieCount() {

        return nplies;

    }

    private boolean hasLRDiagonal(long newboard) {

        long y = newboard & (newboard >> ROWS);

        return (y & (y >> (2 * ROWS))) != 0;

    }

    private boolean hasRLDiagonal(long newboard) {

        long y = newboard & (newboard >> H2);

        return (y & (y >> (2 * H2))) != 0;

    }

    private boolean hasHorizontal(long newboard) {

        long y = newboard & (newboard >> H1);

        return (y & (y >> (2 * H1))) != 0;

    }

    private boolean hasVertical(long newboard) {

        long y = newboard & (newboard >> 1);

        return (y & (y >> 2)) != 0;

    }

    public Mark getMark(int index) {

        return Mark.EMPTY;
    }

    public Mark getMark(int col, int row) {
        return getMark(col * row);
    }


    private int getPlayerIndex(Mark mark) {
        int pI = -1;
        if (mark == Mark.P1) {
            pI = 0;
        } else if (mark == Mark.P2) {
            pI = 1;
        }

        return pI;
    }

    /**
     * @param col
     * @requires gets called for the player which current turn it is
     */
    @Override
    public void makemove(int col, Mark mark) throws InvalidMoveException {


        if (columnHasFreeSpace(col)) {
            // same as modulo 2 but probably more efficient
            int player = nplies & 1;


            moves[nplies] = col;

            // Increment the plie count
            nplies++;

            color[player] ^= 1L << height[col];


            // Increment the height of the column where the piece is
            // placed,
            // This should be done after altering color[player]
            height[col]++;
        } else {
            throw new InvalidMoveException("Column " + col + " is already full");
        }

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
        for (int h = ROWS - 1; h >= 0; h--) {
            for (int w = h; w < SIZE1; w += H1) {
                long mask = 1L << w;
                repr.append(((this.color[0] & mask) != 0) ? " X"
                        : (((this.color[1] & mask) != 0) ? " O" : " ."));
            }
            repr.append("\n");
        }
        return repr.toString();
    }

    public Board deepCopy() {
        int[] moveCopy = Arrays.copyOf(moves, moves.length);
        byte[] heightCopy = Arrays.copyOf(height, height.length);
        long[] colorCopy = Arrays.copyOf(color, color.length);

        return new BinaryBoard(moveCopy, nplies, heightCopy, colorCopy);

    }

}
