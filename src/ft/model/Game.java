/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Models an game of Connect 4.
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class Game {
	
	private Connect4 board;
	
	private ArrayList<Player> playerlist;
	
	private Iterator<Player> playerIterator;
	
	public Game(Player[] players) {
		
		board = new Connect4();
		
		for (Player player : players) {
			
			playerlist.add(player);
			
		}
		
		
		playerIterator = playerlist.iterator();
		
	}
	
	private void nextPlayer() {
		
		if (playerIterator.hasNext()) {
			board.makemove(playerIterator.next().doMove());
		}
		
		else {
			playerIterator = playerlist.iterator();
			nextPlayer();
		}
	}
	
	public void start() {
		
		while (!this.hasFinished()) {
			this.nextPlayer();
		}
		
	}
	
	
	public boolean hasWon() {
		return false;
	}
	
	public boolean hasWinner() {
		return false;
	}
	
	public boolean hasFinished() {
		return false;
	}
	

}
