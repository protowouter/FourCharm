/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model.board;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public interface Board {
	
	public boolean columnHasFreeSpace(int col);
	
	public boolean lastMoveWon();
	
	public boolean full();
	
	public int plieCount();
	
	/**
	 * 
	 * @param col
	 * @requires gets called for the player which current turn it is
	 */
	
	public void makemove(int col);
	
	
	/**
	 * Returns an deepcopy of the board.
	 * @return deepcopy of this board
	 */
	public Board deepCopy();
	

}
