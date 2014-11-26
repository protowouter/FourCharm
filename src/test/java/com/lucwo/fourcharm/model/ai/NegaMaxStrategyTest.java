/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model.ai;

import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.board.Board;
import org.junit.Before;
import org.junit.Test;

public class NegaMaxStrategyTest {

    private NegaMaxStrategy strat;
    private Board board;


    @Before
    public void setUp() throws Exception {

        strat = new NegaMaxStrategy();
        board = new BinaryBoard();


    }

    @Test
    public void testDoMove() throws Exception {

        assert board.columnHasFreeSpace(strat.doMove(board));


    }
}