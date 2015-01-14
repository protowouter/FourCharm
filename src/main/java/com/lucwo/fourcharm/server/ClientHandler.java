/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreClient;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreServer;

import java.util.Set;
import java.util.logging.Logger;

public class ClientHandler implements CoreServer.Iface {

// ------------------ Instance variables ----------------

    private CoreClient.Client client;

// --------------------- Constructors -------------------

    public ClientHandler(CoreClient.Client c) {

        client = c;

    }

// ----------------------- Queries ----------------------

    /**
     * Returns the name of this ClientHandler.
     * @return the name of this ClientHandler
     */
    public String getName() {
        // TODO: Implement
        return null;
    }

    /**
     * Sets the name of this ClientHandler.
     * @param name the name of the ClientHandler
     */
    public void setName(String name) {
        // TODO: Implement
    }

    public CoreClient.Client getClient() {
        return client;
    }


// ----------------------- Commands ---------------------

    /**
     * Returns the ClientGroup of this ClientHandler.
     * @return the ClientGroup of this ClientHandler
     */
    public ClientGroup getClientGroup() {
        // TODO: Implement
        return null;
    }

    /**
     * Sets the ClientGroup of this ClientHandler.
     * @param cG The clientgroup this ClientHandler will belong to.
     */
    public void setClientGroup(ClientGroup cG) {
        // TODO: Implements
    }

    @Override
    public void join(String pName, int gNumber, Set<String> exts) throws C4Exception {
        Logger.getGlobal().info("Received join for user " + pName);
    }

    @Override
    public void ready() throws C4Exception {

    }

    @Override
    public void doMove(int col) throws C4Exception {

    }

}
