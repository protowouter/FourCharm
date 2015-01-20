/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;

public class LobbyGroupTest {

    ClientGroup lobbyGroup;
    FourCharmServer theServer;
    ClientHandler clientje1;
    ClientHandler clientje2;
    Socket sock;

    @Before
    public void setUp() throws Exception {
        sock = new Socket();
        lobbyGroup = new LobbyGroup(theServer);
        clientje1 = new ClientHandler(sock);
        clientje2 = new ClientHandler(sock);
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

    @Test(expected = InvalidCommandError.class)
    public void testSameNameReady() throws Exception {
        lobbyGroup.ready(clientje2);
    }
}