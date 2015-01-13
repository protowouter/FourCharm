/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.ReferenceBoard;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameTest {

    static Player p1 = new LocalAIPlayer(new RandomStrategy(), Mark.P1);
    static Player p2 = new LocalAIPlayer(new RandomStrategy(), Mark.P2);
    Game game;

    @Before
    public void setUp() throws Exception {

        game = new Game(ReferenceBoard.class, p1, p2);
    }

    @Test
    public void testPlay() throws Exception {

        game.play();
    }

    @Test
    public void testPlieCount() throws Exception {

        assertTrue(game.plieCount() == 0);
        game.play();
        assertTrue(game.plieCount() != 0);
    }

    @Test
    public void testHasWinner() throws Exception {

        game.play();

        if (game.hasWinner()) {
            Player winner = game.getWinner();
            assertTrue(winner == p1 || winner == p2);
        } else {
            assertTrue(game.getWinner() == null);
        }
    }

    @Test
    public void testHasFinished() throws Exception {

        assertFalse(game.hasFinished());
        game.play();
        assertTrue(game.hasFinished());
    }

    @Test
    public void testGetBoard() throws Exception {

        assertTrue(game.getBoard() != null);
    }

    @Test
    public void testGetCurrentPlayer() throws Exception {
        game.play();
        assertNotNull(game.getCurrent());
    }
}