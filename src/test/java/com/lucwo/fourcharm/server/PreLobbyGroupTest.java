/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class PreLobbyGroupTest {

    ClientGroup preLobbyGroup;
    FourCharmServer theServer;
    ClientHandler clientje1;
    ClientHandler clientje2;
    ClientGroup lobbyGroup;
    Socket sock;

    @Before
    public void setUp() throws Exception {

        //TODO: the server = new Fourcharmserver zorgt ervoor dat nu de server wel start, maar verder niks doet.
        theServer = new FourCharmServer(8080);
        sock = new Socket();
        preLobbyGroup = new PreLobbyGroup(lobbyGroup, theServer);
        lobbyGroup = new LobbyGroup(theServer);
        clientje1 = new ClientHandler(sock);
        clientje2 = new ClientHandler(null);
        //clientje1.setClientGroup(null);
        clientje1.setName("Wouter");
    }

    @Test
    public void testJoin() throws Exception {
        preLobbyGroup.join(clientje1, clientje1.getName(), 11, null);
        assertEquals("Test to see if clientje1 joins the LobbyGroup", lobbyGroup, clientje1.getClientGroup());
    }

    @Test(expected = InvalidCommandError.class)
    public void testDoMove() throws Exception {
        preLobbyGroup.doMove(clientje2, 4);
    }

    @Test(expected = InvalidCommandError.class)
    public void testReady() throws Exception {
        preLobbyGroup.ready(clientje2);
    }

}