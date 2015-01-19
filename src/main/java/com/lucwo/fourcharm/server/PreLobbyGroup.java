/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidUsernameError;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.util.Set;

public class PreLobbyGroup extends ClientGroup {


    // ------------------ Instance variables ----------------

    ClientGroup lobby;
    FourCharmServer server;

    // --------------------- Constructors -------------------

    public PreLobbyGroup(ClientGroup lob, FourCharmServer theServer) {
        lobby = lob;
        server = theServer;
    }

    // ----------------------- Queries ----------------------

    // ----------------------- Commands ---------------------

    /**
     * The client wants to join the LobbyGroup
     *
     * @param client  The client which performed this command.
     * @param pName   Player name
     * @param gNumber Group number
     * @param exts    Set of extensions supported
     * @throws C4Exception
     */
    @Override
    public void join(ClientHandler client, String pName, int gNumber,
                     Set<Extension> exts) throws C4Exception {
        if (server.hasClientWithName(pName)) {
            throw new InvalidUsernameError("The username " + pName + " is already in use");
        } else {
            client.setName(pName);
            client.getClient().accept(gNumber, exts);
            lobby.addHandler(client);
        }

    }

    /**
     * Makes a move.
     *
     * @param client the client that makes a move
     * @param col    the column the move is about
     * @throws C4Exception the client is not playing a game and is not allowed to make a move
     */
    @Override
    public void doMove(ClientHandler client, int col) throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    /**
     * Sets the clients status to 'ready' and starts a game if there is another client
     * that is ready as well.
     *
     * @param client the client that wants to play a game
     * @throws C4Exception the client is not allowed to play a game in the PreLobbyGroup
     */
    @Override
    public void ready(ClientHandler client) throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    /**
     * Handles the removal of a client from the group.
     *
     * @param client The {@link com.lucwo.fourcharm.server.ClientHandler} which will has been removed from the group.
     */
    @Override
    public void removeClientCallback(ClientHandler client) {
        // No special case when an client disconnects in this state.
    }


}
