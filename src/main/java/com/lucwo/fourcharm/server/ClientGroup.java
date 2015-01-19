/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Interface for modeling a group of clients. This could be a LobbyGroup, a GameGroup or something
 * completely different.
 */

public abstract class ClientGroup {

    private Map<String, ClientHandler> clientCollection;

    public ClientGroup() {
        clientCollection = new HashMap<>();
    }

    public boolean clientNameExists(String name) {
        return clientCollection.keySet().contains(name);
    }


    /**
     * Adds a ClientHandler to this collection of Clients and informs the
     * ClientHandler of this change.
     * @param client the ClientHandler that will be added.
     */
    public void addHandler(ClientHandler client) {
        ClientGroup clientGroup = client.getClientGroup();
        if (clientGroup != null) {
            clientGroup.removeHandler(client);
        }
        clientCollection.put(client.getName(), client);
        client.setClientGroup(this);
    }


    /**
     * Removes a ClientHandler from this collection of Clients and informs the
     * ClientHandler of this change.
     * @param client the Clienthandler that will be removed.
     */
    public void removeHandler(ClientHandler client) {
        clientCollection.remove(client.getName());
        client.setClientGroup(null);
        removeClientCallback(client);
    }


    /**
     * Returns an {@link java.util.Iterator} over the Collection of ClientHandler.
     * @return an iterator over the clients in this group.
     */
    public Collection<ClientHandler> getClients() {
        return clientCollection.values();
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
    public abstract void join(ClientHandler client, String pName, int gNumber,
                              Set<Extension> exts) throws C4Exception;

    public abstract void doMove(ClientHandler client, int col) throws C4Exception;

    public abstract void ready(ClientHandler client) throws C4Exception;

    /**
     * Handles the removal of a client from the group.
     *
     * @param client The {@link com.lucwo.fourcharm.server.ClientHandler} which will has been removed from the group.
     */
    public abstract void removeClientCallback(ClientHandler client);




}
