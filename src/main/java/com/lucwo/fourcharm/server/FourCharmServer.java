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

/**
 * The FourCharmServer class that is responsible for the server. The FourCharmServer makes sure that
 * at a given time there are no players with the same name. This class also maintains a list of
 * GameGroups and uses {@link com.lucwo.fourcharm.server.PreLobbyGroup} and
 * {@link com.lucwo.fourcharm.server.LobbyGroup} to model the state of clients.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class FourCharmServer {

    private ClientGroup lobby;
    private ClientGroup preLobby;
    private Collection<GameGroup> games;
    private boolean running;
    private ServerSocket serverSocket;
    private int poort;

    /**
     * Constructs a new FourCharmServer given a specific port.
     * @param port The port the new server will use.
     */
    public FourCharmServer(int port) {
        lobby = new LobbyGroup(this);
        preLobby = new PreLobbyGroup(lobby, this);
        games = new ArrayList<>();
        running = true;
        poort = port;
    }

    public static void main(String[] args) {

        FourCharmServer server = new FourCharmServer(8080);
        server.openSocket();
        server.startServer();

    }

    public int getSocketPort() {
        return serverSocket.getLocalPort();
    }

    /**
     * Checks if there is already a client with the same name.
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

    public void openSocket() {
        try {
            serverSocket = new ServerSocket(poort);
            serverSocket.setSoTimeout(1000);
            Logger.getGlobal().info("Listening for connections on port " + poort);
        } catch (IOException e) {
            Logger.getGlobal().warning("Cannot listen on port " + poort);
            Logger.getGlobal().throwing("FourCharmServer", "main", e);
        }
    }

    /**
     * Starts the server.
     */
    public void startServer() {

        Logger.getGlobal().info("Starting Fourcharm server");


        while (running) {
            try {
                Socket sock = serverSocket.accept();
                ClientHandler client = new ClientHandler(sock);
                preLobby.addHandler(client);
                new Thread(client).start();
            } catch (IOException e) {
                Logger.getGlobal().throwing("FourCharmServer", "main", e);
            }
        }

        Logger.getGlobal().info("Shutting down Fourcharm server");

    }

    /**
     * Adds a game to the GameGroup
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
     * Makes sure the server will shutdown.
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
