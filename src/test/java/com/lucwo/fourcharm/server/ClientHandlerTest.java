/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import org.junit.Before;
import org.junit.Test;

import java.security.cert.Extension;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ClientHandlerTest {

    ClientHandler clientH;
    ClientHandler clientH2;
    ClientHandler clientH3;
    ClientGroup clientLobbyGroup;
    ClientGroup clientPreLobbyGroup;
    // ClientGroup clientGameGroup;
    FourCharmServer theServer;
    Set<Extension> exts;



    @Before
    public void setUp() throws Exception {
        clientH = new ClientHandler(null);
        clientH2 = new ClientHandler(null);
        clientH3 = new ClientHandler(null);
        clientH.setName("Wouter");
        clientPreLobbyGroup = new PreLobbyGroup(clientLobbyGroup, theServer);
        clientLobbyGroup = new LobbyGroup(theServer);
        clientH2.setClientGroup(clientLobbyGroup);
        // clientGameGroup = new GameGroup(theServer, clientH, clientH3);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("Test to see if GetName() works", "Wouter", clientH.getName());
    }

    @Test
    public void testSetName() throws Exception {
        clientH.setName("Luce");
        assertEquals("Test to see if SetName() works", "Luce", clientH.getName());
    }

    @Test
    public void testGetClient() throws Exception {

        //TODO: getClient vergelijken met een client
        //  assertEquals("Test to see if getClient() works", , clientH3.getClient());

    }

    @Test
    public void testGetClientGroup() throws Exception {
        assertEquals("Test to see if getting a GlientGroup works", clientLobbyGroup, clientH2.getClientGroup());
    }

    @Test
    public void testSetClientGroup() throws Exception {
        clientH2.setClientGroup(clientPreLobbyGroup);
        assertEquals("Test to see if setting a ClientGroup to pre lobby group works", clientPreLobbyGroup, clientH2.getClientGroup());
        clientH2.setClientGroup(clientLobbyGroup);
        assertEquals("Test to see if setting a ClientGroup to lobby group works", clientLobbyGroup, clientH2.getClientGroup());

        //TODO: setclientgroup to gamegroup
       /* clientH2.setClientGroup(clientGameGroup);
        clientH3.setClientGroup(clientGameGroup);
        assertEquals("Test to see ifs setting a clientGroup to game group works", clientGameGroup, clientH2.getClientGroup());*/
    }

  /*  @Test
    public void testJoin() throws Exception {
       // clientPreLobbyGroup.join(clientH3, clientH3.getName(), 23, exts);


    }

    @Test
    public void testReady() throws Exception {

        clientLobbyGroup.ready(clientH3);
    }

    @Test
    public void testDoMove() throws Exception {

        //Game group?
    }*/

    @Test(expected = InvalidCommandError.class)
    public void testError() throws Exception {
        clientPreLobbyGroup.doMove(clientH3, 5);

    }

    @Test
    public void run() throws Exception {

        //TODO: run() test schrijven
        handleClient();
    }

    @Test
    public void handleClient() throws Exception {

        //TODO: handleClient() test schrijven
    }
}