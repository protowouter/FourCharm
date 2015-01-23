/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.model.board.Board;
import com.lucwo.fourcharm.model.player.Mark;

import java.util.Random;

/**
 * The RandomStrategy class implements GameStrategy and is another way
 * to make the computer player of the LocalAIPlayer class 'smarter'.
 * Well, maybe not exactly smarter because the strategy is random, so
 * it will take a lucky guess to win a game.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class RandomStrategy implements GameStrategy {

    private static final Random R_GENERATOR = new Random();

    /**
     * Determines the next move.
     * @param board The current board.
     * @param mark The mark of the current player.
     * @return The best move.
     */
    public int determineMove(Board board, Mark mark) {

        int col = R_GENERATOR.nextInt(board.getColumns());

        while (!board.columnHasFreeSpace(col)) {
            col = R_GENERATOR.nextInt(board.getColumns());
        }

        return col;

    }

    /**
     * Gives the name of the strategy.
     * @return The name of the strategy ('RandomStrategy').
     */
    @Override
    public String toString() {
        return "RandomStrategy";
    }

}
