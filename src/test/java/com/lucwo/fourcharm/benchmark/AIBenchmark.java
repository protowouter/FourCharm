/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.benchmark;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.ComputerPlayer;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.Player;
import com.lucwo.fourcharm.model.ai.NegaMaxStrategy;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Created by woutertimmermans on 26-11-14.
 */

// ------------------ Instance variables ----------------

// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------
public class AIBenchmark {

    /**
     * Amount of iterations of the benchmark.
     */
    public static final int ITERATIONS = 100;
    /**
     * How many times the current percentage should be shown.
     */
    public static final int STEP_PERCENTAGE = 10;


    private AIBenchmark() {
        super();
        // Hide the public constructor
    }

    private static int[] runBenchmark() throws InvalidMoveException, IllegalAccessException, InstantiationException {

        int ties = 0;
        int wins = 0;
        int loss = 0;
        int gCount = 0;
        int pCount = 0;
        final int total = ITERATIONS;
        final int step = total / STEP_PERCENTAGE;

        Player smartPlayer = new ComputerPlayer(new NegaMaxStrategy(), Mark.P1);
        Player dumbPlayer = new ComputerPlayer(new RandomStrategy(), Mark.P2);

        boolean switchPlayer = false;

        while (gCount < (total + 1)) {

            if (((gCount % step) == 0) && (gCount != 0)) {
                float percent = ((float) gCount / (float) total) * 100;
                int intPercent = (int) percent;
                Logger.getGlobal().info(intPercent + "%");
            }

            Game game;

            if (switchPlayer) {
                game = new Game(BinaryBoard.class, new Player[]{dumbPlayer, smartPlayer});
                switchPlayer = false;
            } else {
                game = new Game(BinaryBoard.class, new Player[]{smartPlayer, dumbPlayer});
                switchPlayer = true;
            }


            game.play();

            gCount++;
            pCount += game.plieCount();

            if (game.hasWinner()) {
                if (game.getWinner() == smartPlayer) {
                    wins++;
                } else {
                    loss++;
                }
            } else {
                ties++;
            }

        }

        return new int[]{wins, ties, loss};

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

        Logger.getGlobal().info("Running Benchmark with "
                + formatter.format(ITERATIONS) + " iterations");

        int[] score = AIBenchmark.runBenchmark();

        double percentage = ((double) (score[0] + score[1]) / ITERATIONS) * 100;


        Logger.getGlobal().info("\n\n\nResults:\n-----------\n");

        Logger.getGlobal().info("wins: " + score[0] + "\nties: " + score[1] + "\nlosses: " + score[2]);
        Logger.getGlobal().info("percentage (win + tie): " + percentage + "%");

    }
}
