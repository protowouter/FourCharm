/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;


import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreClient;
import nl.woutertimmermans.connect4.protocol.fgroup.CoreServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public class FourCharmServer {

    private ClientGroup lobby;
    private ClientGroup preLobby;
    private Collection<GameGroup> games;
    private boolean running;

    public FourCharmServer(int port) {
        lobby = new LobbyGroup(this);
        preLobby = new PreLobbyGroup(lobby, this);
        games = new ArrayList<>();
        running = true;
        startServer(port);
    }

    public static void main(String[] args) {

        new FourCharmServer(8080);

    }

    public boolean hasClientWithName(String name) {
        boolean nameExistsinGame = false;
        for (ClientGroup cG : games) {
            nameExistsinGame = cG.clientNameExists(name);
            if (nameExistsinGame) {
                break;
            }
        }
        return nameExistsinGame || lobby.clientNameExists(name);
    }

    public void startServer(int port) {

        Logger.getGlobal().info("Starting Fourcharm server");

        try {
            ServerSocket ss = new ServerSocket(port);
            while (running) {
                Socket sock = ss.accept();
                new Thread(() -> handleClient(sock)).start();


            }
        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmServer", "main", e);
        }

        Logger.getGlobal().info("Shutting down Fourcharm server");

    }

    public void addGame(GameGroup game) {
        games.add(game);
    }

    public void removeGame(GameGroup game) {
        games.remove(game);
    }

    public void stop() {
        running = false;
    }

    public void handleClient(Socket sock) {

        final String m_name = "handleClient";

        BufferedWriter out = null;
        BufferedReader in = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmServer", m_name, e);
        }

        CoreClient.Client clientClient = new CoreClient.Client(out);

        ClientHandler handler = new ClientHandler(clientClient);
        preLobby.addHandler(handler);
        CoreServer.Processor processor = new CoreServer.Processor<>(handler);

        try {
            String input = in.readLine();
            while (input != null) {
                Logger.getGlobal().info("Processing input " + input);
                try {
                    boolean processed = processor.process(input);
                    if (!processed) {
                        Logger.getGlobal().warning("This command is not recognized");
                        C4Exception error = new InvalidCommandError(input + " is not recognized");
                        clientClient.error(error.getErrorCode(), error.getMessage());
                    }
                } catch (C4Exception e) {

                    Logger.getGlobal().info("Throwing exception " + e.getMessage());
                    try {
                        clientClient.error(e.getErrorCode(), e.getMessage());
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
                sock.close();
            } catch (IOException e) {
                Logger.getGlobal().throwing("FourCharmServer", m_name, e);
            }
        }


    }

}
