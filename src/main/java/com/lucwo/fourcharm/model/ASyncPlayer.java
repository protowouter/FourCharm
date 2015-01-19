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
     * 
     * @param inputReader
     *            Reader from which the human input can be parsed
     */
    public ASyncPlayer(String namePie, Mark themark) {
        super();
        mark = themark;
        rij = new LinkedBlockingQueue<>(1);
        name = namePie;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ft.model.Player#determineMove()
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

    public void queueMove(int col) {
        try {
            rij.put(col);
        } catch (InterruptedException e) {
            // TODO: doe iets
        }
    }

    public Mark getMark() {
        return mark;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getMark() + ": " + getClass().getSimpleName() + " " + getName();
    }

}
