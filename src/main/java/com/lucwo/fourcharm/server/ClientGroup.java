/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Abstract class for modeling a group of clients. This could be a LobbyGroup, a GameGroup or a
 * preLobbyGroup. This class is used by the classes mentioned before.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */

public abstract class  ClientGroup {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientGroup.class);

    //@ invariant clientMap != null;
    private ConcurrentHashMap<String, ClientHandler> clientMap;
    //@ invariant server != null;
    private FourCharmServer server;


    //@ requires theServer != null;
    public ClientGroup(FourCharmServer theServer) {
        server = theServer;
        clientMap = new ConcurrentHashMap<>();
    }

    /**
     * Checks if the name of the client already exists.
     *
     * @param name the name that will be checked
     * @return true if the name already exists, false if not
     */
    /*@
        requires name != null;
     */
    public boolean clientNameExists(String name) {
        Boolean isInMap = clientMap.searchKeys(4, clientName -> clientName.equals(name));
        return isInMap != null && isInMap;
    }


    /**
     * Adds a ClientHandler to this collection of Clients and informs the
     * ClientHandler of this change.
     *
     * @param client the ClientHandler that will be added.
     */
    /*@
        requires client != null;
        ensures clientNameExists(client.getName());
     */
    public void addHandler(ClientHandler client) {
        ClientGroup clientGroup = client.getClientGroup();
        if (clientGroup != null) {
            clientGroup.removeHandler(client);
        }
        clientMap.put(client.getName(), client);
        client.setClientGroup(this);
        addClientCallback(client);
    }


    /**
     * Removes a ClientHandler from this collection of Clients and informs the
     * ClientHandler of this change.
     *
     * @param client the Clienthandler that will be removed.
     */
    /*@
        requires client != null;
        ensures !clientNameExists(client.getName());
     */
    public void removeHandler(ClientHandler client) {
        clientMap.remove(client.getName());
        client.setClientGroup(null);
        removeClientCallback(client);
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

    /**
     * Makes a move.
     *
     * @param client the client that will make the move
     * @param col    the column the move is about
     * @throws C4Exception
     */
    public abstract void doMove(ClientHandler client, int col) throws C4Exception;


    /**
     * If the client is ready to start a game, this method will be called.
     *
     * @param client the client that is ready to start a game
     * @throws C4Exception
     */
    public abstract void ready(ClientHandler client) throws C4Exception;


    public void localChat(ClientHandler client, String message) throws C4Exception {
        broadcastChat(client, message);
    }

    public void broadcastChat(ClientHandler client, String message) throws C4Exception {
        forEveryClient(clientHandler -> {
            try {
                clientHandler.getChatClient().message(client.getName(), message);
            } catch (C4Exception e) {
                LOGGER.trace("broadcastChat", e);
            }
        });
    }

    public void globalChat(ClientHandler client, String message) throws C4Exception {
        server.globalChat(client, message);
    }

    //@ ensures \result != null
    public FourCharmServer getServer() {
        return server;
    }

    /**
     * Handles the removal of a client from the group.
     *
     * @param client The {@link com.lucwo.fourcharm.server.ClientHandler}
     *               which will has been removed from the group.
     */
    public abstract void removeClientCallback(ClientHandler client);

    public abstract void addClientCallback(ClientHandler client);

    public void forEveryClient(Consumer<ClientHandler> function) {
        clientMap.forEachValue(4, function);
    }


}
