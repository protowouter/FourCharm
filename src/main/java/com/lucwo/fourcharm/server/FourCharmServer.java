/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;


import com.google.common.collect.ConcurrentHashMultiset;
import com.lucwo.fourcharm.exception.ServerStartException;
import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

/**
 * The FourCharmServer class that is responsible for the server. The FourCharmServer makes sure that
 * at a given time there are no players with the same name. This class also maintains a list of
 * GameGroups and uses {@link com.lucwo.fourcharm.server.PreLobbyGroup} and
 * {@link com.lucwo.fourcharm.server.LobbyGroup} to model the state of clients.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class FourCharmServer {

    public static final Logger LOGGER = LoggerFactory.getLogger(FourCharmServer.class);

    private ClientGroup lobby;
    private ClientGroup preLobby;
    private Collection<GameGroup> games;
    private boolean running;
    private ServerSocket serverSocket;
    private int poort;
    private JmDNS jmDNS;

    /**
     * Constructs a new FourCharmServer given a specific port.
     * @param port The port the new server will use.
     */
    public FourCharmServer(int port) {
        lobby = new LobbyGroup(this);
        preLobby = new PreLobbyGroup(lobby, this);
        games = ConcurrentHashMultiset.create();
        running = true;
        poort = port;
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

    public void openSocket() throws ServerStartException {
        try {
            serverSocket = new ServerSocket(poort, 10, InetAddress.getLocalHost());
            Thread announceThread = new Thread(() -> announceServer(getSocketPort()));
            announceThread.setName("serverAnnouncer");
            announceThread.start();
            LOGGER.info("Listening for connections on port {}", getSocketPort());
        } catch (IOException e) {
            LOGGER.trace("main", e);
            throw new ServerStartException("Unable to start server on port " + poort);
        }
    }

    /**
     * Starts the server.
     */
    public void startServer() {

        LOGGER.info("Starting Fourcharm server");
        int clientCount = 0;


        while (running) {
            try {
                Socket sock = serverSocket.accept();
                LOGGER.debug("Incoming connection from {}", sock.getInetAddress());
                ClientHandler client = new ClientHandler(sock);
                preLobby.addHandler(client);
                Thread t = new Thread(client);
                t.setName("ClientHandler-" + clientCount);
                t.start();
                clientCount++;
            } catch (IOException e) {
                LOGGER.trace("startServer", e);
            }
        }

        LOGGER.info("Shutting down FourCharm server");

    }

    private void announceServer(int port) {
        try {
            jmDNS = JmDNS.create();
            ServiceInfo info = ServiceInfo.create("_c4._tcp.local.", "FourCharm", port, "FourCharm game server");
            jmDNS.registerService(info);

        } catch (IOException e) {
            LOGGER.trace("announceServer", e);
        }

    }

    /**
     * Adds a game to the GameGroup.
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
        try {
            serverSocket.close();
            jmDNS.close();
        } catch (IOException e) {
            LOGGER.trace("stop", e);
        }
        games.forEach(cG -> cG.getClients().forEach(ClientHandler::shutdown));
        preLobby.getClients().forEach(ClientHandler::shutdown);
        lobby.getClients().forEach(ClientHandler::shutdown);


    }

    /**
     * Gives the specific lobby.
     * @return the lobby you've asked for
     */
    public ClientGroup getLobby() {
        return lobby;
    }

    public void globalChat(ClientHandler client, String message) throws C4Exception {
        lobby.broadcastChat(client, message);
        for (GameGroup game : games) {
            game.broadcastChat(client, message);
        }

    }
}
