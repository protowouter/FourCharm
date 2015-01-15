/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.Board;

import java.util.logging.Logger;

/**
 * This MTDfStrategy class makes use of the interface GameStrategy. This
 * is a specific strategy a computer player (LocalAIPlayer class) can use.
 * So this class is responsible for giving the computer player brains. This
 * way the computer player can think ahead and foresee certain moves.
 */

public class MTDfStrategy implements GameStrategy {

    private static final int MAX_DEPTH = 10;
    private static final int DEPTH_STEP = 2;
    private static final int MAX_DURATION = 100_000;

    // ------------------ Instance variables ----------------
    private long start;
    private Double prevValue;
    private NegaMaxStrategy nega;

    // --------------------- Constructors -------------------

    public MTDfStrategy() {

        nega = new NegaMaxStrategy(MAX_DEPTH);
        prevValue = Double.MIN_VALUE;

    }


    // ----------------------- Queries ----------------------

    @Override
    public int determineMove(Board board, Mark mark) {
        start = System.currentTimeMillis();
        nega.resetCounter();

        double bestValue = Double.NEGATIVE_INFINITY;
        int bestMove = -1;
        int columns = board.getColumns();


        for (int col = 0; col < columns; col++) {
            if (board.columnHasFreeSpace(col)) {
                Board cBoard = board.deepCopy();
                try {
                    cBoard.makemove(col, mark);
                } catch (InvalidMoveException e) {
                    Logger.getGlobal().throwing(getClass().toString(), "determineMove", e);
                }
                double value = -mtdf(cBoard, mark.other(), MAX_DEPTH - 1);
                if (value > bestValue) {
                    bestMove = col;
                    bestValue = value;
                }

            }
        }
        Logger.getGlobal().fine("Evaluated nodes: " + nega.getCounter());
        Logger.getGlobal().fine("Best move: " + bestMove);

        prevValue = bestValue;

        if (bestMove == -1) {
            bestMove = new RandomStrategy().determineMove(board, mark);
        }

        return bestMove;
    }

    private double mtdf(Board board, Mark mark, int depth) {
        double guess = prevValue;

        double upperBound = Double.POSITIVE_INFINITY;
        double lowerBound = Double.NEGATIVE_INFINITY;
        double beta;

        while (lowerBound < upperBound) {

            if (guess == lowerBound) {
                beta = guess + 1;
            } else {
                beta = guess;

            }

            final double lBeta = beta;

            guess = nega.negaMax(board, mark, lBeta - 1, lBeta, depth);


            if (guess < beta) {
                upperBound = guess;
            } else {
                lowerBound = guess;
            }


        }
        return guess;
    }


}
