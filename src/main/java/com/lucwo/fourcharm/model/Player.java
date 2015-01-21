/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.board.Board;

/**
 * A participant in a game of connect four. A player has a {@link com.lucwo.fourcharm.model.Mark} and can
 * be asked to make a move on a {@link com.lucwo.fourcharm.model.board.Board} or asked what move is best
 * to make on a given {@link com.lucwo.fourcharm.model.board.Board}.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public interface Player {


    /**
     * Given a board, determine a move to be made, implementing types can use
     * artificial or human intelligence (or lack thereof) to accomplish this.
     * @param board The board used to determine the move.
     * @return A (intelligent) legal move for the given board.
     */
    public int determineMove(Board board);

    /**
     * Makes a move.
     * @param board The current board.
     * @throws InvalidMoveException If the move is not allowed, an InvalidMoveException will be thrown.
     */
    public default void doMove(Board board) throws InvalidMoveException {
        board.makemove(determineMove(board), getMark());
    }

    /**
     * Gives the name.
     * @return The name.
     */
    public String getName();

    /**
     * Give the mark.
     * @return The mark.
     */
    public Mark getMark();

}
