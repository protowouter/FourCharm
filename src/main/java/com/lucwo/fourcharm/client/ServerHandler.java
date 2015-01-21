/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.client;

import com.lucwo.fourcharm.FourCharmController;
import com.lucwo.fourcharm.exception.ServerConnectionException;
import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreClient;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreServer;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
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

public class ServerHandler implements CoreClient.Iface, Runnable {

    private static final int GROUP_NUMBER = 23;

// ------------------ Instance variables ----------------

    private String name;
    private BufferedReader in;
    private CoreServer.Client serverClient;
    private CoreClient.Processor<ServerHandler> processor;
    private FourCharmController controller;
    private Map<String, ASyncPlayer> playerMap;
    private GameStrategy strategy;
    private Player ai;
    private Game game;
    private boolean running;

// --------------------- Constructors -------------------

    /**
     * Constructs a serverHandler.
     * @param namepie The name of the serverHandler.
     * @param hostString The host of the serverHandler (the IP address).
     * @param portString The port the serverHandler will use.
     * @param contr The FourCharmController of the serverHandler.
     * @throws ServerConnectionException If the ServerHandler cannot make a connection to the server.
     */
    public ServerHandler(String namepie, String hostString, String portString, FourCharmController contr) throws ServerConnectionException {
        running = true;
        controller = contr;
        name = namepie;
        playerMap = new HashMap<>();

        try {
            InetAddress host = InetAddress.getByName(hostString);
            int port = Integer.parseInt(portString);
            Socket sock = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream(), Charset.forName("UTF-8")));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), Charset.forName("UTF-8")));
            processor = new CoreClient.Processor<>(this);
            serverClient = new CoreServer.Client(out);
        } catch (IOException e) {
            throw new ServerConnectionException(e.getMessage());
        }


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
            serverClient.join(name, GROUP_NUMBER, new HashSet<>());
        } catch (C4Exception e) {
            Logger.getGlobal().throwing(getClass().toString(), "startGame", e);
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
                processor.process(input);
                input = in.readLine();

            }
        } catch (IOException e) {
            Logger.getGlobal().throwing(getClass().toString(), "startGame", e);
        } catch (C4Exception e) {
            Logger.getGlobal().throwing(getClass().toString(), "startGame", e);
            try {
                serverClient.error(e.getErrorCode(), e.getMessage());
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
        try {
            serverClient.ready();
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
    public void startGame(String p1, String p2) {
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
        }
        game = new Game(BinaryBoard.class, player1, player2);
        controller.setGame(game);
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
                serverClient.doMove(move);
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
    public void doneMove(String playerName, int col) {

        ASyncPlayer player = playerMap.get(playerName);
        if (player != null) {
            player.queueMove(col);
        } else {
            Logger.getGlobal().warning("Player " + playerName + " is unknown");
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
}