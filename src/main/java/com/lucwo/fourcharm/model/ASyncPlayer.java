/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.model.board.Board;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The ASyncPlayer class 'makes' a human player. To play a game of
 * Connect4 as a human, the Game class makes use of the interface
 * Player. The ASyncPlayer class implements Player. The main priority
 * of this class is get the input of the human player to the board.
 *
 *
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class ASyncPlayer implements Player {

    private final Mark mark;
    private BlockingQueue<Integer> rij;
    private String name;

    /**
     * Create an new humanplayer given an way to communicate with the player.
     * @param namePie The given name of the player.
     * @param themark The mark of the player.
     */
    public ASyncPlayer(String namePie, Mark themark) {
        super();
        mark = themark;
        rij = new LinkedBlockingQueue<>(1);
        name = namePie;

    }

    /**
     * Determines a move.
     * @param board The board used to determine the move.
     * @return The determint move (column).
     */
    public int determineMove(Board board) {

        int column = -1;
        try {
            column = rij.take();
        } catch (InterruptedException e) {
            // TODO: doe iets;
        }
        return column;

    }

    /**
     * Puts a move in the queueMove.
     * @param col The move.
     */
    public void queueMove(int col) {
        try {
            rij.put(col);
        } catch (InterruptedException e) {
            // TODO: doe iets
        }
    }

    /**
     * Gives the current mark.
     * @return The mark.
     */
    public Mark getMark() {
        return mark;
    }

    /**
     * Gives the name of the current player.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gives back string representation.
     * @return The string.
     */
    public String toString() {
        return getMark() + ": " + getClass().getSimpleName() + " " + getName();
    }

}
