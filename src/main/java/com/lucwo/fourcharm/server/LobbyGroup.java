/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;

import java.util.Set;

public class LobbyGroup extends ClientGroup {

    // ------------------ Instance variables ----------------

    private ClientHandler readyClient;

    // --------------------- Constructors -------------------

    public LobbyGroup() {
        readyClient = null;
    }

    // ----------------------- Queries ----------------------

    // ----------------------- Commands ---------------------

    /**
     * Client wants to go from the PreLobbyGroup to the LobbyGroup.
     *
     * @param client  The client which performed this command.
     * @param pName   Player name
     * @param gNumber Group number
     * @param exts    Set of extensions supported
     * @throws C4Exception is already in the LobbyGroup
     */
    @Override
    public void join(ClientHandler client, String pName, int gNumber, Set<String> exts) throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    /**
     * Makes a move if a client is in a game. This is not allowed in the LobbyGroup.
     *
     * @param client the client that wants to make a move.
     * @param col    the column number the clients wants to use to make a move
     * @throws C4Exception throws InvalidCommandError because this command is not allowed
     */
    @Override
    public void doMove(ClientHandler client, int col) throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    /**
     * Sets the status of the player to ready. If there is another player ready as well,
     * a new game will be started.
     *
     * @param client the client that wants to play a game
     * @throws C4Exception
     */
    @Override
    public synchronized void ready(ClientHandler client) throws C4Exception {

        if (readyClient == null) {
            readyClient = client;
        } else if (readyClient == client) {
            throw new InvalidCommandError("You are already waiting and not allowed to play against yourself. " +
                    "Please be patient.");
        } else {
            new GameGroup(readyClient, client);
            readyClient = null;
        }

    }


}
