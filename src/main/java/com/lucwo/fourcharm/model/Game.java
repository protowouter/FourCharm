/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package com.lucwo.fourcharm.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

import com.google.common.collect.Iterables;
import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.board.*;

/**
 * Models an game of Connect 4.
 * 
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class Game extends Observable {

    private Board board;

    private List<Player> playerlist;

    private Iterator<Player> playerIterator;
    /**
     * Create an new Game of connect 4. This constructor accepts an class
     * implementing the interface Board and initializes an new board of the
     * given type.
     * 
     * @param boardClass
     *            Class to use as board implementation
     * @param players
     *            An array of player who will take part in this game
     * @param oStream
     *            Stream to communicate with the user
     * @param beVerbose
     *            true if output has to written to oStream, false if not
     * @throws InstantiationException 
     * @throws IllegalAccessException 
     */
    public Game(Class<? extends Board> boardClass, Player[] players) throws InstantiationException, 
            IllegalAccessException {

        initBoard(boardClass);

        playerlist = new ArrayList<Player>();

        for (Player player : players) {

            playerlist.add(player);

        }

        playerIterator = Iterables.cycle(playerlist).iterator();

    }

    /**
     * @param boardClass
     *            Class to use as board implementation
     */
    private void initBoard(Class<? extends Board> boardClass) throws InstantiationException, 
        IllegalAccessException {
            
        board = boardClass.newInstance();
      
    }

    /**
     * Play an game of connect 4 till the game has ended.
     * @throws InvalidMoveException 
     */
    public void play() throws InvalidMoveException{
        
        this.notifyObservers();
        boolean fairplay = true;
        Player current;

        while (!this.hasFinished() && fairplay) {
            this.setChanged();
            this.notifyObservers();
            current = playerIterator.next();
            board.makemove(current.doMove(board.deepCopy()));

        }

        if (this.hasWinner()) {
            this.setChanged();
            this.notifyObservers();
        }

    }

    /**
     * Returns the amount of plies that have been made since the start of the
     * game.
     * 
     * @return 0..boardClass.SIZE
     */
    public int plieCount() {

        return board.getPlieCount();

    }

    /**
     * Returns wether or not an player has won the game. If this method returns
     * true the player wich has won the game is the last player to make a move
     * because the rules of connect4 offers no posibility to let an other player
     * win solely based on the move you make.
     * 
     * @return true if a player has won the game; otherwise false
     */
    public boolean hasWinner() {
        return board.lastMoveWon();
    }

    /**
     * Return wether or not the game has ended.
     * 
     * @return true if board is full or a player won the game
     */
    public boolean hasFinished() {

        return board.isFull() || board.lastMoveWon();

    }
    
    /**
     * @return copy of the board used for the game state
     */
    public Board getBoard() {
        return board.deepCopy();
    }

}
