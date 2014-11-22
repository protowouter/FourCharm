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

import ft.model.ai.*;
import ft.model.board.*;

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
	
	
	/**
	 * Create an new Game of connect 4. This constructor accepts an class implementing
	 * the interface Board and initializes an new board of the given type.
	 * @param boardClass Class to use as board implementation
	 * @param players An array of player who will take part in this game
	 * @param oStream Stream to communicate with the user
	 * @param beVerbose true if output has to written to oStream, false if not
	 */
	public Game(Class<? extends Board> boardClass
					, Player[] players, PrintStream oStream, boolean beVerbose) {
		
		
		initBoard(boardClass);
		
		stream = oStream;
		
		verbose = beVerbose;
		
		playerlist = new ArrayList<Player>();
		
		for (Player player : players) {
			
			playerlist.add(player);
			
		}
		
		playerIterator = Iterables.cycle(playerlist).iterator();
		
		
	}
	
	/**
	 * @param boardClass Class to use as board implementation
	 */
	private void initBoard(Class<? extends Board> boardClass) {
		try {
			board = boardClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Play an game of connect 4 till the game has ended.
	 */
	public void play() {
		
		while (!this.hasFinished()) {
			if (verbose) { 
				stream.println(board);
			}
			board.makemove(playerIterator.next().doMove(board.deepCopy()));
			
		}
		
		if (this.hasWinner()) {
			if (verbose) {
				stream.println(board);
				stream.println("Someone Won!");
			}
		}
		
	}
	
	
	/**
	 * Returns the amount of plies that have been made since the start of the game.
	 * @return 0..boardClass.SIZE
	 */
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
	
	
	/**
	 * Return wether or not the game has ended.
	 * @return true if board is full or a player won the game
	 */
	public boolean hasFinished() {
		
		return board.full() || board.lastMoveWon();
		
	}
	
	public static void main(String[] args) {
		
		BufferedReader dis = new BufferedReader(new InputStreamReader(System.in));
		
		new Game(ReferenceBoard.class, new Player[]{new HumanPlayer(dis), 
			new ComputerPlayer(new RandomStrategy())}, System.out, true).play();
	}
	

}
