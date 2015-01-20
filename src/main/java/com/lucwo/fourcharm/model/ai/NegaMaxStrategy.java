/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.Board;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * The NegaMaxStrategy class implements the GameStrategy interface. This combination
 * is used by the LocalAIPlayer class to make the computer player 'smart' and to make
 * the computer player able to win games by foreseeing moves. The algorithm used by the
 * NegaMaxStrategy class is the NegaMax algorithm.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class NegaMaxStrategy implements GameStrategy {

    /**
     * Default search depth for the NegaMax algorithm.
     */
    public static final int DEF_DEPTH = 6;
    public static final int FOE_POS_VALUE = -1000;
    public static final int FRIENDLY_POS_VALUE = 1000;
    public static final int EMPTY_POS_VALUE = 10;
    public static final ExecutorService NEGA_EXEC = ForkJoinPool.commonPool();

    public static final int POS_TABLE_SIZE = 20_000_000;
    public static final Map<Long, TransPosEntry> TRANS_POS_TABLE = new ConcurrentSkipListMap<>();

    private final AtomicLong nodeCounter = new AtomicLong();

    private int searchDept;

    public NegaMaxStrategy() {
        this(DEF_DEPTH);
    }

    public NegaMaxStrategy(int depth) {
        searchDept = depth;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ft.model.Player#determineMove(ft.model.board.Board)
     */
    @Override
    public int determineMove(Board board, Mark mark) {
        return determineMove(board.deepCopy(), mark, searchDept);
    }

    /**
     * Determine the move to be made using the negamax algorithm.
     *
     * @param board board for which the best move will be determined
     * @param depth search depth for the NegaMax algorithm
     * @return The best move according to the negamax algorithm.
     */
    public int determineMove(Board board, Mark mark, int depth) {
        resetCounter();
        Result result = negaMax(board, mark, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth);
        int bestMove = result.column;
        Double bestValue = result.value;
        Logger.getGlobal().fine("Calculated nodes: " + nodeCounter.get());
        Logger.getGlobal().fine("Best move: " + bestMove + " Value: " + bestValue);
        if (bestMove == -1) {
            bestMove = new RandomStrategy().determineMove(board, mark);
        }

        return bestMove;

    }

    /**
     * @param board Board for which the negaMax value will be determined
     * @param depth Depth at which will be searched for the best move
     * @return The negamax value of the current board state and the best move
     */
    public Result negaMax(Board board, Mark mark, double alphaOrig, double betaOrig, int depth) {
        double alpha = alphaOrig;
        double beta = betaOrig;
        long posKey = board.positionCode();
        Result result = null;
        boolean foundValue = false;

        TransPosEntry ttEntry = TRANS_POS_TABLE.get(posKey % POS_TABLE_SIZE);
        if (ttEntry != null && ttEntry.key == posKey && ttEntry.depth >= depth) {

            if (ttEntry.flag == Flag.EXACT) {
                result = new Result(ttEntry.move, ttEntry.value);
                foundValue = true;
            } else if (ttEntry.flag == Flag.LOWER_BOUND) {
                alpha = Math.max(alpha, ttEntry.value);
            } else if (ttEntry.flag == Flag.UPPER_BOUND) {
                beta = Math.min(beta, ttEntry.value);
            }
            if (alpha >= beta) {
                result = new Result(ttEntry.move, ttEntry.value);
                foundValue = true;
            }

        }

        if (!foundValue) {
            if (depth == 0 || board.isFull() || board.hasWon(mark.other())) {
                result = new Result(-1, nodeValue(board, mark));
            } else {

                result = getNegaResult(board, mark, depth, alpha, beta);

                saveToTransPostTable(alphaOrig, depth, beta, posKey, result);

            }
        }

        return result;

    }

    private Result getNegaResult(Board board, Mark mark, int depth, double alpha, double beta) {
        double newAlpha = alpha;
        Result result;
        double bestValue = Double.NEGATIVE_INFINITY;
        int bestMove = -1;
        int columns = board.getColumns();


        boolean searching = true;
        for (int col = 0; searching && col < columns; col++) {
            if (board.columnHasFreeSpace(col)) {
                try {
                    Board childBoard = board.deepCopy();
                    childBoard.makemove(col, mark);
                    double val = -negaMax(childBoard, mark.other(), -beta, -alpha, depth - 1).value;
                    if (val > bestValue) {
                        bestValue = val;
                        newAlpha = val;
                        bestMove = col;
                    }
                    searching = newAlpha < beta;

                } catch (InvalidMoveException e) {
                    Logger.getGlobal().throwing(getClass().toString(), "getNegaResult", e);
                }
            }



        }


        result = new Result(bestMove, bestValue);
        return result;
    }

    private void saveToTransPostTable(double alphaOrig, int depth, double beta, long posKey, Result result) {
        TransPosEntry ttEntry;
        ttEntry = new TransPosEntry();
        ttEntry.value = result.value;
        ttEntry.move = result.column;
        if (result.value <= alphaOrig) {
            ttEntry.flag = Flag.UPPER_BOUND;
        } else if (result.value >= beta) {
            ttEntry.flag = Flag.LOWER_BOUND;
        } else {
            ttEntry.flag = Flag.EXACT;
        }
        ttEntry.depth = depth;
        ttEntry.key = posKey;
        TRANS_POS_TABLE.put(posKey % POS_TABLE_SIZE, ttEntry);
    }

    public double nodeValue(Board board, Mark mark) {
        upCounter();


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

        value = board.hasWon(mark) ? Double.POSITIVE_INFINITY : value;
        value = board.hasWon(mark.other()) ? Double.NEGATIVE_INFINITY : value;

        return value;
    }

    private double horizontalValue(Board board, Mark mark, int vCol, int vRow) {

        int cols = board.getColumns();
        int streak = board.getWinStreak();
        int value = 0;

        int startCol = vCol - streak + 1;
        startCol = startCol < 0 ? 0 : startCol;


        for (int col = startCol; col < vCol + streak && col < cols; col++) {
            value += positionValue(board, mark, col, vRow);
        }

        return value;

    }

    private double verticalValue(Board board, Mark mark, int vCol, int vRow) {

        int rows = board.getRows();
        int streak = board.getWinStreak();
        int value = 0;

        int startRow = vRow - streak + 1;
        startRow = startRow < 0 ? 0 : startRow;

        for (int row = startRow; row < vRow + streak && row < rows; row++) {
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

        //Up and left
        for (int col = vCol, row = vRow; col > vCol - streak && col >= 0
                && row < vRow + streak && row < rows; row++, col--) {
            value += positionValue(board, mark, col, row);
        }

        //Down and right
        for (int col = vCol, row = vRow; col < cols && col < vCol + streak
                && row >= 0 && row > vRow - streak; col++, row--) {
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
     *
     * @ . . . . . .
     */

    private double rLDiagonalValue(Board board, Mark mark, int vCol, int vRow) {

        int rows = board.getRows();
        int cols = board.getColumns();
        int streak = board.getWinStreak();
        int value = 0;

        //Down and left
        for (int col = vCol, row = vRow; col >= 0 && col > vCol - streak
                && row >= 0 && row > vRow - streak; row--, col--) {
            value += positionValue(board, mark, col, row);
        }

        //Up and right
        for (int col = vCol, row = vRow; col < cols && col < vCol + streak
                && row < rows && row < vRow + streak; col++, row++) {
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

    public void upCounter() {
        nodeCounter.getAndIncrement();
    }

    public long getCounter() {
        return nodeCounter.get();
    }

    public void resetCounter() {
        nodeCounter.set(0);
    }

    @Override
    public String toString() {
        return "NegamaxStrategy";
    }

    enum Flag {

        EXACT, UPPER_BOUND, LOWER_BOUND

    }

    public static class Result {
        int column;
        Double value;

        public Result(int col, Double val) {
            column = col;
            value = val;
        }
    }

    private static class TransPosEntry {

        Flag flag;
        double value;
        int move;
        int depth;
        long key;

    }
}
