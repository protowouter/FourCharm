/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.player.ASyncPlayer;
import com.lucwo.fourcharm.model.player.Mark;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidMoveError;
import org.junit.Before;
import org.junit.Test;

public class GameGroupTest {


    @Mocked
    FourCharmServer server;
    @Mocked
    ClientHandler c1;
    @Mocked
    ClientHandler c2;
    @Mocked
    ClientHandler c3;
    @Mocked
    Game game;
    private GameGroup gameGroup;

    @Before
    public void setUp() throws Exception {

        gameGroup = new GameGroup(server, c1, c2);

    }

    @Test(expected = InvalidCommandError.class)
    public void testJoin() throws Exception {

        gameGroup.join(c3, "Frits", 23, null);

    }

    @Test
    public void testDoMove() throws Exception {

        new Expectations() {{
            c1.getName();
            result = "Frits";
            game.getBoard().columnHasFreeSpace(5);
            result = true;
            game.getCurrent();
            result = new ASyncPlayer("Frits", Mark.P1);
        }};

        gameGroup.doMove(c1, 5);

    }

    @Test(expected = InvalidMoveError.class)
    public void testDoMoveInvalidMove() throws Exception {
        gameGroup.doMove(c1, 8);
    }

    @Test(expected = InvalidMoveError.class)
    public void testDoMoveWrongPlayer() throws Exception {

        new Expectations() {{
            c2.getName();
            result = "Henk";
            game.getBoard().columnHasFreeSpace(5);
            result = true;
            game.getCurrent();
            result = new ASyncPlayer("Frits", Mark.P1);
        }};

        gameGroup.doMove(c2, 5);
    }

    @Test(expected = InvalidCommandError.class)
    public void testReady() throws Exception {
        gameGroup.ready(c3);
    }

    @Test
    public void testRemoveClientCallback() throws Exception {

        new NonStrictExpectations() {{
            c1.getCoreClient();
        }};
        gameGroup.removeClientCallback(c2);

    }

    @Test
    public void testUpdate() throws Exception {

        //TODO: invullen
    }
}