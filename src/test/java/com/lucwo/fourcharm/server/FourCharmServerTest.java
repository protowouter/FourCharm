/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;

import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FourCharmServerTest {

    private FourCharmServer server;
    @Mocked
    private PreLobbyGroup preLobby;
    @Mocked
    private LobbyGroup lobby;
    @Mocked
    private GameGroup game;
    @Mocked
    private ServerSocket socket;

    @Before
    public void setUp() throws Exception {

        server = new FourCharmServer(0);
        server.addGame(game);

    }

    @Test
    public void testMain() throws Exception {

    }

    @Test
    public void testHasClientWithNameGame() throws Exception {

        new Expectations() {{
            game.clientNameExists("aapje");
            result = true;
        }};

        assertTrue(server.hasClientWithName("aapje"));

    }

    @Test
    public void testHasClientWithNameLobby() throws Exception {

        new Expectations() {{
            lobby.clientNameExists("aapje");
            result = true;
        }};

        assertTrue(server.hasClientWithName("aapje"));
    }

    @Test
    public void testOpenSocket(@Mocked ServerSocket anySocket) throws Exception {

        new Expectations() {{
            new ServerSocket(0);
        }};

        server.openSocket();
    }

    @Test
    public void testStartServer(@Mocked ClientHandler anyClient) throws Exception {

        final Socket sock = new Socket();

        new Expectations() {{

            ServerSocket socket1 = new ServerSocket(0);
            socket1.accept();
            returns(sock);
            //preLobby.removeHandler(null);
            //anyClient.getClientGroup(); result = preLobby;

        }};


        server.openSocket();
        Thread serverThread = new Thread(server::startServer);
        serverThread.start();
        Thread.sleep(1000);
        server.stop();
        serverThread.join();
    }


    @Test
    public void testGetLobby() throws Exception {

        assertNotNull("getLobby should return the server lobby", server.getLobby());

    }
}