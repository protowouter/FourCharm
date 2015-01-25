/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.client;

import com.lucwo.fourcharm.controller.FourCharmController;
import com.lucwo.fourcharm.exception.ServerConnectionException;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.player.ASyncPlayer;
import com.lucwo.fourcharm.model.player.LocalAIPlayer;
import com.lucwo.fourcharm.model.player.Mark;
import com.lucwo.fourcharm.model.player.Player;
import com.lucwo.fourcharm.util.ExtensionFactory;
import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidMoveError;
import nl.woutertimmermans.connect4.protocol.fgroup.chat.ChatClient;
import nl.woutertimmermans.connect4.protocol.fgroup.chat.ChatServer;
import nl.woutertimmermans.connect4.protocol.fgroup.core.CoreClient;
import nl.woutertimmermans.connect4.protocol.fgroup.core.CoreServer;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Handles the connection to the server from the perspective of the client.
 * This class makes use of the Protocol classes, the Board Model classes and the
 * strategy classes to achieve its responsibilities.
 *
 * @author Luce Sandfort and Wouter Timmermans.
 */

public class ServerHandler implements CoreClient.Iface, ChatClient.Iface, Runnable {

    private static final int GROUP_NUMBER = 23;

// ------------------ Instance variables ----------------

    private String name;
    private BufferedReader in;
    private BufferedWriter out;
    private CoreServer.Client coreServerClient;
    private ChatServer.Client chatServerClient;
    private CoreClient.Processor<ServerHandler> coreProcessor;
    private ChatClient.Processor<ServerHandler> chatProcessor;
    private FourCharmController controller;
    private Map<String, ASyncPlayer> playerMap;
    private GameStrategy strategy;
    private Player ai;
    private Game game;
    private boolean running;
    private Set<Extension> extensions;

// --------------------- Constructors -------------------

    /**
     * Constructs a serverHandler.
     * @param namepie The name of the serverHandler.
     * @param hostString The host of the serverHandler (the IP address).
     * @param portString The port the serverHandler will use.
     * @param contr The FourCharmController of the serverHandler.
     * @throws ServerConnectionException If the ServerHandler cannot
     * make a connection to the server.
     */
    public ServerHandler(String namepie, String hostString, String portString,
                         FourCharmController contr) throws ServerConnectionException {
        running = true;
        controller = contr;
        name = namepie;
        playerMap = new HashMap<>();

        try {
            InetAddress host = InetAddress.getByName(hostString);
            int port = Integer.parseInt(portString);
            Socket sock = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream(),
                    Charset.forName("UTF-8")));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream(), Charset.forName("UTF-8")));
            coreProcessor = new CoreClient.Processor<>(this);
            chatProcessor = new ChatClient.Processor<>(this);
            coreServerClient = new CoreServer.Client(out);
            chatServerClient = new ChatServer.Client(null);
        } catch (IOException e) {
            Logger.getGlobal().throwing(getClass().toString(), "constructor", e);
            throw new ServerConnectionException(e.getMessage());
        }

        extensions = ExtensionFactory.createExtensionSet();


    }

    /**
     * Joins the server and starts handling the commands.
     */
    @Override
    public void run() {
        joinServer();
        handleServerCommands();
    }


// ----------------------- Queries ----------------------

    /**
     * Gives the strategy that is used by the computerplayer.
     * @return The used strategy (this is either MDTf strategy or a random strategy).
     */
    public GameStrategy getStrategy() {
        return strategy;
    }

// ----------------------- Commands ---------------------

    /**
     * Changes the strategy that is used by the computerplayer.
     * @param strat The strategy that will be used from now on (this is either a MDTf strategy or
     *              a random strategy).
     */
    public void setStrategy(GameStrategy strat) {
        strategy = strat;
    }

    /**
     * Method to join a server. Protocol errors are caught and logged.
     */
    public void joinServer() {
        try {
            coreServerClient.join(name, GROUP_NUMBER, extensions);
        } catch (C4Exception e) {
            Logger.getGlobal().throwing(getClass().toString(), "joinServer", e);
        }
    }

    /**
     * Method to handle server commands. Input Ouput errors and Protocol errors
     * are caught and logged.
     */
    public void handleServerCommands() {
        try {
            String input = in.readLine();
            while (running && input != null) {
                Logger.getGlobal().fine("Processing input " + input);
                boolean processed = coreProcessor.process(input);
                if (!processed) {
                    chatProcessor.process(input);
                }
                input = in.readLine();

            }
        } catch (IOException e) {
            Logger.getGlobal().throwing(getClass().toString(), "handleServerCommands", e);
        } catch (C4Exception e) {
            Logger.getGlobal().throwing(getClass().toString(), "handleServerCommands", e);
            try {
                coreServerClient.error(e.getErrorCode(), e.getMessage());
            } catch (C4Exception c4) {
                Logger.getGlobal().throwing(getClass().toString(), "startGame", c4);
            }
        }
    }

    /**
     * Accepts a client. If the client is accepted, his state will change to the 'ready' state.
     * @param gNumber The given group number of the client that is joining the server.
     * @param exts The arguments given by the client that is joining the server.
     */
    @Override
    public void accept(int gNumber, Set<Extension> exts) {
        if (exts != null && exts.contains(ExtensionFactory.chat())) {
            chatServerClient = new ChatServer.Client(out);
        }
        try {
            coreServerClient.ready();
        } catch (C4Exception e) {
            Logger.getGlobal().throwing("ServerHandler", "accept", e);
        }
    }

    /**
     * The method that starts a new game. It takes the input and starts a game with the
     * two players specified in the input.
     * @param p1 The name of the first player.
     * @param p2 The name of the second player.
     */
    @Override
    public void startGame(String p1, String p2) throws C4Exception {
        if (game == null || game.hasFinished()) {
            Logger.getGlobal().info("Starting game with players " + p1 + " " + p2);
            ASyncPlayer player1 = new ASyncPlayer(p1, Mark.P1);
            ASyncPlayer player2 = new ASyncPlayer(p2, Mark.P2);
            playerMap.put(player1.getName(), player1);
            playerMap.put(player2.getName(), player2);
            if (strategy != null) {
                Mark aiMark;
                if (p1.equals(name)) {
                    aiMark = Mark.P1;
                } else {
                    aiMark = Mark.P2;
                }
                ai = new LocalAIPlayer(strategy, aiMark);
            } else {
                ai = null;
            }
            game = new Game(BinaryBoard.class, player1, player2);
            controller.setGame(game);
        } else {
            throw new InvalidCommandError("There is already a game running");
        }

    }

    /**
     * Requests a move from the given player. The method starts a new Thread to do so.
     * @param player The player that needs to do a move.
     */
    @Override
    public void requestMove(String player) {

        new Thread(() -> handleRequestMove(player)).start();

    }

    /**
     * Handles the request move method. The method will use the AI if there is one,
     * otherwise the serverHandler will get the move from the controller.
     * @param player The player who's turn it is.
     */
    private void handleRequestMove(String player) {
        if (name.equals(player)) {
            int move;
            if (ai != null) {
                move = ai.determineMove(game.getBoard());
            } else {
                move = controller.getHumanPlayerMove();
            }
            try {
                coreServerClient.doMove(move);
            } catch (C4Exception e) {
                Logger.getGlobal().throwing(getClass().toString(), "requestMove", e);
            }
        }
    }

    /**
     * If a move is done by a player, this method handles the move. If the player exists,
     * the move will be put in the queueMove. Else an error is caught.
     * @param playerName The player who did the move.
     * @param col The move which has been received from the server.
     */
    @Override
    public void doneMove(String playerName, int col) throws C4Exception {
        ASyncPlayer player = playerMap.get(playerName);
        if (game.getCurrent() == player) {
            if (game.getBoard().columnHasFreeSpace(col)) {
                player.queueMove(col);
            } else {
                throw new InvalidMoveError("The board has no free space a column " + col);
            }
        } else {
            throw new InvalidMoveError("It is not the turn of player " + playerName);
        }
    }

    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        running = false;
    }

    /**
     * Ends a game if a player disconnects.
     * @param player The player that disconnects.
     */
    @Override
    public void gameEnd(String player) {
        disconnect();
    }

    /**
     * This method gives an error.
     * @param eCode The error code.
     * @param message The error message.
     */
    @Override
    public void error(int eCode, String message) {
        controller.showError("Error " + eCode + ": " + message);

    }

    /**
     * Used by the server to relay messages from a client to other clients.
     *
     * @param playerName The name of the user that sent the message.
     * @param message    The message that another client has sent.
     */
    @Override
    public void message(String playerName, String message) throws C4Exception {
        controller.showChat(playerName, message);
    }

    public void globalChat(String message) {
        try {
            chatServerClient.chatGlobal(message);
        } catch (C4Exception e) {
            e.printStackTrace(); // TODO better error handling
        }
    }
}
