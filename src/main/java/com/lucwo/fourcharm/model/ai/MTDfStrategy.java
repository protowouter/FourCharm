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
 * This class also uses the NegaMaxStrategy class to come up with new moves.
 *
 * @author Luce Sandfort and Wouter Timmermans.
 */

public class MTDfStrategy implements GameStrategy {

    private static final int MAX_DURATION = 10_000;
    private static final double FIRST_GUESS = 17880;
    private static final int[] COLS = new int[]{3, 4, 2, 5, 1, 6, 0};
    private static final int DEPTH_STEP = 2;

    // ------------------ Instance variables ----------------
    private long endTime;
    private Double prevValue;
    private NegaMaxStrategy nega;

    // --------------------- Constructors -------------------

    /**
     * This method constructs the MTDf strategy.
     */
    public MTDfStrategy() {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "5");


        nega = new NegaMaxStrategy();
        prevValue = FIRST_GUESS;

    }


    // ----------------------- Queries ----------------------

    //TODO: Misschien nog wat uitgebreider beschrijven?
    /**
     * Determines the next moves given a maximum thinking time. Every spot gets a number
     * and the spot with the highest number is the best move to make.
     * @param board The board that is used by the game.
     * @param mark The mark of the current player.
     * @return The best move possible for the current situation on the board.
     */
    @Override
    public int determineMove(Board board, Mark mark) {
        nega.resetCounter();
        endTime = System.currentTimeMillis() + MAX_DURATION;
        int freeSpots = board.getSpotCount() - board.getPlieCount();
        double bestValue = Double.NEGATIVE_INFINITY;
        int bestMove = -1;
        int achievedDepth = 0;

        for (int depth = DEPTH_STEP; System.currentTimeMillis() < endTime && depth < freeSpots - 1;
             depth += DEPTH_STEP) {
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
                    Logger.getGlobal().finest("Depth: " + achievedDepth + " Col: " + valFut.getKey() + " Value: " + value);
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


    /**
     * The method gives the NegaMax value in less steps than the NegaMax algorithm.
     * @param board The board that is used by the game.
     * @param mark The mark of the current player.
     * @param depth The maximum search depth of the algorithm.
     * @return The NegaMax value of the current board.
     */
    private double mtdf(Board board, Mark mark, int depth) {
        double guess = prevValue;

        double upperBound = Double.POSITIVE_INFINITY;
        double lowerBound = Double.NEGATIVE_INFINITY;

        final int TIMEOUT = 10;

        while (lowerBound < upperBound && System.currentTimeMillis() - TIMEOUT < endTime) {
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

    /**
     * The name of the strategy.
     * @return The name of the strategy.
     */
    @Override
    public String toString() {
        return "MTDfStrategy";
    }


}
