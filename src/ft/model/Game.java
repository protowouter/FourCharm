/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		
		playerlist = new ArrayList<Player>();
		
		for (Player player : players) {
			
			playerlist.add(player);
			
		}
		
		
		playerIterator = playerlist.iterator();
		
	}
	
	private void nextPlayer() {
		
		if (playerIterator.hasNext()) {
			board.makemove(playerIterator.next().doMove(board));
		}
		
		else {
			playerIterator = playerlist.iterator();
			nextPlayer();
		}
	}
	
	public void start() {
		
		while (!this.hasFinished()) {
			System.out.println(board);
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
	
	public static void main(String[] args) {
		
		BufferedReader dis = new BufferedReader(new InputStreamReader(System.in));
		
		new Game(new Player[]{new HumanPlayer(dis), new RandomComputerPlayer()}).start();
	}
	

}
