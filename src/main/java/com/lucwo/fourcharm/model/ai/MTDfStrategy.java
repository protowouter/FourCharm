/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.Board;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class MTDfStrategy implements GameStrategy {

    public static final ExecutorService VALUE_EXECUTOR = Executors.newCachedThreadPool();
    private static final int MAX_DEPTH = 10;
    private static final int DEPTH_STEP = 2;
    private static final int MAX_DURATION = 100_000;

    // ------------------ Instance variables ----------------
    private long start;
    private Double prevValue;
    private NegaMaxStrategy nega;

    // --------------------- Constructors -------------------

    public MTDfStrategy() {

        nega = new NegaMaxStrategy();
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
        Map<Integer, Future<Double>> values = new HashMap<>();


        for (int col = 0; col < columns; col++) {
            if (board.columnHasFreeSpace(col)) {
                try {
                    Board cBoard = board.deepCopy();
                    cBoard.makemove(col, mark);
                    values.put(col, VALUE_EXECUTOR.submit(() ->
                            -mtdf(cBoard, mark.other(), MAX_DEPTH - 1)));
                } catch (InvalidMoveException e) {
                    Logger.getGlobal().throwing(getClass().toString(), "determineMove", e);
                }
            }
        }

        for (int col = 0; col < columns; col++) {
            if (values.get(col) != null) {
                double value = 0;

                try {
                    value = values.get(col).get();
                } catch (InterruptedException | ExecutionException e) {
                    Logger.getGlobal().throwing(getClass().toString(), "determineMove", e);
                }
                Logger.getGlobal().fine("Move: " + col + " Value: " + value);
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
            FutureTask<Double> guessValue = new FutureTask<>(() -> nega.negaMax(board, mark, lBeta - 1, lBeta, depth));
            try {
                VALUE_EXECUTOR.submit(guessValue);
                guess = guessValue.get((start + MAX_DURATION) - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                guessValue.cancel(true);
            }


            if (guess < beta) {
                upperBound = guess;
            } else {
                lowerBound = guess;
            }


        }
        return guess;
    }


}
