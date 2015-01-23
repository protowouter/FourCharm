/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.client;

import com.lucwo.fourcharm.controller.FourCharmController;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.player.ASyncPlayer;
import com.lucwo.fourcharm.model.player.Mark;
import mockit.Expectations;
import mockit.Mocked;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreServer;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.net.Socket;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ServerHandlerTest {

    private ServerHandler handler;
    @Mocked
    CoreServer.Client serverClient;
    @Mocked
    private FourCharmController controller;
    @Mocked
    private Socket sock;

    @Before
    public void setup() throws Exception {
        handler = new ServerHandler("Wouter", "localhost", "1337", controller);
    }

    @Test
    public void testSetStrategy() throws Exception {
        final GameStrategy strat = new RandomStrategy();
        assertNull("Strategy should be null initially", handler.getStrategy());
        handler.setStrategy(strat);
        assertEquals("SetStrategy should update the Strategy", strat, handler.getStrategy());
    }

    @Test
    public void testJoinServer() throws Exception {

        new Expectations() {{
            serverClient.join("Wouter", 23, withAny(new HashSet<>()));
        }};

        handler.joinServer();

    }

    @Test
    public void testHandleServerCommands(@Mocked BufferedReader anyReader) throws Exception {

        new Expectations() {{
            anyReader.readLine();
            returns("hoi", "hallo", null);

        }};

        handler.handleServerCommands();
    }

    @Test
    public void testAccept() throws Exception {

        new Expectations() {{
            serverClient.ready();
        }};

        handler.accept(23, null);

    }

    @Test
    public void testStartGame(@Mocked Game anyGame, @Mocked ASyncPlayer anyPlayer) throws Exception {

        new Expectations() {{
            ASyncPlayer p1 = new ASyncPlayer("start", Mark.P1);
            ASyncPlayer p2 = new ASyncPlayer("game", Mark.P2);
            Game game = new Game(BinaryBoard.class, p1, p2);
            controller.setGame(game);
        }};

        handler.startGame("start", "game");

    }

    @Test
    public void testDoneMove(@Mocked ASyncPlayer anyPlayer) throws Exception {

        new Expectations() {{
            anyPlayer.queueMove(3);
            anyPlayer.getName();
            returns("start", "game");
        }};
        handler.startGame("accept 23", "start_game frits henkie");
        handler.doneMove("start", 3);

    }

    @Test
    public void testDisconnect() throws Exception {

    }

    @Test
    public void testGameEnd() throws Exception {

    }

    @Test
    public void testError() throws Exception {

    }
}