/**
 * Heavily influenced by the Fhourstones 3.0 Board Logic Copyright 2000-2004 John Tromp
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package main.java.model.board;

/**
 * Class for modeling an board for the game connect four. This class's
 * responsibility is to keep the state of the board. For efficiency reasons the
 * board state is implemented in an array of 2 longs
 * 
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class BinaryBoard implements Board {
    /**
     * The multiple of the index for the first row of each column.
     */
    public static final int H1 = ROWS + 1;

    public static final int H2 = ROWS + 2;
    /**
     * Amount of spaces in the board.
     */
    public static final int SIZE = COLUMNS * ROWS;
    /**
     * Amount of spaces in the board plus the top row used by the
     * implementation.
     */
    public static final int SIZE1 = H1 * COLUMNS;
    /**
     * As much one's as there are spots in the grid and the top row.
     */
    public static final long ALL1 = (1L << SIZE1) - 1L;

    public static final int COL1 = (1 << H1) - 1;

    public static final long BOTTOM = ALL1 / COL1;
    /**
     * Bitmask for detecting overflows in columns.
     */
    public static final long TOP = BOTTOM << ROWS;

    // Array with moves since the start of the game
    private int[] moves;
    // Amount of turns since the start of the game
    private int nplies;
    // Array with the index of lowest free sqaure
    // for every column; assumes SIZE < 128
    private byte[] height; 
    // Holds bitboard for every color
    private long[] color; 

    /**
     * Make an new BinaryBoard and reset it to default settings.
     */
    public BinaryBoard() {

        color = new long[PLAYERS];
        height = new byte[COLUMNS];
        moves = new int[SIZE];
        reset();
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

    public boolean lastMoveWon() {

        long board = color[(nplies - 1) & 1];

        return hasLRDiagonal(board) || hasHorizontal(board)
                || hasRLDiagonal(board) || hasVertical(board);

    }

    public boolean full() {

        return nplies >= SIZE - 1;

    }

    public int plieCount() {

        return nplies;

    }

    private boolean hasLRDiagonal(long newboard) {

        long y = newboard & (newboard >> ROWS);

        return (y & (y >> 2 * ROWS)) != 0;

    }

    private boolean hasRLDiagonal(long newboard) {

        long y = newboard & (newboard >> H2);

        return (y & (y >> 2 * H2)) != 0;

    }

    private boolean hasHorizontal(long newboard) {

        long y = newboard & (newboard >> H1);

        return (y & (y >> 2 * H1)) != 0;

    }

    private boolean hasVertical(long newboard) {

        long y = newboard & (newboard >> 1);

        return (y & (y >> 2)) != 0;

    }

    /**
     * 
     * @param col
     * @requires gets called for the player which current turn it is
     */

    public void makemove(int col) {
        
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
        for (int h = ROWS - 1; h >= 0; h--) {
            for (int w = h; w < SIZE1; w += H1) {
                long mask = 1L << w;
                repr.append((color[0] & mask) != 0 ? " @"
                        : (color[1] & mask) != 0 ? " 0" : " .");
            }
            repr.append("\n");
        }
        return repr.toString();
    }

    public Board deepCopy() {
        BinaryBoard board = new BinaryBoard();
        board.reset();
        for (int i = 0; i < nplies; i++) {
            board.makemove(moves[i]);
        }
        return board;
    }

}
