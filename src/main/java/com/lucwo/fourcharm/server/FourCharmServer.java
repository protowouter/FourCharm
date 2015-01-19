/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;


import java.io.IOException;
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
                ClientHandler client = new ClientHandler(sock);
                preLobby.addHandler(client);
                new Thread(client).start();


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

    public ClientGroup getLobby() {
        return lobby;
    }

}
