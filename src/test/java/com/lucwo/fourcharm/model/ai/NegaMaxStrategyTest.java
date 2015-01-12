/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.board.Board;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NegaMaxStrategyTest {

    public static int NEGA_DEPT = 6;

    private NegaMaxStrategy strat;
    private Board board;


    @Before
    public void setUp() throws Exception {

        strat = new NegaMaxStrategy(NEGA_DEPT);
        board = new BinaryBoard();


    }

    @Test
    public void testDoMove() throws Exception {

        assertTrue(board.columnHasFreeSpace(strat.determineMove(board, Mark.P1)));


    }
}