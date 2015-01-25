/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.benchmark;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.player.LocalAIPlayer;
import com.lucwo.fourcharm.model.player.Mark;
import com.lucwo.fourcharm.model.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public class AIBenchmark {

    /**
     * Amount of iterations of the benchmark.
     */
    public static final int ITERATIONS = 100;
    /**
     * How many times the current percentage should be shown.
     */
    public static final double STEP_PERCENTAGE = 0.01;
    private static Logger logger = LoggerFactory.getLogger(AIBenchmark.class);


    private AIBenchmark() {
        super();
        // Hide the public constructor
    }
    
    private static int[] runBenchmark() throws InvalidMoveException, IllegalAccessException, InstantiationException {

        int ties = 0;
        int wins = 0;
        int loss = 0;
        int moves = 0;
        double gCount = 0;
        final int total = ITERATIONS;
        final double step = total * STEP_PERCENTAGE;

        Player smartPlayer1 = new LocalAIPlayer(new MTDfStrategy(), Mark.P1);
        Player smartPlayer2 = new LocalAIPlayer(new MTDfStrategy(), Mark.P2);
        Player dumbPlayer1 = new LocalAIPlayer(new RandomStrategy(), Mark.P1);
        Player dumbPlayer2 = new LocalAIPlayer(new RandomStrategy(), Mark.P2);

        boolean switchPlayer = false;

        while (gCount < total) {

            if (((gCount % step) == 0) && (gCount != 0)) {
                float percent = ((float) gCount / (float) total) * 100;
                int intPercent = (int) percent;
                logger.info("{}%", intPercent);
            }

            Game game;

            if (switchPlayer) {
                game = new Game(BinaryBoard.class, dumbPlayer1, smartPlayer2);
                switchPlayer = false;
            } else {
                game = new Game(BinaryBoard.class, smartPlayer1, dumbPlayer2);
                switchPlayer = true;
            }


            game.play();

            gCount++;
            moves += game.plieCount();

            if (game.hasWinner()) {
                if (game.getWinner() == smartPlayer1 || game.getWinner() == smartPlayer2) {
                    wins++;
                } else {
                    logger.info(Integer.toString(game.getBoard().getPlieCount()));
                    logger.info(Arrays.toString(game.getBoard().getMoves()));
                    logger.info("Player 1: {}", (switchPlayer ? dumbPlayer1 : smartPlayer1).toString());
                    logger.info("Player 2: {}", (!switchPlayer ? dumbPlayer1 : smartPlayer1).toString());
                    logger.info(game.getBoard().toString());
                    loss++;
                }
            } else {
                ties++;
                logger.info("Tie: {}", game.getBoard().toString());
            }

        }

        return new int[]{wins, ties, loss, moves};

    }

    /**
     * Run the benchmark.
     *
     * @param args N/A
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void main(String[] args) throws InvalidMoveException, InstantiationException, IllegalAccessException {

        DecimalFormat formatter = (DecimalFormat) NumberFormat
                .getInstance(Locale.ITALY);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        logger.info("Running Benchmark with {} iterations", formatter.format(ITERATIONS));

        long start = System.currentTimeMillis();

        int[] score = AIBenchmark.runBenchmark();

        double percentage = ((double) (score[0] + score[1]) / ITERATIONS) * 100;


        logger.info("\n\n\nResults:\n-----------\n");

        logger.info("wins: " + score[0] + "\nties: " + score[1] + "\nlosses: " + score[2]);
        logger.info("percentage (win + tie): " + percentage + "%");

        long duration = System.currentTimeMillis() - start;
        logger.info("mps: " + ((double) score[3]) / duration * 1000);

    }
}
