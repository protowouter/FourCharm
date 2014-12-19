/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.board.Board;

import java.util.Observable;
import java.util.logging.Logger;

/**
 * Models an game of Connect 4.
 * 
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class Game extends Observable implements Runnable {

    private Board board;
    private Player winner;
    private Player player1;
    private Player player2;
    private Player current;

    /**
     * Create an new Game of connect 4. This constructor accepts an class
     * implementing the interface Board and initializes an new board of the
     * given type.
     *
     * @param boardClass Class to use as board implementation
     * @param p1 The first player who wil play this game
     * @param p2 The second player who wil play this game
     * @throws InstantiationException 
     * @throws IllegalAccessException 
     */
    public Game(Class<? extends Board> boardClass, Player p1, Player p2)
            throws InstantiationException, IllegalAccessException {
        super();

        initBoard(boardClass);

        player1 = p1;
        player2 = p2;

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
     */
    public void play() {

        notifyObservers();
        current = null;
        boolean fairplay = true;

        while (fairplay && (current == null || !hasFinished())) {
            current = nextPlayer();
            try {
                current.doMove(board);
                Logger.getGlobal().finer(board.toString());
            } catch (InvalidMoveException e) {
                fairplay = false;
            }
            setChanged();
            notifyObservers();

        }

        if (hasWinner()) {
            winner = current;
            setChanged();
            notifyObservers();
        }

        Logger.getGlobal().fine("Game ended, winner: " + winner);

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
        return board.hasWon(current.getMark());
    }


    /**
     * Returns the winner of the game, if there is a winner.
     *
     * @return The Player who won the game, null if there is no winner (yet)
     */
    public Player getWinner() {
        return winner;
    }

    public Player getCurrent() {
        return current;
    }

    /**
     * Return wether or not the game has ended.
     * 
     * @return true if board is full or a player won the game
     */
    public boolean hasFinished() {

        return board.isFull() || hasWinner();

    }
    
    /**
     * @return copy of the board used for the game state
     */
    public Board getBoard() {
        return board.deepCopy();
    }

    private Player nextPlayer() {

        Player next = player1;

        if (current == player1) {
            next = player2;
        } else if (current == player2) {
            next = player1;
        }

        return next;

    }

    public void run() {
        play();
    }

}
