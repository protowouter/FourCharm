/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreClient;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreServer;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.util.Set;
import java.util.logging.Logger;

public class ClientHandler implements CoreServer.Iface {

// ------------------ Instance variables ----------------

    private CoreClient.Client client;
    private ClientGroup group;
    private String name;

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
        return name;
    }

    /**
     * Sets the name of this ClientHandler.
     * @param naam the name of the ClientHandler
     */
    public void setName(String naam) {
        name = naam;
    }

    /**
     * Gives the client.
     *
     * @return the client
     */
    public CoreClient.Client getClient() {
        return client;
    }


// ----------------------- Commands ---------------------

    /**
     * Returns the ClientGroup of this ClientHandler.
     * @return the ClientGroup of this ClientHandler
     */
    public ClientGroup getClientGroup() {
        return group;
    }

    /**
     * Sets the ClientGroup of this ClientHandler.
     * @param cG The clientgroup this ClientHandler will belong to.
     */
    public void setClientGroup(ClientGroup cG) {
        group = cG;
    }

    /**
     * Join the lobby.
     * @param pName the name of the player
     * @param gNumber the groupnumber
     * @param exts extensions
     * @throws C4Exception
     */
    @Override
    public void join(String pName, int gNumber, Set<Extension> exts) throws C4Exception {
        Logger.getGlobal().info("Received join for user " + pName);
        group.join(this, pName, gNumber, exts);

    }

    /**
     * When the client is ready, he will send a ready command to get in a queue.
     * When two clients are ready a new game will be started.
     * @throws C4Exception
     */
    @Override
    public void ready() throws C4Exception {
        Logger.getGlobal().info("Received ready for user " + getName());
        group.ready(this);
    }

    /**
     * Makes a move.
     * @param col the column the move is about
     * @throws C4Exception
     */
    @Override
    public void doMove(int col) throws C4Exception {
        Logger.getGlobal().info("Received doMove for user " + getName());
        group.doMove(this, col);
    }

    @Override
    public void error(int errorCode, String message) throws C4Exception {
        Logger.getGlobal().info(errorCode + " " + message);
    }

}
