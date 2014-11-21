/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model.ai;

import ft.model.board.Board;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class NegaMaxStrategy implements GameStrategy {
	
	
	/**
	 * Default search depth for the NegaMax algorithm.
	 */
	public static final int DEF_DEPTH = 5;

	/* (non-Javadoc)
	 * @see ft.model.Player#doMove(ft.model.board.Board)
	 */
	@Override
	public int doMove(Board board) {
		return doMove(board, DEF_DEPTH);
	}
	
	/**
	 * 
	 * @param board 
	 * @param depth search depth for the NegaMax algorithm
	 * @return
	 */
	public int doMove(Board board, int depth) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
}
