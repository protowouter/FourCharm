/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientHandlerTest {

    @Mocked
    Socket socket;
    private ClientHandler clientHandler;
    @Injectable
    private ClientGroup group;

    @Before
    public void setup() throws Exception {
        clientHandler = new ClientHandler(socket);
        clientHandler.setClientGroup(group);
        clientHandler.init();
    }

    @Test
    public void testSetName() throws Exception {
        clientHandler.setName("Frits");
        assertEquals("ClientHandler should return the client name", "Frits", clientHandler.getName());
    }

    @Test
    public void testGetClient() throws Exception {
        assertNotNull(clientHandler.getCoreClient());
    }

    @Test
    public void testGetClientGroup() throws Exception {
        assertEquals("getClientGroup should return the current group", group, clientHandler.getClientGroup());
    }

    @Test
    public void testSetClientGroup(@Mocked ClientGroup newGroup) throws Exception {

        clientHandler.setClientGroup(newGroup);
        assertEquals("setClientGroup should update the current group", newGroup, clientHandler.getClientGroup());

    }

    @Test
    public void testJoin() throws Exception {

        new Expectations() {{
            group.join(clientHandler, "Frits", 23, null);
        }};

        clientHandler.join("Frits", 23, null);


    }

    @Test
    public void testReady() throws Exception {

        new Expectations() {{
            group.ready(clientHandler);
        }};

        clientHandler.ready();

    }

    @Test
    public void testDoMove() throws Exception {

        new Expectations() {{
            group.doMove(clientHandler, 2);
        }};

        clientHandler.doMove(2);

    }

    @Test
    public void testHandleClient(@Mocked BufferedReader anyReader) throws Exception {

        new Expectations() {{
            anyReader.readLine();
            returns("hallo", "hoi", null);
            group.removeHandler(clientHandler);
        }};


        clientHandler.handleClient();

    }
}