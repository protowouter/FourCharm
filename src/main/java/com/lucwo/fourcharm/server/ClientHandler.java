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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

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

    private SocketChannel socket;
    //@ invariant coreClient != null;

    private CoreClient.AsyncClient coreClient;
    //@ invariant chatClient != null;
    private ChatClient.AsyncClient chatClient;
    //@ invariant lobbyClient != null;
    private LobbyClient.AsyncClient lobbyClient;
    private boolean running;
    //@ invariant server != null;
    private FourCharmServer server;
    private SelectionKey key;

// --------------------- Constructors -------------------

    /**
     * Constructs a new ClientHandler with a given socket.
     *
     * @param sock The socket which will be used to communicate with the client.
     */
    /*@
        requires sock !=null && s != null;
     */
    public ClientHandler(SocketChannel sock, FourCharmServer s) {
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
    /*@ pure */
    public CoreClient.AsyncClient getCoreClient() {
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
        handleClient();
    }

    /**
     * Processes the commands received from the client via the {@link java.net.Socket}.
     */
    public void handleClient() {

        final String mName = "handleClient";

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        group.removeHandler(this);
                server.stateChange(this, LobbyState.OFFLINE);
                LOGGER.debug("Client {} disconnected", getName());


    }

    public void handleRead(SelectionKey key) throws IOException {
        ByteBuffer buf = ByteBuffer.allocateDirect(5120);
        buf.clear();
        StringBuilder builder = (StringBuilder) key.attachment();
        builder = builder == null ? new StringBuilder() : builder;
        int readCount = socket.read(buf);

        if (readCount > 0) {
            buf.flip();
            CharBuffer decChar = Charset.forName("UTF-8").decode(buf);
            builder.append(decChar);
            String[] commands = removeCommands(builder, key);
            ForkJoinPool.commonPool().submit(() -> executeCommands(commands));
        } else if (readCount == -1) {
            shutdown();
        }


    }

    private String[] removeCommands(StringBuilder builder, SelectionKey key) {
        List<String> result = new LinkedList<>();
        Scanner commandScanner = new Scanner(builder.toString());
        builder = new StringBuilder();

        while (commandScanner.hasNextLine()) {
            result.add(commandScanner.nextLine());
        }
        if (commandScanner.hasNext(".+")) {
            builder.append(commandScanner.next(".+"));
        }
        key.attach(builder);
        return result.toArray(new String[result.size()]);
    }

    private void executeCommands(String[] commands) {
        CoreServer.Processor coreProcessor = new CoreServer.Processor<>(this);
        ChatServer.Processor chatProcessor = new ChatServer.Processor<>(this);

        for (String input : commands) {
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
        }

    }

    public void handleWrite() {
        coreClient.write();
    }

    public void shutdown() {
        try {
            socket.close();
            if (group != null) {
                group.removeHandler(this);
            }
        } catch (IOException e) {
            LOGGER.trace("shutdown", e);
        }
        running = false;
    }


    public void init(SelectionKey key) {
        coreClient = new CoreClient.AsyncClient(key);
        chatClient = new ChatClient.AsyncClient(null);
        lobbyClient = new LobbyClient.AsyncClient(null);
    }


    public void registerExtensions(Set<Extension> extensions) {
        Extension chat = ExtensionFactory.chat();
        Extension lobby = ExtensionFactory.lobby();

        if (extensions != null) {
            if (extensions.contains(chat)) {
                chatClient = new ChatClient.AsyncClient(key);
            }
            if (extensions.contains(lobby)) {
                lobbyClient = new LobbyClient.AsyncClient(key);
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
    public ChatClient.AsyncClient getChatClient() {
        return chatClient;
    }

    /*@ pure */
    public LobbyClient.AsyncClient getLobbyClient() {
        return lobbyClient;
    }
}
