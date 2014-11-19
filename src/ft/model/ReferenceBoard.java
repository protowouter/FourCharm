/**
 * Heavily influenced by the Fhourstones 3.0 Board Logic Copyright 2000-2004 John Tromp
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

import java.util.Arrays;

/**
 * Class for modeling an board for the game connect four. This class's responsibility is
 * to keep the state of the board. This is the reference implementation implemented using an
 * 2D array
 * @author Luce Sandfort and Wouter Timmermans
 *
 */

public class ReferenceBoard {
	
	/**
	 * Amount of columns of the board.
	 */
	public static final int COLUMNS = 7;
	/**
	 * Amount of rows of the board.
	 */
	public static final int ROWS	= 6;
	
	public static final int SIZE = COLUMNS * ROWS;
	
	public static final int EMPTY = -1;
	
	public static final int PLAYER1 = 0;
	
	public static final int PLAYER2 = 1;
	
	private int[] moves; // Array with moves since the start of the game
	
	private int nplies; // Amount of turns since the start of the game
	
	private static int[][] board; // Holds bitboard for every color
	
	
	public ReferenceBoard() {
		
		board = new int[COLUMNS][ROWS];
		Arrays.fill(board, EMPTY);
		moves = new int[SIZE];
		reset();
	}
	
	private void reset() {
		nplies = 0;
		board = new int[COLUMNS][ROWS];
	}
	
	
	public boolean columnHasFreeSpace(int col) {
		
		return board[col][ROWS - 1] == EMPTY; 
		
	}
	
	
	public boolean lastMoveWon() {

		
		int player = nplies & 1;
		
		return hasLRDiagonal(player) || hasHorizontal(player) 
				|| hasRLDiagonal(player) || hasVertical(player);
		
	}
	
	public boolean full() {
		
		return nplies >= SIZE - 1;
		
	}
	
	public int plieCount() {
		
		return nplies;
		
	}
	
	private boolean hasLRDiagonal(int newboard) {
		
		return false;
		
	}
	
	private boolean hasRLDiagonal(int newboard) {
		
		return false;
		
	}
	
	private boolean hasHorizontal(int player) {
		
		boolean horizontal = false;
		
		for (int row = 0; row < ROWS && !horizontal; row++) {
			boolean possible = true;
			for (int column = 0; column < COLUMNS && possible; column++) {
				possible = board[column][row] == player;
			}
			horizontal = possible;
			
		}
		
		return horizontal;
		
		
	}
	
	private boolean hasVertical(int newboard) {
		
		return false;
		
		
	}
	
	/**
	 * 
	 * @param col
	 * @requires gets called for the player which current turn it is
	 */
	
	public void makemove(int col) {
		
		int player = nplies & 1; // same as modulo 2 but probably more efficient
		
		nplies++; //Increment the plie count
		
		boolean needPlacement = false;
		
		for (int i = 0; i < ROWS && needPlacement; i++) {
			if (board[col][i] == EMPTY) {
				board[col][i] = player;
				needPlacement = true;
			}
		}
		
		moves[nplies] = col;
		
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

	    return repr.toString();
	}
	
	

}
