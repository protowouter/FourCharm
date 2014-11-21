/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

import ft.model.ai.GameStrategy;
import ft.model.board.Board;


/**
 * Create an AI player given an strategy.
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class ComputerPlayer implements Player {
	
	GameStrategy strategy;
	
	/**
	 * Create an new ComputerPlayer using an strategy to do moves on the board.
	 * @param computerStrategy strategy to be used by this player
	 */
	public ComputerPlayer(GameStrategy computerStrategy) {
		
		strategy = computerStrategy;
		
	}
	
	public int doMove(Board board) {
		return strategy.doMove(board);
	}

}
