/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.util.Set;

/**
 * The LobbyGroup class extends the ClientGroup abstract class. It makes a Lobby and
 * adds Clients to the LobbyGroup. From these Clients, whenever they are ready, 2 are
 * chosen and they will be added to the GameGroup to start a new game. Before a Client
 * can enter the LobbyGroup, he will be in the PreLobbyGroup as long as he did not give
 * the command 'join'.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class LobbyGroup extends ClientGroup {

    // ------------------ Instance variables ----------------

    private ClientHandler readyClient;
    private FourCharmServer server;

    // --------------------- Constructors -------------------

    /**
     * Constructs a new LobbyGroup.
     * @param theServer The server the LobbyGroup is constructed on.
     */
    public LobbyGroup(FourCharmServer theServer) {
        super(theServer);
        readyClient = null;
        server = theServer;
    }

    // ----------------------- Queries ----------------------

    // ----------------------- Commands ---------------------

    /**
     * ServerHandler wants to go from the PreLobbyGroup to the LobbyGroup.
     * @param client  The client which performed this command.
     * @param pName   Player name
     * @param gNumber Group number
     * @param exts    Set of extensions supported
     * @throws C4Exception is already in the LobbyGroup
     */
    @Override
    public void join(ClientHandler client, String pName, int gNumber, Set<Extension> exts)
            throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    /**
     * Makes a move if a client is in a game. This is not allowed in the LobbyGroup.
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
     * @param client the client that wants to play a game
     * @throws C4Exception
     */
    @Override
    public synchronized void ready(ClientHandler client) throws C4Exception {

        if (readyClient == null) {
            readyClient = client;
        } else if (readyClient == client) {
            throw new InvalidCommandError("You are already waiting and not " +
                    "allowed to play against yourself. Please be patient.");
        } else {
            GameGroup game = new GameGroup(server, readyClient, client);
            server.addGame(game);
            game.startGame();
            readyClient = null;
        }

    }

    /**
     * Handles the removal of a client from the group.
     * @param client The {@link com.lucwo.fourcharm.server.ClientHandler}
     *               which will has been removed from the group.
     */
    @Override
    public synchronized void removeClientCallback(ClientHandler client) {
        if (readyClient == client) {
            readyClient = null;
        }
    }


}
