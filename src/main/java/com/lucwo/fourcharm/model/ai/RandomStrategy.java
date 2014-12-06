/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.Board;

import java.util.Random;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class RandomStrategy implements GameStrategy {

    private static final Random R_GENERATOR = new Random();

    /*
     * (non-Javadoc)
     * 
     * @see ft.model.GameStrategy#determineMove()
     */
    public int determineMove(Board board, Mark mark) {

        int col = R_GENERATOR.nextInt(board.getColumns());

        while (!board.columnHasFreeSpace(col)) {
            col = R_GENERATOR.nextInt(board.getColumns());
        }

        return col;

    }

}
