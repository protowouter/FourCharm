/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

import java.util.Random;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class RandomStrategy implements GameStrategy {

	/* (non-Javadoc)
	 * @see ft.model.GameStrategy#doMove()
	 */
	@Override
	public int doMove(Connect4 board) {
		
		Random random = new Random();
		
		int col = random.nextInt(7);
		
		while (!board.columnHasFreeSpace(col)) {
        	col = random.nextInt(7);
        }
		
		return col;
		
		
	}

}
