/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

public class ComputerPlayer implements Player {
	
	GameStrategy strategy;
	
	public ComputerPlayer(GameStrategy computerStrategy) {
		
		strategy = computerStrategy;
		
	}
	
	public int doMove(Board board) {
		return strategy.doMove(board);
	}

}
