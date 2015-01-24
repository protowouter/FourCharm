/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.player;

import com.lucwo.fourcharm.model.board.Board;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * The AsyncPlayer class models a player that makes its decision
 * for a move in a different 'Thread' than the game class.
 * This could for instance be a human connected to the local
 * computer or a player on the other end of a server connection.
 * This class uses in internal queue to provide synchronisation
 * between the game thread and the thread which receives the input
 * from the actual player.
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
            Logger.getGlobal().throwing(getClass().toString(), "determineMove", e);
            column = determineMove(board);
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
            Logger.getGlobal().throwing(getClass().toString(), "queueMove", e);
            queueMove(col);
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
