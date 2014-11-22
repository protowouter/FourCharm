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
		int move = 0;
		try {
			move = doMove(board, DEF_DEPTH);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return move;
	}
	
	/**
	 * Determine the move to be made using the negamax algorithm.
	 * @param board 
	 * @param depth search depth for the NegaMax algorithm
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @return
	
	 */
	public int doMove(Board board, int depth) throws IllegalAccessException, NoSuchFieldException {
		
		double bestValue = Double.NEGATIVE_INFINITY;
		int bestMove = 0;
		int columns = (int) board.getClass().getField("COLUMNS").get(board);
		
		for (int col = 0; col < columns; col++) {
			if (board.columnHasFreeSpace(col)) {
				Board cBoard = board.deepCopy();
				cBoard.makemove(col);
				double value = -negaMax(cBoard, depth);
				System.out.println("Move: " + col + "Value:" + value);
				if (value > bestValue) {
					bestMove = col;
					bestValue = value;
				}
				
			}
		}
		
		return bestMove;
		
		
		
		
	}
	
	
	/**
	 * 
	 * @param board
	 * @param depth
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public double negaMax(Board board, int depth) throws IllegalAccessException, NoSuchFieldException {
		int player = board.plieCount() & 1; // Player which move it is
		player = player == 0 ? -1 : player;
		double value;
		
		if (depth == 0 || board.full() || board.lastMoveWon()) {
			value =  player * nodeValue(board);
		} else {
			
			double bestValue = Double.NEGATIVE_INFINITY;
			int columns = (int) board.getClass().getField("COLUMNS").get(board);
		
			for (int col = 0; col < columns; col++) {
				if (board.columnHasFreeSpace(col)) {
					Board childBoard = board.deepCopy();
					childBoard.makemove(col);
					double tValue = -negaMax(childBoard, depth - 1);
					bestValue = Math.max(bestValue, tValue);
				}
			}
			
			value = bestValue;
			
		}
		
		return value;
		
		
	}
	
	private double nodeValue(Board board) {
		
		return board.lastMoveWon() ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
	}
	
	
	
}
