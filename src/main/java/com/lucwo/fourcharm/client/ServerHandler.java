/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.client;

import com.lucwo.fourcharm.FourCharmController;
import com.lucwo.fourcharm.exception.ServerConnectionException;
import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
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

// --------------------- Constructors -------------------

    public ServerHandler(String namepie, String hostString, String portString, FourCharmController contr) throws ServerConnectionException {
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

    @Override
    public void run() {
        joinServer();
        handleServerCommands();
    }


// ----------------------- Queries ----------------------

    public GameStrategy getStrategy() {
        return strategy;
    }

// ----------------------- Commands ---------------------

    public void setStrategy(GameStrategy strat) {
        strategy = strat;
    }

    public void joinServer() {
        try {
            serverClient.join(name, GROUP_NUMBER, new HashSet<>());
        } catch (C4Exception e) {
            Logger.getGlobal().throwing(getClass().toString(), "startGame", e);
        }
    }

    public void handleServerCommands() {
        try {
            String input = in.readLine();
            while (input != null) {
                Logger.getGlobal().info("Processing input " + input);
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

    @Override
    public void accept(int gNumber, Set<Extension> exts) {
        try {
            serverClient.ready();
        } catch (C4Exception e) {
            Logger.getGlobal().throwing("ServerHandler", "accept", e);
        }
    }

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
            ai = new LocalAIPlayer(new MTDfStrategy(), aiMark);
        }
        game = new Game(BinaryBoard.class, player1, player2);
        controller.setGame(game);
    }

    @Override
    public void requestMove(String player) {

        new Thread(() -> handleRequestMove(player)).start();

    }

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

    @Override
    public void doneMove(String playerName, int col) {

        ASyncPlayer player = playerMap.get(playerName);
        if (player != null) {
            player.queueMove(col);
        } else {
            Logger.getGlobal().warning("Player " + playerName + " is unknown");
        }


    }

    @Override
    public void gameEnd(String player) {

    }

    @Override
    public void error(int eCode, String message) {

    }
}
