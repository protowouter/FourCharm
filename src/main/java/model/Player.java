/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package main.java.model;

import main.java.model.board.Board;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public interface Player {
	
	
	/**
	 * Given an board, determine an move to be made, implementing types can use artificial or human
	 * intelligence (or lack thereof) to accomplish this.
	 * @param board copy of board used to determine the move 
	 * (this should not be the same board instance used for the game)
	 * @return a (intelligent) legal move for the given board
	 */
	public int doMove(Board board);

}
