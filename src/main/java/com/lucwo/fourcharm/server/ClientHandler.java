/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreClient;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreServer;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.io.*;
import java.net.Socket;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A ClientHandler is responsible for maintaining a connection with a client and passing received
 * commands to the {@link ClientGroup} the ClientHandler currently resides in.
 * For parsing the received commands from the client the C4 Protocol module is used.
 * The ClientHandler can also be used by other parts of the server to send commands to the client.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */

public class ClientHandler implements CoreServer.Iface, Runnable {

// ------------------ Instance variables ----------------

    private ClientGroup group;
    private String name;
    private Socket socket;
    private CoreClient.Client client;
    private BufferedReader in;

// --------------------- Constructors -------------------

    /**
     * Constructs a new ClientHandler with a given socket.
     *
     * @param sock The socket which will be used to communicate with the client.
     */
    public ClientHandler(Socket sock) {
        socket = sock;
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
     * @param newName the name of the ClientHandler
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * Returns the protocol @{link C4Client} to communicate with the client.
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

    /**
     * Gives an error.
     *
     * @param errorCode the code of the error
     * @param message   the message about the error
     * @throws C4Exception
     */
    @Override
    public void error(int errorCode, String message) throws C4Exception {
        Logger.getGlobal().info(errorCode + " " + message);
    }

    /**
     * Starts the input handling of this client.
     * @see Thread#run()
     */
    @Override
    public void run() {
        init();
        handleClient();
    }

    /**
     * Processes the commands received from the client via the {@link java.net.Socket}.
     */
    public void handleClient() {

        final String mName = "handleClient";

        CoreServer.Processor processor = new CoreServer.Processor<>(this);

        try {
            String input = in == null ? null : in.readLine();
            while (input != null) {
                Logger.getGlobal().info("Processing input " + input);
                try {
                    boolean processed = processor.process(input);
                    if (!processed) {
                        Logger.getGlobal().warning("This command is not recognized");
                        C4Exception error = new InvalidCommandError(input + " is not recognized");
                        client.error(error.getErrorCode(), error.getMessage());
                    }
                } catch (C4Exception e) {

                    Logger.getGlobal().info("Sending exception " + e.getMessage());
                    try {
                        client.error(e.getErrorCode(), e.getMessage());
                    } catch (C4Exception e1) {
                        Logger.getGlobal().throwing(getClass().toString(), mName, e);
                    }

                }
                input = in.readLine();
            }

        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmServer", mName, e);
        } finally {
            try {
                socket.close();
                group.removeHandler(this);
            } catch (IOException e) {
                Logger.getGlobal().throwing("FourCharmServer", mName, e);
            }
        }


    }

    public void init() {
        BufferedWriter out = null;
        in = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmServer", "init", e);
        }

        client = new CoreClient.Client(out);
    }
}
