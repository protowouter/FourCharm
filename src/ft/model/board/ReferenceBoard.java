/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model.board;

import java.util.Arrays;

/**
 * Class for modeling an board for the game connect four. This class's responsibility is
 * to keep the state of the board. This is the reference implementation implemented using an
 * 2D array
 * @author Luce Sandfort and Wouter Timmermans
 *
 */

public class ReferenceBoard implements Board {
	
	// ------------------ Class variables ----------------
	/**
	 * Amount of columns of the board.
	 */
	public static final int COLUMNS = 7;
	/**
	 * Amount of rows of the board.
	 */
	public static final int ROWS	= 6;
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
	
	private int[] moves; // Array with moves since the start of the game
	
	private int nplies; // Amount of turns since the start of the game
	
	private static int[][] board; // Holds bitboard for every color
	
	// --------------------- Constructors -------------------
	
	
	/**
	 * Creates an new instance of an Board. In its initial state the board is empty
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
	
	public boolean full() {
		
		return nplies >= SIZE - 1;
		
	}
	
	public int plieCount() {
		
		return nplies;
		
	}
	
	private boolean hasLRDiagonal(int player) {
		
		boolean diag = false;
		
		for (int column = 4; column < COLUMNS && !diag; column++) {
			int streak = 0;
			int tColumn = column;
			for (int row = 0; row < ROWS && tColumn < COLUMNS; row++, tColumn--) {
				streak = board[column][row] == player ? streak + 1 : 0;
			}
			diag = streak == 4;
		}
		
		return diag;
		
	}
	
	private boolean hasRLDiagonal(int player) {
		
		boolean diag = false;
		
		for (int column = COLUMNS - 4; column >= 0 && !diag; column--) {
			int streak = 0;
			int tColumn = column;
			for (int row = 0; row < ROWS && tColumn < COLUMNS; row++, tColumn++) {
				streak = board[column][row] == player ? streak + 1 : 0;
			}
			diag = streak == 4;
		}
		
		return diag;
		
	}
	
	private boolean hasHorizontal(int player) {
		
		boolean horizontal = false;
		
		for (int row = 0; row < ROWS && !horizontal; row++) {
			int streak = 0;
			for (int column = 0; column < COLUMNS && streak < 4; column++) {
				streak = board[column][row] == player ? streak + 1 : 0;
			}
			horizontal = streak == 4;
			
		}
		
		return horizontal;
		
		
	}
	
	private boolean hasVertical(int player) {
		
		boolean vertical = false;
		
		for (int column = 0; column < COLUMNS && !vertical; column++) {
			int streak = 0;
			for (int row = 0; row < ROWS && streak < 4; row++) {
				streak = board[column][row] == player ? streak + 1 : 0;
			}
			vertical = streak == 4;
			
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
	    		} else {
	    			repr.append(" .");
	    		}
	    	}
	    	repr.append("\n");
	    }

	    return repr.toString();
	}
	

	
	// ----------------------- Commands ---------------------
	
	/**
	 * 
	 * @param col
	 * @requires gets called for the player which current turn it is
	 */
	public void makemove(int col) {
		
		int player = nplies & 1; // same as modulo 2 but probably more efficient
		
		nplies++; //Increment the plie count
		
		boolean needPlacement = true;
		
		for (int i = 0; i < ROWS && needPlacement; i++) {
			if (board[col][i] == EMPTY) {
				board[col][i] = player;
				needPlacement = false;
			}
		}
		
		moves[nplies] = col;
		
	}
	
	private void reset() {
		nplies = 0;
		board = new int[COLUMNS][ROWS];
		for (int i = 0; i < COLUMNS; i++) {
			Arrays.fill(board[i], EMPTY);
		}
	}

}