/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

import java.io.BufferedReader;
import java.io.IOException;

import ft.model.board.BinaryBoard;
import ft.model.board.Board;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class HumanPlayer implements Player {
	
	private BufferedReader reader;
	
	
	
	public HumanPlayer(BufferedReader inputReader) {
		
		reader = inputReader;
		
	}

	/* (non-Javadoc)
	 * @see ft.model.Player#doMove()
	 */
	public int doMove(Board board) {
		
		System.out.println("Voer kolomnummer in: ");
		
		int move = 0;
		
		String line = "";
		try { 
			line = reader.readLine(); 
		} catch (IOException e) {
	        System.out.println(e);
	        System.exit(0);
	    }
	    if (line == null) {
	        doMove(board);
	    }
	    for (int i = 0; i < line.length(); i++) {
	        int col = line.charAt(i) - '1';
	        if (col >= 0 && col < BinaryBoard.COLUMNS && board.columnHasFreeSpace(col)) {
	        	move = col;
	        }
	    }
	    return move;
	}

}
