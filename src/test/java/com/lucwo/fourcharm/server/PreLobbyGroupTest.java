/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import mockit.Expectations;
import mockit.Mocked;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidUsernameError;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreClient;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class PreLobbyGroupTest {

    ClientGroup preLobbyGroup;
    @Mocked FourCharmServer theServer;
    ClientHandler clientje1;
    @Mocked CoreClient.Client c1Client;
    ClientHandler clientje2;
    ClientGroup lobbyGroup;
    Socket sock;

    @Before
    public void setUp() throws Exception {

        //TODO: the server = new Fourcharmserver zorgt ervoor dat nu de server wel start, maar verder niks doet.
        theServer = new FourCharmServer(8080);
        sock = new Socket();
        lobbyGroup = new LobbyGroup(theServer);
        preLobbyGroup = new PreLobbyGroup(lobbyGroup, theServer);
        clientje1 = new ClientHandler(sock);
        clientje2 = new ClientHandler(sock);
        clientje1.init();
        clientje2.init();
    }

    @Test(expected = InvalidCommandError.class)
    public void testDoMove() throws Exception {
        preLobbyGroup.doMove(clientje2, 4);
    }

    @Test(expected = InvalidCommandError.class)
    public void testReady() throws Exception {
        preLobbyGroup.ready(clientje2);
    }

    @Test
    public void testJoin() throws Exception {
        new Expectations() {
            {
                theServer.hasClientWithName("Wouter");
                result = false;
            }
        };
        preLobbyGroup.join(clientje1, "Wouter", 23, null);
        assertEquals("Join should set the ClientHandler name", "Wouter", clientje1.getName());
    }

    @Test(expected = InvalidUsernameError.class)
    public void testJoinExistingName() throws Exception {
        new Expectations() {
            {
                theServer.hasClientWithName("Luce");
                result = true;
            }
        };
        preLobbyGroup.join(clientje2, "Luce", 23, null);
    }

}