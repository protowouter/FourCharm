/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.model;

import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.ReferenceBoard;
import org.junit.Before;
import org.junit.Test;

public class GameTest {

    static Player p1 = new ComputerPlayer(new RandomStrategy());
    static Player p2 = new ComputerPlayer(new RandomStrategy());
    Game game;

    @Before
    public void setUp() throws Exception {

        game = new Game(ReferenceBoard.class, new Player[]{p1, p2});

    }

    @Test
    public void testPlay() throws Exception {


        game.play();


    }

    @Test
    public void testPlieCount() throws Exception {

        assert game.plieCount() == 0;
        game.play();
        assert game.plieCount() != 0;

    }

    @Test
    public void testHasWinner() throws Exception {

        game.play();

        if (game.hasWinner()) {
            Player winner = game.getWinner();
            assert winner == p1 || winner == p2;
        } else {
            assert game.getWinner() == null;
        }

    }

    @Test
    public void testHasFinished() throws Exception {

        game.play();
        assert game.hasFinished();

    }

    @Test
    public void testGetBoard() throws Exception {

        assert game.getBoard() != null;

    }
}