/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;

import java.util.Set;

public class PreLobbyGroup extends ClientGroup {


    // ------------------ Instance variables ----------------

    ClientGroup lobby;

    // --------------------- Constructors -------------------

    public PreLobbyGroup(ClientGroup lob) {
        lobby = lob;
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
                     Set<String> exts) throws C4Exception {
        //TODO: Check dat de naam niet al bestaat in de server.
        client.setName(pName);
        client.getClient().accept(gNumber, exts);
        lobby.addHandler(client);
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




}
