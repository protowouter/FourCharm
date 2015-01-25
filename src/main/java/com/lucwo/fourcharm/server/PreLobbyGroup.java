/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidParameterError;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidUsernameError;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The PreLobbyGroup class extends the ClientGroup abstract class. Every Client
 * that wants to play a game has to enter the PreLobbyGroup first. After this, by
 * using the command join, the Client will be able to move to the LobbyGroup to
 * find a partner to play a game with. This class makes use of the Protocol classes
 * as well.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */

public class PreLobbyGroup extends ClientGroup {

    private static final int LUCWO_GROUP_NUMBER = 23;


    // ------------------ Instance variables ----------------

    private ClientGroup lobby;
    private Set<Extension> extensions;

    // --------------------- Constructors -------------------

    /**
     * Constructs a PreLobbyGroup.
     * @param lob The Clientgroup
     * @param theServer The server the PreLobbyGroup will be on.
     */
    public PreLobbyGroup(ClientGroup lob, FourCharmServer theServer) {
        super(theServer);
        lobby = lob;

        Extension chat = new Extension();
        try {
            chat.setValue("Chat");
        } catch (InvalidParameterError e) {
            Logger.getGlobal().throwing(getClass().toString(), "constructor", e);
        }
        extensions = new HashSet<>();
        extensions.add(chat);
    }

    // ----------------------- Queries ----------------------

    // ----------------------- Commands ---------------------

    /**
     * The client wants to join the LobbyGroup.
     *
     * @param client  The client which performed this command.
     * @param pName   Player name
     * @param gNumber Group number
     * @param exts    Set of extensions supported
     * @throws C4Exception
     */
    @Override
    public synchronized void join(ClientHandler client, String pName, int gNumber,
                     Set<Extension> exts) throws C4Exception {
        if (getServer().hasClientWithName(pName)) {
            throw new InvalidUsernameError("The username " + pName + " is already in use");
        } else {
            client.setName(pName);
            client.registerExtensions(exts);
            client.getCoreClient().accept(LUCWO_GROUP_NUMBER, extensions);
            lobby.addHandler(client);
        }

    }

    /**
     * Makes a move.
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
     * @param client the client that wants to play a game
     * @throws C4Exception the client is not allowed to play a game in the PreLobbyGroup
     */
    @Override
    public void ready(ClientHandler client) throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    @Override
    public void localChat(ClientHandler client, String message) throws C4Exception {
        throw new InvalidCommandError("You are not allowed to send a chat message at this time");
    }

    @Override
    public void globalChat(ClientHandler client, String message) throws C4Exception {
        throw new InvalidCommandError("You are not allowed to send a chat message at this time");
    }

    /**
     * Handles the removal of a client from the group.
     * @param client The {@link com.lucwo.fourcharm.server.ClientHandler}
     *               which will has been removed from the group.
     */
    @Override
    public void removeClientCallback(ClientHandler client) {
        // No special case when a client disconnects in this state.
    }


}
