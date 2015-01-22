/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.fgroup.CoreClient;
import org.junit.Before;
import org.junit.Test;

import java.net.Socket;
import java.security.cert.Extension;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ClientHandlerTest {

    ClientHandler clientH;
    ClientHandler clientH2;
    ClientHandler clientH3;
    ClientGroup clientLobbyGroup;
    ClientGroup clientPreLobbyGroup;
    ClientGroup clientGameGroup;
    FourCharmServer theServer;
    Set<Extension> exts;



    @Before
    public void setUp() throws Exception {
        clientH = new ClientHandler(new Socket());
        clientH.init();
        clientH2 = new ClientHandler(null);
        clientH3 = new ClientHandler(null);
        clientH.setName("Wouter");
        clientPreLobbyGroup = new PreLobbyGroup(clientLobbyGroup, theServer);
        clientLobbyGroup = new LobbyGroup(theServer);
        clientH2.setClientGroup(clientLobbyGroup);
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

        CoreClient.Client bogus = null;
        assertNotEquals("getClient() should return non-null result", bogus, clientH.getClient());

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
    }
}