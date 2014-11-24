/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model.ai;

import java.util.Random;

import com.lucwo.fourcharm.model.board.Board;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class RandomStrategy implements GameStrategy {

    private static final Random R_GENERATOR = new Random();

    /*
     * (non-Javadoc)
     * 
     * @see ft.model.GameStrategy#doMove()
     */
    public int doMove(Board board) {

        int col = R_GENERATOR.nextInt(board.getColumns());

        while (!board.columnHasFreeSpace(col)) {
            col = R_GENERATOR.nextInt(board.getColumns());
        }

        return col;

    }

}
