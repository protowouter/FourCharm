/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

import java.io.BufferedReader;
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
		} else {
			playerIterator = playerlist.iterator();
			nextPlayer();
		}
	}
	
	public void start() {
		
		while (!this.hasFinished()) {
			System.out.println(board);
			this.nextPlayer();
		}
		
		if (this.hasWinner()) {
			//Winner is the last player
			//Player winner = 
		}
		
	}
	
	
	/**
	 * Returns wether or not an player has won the game. If this method returns
	 * true the player wich has won the game is the last player to make a move
	 * because the rules of connect4 offers no posibility to let an other player
	 * win solely based on the move you make. 
	 * @return true if a player has won the game; otherwise false
	 */
	public boolean hasWinner() {
		return board.lastMoveWon();
	}
	
	public boolean hasFinished() {
		
		return board.full() && board.lastMoveWon();
		
	}
	
	public static void main(String[] args) {
		
		BufferedReader dis = new BufferedReader(new InputStreamReader(System.in));
		
		new Game(new Player[]{new HumanPlayer(dis), 
			new ComputerPlayer(new RandomStrategy())}).start();
	}
	

}
