/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.player;

import com.lucwo.fourcharm.model.board.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(ASyncPlayer.class);

    private final Mark mark;
    private BlockingQueue<Integer> rij;
    private String name;
    private boolean waiting;

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

        Integer column = null;
        waiting = true;
        while (waiting && column == null) {
            try {
                column = rij.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.trace("determineMove", e);
                column = determineMove(board);
            }
        }
        return column == null ? -1 : column;

    }

    public void abortMove() {
        waiting = false;
    }

    /**
     * Puts a move in the queueMove.
     * @param col The move.
     */
    public void queueMove(int col) {
        try {
            rij.put(col);
        } catch (InterruptedException e) {
            LOGGER.trace("queueMove", e);
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
