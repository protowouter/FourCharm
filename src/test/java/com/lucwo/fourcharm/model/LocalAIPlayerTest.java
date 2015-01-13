/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.board.Board;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocalAIPlayerTest {

    LocalAIPlayer player;
    Board board;

    @Before
    public void setUp() throws Exception {

        player = new LocalAIPlayer(new RandomStrategy(), Mark.P1);
        board = new BinaryBoard();


    }


    @Test
    public void testDoMove() throws Exception {

        assertTrue(board.columnHasFreeSpace(player.determineMove(board)));

    }

    @Test
    public void testGetMark() throws Exception {
        assertEquals(player.getMark(), Mark.P1);
    }
}