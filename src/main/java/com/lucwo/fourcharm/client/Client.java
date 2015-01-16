/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.client;

import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
import com.lucwo.fourcharm.model.board.ReferenceBoard;
import com.lucwo.fourcharm.server.MoveQueue;
import com.lucwo.fourcharm.view.FourCharmTUI;
import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreClient;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreServer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.logging.Logger;

public class Client implements CoreClient.Iface, Runnable, MoveRequestable {

    private static int GROUP_NUMBER = 23;

// ------------------ Instance variables ----------------

    private Socket sock;
    private String name;
    private BufferedReader in;
    private BufferedWriter out;
    private CoreServer.Client serverClient;
    private CoreClient.Processor<Client> processor;
    private FourCharmTUI controller;
    private Game game;
    private Map<String, MoveQueue> queueMap;
    private Player ai;
    private Observer gameObserver;

// --------------------- Constructors -------------------

    public Client(String namepie, InetAddress host, int port, Observer obsv) {
        gameObserver = obsv;
        name = namepie;
        queueMap = new HashMap<>();

        try {
            sock = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            processor = new CoreClient.Processor<>(this);
            serverClient = new CoreServer.Client(out);
        } catch (IOException e) {
            Logger.getGlobal().throwing("Client", "Constructor", e);
        }


    }

    @Override
    public void run() {
        try {
            serverClient.join(name, GROUP_NUMBER, new HashSet<String>());
        } catch (C4Exception e) {
            Logger.getGlobal().throwing("Client", "run", e);
        }
        try {
            String input = in.readLine();
            while (input != null) {
                Logger.getGlobal().info("Processing input " + input);
                processor.process(input);
                input = in.readLine();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (C4Exception e) {
            Logger.getGlobal().warning(e.getMessage() +
                    " May not be sent to the server, so there");
        }
    }


// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public void queueMove(int col) {

    }

    @Override
    public void accept(int gNumber, Set<String> exts) {

        try {
            serverClient.ready();
        } catch (C4Exception e) {
            Logger.getGlobal().throwing("Client", "accept", e);
        }

    }

    @Override
    public void startGame(String p1, String p2) {

        Logger.getGlobal().info("Starting game with players " + p1 + " " + p2);

        MoveQueue p1Q = new MoveQueue();
        MoveQueue p2Q = new MoveQueue();
        Player player1 = new ASyncPlayer(p1, p1Q, Mark.P1);
        Player player2 = new ASyncPlayer(p2, p2Q, Mark.P2);
        queueMap.put(player1.getName(), p1Q);
        queueMap.put(player2.getName(), p2Q);
        Mark aiMark;
        if (p1.equals(name)) {
            aiMark = Mark.P1;
        } else {
            aiMark = Mark.P2;
        }
        ai = new LocalAIPlayer(new MTDfStrategy(), aiMark);

        game = new Game(ReferenceBoard.class, player1, player2);
        game.addObserver(gameObserver);
        new Thread(game).start();


    }

    @Override
    public void requestMove(String player) {

        if (player.equals(name)) {

            new Thread(() -> {
                try {
                    serverClient.doMove(ai.determineMove(game.getBoard()));
                } catch (C4Exception e) {
                    Logger.getGlobal().throwing("Client", "requestMove", e);
                }

            }).start();

        }

    }

    @Override
    public void doneMove(String player, int col) {

        MoveQueue queue = queueMap.get(player);
        if (queue != null) {
            try {
                queue.getQueue().put(col);
            } catch (InterruptedException e) {
                Logger.getGlobal().throwing("Client", "doneMove", e);
            }
        } else {
            Logger.getGlobal().warning("Player " + player + " is unknown");
        }


    }

    @Override
    public void gameEnd(String player) {

    }

    @Override
    public void error(int eCode, String message) {

    }

    @Override
    public int requestMove() {
        return 0;
    }
}
