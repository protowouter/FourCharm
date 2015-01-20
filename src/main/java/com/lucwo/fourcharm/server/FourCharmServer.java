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
    private int poort;

    public FourCharmServer(int port) {
        lobby = new LobbyGroup(this);
        preLobby = new PreLobbyGroup(lobby, this);
        games = new ArrayList<>();
        running = true;
        poort = port;
    }

    public static void main(String[] args) {

        FourCharmServer server = new FourCharmServer(8080);
        server.startServer();

    }

    /**
     * Checks if there is already a client with the same name.
     *
     * @param name the name that will be checked
     * @return true if there exists another client with the same name,
     * false if there does not exist another client with the same name.
     */
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

    /**
     * Starts the server.
     */
    public void startServer() {

        Logger.getGlobal().info("Starting Fourcharm server");


        try {
            ServerSocket ss = new ServerSocket(poort);
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

    /**
     * Adds a game to the GameGroup
     *
     * @param game the game that will be added
     */
    public void addGame(GameGroup game) {
        games.add(game);
    }

    /**
     * Removes a game from the GameGroup whenever a game is finished,
     * or when a game stopped.
     * @param game the game that will be removed
     */
    public void removeGame(GameGroup game) {
        games.remove(game);
    }

    /**
     * Makes sure the server will stop.
     */
    public void stop() {
        running = false;
    }

    /**
     * Gives the specific lobby.
     * @return the lobby you've asked for
     */
    public ClientGroup getLobby() {
        return lobby;
    }

}
