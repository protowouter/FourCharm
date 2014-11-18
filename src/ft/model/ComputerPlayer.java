/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

public class ComputerPlayer implements Player {
	
	GameStrategy strategy;
	
	public ComputerPlayer(GameStrategy computerStrategy) {
		
		strategy = computerStrategy;
		
	}
	
	public int doMove(Connect4 board) {
		return strategy.doMove(board);
	}

}
