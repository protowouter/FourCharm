/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.benchmark;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.ComputerPlayer;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.Player;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.board.Board;
import com.lucwo.fourcharm.model.board.ReferenceBoard;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Class for perfoming an benchmark of the BinaryBoard.
 * 
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class RandomBenchmark {
    
    /**
     * Amount of iterations of the benchmark.
     */
    public static final int ITERATIONS = 1_000_000;
    /**
     * How many times the current percentage should be shown.
     */
    public static final int STEP_PERCENTAGE = 10;


    private static int runBenchmark(Class<? extends Board> boardClass)
            throws InstantiationException, IllegalAccessException, InvalidMoveException {

        int gCount = 0;
        long pCount = 0;
        final int total = ITERATIONS;
        final int step = total / STEP_PERCENTAGE;
        long oStartTime = System.currentTimeMillis();

        while (gCount < (total + 1)) {

            if (((gCount % step) == 0) && (gCount != 0)) {
                float percent = ((float) gCount / (float) total) * 100;
                int intPercent = (int) percent;
                Logger.getGlobal().info(intPercent + "%");
            }

            Player p1 = new ComputerPlayer(new RandomStrategy(), Mark.P1);
            Player p2 = new ComputerPlayer(new RandomStrategy(), Mark.P2);

            Player current = null;

            int plieCount = 0;

            Board board = boardClass.newInstance();

            boolean finished = false;

            while (current == null || !finished) {

                if (current == p1) {
                    current = p2;
                } else if (current == p2) {
                    current = p1;
                } else {
                    current = p1;
                }

                current.doMove(board);
                plieCount++;

                finished = board.isFull() || board.hasWon(current.getMark());


            }

   

            
            gCount++;
            pCount += plieCount;

        }
        long endTime = System.currentTimeMillis();

        return (int) (pCount / ((float) (endTime - oStartTime) / 1000));

    }

    /**
     * Run the benchmark.
     * 
     * @param args
     *            N/A
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void main(String[] args) throws InstantiationException,
            IllegalAccessException, InvalidMoveException {

        DecimalFormat formatter = (DecimalFormat) NumberFormat
                .getInstance(Locale.ITALY);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        Logger.getGlobal().info("Running Benchmark with "
                + formatter.format(ITERATIONS) + " iterations");

        Logger.getGlobal().info("\n\n" + BinaryBoard.class.getSimpleName() + ":");
        int binMps = runBenchmark(BinaryBoard.class);
        Logger.getGlobal().info("\n\n" + ReferenceBoard.class.getSimpleName() + ":");
        int refMps = runBenchmark(ReferenceBoard.class);
        float speedup = (float) binMps / refMps;

        Logger.getGlobal().info("\n\n\nResults:\n-----------\n");

        Logger.getGlobal().info("BinaryBoard: " + formatter.format(binMps)
                + " mps (moves per second)");
        Logger.getGlobal().info("ReferenceBoard: " + formatter.format(refMps)
                + " mps (moves per second)");
        Logger.getGlobal().info("\nBinaryBoard is " + speedup
                + "x faster than the 2D array reference implementation");

    }

}
