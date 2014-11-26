/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.board.Board;

import java.util.logging.Logger;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class NegaMaxStrategy implements GameStrategy {

    /**
     * Default search depth for the NegaMax algorithm.
     */
    public static final int DEF_DEPTH = 5;
    private final GameStrategy rStrat = new RandomStrategy();

    /*
     * (non-Javadoc)
     * 
     * @see ft.model.Player#doMove(ft.model.board.Board)
     */
    @Override
    public int doMove(Board board) {
        return doMove(board, DEF_DEPTH);
    }

    /**
     * Determine the move to be made using the negamax algorithm.
     * 
     * @param board
     * @param depth
     *            search depth for the NegaMax algorithm
     * @throws InvalidMoveException
     * @return The best move according to the negamax algorithm.
     */
    public int doMove(Board board, int depth) {

        double bestValue = Double.NEGATIVE_INFINITY;
        int bestMove = 0;
        int samevalues = 1;
        int freecolumns = 0;
        int columns = board.getColumns();

        for (int col = 0; col < columns; col++) {
            if (board.columnHasFreeSpace(col)) {
                try {
                    Board cBoard = board.deepCopy();
                    cBoard.makemove(col);
                    freecolumns++;
                    double value = -negaMax(cBoard, depth);
                    Logger.getGlobal().fine("Move: " + col + "Value:" + value);
                    if (value > bestValue) {
                        bestMove = col;
                        bestValue = value;
                    } else if (value == bestValue) {
                        samevalues++;
                    }
                } catch (InvalidMoveException e) {
                    Logger.getGlobal().throwing("NegaMaxStrategy", "negaMax", e);
                }
                

            }
        }

        return (samevalues == freecolumns) ? rStrat.doMove(board.deepCopy())
                : bestMove;

    }

    /**
     * 
     * @param board
     * @param depth
     * @throws InvalidMoveException
     * @return The negamax value of the current board state
     */
    private double negaMax(Board board, int depth) {
        double value;

        if ((depth == 0) || board.isFull() || board.lastMoveWon()) {
            value = nodeValue(board);
        } else {

            double bestValue = Double.NEGATIVE_INFINITY;
            int columns = board.getColumns();

            for (int col = 0; col < columns; col++) {
                if (board.columnHasFreeSpace(col)) {
                    try {
                        Board childBoard = board.deepCopy();
                        childBoard.makemove(col);
                        double tValue = -negaMax(childBoard, depth - 1);
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

    private double nodeValue(Board board) {
        // FIXME Write an better evaluation function

        boolean full = board.isFull();
        boolean won = board.lastMoveWon();
        double value;

        if (full) {
            value = 0;
        } else {
            value = won ? -1 : 1;
        }

        return value;
    }

}
