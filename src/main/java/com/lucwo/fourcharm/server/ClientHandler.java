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

public class ClientHandler implements CoreServer.Iface, Runnable {

// ------------------ Instance variables ----------------

    private ClientGroup group;
    private String name;
    private Socket socket;
    private CoreClient.Client client;

// --------------------- Constructors -------------------

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

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        handleClient();
    }

    public void handleClient() {

        final String m_name = "handleClient";

        BufferedWriter out = null;
        BufferedReader in = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmServer", m_name, e);
        }

        client = new CoreClient.Client(out);
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

                    Logger.getGlobal().info("Throwing exception " + e.getMessage());
                    try {
                        client.error(e.getErrorCode(), e.getMessage());
                    } catch (C4Exception e1) {
                        e1.printStackTrace();
                    }

                }
                input = in.readLine();
            }

        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmServer", m_name, e);
        } finally {
            try {
                socket.close();
                group.removeHandler(this);
            } catch (IOException e) {
                Logger.getGlobal().throwing("FourCharmServer", m_name, e);
            }
        }


    }
}
