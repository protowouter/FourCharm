/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;


import com.google.common.collect.ConcurrentHashMultiset;
import com.lucwo.fourcharm.exception.ServerStartException;
import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.parameters.LobbyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * The FourCharmServer class that is responsible for the server. The FourCharmServer makes sure that
 * at a given time there are no players with the same name. This class also maintains a list of
 * GameGroups and uses {@link com.lucwo.fourcharm.server.PreLobbyGroup} and
 * {@link com.lucwo.fourcharm.server.LobbyGroup} to model the state of clients.
 * This class implements uses the server using non blocking io from the java.nio package.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class FourCharmServer {

    public static final Logger LOGGER = LoggerFactory.getLogger(FourCharmServer.class);

    //@ invariant lobby != null;
    private ClientGroup lobby;
    //@ invariant preLobby != null;
    private ClientGroup preLobby;
    //@ invariant games != null;
    private ConcurrentHashMultiset<GameGroup> games;
    private boolean running;
    private ServerSocketChannel serverChannel;
    private int poort;
    private JmDNS jmDNS;
    //@ invariant lobbyStates != null;
    private Map<ClientHandler, LobbyState> lobbyStates;
    private Map<SelectionKey, ClientHandler> socketChannelClientHandlerMap;

    // Selector is the "reactor" of the java.nio package.
    private Selector selector;

    /**
     * Constructs a new FourCharmServer given a specific port.
     *
     * @param port The port the new server will use.
     */
    public FourCharmServer(int port) {
        lobby = new LobbyGroup(this);
        preLobby = new PreLobbyGroup(lobby, this);
        games = ConcurrentHashMultiset.create();
        running = true;
        poort = port;
        lobbyStates = new ConcurrentHashMap<>();
        socketChannelClientHandlerMap = new HashMap<>();
    }

    public int getSocketPort() {
        return serverChannel.socket().getLocalPort();
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


    public void openSocket() throws ServerStartException {
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(), poort);
            serverChannel.socket().bind(isa);

            //Key for accepting incoming connections
            SelectionKey acceptKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);

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
                selector.select();
            } catch (IOException e) {
                LOGGER.trace("selecting keys", e);
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = readyKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isAcceptable()) {
                    accept(key);
                }
                if (key.isReadable()) {
                    read(key);
                }
                if (key.isWritable()) {
                    write(key);
                }
                it.remove();
            }
        }
        LOGGER.info("Shutting down FourCharm server");
    }



    private void accept(SelectionKey key) {
        SocketChannel socket;
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        try {
            socket = ssc.accept();
            if (socket != null) {
                socket.configureBlocking(false);
                SelectionKey newSocketKey =
                        socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                ClientHandler client = new ClientHandler(socket, this);
                client.init();
                preLobby.addHandler(client);
                socketChannelClientHandlerMap.put(newSocketKey, client);

            }
        } catch (IOException e) {
            LOGGER.trace("startServer", e);
        }

    }

    private void read(SelectionKey key) {
        try {
            socketChannelClientHandlerMap.get(key).handleRead();
        } catch (IOException e) {
            LOGGER.trace("read", e);
        }
    }

    private void write(SelectionKey key) {
        socketChannelClientHandlerMap.get(key).handleWrite();
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
     *
     * @param game the game that will be added
     */
    public void addGame(GameGroup game) {
        games.add(game);
    }

    /**
     * Removes a game from the GameGroup whenever a game is finished,
     * or when a game stopped.
     *
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
            serverChannel.close();
            jmDNS.close();
        } catch (IOException e) {
            LOGGER.trace("stop", e);
        }
        games.forEach(cG -> cG.forEveryClient(ClientHandler::shutdown));
        preLobby.forEveryClient(ClientHandler::shutdown);
        lobby.forEveryClient(ClientHandler::shutdown);


    }

    /**
     * Gives the specific lobby.
     *
     * @return the lobby you've asked for
     */
    public ClientGroup getLobby() {
        return lobby;
    }

    /*@
        requires client != null
     */
    public void globalChat(ClientHandler client, String message) throws C4Exception {
        lobby.broadcastChat(client, message);
        games.forEach(game -> {
            try {
                game.broadcastChat(client, message);
            } catch (C4Exception e) {
                LOGGER.trace("globalChat", e);
            }
        });

    }


    /*@
        requires client != null;
     */
    public void sendCurrentStates(ClientHandler client) {
        for (Map.Entry<ClientHandler, LobbyState> e : lobbyStates.entrySet()) {
            try {
                client.getLobbyClient().stateChange(e.getKey().getName(), e.getValue());
            } catch (C4Exception e1) {
                LOGGER.trace("sendCurrentStates", e);
            }
        }
    }


    /*@
        requires client != null && state != null;
     */
    public synchronized void stateChange(ClientHandler client, LobbyState state) {
        if (state != LobbyState.OFFLINE) {
            lobbyStates.put(client, state);
        } else {
            lobbyStates.remove(client);
        }
        Consumer<ClientHandler> alertStateChange = ch -> {
            try {
                ch.getLobbyClient().stateChange(client.getName(), state);
            } catch (C4Exception e) {
                LOGGER.trace("stateChange", e);
            }
        };
        lobby.forEveryClient(alertStateChange);
        games.forEach(game -> game.forEveryClient(alertStateChange));
    }
}
