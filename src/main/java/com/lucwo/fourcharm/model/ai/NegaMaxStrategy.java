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
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The NegaMaxStrategy class implements the GameStrategy interface. This combination
 * is used by the LocalAIPlayer class to make the computer player 'smart' and to make
 * the computer player able to win games by foreseeing moves. The algorithm used by the
 * NegaMaxStrategy class is the NegaMax algorithm. This implementation makes use of Alpha-Beta
 * pruning and a transposition table to optimize performance.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class NegaMaxStrategy implements GameStrategy {

    /**
     * Default search depth for the NegaMax algorithm.
     */
    public static final int DEF_DEPTH = 6;
    private static final Logger LOGGER = LoggerFactory.getLogger(MTDfStrategy.class);
    private static final Marker AI_DEBUG = MarkerFactory.getMarker("AI_DEBUG");
    private static final Marker AI_INFO = MarkerFactory.getMarker("AI_INFO");
    private static final int FOE_POS_VALUE = -1000;
    private static final int FRIENDLY_POS_VALUE = 1000;
    private static final int EMPTY_POS_VALUE = 10;
    private static final int POS_TABLE_SIZE = 20_000_000;
    private static final Map<Long, TransPosEntry> TRANS_POS_TABLE = new ConcurrentSkipListMap<>();

    private final AtomicLong nodeCounter = new AtomicLong();

    private int searchDept;

    /**
     * Constructs a new NegaMaxStrategy. Uses the default search depth.
     */
    public NegaMaxStrategy() {
        this(DEF_DEPTH);
    }

    /**
     * Constructs a new NegaMaxStrategy with the given depth.
     * @param depth The depth the NegaMaxStrategy will use.
     */
    /*@
     * requires depth >= 1;
     */
    public NegaMaxStrategy(int depth) {
        searchDept = depth;
    }

    /**
     * Determine the move to be made using the NegaMax algorithm.
     * @param board The board for which the best move will be determined.
     * @param mark The mark of the current player.
     * @return The best move according to the negamax algorithm.
     */
    @Override
    public int determineMove(Board board, Mark mark) {
        return determineMove(board.deepCopy(), mark, searchDept);
    }

    /**
     * Determine the move to be made using the NegaMax algorithm given a specific
     * thinking time / depth.
     *
     * @param board The board for which the best move will be determined.
     * @param mark The mark of the current player.
     * @param depth The search depth for the NegaMax algorithm.
     * @return The best move according to the negamax algorithm.
     */
    public int determineMove(Board board, Mark mark, int depth) {
        resetCounter();
        // Best and worst move are not know yet so use -infinity for alpha and infinity for beta
        Result result = negaMax(board, mark, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, depth);
        int bestMove = result.column;
        Double bestValue = result.value;
        LOGGER.debug(AI_DEBUG, "Calculated nodes: {}", nodeCounter.get());
        LOGGER.debug(AI_INFO, "Best move: {} Value: {}", bestMove, bestValue);
        if (bestMove == -1) {
            bestMove = new RandomStrategy().determineMove(board, mark);
        }

        return bestMove;

    }

    /**
     * Finds the best move on a {@link com.lucwo.fourcharm.model.board.Board}
     * for a given {@link com.lucwo.fourcharm.model.player.Mark}.
     * @param board Board on which the NegaMax search will be performed.
     * @param mark The mark of the current player.
     * @param alphaOrig The value of the worst move that has been found for the maximizing player.
     * @param betaOrig The value of the best move that has been found for the minimizing player.
     * @param depth Depth at which will be searched for the best move
     * @return The negamax value of the current board state and the best move
     */
    public Result negaMax(Board board, Mark mark, double alphaOrig, double betaOrig, int depth) {
        double alpha = alphaOrig;
        double beta = betaOrig;
        long posKey = board.positionCode();
        Result result = null;
        boolean foundValue = false;


        // Perform a transposition table lookup
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

        // Helaas! The exact node value was not found. Continue searching.
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

    /**
     * Performs the actual NegaMax search.
     * @param board The current board.
     * @param mark The mark of the current player.
     * @param depth The maximum searching depth.
     * @param alpha The value of the worst move that has been found for the maximizing player.
     * @param beta The value of the best move that has been found for the minimizing player.
     * @return The best move and its value. This value will be the highest value.
     */

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
                    double val = -negaMax(childBoard, mark.other(),
                            -beta, -newAlpha, depth - 1).value;
                    if (val > bestValue) {
                        bestValue = val;
                        newAlpha = val;
                        bestMove = col;
                    }
                    searching = newAlpha < beta;

                } catch (InvalidMoveException e) {
                    LOGGER.trace("getNegaResult", e);
                }
            }
        }
        result = new Result(bestMove, bestValue);
        return result;
    }

    /**
     * Saves the found NegaMax value in the ranspositiontable.
     * @param alphaOrig the original value of alpha.
     * @param depth the depth at which the value was found.
     * @param beta the beta value when the value was found.
     * @param posKey the "hashcode" of the board node.
     * @param result the found Result containing the NegaMax value.
     */
    private void saveToTransPostTable(double alphaOrig, int depth, double beta,
                                      long posKey, Result result) {
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

    /**
     * Calculates the value of a specific board node (board configuration).
     * @param board The current board.
     * @param mark The mark of the current player.
     * @return The calculated value.
     */
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

    /**
     * Calculates the horizontal value of a spot.
     * @param board The current board.
     * @param mark The mark of the current player.
     * @param vCol The column of the spot.
     * @param vRow The row of the spot.
     * @return The calculated horizontal value.
     */
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

    /**
     * Calculates the vertical value of a spot.
     * @param board The current board.
     * @param mark The mark of the current player.
     * @param vCol The column of the spot.
     * @param vRow The row of the spot.
     * @return The calculated vertical value.
     */
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
     * Calculates the diagonal value by finding rows of the following form:
     * . . . . . . .
     * . . . . . . .
     * . . . @ . . .
     * . . . . @ . .
     * . . . . . @ .
     * . . . . . . @
     * @param board The current board.
     * @param mark The mark of the current player.
     * @param vCol The columns of the spot.
     * @param vRow The rows of the spot.
     * @return The calculated diagonal value.
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
     * Calculates the diagonal value by finding rows of the following form:
     * . . . . . . .
     * . . . . . . .
     * . . . @ . . .
     * . . @ . . . .
     * . @ . . . . .
     * @ . . . . . .
     * @param board The current board.
     * @param mark The mark of the current player.
     * @param vCol The columns of the spot.
     * @param vRow The rows of the spot.
     * @return The calculated diagonal value.
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

    /**
     * Calculates a position value.
     * @param board The current board.
     * @param mark The mark of the current player.
     * @param col The column of the move.
     * @param row The row of the move.
     * @return The calculated postion value.
     */
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

    /**
     * Increases the nodeCounter.
     */
    public void upCounter() {
        nodeCounter.getAndIncrement();
    }

    /**
     * Gives the current nodeCounter value.
     * @return the nodeCounter.
     */
    public long getCounter() {
        return nodeCounter.get();
    }

    /**
     * Resets the nodeCounter.
     */
    public void resetCounter() {
        nodeCounter.set(0);
    }

    /**
     * Gives the name of the strategy.
     * @return 'NegaMaxStrategy'
     */
    @Override
    public String toString() {
        return "NegamaxStrategy";
    }


    /**
     * Flag used to determine if a value in the transposition value can be used as the value(EXACT),
     * as alpha (LOWER_BOUND) or beta (UPPER_BOUND).
     */
    enum Flag {

        EXACT, UPPER_BOUND, LOWER_BOUND
    }

    /**
     * Models an result of the NegaMax algorithm (best move and value).
     */
    public static class Result {
        int column;
        Double value;

        public Result(int col, Double val) {
            column = col;
            value = val;
        }
    }

    /**
     * Entry in the transpositiontable.
     */
    private static class TransPosEntry {

        Flag flag;
        double value;
        int move;
        int depth;
        long key;

    }
}
