/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model;

import com.google.common.collect.Iterables;
import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.board.Board;

import java.util.*;

/**
 * Models an game of Connect 4.
 * 
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class Game extends Observable {

    private final Iterator<Player> playerIterator;
    private Board board;
    private Player winner;

    /**
     * Create an new Game of connect 4. This constructor accepts an class
     * implementing the interface Board and initializes an new board of the
     * given type.
     * 
     * @param boardClass
     *            Class to use as board implementation
     * @param players
     *            An array of player who will take part in this game
     * @throws InstantiationException 
     * @throws IllegalAccessException 
     */
    public Game(Class<? extends Board> boardClass, Player[] players) throws InstantiationException, 
            IllegalAccessException {
        super();

        initBoard(boardClass);

        List<Player> playerlist = new ArrayList<>();

        Collections.addAll(playerlist, players);

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
    public void play() throws InvalidMoveException {

        notifyObservers();
        Player current = null;

        while (!hasFinished()) {
            setChanged();
            notifyObservers();
            current = playerIterator.next();
            board.makemove(current.determineMove(board.deepCopy()), current.getMark());

        }

        if (hasWinner()) {
            winner = current;
            setChanged();
            notifyObservers();
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
        return board.hasWon(Mark.P1) || board.hasWon(Mark.P2);
    }


    /**
     * Returns the winner of the game, if there is a winner
     *
     * @return The Player who won the game, null if there is no winner (yet)
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Return wether or not the game has ended.
     * 
     * @return true if board is full or a player won the game
     */
    public boolean hasFinished() {

        return board.isFull() || board.hasWon(Mark.P1) || board.hasWon(Mark.P2);

    }
    
    /**
     * @return copy of the board used for the game state
     */
    public Board getBoard() {
        return board.deepCopy();
    }

}
