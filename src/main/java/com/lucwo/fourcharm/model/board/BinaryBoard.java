/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.board;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.player.Mark;

import java.util.Arrays;

/**
 * Class for modelling a board for the game connect four. The responsibility
 * of this class is to keep the state of the board. For efficiency reasons the
 * board state is implemented in an array of 2 longs. This class makes use of
 * the Board class to achieve its responsibilities.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class BinaryBoard extends Board {
    /**
     * Amount of players in a game of Connect4.
     */
    private static final int PLAYERS = 2;
    /**
     * The multiplier of the index for the first row of each column.
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
    // Array with the index of lowest free square
    // for every column; assumes SIZE < 128
    private final byte[] height;
    // Holds bitboard for every color
    private final long[] color;
    // Amount of turns since the start of the game
    private int nplies;

    /**
     * Make a new BinaryBoard and reset it to default settings.
     */
    public BinaryBoard() {
        super();

        color = new long[PLAYERS];
        height = new byte[COLUMNS];
        moves = new int[SIZE];
        reset();
    }

    /**
     * Private constructor to be used in making deepcopies.
     *
     * @param origMoves  Moves from original board.
     * @param origNplies Amount of moves made on the original board.
     * @param origHeight Array of colunm heights of the original board.
     * @param origColor  Array of player bitmasks of the original board.
     */
    private BinaryBoard(int[] origMoves, int origNplies, byte[] origHeight, long[] origColor) {
        super();
        moves = origMoves;
        nplies = origNplies;
        height = origHeight;
        color = origColor;
    }

    /**
     * Resets the binary board.
     */
    private void reset() {
        nplies = 0;
        color[0] = 0L;
        color[1] = 0L;

        for (int i = 0; i < COLUMNS; i++) {
            height[i] = (byte) (H1 * i);
        }
    }

    /**
     * Checks if the column has free space.
     *
     * @param col the column for which the free space will be checked.
     * @return True is the column has free space, false if not.
     */
    public boolean columnHasFreeSpace(int col) {

        return isLegalBoard(color[0] | (1L << height[col])) &&
                isLegalBoard(color[1] | (1L << height[col]));

    }

    /**
     * Checks if the board is legal.
     *
     * @param newBoard The board that will be checked.
     * @return True if the board is legal, false if not.
     */
    private boolean isLegalBoard(long newBoard) {

        // Checks whether no columns have overflown
        return (newBoard & TOP) == 0;
    }

    /**
     * Checks if a player has won.
     *
     * @param mark The player to check.
     * @return True if the player has won, false if not.
     */
    public boolean hasWon(Mark mark) {

        long board = color[getPlayerIndex(mark)];

        return hasLRDiagonal(board) || hasHorizontal(board)
                || hasRLDiagonal(board) || hasVertical(board);
    }

    /**
     * Checks if the board is full.
     *
     * @return True if full, false if not full.
     */
    public boolean isFull() {

        return nplies >= SIZE;

    }

    /**
     * Gives the plieCount.
     *
     * @return The plieCount.
     */
    public int getPlieCount() {

        return nplies;

    }

    /**
     * Gives an int array of the moves that are made.
     *
     * @return The moves that are made.
     */
    public int[] getMoves() {
        return Arrays.copyOf(moves, moves.length);
    }

    /**
     * Checks if the board has a diagonal (four in a row).
     *
     * @param newBoard The board that will be checked.
     * @return True if there are four connected spots on a diagonal line, false if not.
     */
    private boolean hasLRDiagonal(long newBoard) {

        long y = newBoard & (newBoard >> ROWS);

        return (y & (y >> (2 * ROWS))) != 0;
    }

    /**
     * Checks if the board has a diagonal (four in a row).
     *
     * @param newBoard The board that will be checked.
     * @return True if there are four connected spots on a diagonal line, false if not.
     */
    private boolean hasRLDiagonal(long newBoard) {

        long y = newBoard & (newBoard >> H2);

        return (y & (y >> (2 * H2))) != 0;
    }

    /**
     * Checks if the board has a horizontal row.
     *
     * @param newBoard The board that will be checked.
     * @return True if there are four connected spots on a horizontal line, false if not.
     */
    private boolean hasHorizontal(long newBoard) {

        long y = newBoard & (newBoard >> H1);

        return (y & (y >> (2 * H1))) != 0;
    }

    /**
     * Checks if the board has a vertical row.
     *
     * @param newBoard The board that will be checked.
     * @return True if there are four connected spots on a vertical line, false if not.
     */
    private boolean hasVertical(long newBoard) {

        long y = newBoard & (newBoard >> 1);

        return (y & (y >> 2)) != 0;

    }

    /**
     * Gives the mark of a spot on the board.
     *
     * @param index The index of the spot on the board.
     * @return The mark of a spot.
     */
    public Mark getMark(int index) {

        int col = index / ROWS;
        int row = index % ROWS;

        return getInternalMark(col * H1 + row);
    }

    /**
     * Because the binary board uses a different numbering of spot indexes, this method is used to
     * get a mark for an internal index.
     *
     * @param internalIndex The index number.
     * @return The {@link com.lucwo.fourcharm.model.player.Mark} on the given index.
     */
    private Mark getInternalMark(int internalIndex) {

        Mark mark = Mark.EMPTY;

        if ((color[0] & 1L << internalIndex) != 0) {
            mark = Mark.P1;
        } else if ((color[1] & 1L << internalIndex) != 0) {
            mark = Mark.P2;
        }

        return mark;
    }

    /**
     * Gives the integer of the player index.
     *
     * @param m The given mark.
     * @return The int of the player index.
     */
    private int getPlayerIndex(Mark m) {
        int result = -1;
        if (m == Mark.P1) {
            result = 0;
        } else if (m == Mark.P2) {
            result = 1;
        }
        return result;
    }


    /**
     * Makes a move.
     *
     * @param col  The column where a move will be made.
     * @param mark The current mark.
     * @throws InvalidMoveException if the move is not allowed.
     */
    @Override
    public void makemove(int col, Mark mark) throws InvalidMoveException {


        if (col >= 0 && col < COLUMNS && columnHasFreeSpace(col)) {
            int player = getPlayerIndex(mark);

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

    /**
     * Makes a string representation.
     *
     * @return The representation.
     */
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

    /**
     * Gives the positioncode.
     *
     * @return The position code.
     */
    public long positionCode() {

        return 2 * color[0] + color[1] + BOTTOM;

    }

    /**
     * Makes a deepcopy of the board.
     *
     * @return A deepcopy of the current board.
     */
    public Board deepCopy() {
        int[] moveCopy = Arrays.copyOf(moves, moves.length);
        byte[] heightCopy = Arrays.copyOf(height, height.length);
        long[] colorCopy = Arrays.copyOf(color, color.length);

        return new BinaryBoard(moveCopy, nplies, heightCopy, colorCopy);

    }

}
