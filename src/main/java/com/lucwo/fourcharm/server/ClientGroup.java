/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;

import java.util.Iterator;
import java.util.Set;

/**
 * Interface for modeling a group of clients. This could be a LobbyGroup, a GameGroup or something
 * completely different.
 */

public abstract class ClientGroup {

    public ClientGroup() {
        // TODO: Implement
    }


    /**
     * Adds a ClientHandler to this collection of Clients and informs the
     * ClientHandler of this change.
     *
     * @param client the ClientHandler that will be added.
     */
    public void addHandler(ClientHandler client) {
        // TODO: Implement
    }


    /**
     * Removes a ClientHandler from this collection of Clients and informs the
     * ClientHandler of this change.
     *
     * @param client the Clienthandler that will be removed.
     */
    public void removeHandler(ClientHandler client) {
        // TODO: Implement
    }


    /**
     * Returns an {@link java.util.Iterator} over the Collection of ClientHandler
     *
     * @return
     */
    public Iterator<ClientHandler> getClients() {
        // TODO: Implement
        return null;
    }

    /**
     * Handles the join command from a client. The implementation is left to Classes
     * which extend ClientGroup.
     *
     * @param client  The client which performed this command.
     * @param pName   Player name
     * @param gNumber Group number
     * @param exts    Set of extensions supported
     */
    public abstract void join(ClientHandler client, String pName, int gNumber, Set<String> exts) throws C4Exception;

    public abstract void doMove(ClientHandler client, int col);

    public abstract void ready(ClientHandler client);


}
