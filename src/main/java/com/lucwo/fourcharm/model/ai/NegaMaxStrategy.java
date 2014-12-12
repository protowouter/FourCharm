/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.Board;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * @author Luce Sandfort and Wouter Timmermans
 */
public class NegaMaxStrategy implements GameStrategy {

    /**
     * Default search depth for the NegaMax algorithm.
     */
    public static final int DEF_DEPTH = 6;
    public static final ExecutorService VALUE_EXECUTOR = Executors.newCachedThreadPool();
    public static final int FOE_POS_VALUE = 0;
    public static final int FRIENDLY_POS_VALUE = 2;
    public static final int EMPTY_POS_VALUE = 1;

    public static final int POS_TABLE_SIZE = 10_000_000;
    public static final Map<Long, TransPosEntry> TRANS_POS_TABLE = new ConcurrentHashMap<>(POS_TABLE_SIZE);

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
        Map<Integer, Future<Double>> values = new HashMap<>();


        for (int col = 0; col < columns; col++) {
            if (board.columnHasFreeSpace(col)) {
                try {
                    Board cBoard = board.deepCopy();
                    cBoard.makemove(col, mark);
                    values.put(col, NegaMaxStrategy.VALUE_EXECUTOR.submit(() ->
                            -negaMax(cBoard, mark.other(), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth - 1)));
                } catch (InvalidMoveException e) {
                    Logger.getGlobal().throwing(getClass().toString(), "negaMax", e);
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

        return bestMove;

    }

    /**
     * @param board Board for which the negaMax value will be determined
     * @param depth Depth at which will be searched for the best move
     * @return The negamax value of the current board state
     */
    public double negaMax(Board board, Mark mark, double alpha, double beta, int depth) {
        double newAlpha = alpha;
        double newBeta = beta;
        long posKey = board.positioncode();
        double value = 0;
        boolean foundValue = false;

        TransPosEntryLookup transPosEntryLookup = new TransPosEntryLookup(depth, newAlpha, newBeta, posKey, value, foundValue).invoke();
        foundValue = transPosEntryLookup.isFoundValue();
        value = transPosEntryLookup.getValue();
        newBeta = transPosEntryLookup.getBeta();
        newAlpha = transPosEntryLookup.getAlpha();

        if (!foundValue && (depth == 0) || board.isFull() || board.hasWon(mark)) {
            value = nodeValue(board, mark);
        } else if (!foundValue) {

            NegaMaxCalculator negaMaxCalc = new NegaMaxCalculator(board, mark, depth, newAlpha, newBeta).calculate();
            newAlpha = negaMaxCalc.getNewAlpha();
            value = negaMaxCalc.getValue();

        }

        // Deze alpha hier MOET de originele alpha zijn
        addTransPosEntry(depth, alpha, newBeta, posKey, value);


        return value;

    }

    private void addTransPosEntry(int depth, double alphaOrig, double newBeta, long posKey, double value) {
        TransPosEntry ttEntry;
        ttEntry = new TransPosEntry();
        ttEntry.value = value;
        if (value <= alphaOrig) {
            ttEntry.flag = Flag.UPPER_BOUND;
        } else if (value >= newBeta) {
            ttEntry.flag = Flag.LOWER_BOUND;
        } else {
            ttEntry.flag = Flag.EXACT;
        }
        ttEntry.depth = depth;
        ttEntry.key = posKey;
        TRANS_POS_TABLE.put(posKey % POS_TABLE_SIZE, ttEntry);
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
            value += positionValue(board, mark, col, vRow);
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
            value += positionValue(board, mark, vCol, row);
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
            value += positionValue(board, mark, col, row);
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
            value += positionValue(board, mark, col, row);
        }

        return value;

    }

    private int positionValue(Board board, Mark mark, int col, int row) {
        Mark posMark = board.getMark(col, row);
        int value = FOE_POS_VALUE;
        if (posMark == mark) {
            value = FRIENDLY_POS_VALUE;
        } else if (posMark == Mark.EMPTY) {
            value = EMPTY_POS_VALUE;
        }

        return value;
    }

    enum Flag {

        EXACT, UPPER_BOUND, LOWER_BOUND

    }

    class TransPosEntry {

        Flag flag;
        double value;
        int depth;
        long key;

    }

    private class TransPosEntryLookup {
        private int depth;
        private double alpha;
        private double beta;
        private long posKey;
        private double value;
        private boolean foundValue;

        public TransPosEntryLookup(int depth, double newAlpha, double newBeta, long posKey, double value, boolean foundValue) {
            this.depth = depth;
            this.alpha = newAlpha;
            this.beta = newBeta;
            this.posKey = posKey;
            this.value = value;
            this.foundValue = foundValue;
        }

        public double getAlpha() {
            return alpha;
        }

        public double getBeta() {
            return beta;
        }

        public double getValue() {
            return value;
        }

        public boolean isFoundValue() {
            return foundValue;
        }

        public TransPosEntryLookup invoke() {
            TransPosEntry ttEntry = TRANS_POS_TABLE.get(posKey % POS_TABLE_SIZE);
            if (ttEntry != null && ttEntry.key == posKey && ttEntry.depth >= depth) {

                if (ttEntry.flag == Flag.EXACT) {
                    value = ttEntry.value;
                    foundValue = true;
                } else if (ttEntry.flag == Flag.LOWER_BOUND) {
                    alpha = Math.max(alpha, ttEntry.value);
                } else if (ttEntry.flag == Flag.UPPER_BOUND) {
                    beta = Math.min(beta, ttEntry.value);
                }
                if (alpha >= beta) {
                    value = ttEntry.value;
                    foundValue = true;
                }

            }
            return this;
        }
    }

    private class NegaMaxCalculator {
        private Board board;
        private Mark mark;
        private int depth;
        private double newAlpha;
        private double newBeta;
        private double value;

        public NegaMaxCalculator(Board board, Mark mark, int depth, double newAlpha, double newBeta) {
            this.board = board;
            this.mark = mark;
            this.depth = depth;
            this.newAlpha = newAlpha;
            this.newBeta = newBeta;
        }

        public double getNewAlpha() {
            return newAlpha;
        }

        public double getValue() {
            return value;
        }

        public NegaMaxCalculator calculate() {
            double bestValue = Double.NEGATIVE_INFINITY;
            int columns = board.getColumns();

            try {

                boolean searching = true;
                for (int col = 0; searching && col < columns; col++) {
                    if (board.columnHasFreeSpace(col)) {

                        Board childBoard = board.deepCopy();
                        childBoard.makemove(col, mark);
                        double tValue = -negaMax(childBoard, mark.other(), -newBeta, -newAlpha, depth - 1);
                        bestValue = Math.max(bestValue, tValue);
                        newAlpha = Math.max(newAlpha, tValue);

                        searching = newAlpha <= newBeta;

                    }
                }
            } catch (InvalidMoveException e) {
                Logger.getGlobal().throwing("NegaMaxStrategy", "negaMax", e);
            }

            value = bestValue;
            return this;
        }
    }
}
