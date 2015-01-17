/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.Board;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * This MTDfStrategy class makes use of the interface GameStrategy. This
 * is a specific strategy a computer player (LocalAIPlayer class) can use.
 * So this class is responsible for giving the computer player brains. This
 * way the computer player can think ahead and foresee certain moves.
 */

public class MTDfStrategy implements GameStrategy {

    private static final int MAX_DURATION = 10_000;
    private static final double FIRST_GUESS = 840;
    private static final int[] COLS = new int[]{3, 4, 2, 5, 1, 6, 0};

    // ------------------ Instance variables ----------------
    private long endTime;
    private Double prevValue;
    private NegaMaxStrategy nega;

    // --------------------- Constructors -------------------

    public MTDfStrategy() {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "5");


        nega = new NegaMaxStrategy();
        prevValue = FIRST_GUESS;

    }


    // ----------------------- Queries ----------------------

    @Override
    public int determineMove(Board board, Mark mark) {
        nega.resetCounter();
        endTime = System.currentTimeMillis() + MAX_DURATION;
        int freeSpots = board.getSpotCount() - board.getPlieCount();
        double bestValue = Double.NEGATIVE_INFINITY;
        int bestMove = -1;
        int achievedDepth = 0;

        for (int depth = 2; System.currentTimeMillis() < endTime && depth < freeSpots - 1;
             depth++) {
            int bestMoveCurrentIteration = -1;
            double bestValueCurrentIteration = Double.NEGATIVE_INFINITY;
            final int mtDepth = depth - 1;

            Map<Integer, Future<Double>> valueFutures = new TreeMap<>();


            for (int col : COLS) {
                if (board.columnHasFreeSpace(col)) {
                    try {
                        Board cBoard = board.deepCopy();
                        cBoard.makemove(col, mark);
                        Future<Double> valFut = ForkJoinPool.commonPool()
                                .submit(() -> -mtdf(cBoard, mark.other(), mtDepth));
                        valueFutures.put(col, valFut);
                    } catch (InvalidMoveException e) {
                        Logger.getGlobal().throwing(getClass().toString(), "determineMove", e);
                    }
                }
            }
            try {
                for (Map.Entry<Integer, Future<Double>> valFut : valueFutures.entrySet()) {

                    double value = valFut.getValue().
                            get(endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                    if (value > bestValueCurrentIteration) {
                        bestMoveCurrentIteration = valFut.getKey();
                        bestValueCurrentIteration = value;
                    }
                }
                bestMove = bestMoveCurrentIteration;
                bestValue = bestValueCurrentIteration;
                achievedDepth = depth;
            } catch (InterruptedException | ExecutionException e) {
                Logger.getGlobal().throwing(getClass().toString(), "determineMove", e);
            } catch (TimeoutException e) {
                Logger.getGlobal().finer("Time's up: " + e.toString());
            }

        }
        if (bestMove == -1) {
            bestMove = new RandomStrategy().determineMove(board, mark);
        } else {
            prevValue = bestValue;
        }
        Logger.getGlobal().fine("Evaluated nodes: " + nega.getCounter());
        Logger.getGlobal().fine("Search achieved a depth of " + achievedDepth);
        Logger.getGlobal().fine("Best move: " + bestMove);
        Logger.getGlobal().fine("Best move value: " + bestValue);
        return bestMove;
    }


    private double mtdf(Board board, Mark mark, int depth) {
        double guess = prevValue;

        double upperBound = Double.POSITIVE_INFINITY;
        double lowerBound = Double.NEGATIVE_INFINITY;

        while (lowerBound < upperBound && System.currentTimeMillis() - 1000 < endTime) {
            double beta;

            if (guess == lowerBound) {
                beta = guess + 1;
            } else {
                beta = guess;

            }

            guess = nega.negaMax(board, mark, beta - 1, beta, depth).value;

            if (guess < beta) {
                upperBound = guess;
            } else {
                lowerBound = guess;
            }


        }
        return guess;
    }


}
