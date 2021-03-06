/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.board.Board;
import com.lucwo.fourcharm.model.player.ASyncPlayer;
import com.lucwo.fourcharm.model.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;

/**
 * The Game class models a game of Connect4. This class makes use of Observable
 * and Runnable. Furthermore this class uses the Board class in the constructor,
 * so a new board will be made for this specific game. Using these classes and
 * interfaces, the main priority of this Game class is to create, play and end
 * a game of Connect4.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class Game extends Observable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private Board board;
    private Player winner;
    private Player player1;
    private Player player2;
    private Player current;
    private boolean running;
    private boolean moveMade;

    /**
     * Create an new Game of connect 4. This constructor accepts an class
     * implementing the interface Board and initializes an new board of the
     * given type.
     *
     * @param boardClass Class to use as board implementation
     * @param p1         The first player who wil play this game
     * @param p2         The second player who wil play this game
     */
    public Game(Class<? extends Board> boardClass, Player p1, Player p2) {
        super();

        initBoard(boardClass);

        player1 = p1;
        player2 = p2;
        running = true;
        current = p1;
        moveMade = false;
    }

    /**
     * @param boardClass Class to use as board implementation
     */
    private void initBoard(Class<? extends Board> boardClass) {

        try {
            board = boardClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.trace("initBoard", e);
        }

    }

    /**
     * Play an game of connect 4 till the game has ended.
     */
    public void play() {

        notifyObservers();
        boolean fairplay = true;

        while (running && fairplay && !hasFinished()) {
            current = nextPlayer();
            moveMade = true;
            setChanged();
            notifyObservers();
            try {
                current.doMove(board);
            } catch (InvalidMoveException e) {
                LOGGER.trace("player", e);
                fairplay = false;
            }

        }

        if (hasWinner()) {
            winner = current;
            setChanged();
            notifyObservers();
        }

        LOGGER.info("Game ended, winner: {}", winner);

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
     * Returns whether or not an player has won the game. If this method returns
     * true the player wich has won the game is the last player to make a move
     * because the rules of connect4 offers no possibility to let an other player
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
     * Give a copy of the board.
     *
     * @return copy of the board used for the game state
     */
    public Board getBoard() {
        return board.deepCopy();
    }

    /**
     * Gives the next player.
     *
     * @return The player who is next.
     */
    private Player nextPlayer() {

        Player next = player1;

        if (moveMade && current == player1) {
            next = player2;
        }

        return next;

    }

    /**
     * Runs the game.
     */
    public void run() {
        play();
    }

    /**
     * Closes the game by using shutdown.
     */
    public void shutdown() {
        running = false;
        if (current instanceof ASyncPlayer) {
            ((ASyncPlayer) current).abortMove();
        }
    }

}
