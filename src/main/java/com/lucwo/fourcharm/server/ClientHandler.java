/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import com.lucwo.fourcharm.util.ExtensionFactory;
import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.fgroup.chat.ChatClient;
import nl.woutertimmermans.connect4.protocol.fgroup.chat.ChatServer;
import nl.woutertimmermans.connect4.protocol.fgroup.core.CoreClient;
import nl.woutertimmermans.connect4.protocol.fgroup.core.CoreServer;
import nl.woutertimmermans.connect4.protocol.fgroup.lobby.LobbyClient;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;
import nl.woutertimmermans.connect4.protocol.parameters.LobbyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * A ClientHandler is responsible for maintaining a connection with a client and passing received
 * commands to the {@link ClientGroup} the ClientHandler currently resides in.
 * For parsing the received commands from the client the C4 Protocol module is used.
 * The ClientHandler can also be used by otherparts of the server to send commands to the client.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */

public class ClientHandler implements CoreServer.Iface, ChatServer.Iface, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

// ------------------ Instance variables ----------------

    private ClientGroup group;
    //@ invariant name != null;
    private String name;
    //@ invariant socket != null;
    private Socket socket;
    //@ invariant coreClient != null;
    private CoreClient.Client coreClient;
    //@ invariant chatClient != null;
    private ChatClient.Client chatClient;
    //@ invariant lobbyClient != null;
    private LobbyClient.Client lobbyClient;
    private BufferedReader in;
    private BufferedWriter out;
    private boolean running;
    //@ invariant server != null;
    private FourCharmServer server;

// --------------------- Constructors -------------------

    /**
     * Constructs a new ClientHandler with a given socket.
     *
     * @param sock The socket which will be used to communicate with the client.
     */
    /*@
        requires sock !=null && s != null;
     */
    public ClientHandler(Socket sock, FourCharmServer s) {
        socket = sock;
        running = true;
        name = sock.toString();
        server = s;
    }

// ----------------------- Queries ----------------------

    /**
     * Returns the name of this ClientHandler.
     *
     * @return the name of this ClientHandler
     */
    //@ ensures \result != null;
    /*@ pure */ public String getName() {
        return name;
    }

    /**
     * Sets the name of this ClientHandler.
     *
     * @param newName the name of the ClientHandler
     */
    /*@
    requires newName != null;
    ensures getName().equals(newName);
     */
    public void setName(String newName) {
        name = newName;
    }

    /**
     * Returns the protocol @{link C4Client} to communicate with the coreClient.
     *
     * @return the coreClient
     */
    //@ ensures \result != null;
    /*@ pure */ public CoreClient.Client getCoreClient() {
        return coreClient;
    }


// ----------------------- Commands ---------------------

    /**
     * Returns the ClientGroup of this ClientHandler.
     *
     * @return the ClientGroup of this ClientHandler
     */

    /*@ pure */ public ClientGroup getClientGroup() {
        return group;
    }

    /**
     * Sets the ClientGroup of this ClientHandler.
     *
     * @param cG The clientgroup this ClientHandler will belong to.
     */
    /*@
        requires cG != null;
        ensures getClientGroup().equals(cG);
     */
    public void setClientGroup(ClientGroup cG) {
        group = cG;
    }

    /**
     * Join the lobby.
     *
     * @param pName   the name of the player
     * @param gNumber the groupnumber
     * @param exts    extensions
     * @throws C4Exception
     */
    /*@
        requires pName != null && gNumber >= 0;
        requires getClientGroup() != null;
     */
    @Override
    public void join(String pName, int gNumber, Set<Extension> exts) throws C4Exception {
        LOGGER.info("Received join for user {}", pName);
        group.join(this, pName, gNumber, exts);

    }

    /**
     * When the client is ready, he will send a ready command to get in a queue.
     * When two clients are ready a new game will be started.
     *
     * @throws C4Exception
     */
    /*@
        requires getClientGroup() != null
     */
    @Override
    public void ready() throws C4Exception {
        LOGGER.info("Received ready for user {}", getName());
        group.ready(this);
    }

    /**
     * Makes a move.
     *
     * @param col the column the move is about
     * @throws C4Exception
     */
    /*@
        requires getClientGroup() != null
     */
    @Override
    public void doMove(int col) throws C4Exception {
        LOGGER.info("Received doMove for user ", getName());
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
        LOGGER.info("{} {}", errorCode, message);
    }

    /**
     * Starts the input handling of this coreClient.
     *
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

        try {

            processCommands();

        } catch (IOException e) {
            LOGGER.trace("handleClient", e);
        } finally {
            try {
                socket.close();
                group.removeHandler(this);
                server.stateChange(this, LobbyState.OFFLINE);
                LOGGER.debug("Client {} disconnected", getName());
            } catch (IOException e) {
                LOGGER.trace("handleClient", e);
            }
        }


    }

    private void processCommands() throws IOException {
        CoreServer.Processor coreProcessor = new CoreServer.Processor<>(this);
        ChatServer.Processor chatProcessor = new ChatServer.Processor<>(this);

        String input = in == null ? null : in.readLine();
        while (running && input != null) {
            LOGGER.info("Processing input {}", input);
            try {
                boolean processed = coreProcessor.process(input);
                if (!processed) {
                    processed = chatProcessor.process(input);
                }
                if (!processed) {
                    LOGGER.warn("The command {} is not recognized", input);
                    C4Exception error = new InvalidCommandError(input + " is not recognized");
                    coreClient.error(error.getErrorCode(), error.getMessage());
                }
            } catch (C4Exception e) {

                LOGGER.warn("Sending exception: {}", e.getMessage());
                try {
                    coreClient.error(e.getErrorCode(), e.getMessage());
                } catch (C4Exception e1) {
                    LOGGER.trace("processCommands", e1);
                }

            }
            input = in.readLine();
        }
    }

    public void shutdown() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.trace("shutdown", e);
        }
        running = false;
    }


    public void init() {
        out = null;
        in = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        } catch (IOException e) {
            LOGGER.trace("init", e);
        }

        coreClient = new CoreClient.Client(out);
        chatClient = new ChatClient.Client(null);
        lobbyClient = new LobbyClient.Client(null);
    }


    public void registerExtensions(Set<Extension> extensions) {
        Extension chat = ExtensionFactory.chat();
        Extension lobby = ExtensionFactory.lobby();

        if (extensions != null) {
            if (extensions.contains(chat)) {
                chatClient = new ChatClient.Client(out);
            }
            if (extensions.contains(lobby)) {
                lobbyClient = new LobbyClient.Client(out);
            }
        }

    }

    /**
     * Used by clients to send messages to other clients in the local environment.
     *
     * @param message The message that will be sent.
     */
    /*@
        requires getClientGroup() != null
     */
    @Override
    public void chatLocal(String message) throws C4Exception {
        group.localChat(this, message);
    }

    /**
     * Used by clients to send messages that will probably to clients in any environment.
     *
     * @param message The message that will be sent.
     */
    /*@
        requires getClientGroup() != null
     */
    @Override
    public void chatGlobal(String message) throws C4Exception {
        group.globalChat(this, message);
    }

    /*@ pure */
    public ChatClient.Client getChatClient() {
        return chatClient;
    }

    /*@ pure */
    public LobbyClient.Client getLobbyClient() {
        return lobbyClient;
    }
}
