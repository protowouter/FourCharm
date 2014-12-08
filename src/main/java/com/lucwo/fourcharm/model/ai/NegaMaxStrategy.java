/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.Board;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * @author Luce Sandfort and Wouter Timmermans
 */
public class NegaMaxStrategy implements GameStrategy {

    /**
     * Default search depth for the NegaMax algorithm.
     */
    public static final int DEF_DEPTH = 5;
    public static final ExecutorService VALUE_EXECUTOR = Executors.newCachedThreadPool();
    private final GameStrategy rStrat = new RandomStrategy();

    /*
     * (non-Javadoc)
     * 
     * @see ft.model.Player#determineMove(ft.model.board.Board)
     */
    @Override
    public int determineMove(Board board, Mark mark) {
        return determineMove(board.deepCopy(), mark, DEF_DEPTH);
    }

    /**
     * Determine the move to be made using the negamax algorithm.
     *
     * @param board board for which the best move will be determined
     * @param depth search depth for the NegaMax algorithm
     * @return The best move according to the negamax algorithm.
     */
    public int determineMove(Board board, Mark mark, int depth) {

        double bestValue = Double.NEGATIVE_INFINITY;
        int bestMove = 0;
        int columns = board.getColumns();
        Map<Integer, Future<Double>> vals = new HashMap<>();



        for (int col = 0; col < columns; col++) {
            if (board.columnHasFreeSpace(col)) {
                try {
                    Board cBoard = board.deepCopy();
                    cBoard.makemove(col, mark);
                    vals.put(col, NegaMaxStrategy.VALUE_EXECUTOR.submit(() ->
                            -negaMax(cBoard, mark.other(), depth - 1)));
                } catch (InvalidMoveException e) {
                    Logger.getGlobal().throwing("NegaMaxStrategy", "negaMax", e);
                }
            }
        }

        for (int col = 0; col < columns; col++) {
            if (vals.get(col) != null) {
                double value = 0;

                try {
                    value = vals.get(col).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                Logger.getGlobal().fine("Move: " + col + " Value: " + value);
                if (value > bestValue) {
                    bestMove = col;
                    bestValue = value;
                }

            }

        }

        return bestMove;

    }

    /**
     * @param board Board for which the negaMax value will be determined
     * @param depth Depth at which will be searched for the best move
     * @return The negamax value of the current board state
     */
    private double negaMax(Board board, Mark mark, int depth) {
        double value;

        if ((depth == 0) || board.isFull() || board.hasWon(mark)) {
            value = nodeValue(board, mark);
        } else {

            double bestValue = Double.NEGATIVE_INFINITY;
            int columns = board.getColumns();

            for (int col = 0; col < columns; col++) {
                if (board.columnHasFreeSpace(col)) {
                    try {
                        Board childBoard = board.deepCopy();
                        childBoard.makemove(col, mark);
                        double tValue = -negaMax(childBoard, mark.other(), depth - 1);
                        bestValue = Math.max(bestValue, tValue);
                    } catch (InvalidMoveException e) {
                        Logger.getGlobal().throwing("NegaMaxStrategy", "negaMax", e);
                    }
                }
            }

            value = bestValue;

        }

        return value;

    }

    private double nodeValue(Board board, Mark mark) {


        int cols = board.getColumns();
        int rows = board.getRows();
        double value = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                value += horizontalValue(board, mark, col, row) +
                        verticalValue(board, mark, col, row) +
                        lRDiagonalValue(board, mark, col, row) +
                        rLDiagonalValue(board, mark, col, row);
            }
        }

        boolean mWin = board.hasWon(mark);
        boolean oWin = board.hasWon(mark.other());

        if (mWin) {
            value = Double.POSITIVE_INFINITY;
        } else if (oWin) {
            value = Double.NEGATIVE_INFINITY;
        }

        return value;
    }

    private double horizontalValue(Board board, Mark mark, int vCol, int vRow) {

        int cols = board.getColumns();
        int streak = board.getWinStreak();
        int value = 0;

        int startCol = vCol - streak;
        startCol = startCol < 0 ? 0 : startCol;


        for (int col = startCol; col < col + streak && col < cols; col++) {
            switch (board.getMark(col, vRow)) {
                case P1:
                    value = mark == Mark.P1 ? value + 2 : value;
                    break;
                case P2:
                    value = mark == Mark.P2 ? value + 2 : value;
                    break;
                default:
                    value += 1;
                    break;
            }
        }

        return value;

    }

    private double verticalValue(Board board, Mark mark, int vCol, int vRow) {

        int rows = board.getRows();
        int streak = board.getWinStreak();
        int value = 0;

        int startRow = vRow - streak;
        startRow = startRow < 0 ? 0 : startRow;

        for (int row = startRow; row < row + streak && row < rows; row++) {
            switch (board.getMark(vCol, row)) {
                case P1:
                    value = mark == Mark.P1 ? value + 2 : value;
                    break;
                case P2:
                    value = mark == Mark.P2 ? value + 2 : value;
                    break;
                default:
                    value += 1;
                    break;
            }
        }

        return value;

    }

    /**
     * Find columns of the following form:
     * . . . . . . .
     * . . . . . . .
     * . . . @ . . .
     * . . . . @ . .
     * . . . . . @ .
     * . . . . . . @
     */

    private double lRDiagonalValue(Board board, Mark mark, int vCol, int vRow) {

        int rows = board.getRows();
        int cols = board.getColumns();
        int streak = board.getWinStreak();
        int value = 0;

        int startRow = vRow - streak;
        startRow = startRow < 0 ? 0 : startRow;

        int startCol = vCol + streak;
        startCol = startCol > cols - 1 ? cols - 1 : startCol;

        for (int col = startCol, row = startRow; col > vCol - streak && col > 0
                && row < startRow + streak && row < rows; col--, row++) {
            switch (board.getMark(col, row)) {
                case P1:
                    value = mark == Mark.P1 ? value + 2 : value;
                    break;
                case P2:
                    value = mark == Mark.P2 ? value + 2 : value;
                    break;
                default:
                    value += 1;
                    break;
            }
        }

        return value;

    }

    /**
     * Find columns of the following form:
     * . . . . . . .
     * . . . . . . .
     * . . . @ . . .
     * . . @ . . . .
     * . @ . . . . .
     * @ . . . . . .
     */

    private double rLDiagonalValue(Board board, Mark mark, int vCol, int vRow) {

        int rows = board.getRows();
        int cols = board.getColumns();
        int streak = board.getWinStreak();
        int value = 0;

        int startRow = vRow - streak;
        startRow = startRow < 0 ? 0 : startRow;

        int startCol = vCol - streak;
        startCol = startCol < 0 ? 0 : startCol;

        for (int col = startCol, row = startRow; col < vCol + streak && col < cols
                && row < vRow + streak && row < rows; col++, row++) {
            switch (board.getMark(col, row)) {
                case P1:
                    value = mark == Mark.P1 ? value + 2 : value;
                    break;
                case P2:
                    value = mark == Mark.P2 ? value + 2 : value;
                    break;
                default:
                    value += 1;
                    break;
            }
        }

        return value;

    }

}
