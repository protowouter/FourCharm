/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.board.Board;
import com.lucwo.fourcharm.model.player.Mark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(MTDfStrategy.class);
    private static final Marker AI_DEBUG = MarkerFactory.getMarker("AI_DEBUG");
    private static final Marker AI_INFO = MarkerFactory.getMarker("AI_INFO");

    private static final int DEF_DURATION = 10_000;
    private static final double FIRST_GUESS = 17880;
    private static final int[] COLS = new int[]{3, 4, 2, 5, 1, 6, 0};
    private static final int DEPTH_STEP = 2;
    private static final int TIMEOUT = 10;

    // ------------------ Instance variables ----------------
    private long endTime;
    private Double prevValue;
    private NegaMaxStrategy nega;
    private int duration;

    // --------------------- Constructors -------------------

    /**
     * This method constructs the MTDf strategy.
     */
    public MTDfStrategy() {
        this(DEF_DURATION);




    }

    public MTDfStrategy(int time) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "5");
        duration = time * 1000;
        nega = new NegaMaxStrategy();
        prevValue = FIRST_GUESS;
    }


    // ----------------------- Queries ----------------------

    /**
     * Determines the next moves given a maximum thinking time. Every spot gets a value
     * and the spot with the highest value is the best move to make.
     *
     * @param board The current board state used to determine the best move.
     * @param mark  The mark of the current player.
     * @return The best move possible for the current situation on the board.
     */
    @Override
    public int determineMove(Board board, Mark mark) {
        nega.resetCounter();
        endTime = System.currentTimeMillis() + duration;
        int freeSpots = board.getSpotCount() - board.getPlieCount();
        double bestValue = Double.NEGATIVE_INFINITY;
        int bestMove = -1;
        int achievedDepth = 0;

        for (int depth = DEPTH_STEP - 1; System.currentTimeMillis() < endTime && depth < freeSpots - 1;
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
                        LOGGER.trace("determineMove", e);
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
                    LOGGER.debug(AI_DEBUG, "Depth: {} Col: {} Value: {}", achievedDepth, valFut.getKey(), value);
                }
                bestMove = bestMoveCurrentIteration;
                bestValue = bestValueCurrentIteration;
                achievedDepth = depth;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.trace("determineMove", e);
            }

        }
        if (bestMove == -1) {
            bestMove = new RandomStrategy().determineMove(board, mark);
        } else {
            prevValue = bestValue;
        }
        LOGGER.debug(AI_DEBUG, "Evaluated nodes: {}", nega.getCounter());
        LOGGER.debug(AI_DEBUG, "Search achieved a depth of {}", achievedDepth);
        LOGGER.debug(AI_INFO, "Best move {}", bestMove);
        LOGGER.debug(AI_DEBUG, "Best move value {}", bestValue);
        nega.abort();
        return bestMove;
    }


    /**
     * The method gives the NegaMax value in less steps than the NegaMax algorithm
     * by using a zero width alpha beta window.
     *
     * @param board The board that is used by the game.
     * @param mark  The mark of the current player.
     * @param depth The maximum search depth of the algorithm.
     * @return The NegaMax value of the current board.
     */
    private double mtdf(Board board, Mark mark, int depth) {
        double guess = prevValue;

        //@ invariant upperBound > lowerBound;
        double upperBound = Double.POSITIVE_INFINITY;
        //@ invariant lowerBound < upperBound;
        double lowerBound = Double.NEGATIVE_INFINITY;

        while (lowerBound < upperBound && System.currentTimeMillis() - TIMEOUT < endTime) {
            double beta;

            if (guess == lowerBound) {
                beta = guess + 1;
            } else {
                beta = guess;

            }

            guess = nega.startNegaMax(board, mark, beta - 1, beta, depth).value;

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
     *
     * @return The name of the strategy.
     */
    @Override
    public String toString() {
        return "MTDfStrategy";
    }


}
