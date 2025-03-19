/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import mockit.Expectations;
import mockit.Mocked;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;

public class LobbyGroupTest {

    ClientGroup lobbyGroup;
    @Mocked
    FourCharmServer theServer;
    @Mocked
    ClientHandler clientje1;
    @Mocked
    ClientHandler clientje2;
    Socket sock;

    @Before
    public void setUp() throws Exception {
        sock = new Socket();
        lobbyGroup = new LobbyGroup(theServer);
        clientje1 = new ClientHandler(sock, theServer);
        clientje2 = new ClientHandler(sock, theServer);
        lobbyGroup.ready(clientje2);
    }

    @Test(expected = InvalidCommandError.class)
    public void testJoin() throws Exception {
        lobbyGroup.join(clientje1, clientje1.getName(), 11, null);
    }

    @Test(expected = InvalidCommandError.class)
    public void testDoMove() throws Exception {
        lobbyGroup.doMove(clientje1, 4);
    }

    @Test
    public void testReady(@Mocked GameGroup anyGameGroup) throws Exception {
        new Expectations() {
            {
                new GameGroup(theServer, clientje2, clientje1);// expect constructor
            }
        };
        lobbyGroup.ready(clientje1);
    }

    @Test(expected = InvalidCommandError.class)
    public void testSameNameReady() throws Exception {
        lobbyGroup.ready(clientje2);
    }
}