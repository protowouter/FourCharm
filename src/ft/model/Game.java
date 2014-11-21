/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package ft.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.collect.Iterables;

import ft.model.ai.RandomStrategy;
import ft.model.board.BinaryBoard;
import ft.model.board.Board;

/**
 * Models an game of Connect 4.
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class Game {
	
	private Board board;
	
	private ArrayList<Player> playerlist;
	
	private Iterator<Player> playerIterator;
	
	private PrintStream stream;
	
	private boolean verbose;
	
	public Game(Class<? extends Board> boardClass
					, Player[] players, PrintStream oStream, boolean beVerbose) {
		
		try {
			board = boardClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		stream = oStream;
		
		verbose = beVerbose;
		
		playerlist = new ArrayList<Player>();
		
		for (Player player : players) {
			
			playerlist.add(player);
			
		}
		
		playerIterator = Iterables.cycle(playerlist).iterator();
		
		
	}
	
	private void nextPlayer() {
		
		
		board.makemove(playerIterator.next().doMove(board));
			
	}
	
	public void start() {
		
		while (!this.hasFinished()) {
			if (verbose) { 
				stream.println(board);
			}
			this.nextPlayer();
			
		}
		
		if (this.hasWinner()) {
			if (verbose) {
				stream.println(board);
				stream.println("Someone Won!");
			}
		}
		
	}
	
	public int plieCount() {
		
		return board.plieCount();
		
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
		
		return board.full() || board.lastMoveWon();
		
	}
	
	public static void main(String[] args) {
		
		BufferedReader dis = new BufferedReader(new InputStreamReader(System.in));
		
		new Game(BinaryBoard.class, new Player[]{new HumanPlayer(dis), 
			new ComputerPlayer(new RandomStrategy())}, System.out, true).start();
	}
	

}
