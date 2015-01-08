/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.board.Board;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ComputerPlayerTest {

    ComputerPlayer player;
    Board board;

    @Before
    public void setUp() throws Exception {

        player = new ComputerPlayer(new RandomStrategy(), Mark.P1);
        board = new BinaryBoard();


    }


    @Test
    public void testDoMove() throws Exception {

        assertTrue(board.columnHasFreeSpace(player.determineMove(board)));

    }
}