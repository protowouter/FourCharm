/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import com.lucwo.fourcharm.model.ASyncPlayer;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidMoveError;
import nl.woutertimmermans.connect4.protocol.exceptions.PlayerDisconnectError;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.util.*;
import java.util.logging.Logger;

public class GameGroup extends ClientGroup implements Observer {

// ------------------ Instance variables ----------------

    Map<ClientHandler, ASyncPlayer> playerMap;
    Game game;
    FourCharmServer server;


    // --------------------- Constructors -------------------
    public GameGroup(FourCharmServer theServer, ClientHandler client1, ClientHandler client2) {
        // TODO: refactor to make use of ASyncPlayer.getMoveRequester()
        server = theServer;
        playerMap = new HashMap<>();
        ASyncPlayer player1 = new ASyncPlayer(client1.getName(), Mark.P1);
        ASyncPlayer player2 = new ASyncPlayer(client2.getName(), Mark.P2);
        playerMap.put(client1, player1);
        playerMap.put(client2, player2);
        addHandler(client1);
        addHandler(client2);

        game = new Game(BinaryBoard.class, player1, player2);
        game.addObserver(this);
        try {
            client1.getClient().startGame(client1.getName(), client2.getName());
        } catch (C4Exception e) {
            e.printStackTrace();
        }
        try {
            client2.getClient().startGame(client1.getName(), client2.getName());
        } catch (C4Exception e) {
            e.printStackTrace();
        }
    }

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    /**
     * ServerHandler wants to go from the PreLobbyGroup to the LobbyGroup.
     *
     * @param client  The client which performed this command.
     * @param pName   Player name
     * @param gNumber Group number
     * @param exts    Set of extensions supported
     * @throws C4Exception client is already in the GameGroup
     */
    @Override
    public void join(ClientHandler client, String pName, int gNumber, Set<Extension> exts) throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    /**
     * Makes a move for a specific client
     *
     * @param client the client that is about to make a move
     * @param col    the column the move will be about
     * @throws C4Exception
     */
    @Override
    public void doMove(ClientHandler client, int col) throws C4Exception {

        if (game.getCurrent().getName().equals(client.getName())) {

            try {
                for (ClientHandler c : getClients()) {
                    c.getClient().doneMove(client.getName(), col);
                }
                playerMap.get(client).queueMove(col);
            } catch (IllegalStateException e) {
                throw new InvalidMoveError("You are not allowed to make a move right now");
            }
        } else {
            throw new InvalidMoveError("You are not allowed to make a move right now");
        }

    }

    /**
     * Sets the status of the player to ready. If there is another player ready as well,
     * a new game will be started.
     *
     * @param client the client that wants to play a game
     * @throws C4Exception the client is not allowed to use this command
     */
    @Override
    public void ready(ClientHandler client) throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    /**
     * Handles the removal of a client from the group.
     * @param client The {@link com.lucwo.fourcharm.server.ClientHandler} which will has been removed from the group.
     */
    @Override
    public void removeClientCallback(ClientHandler client) {
        for (ClientHandler cH : getClients()) {
            C4Exception e = new PlayerDisconnectError("Player " + client.getName() + " disconnected");
            try {
                cH.getClient().error(e.getErrorCode(), e.getMessage());
            } catch (C4Exception e1) {
                Logger.getGlobal().throwing(getClass().toString(), "removeClientCallback", e);
            }
        }
        endGame();
    }

    /**
     * Starts a new game.
     */
    public void startGame() {

        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    /**
     * Ends a game that started before.
     */
    private void endGame() {

        String winnerName = null;
        if (game.hasWinner()) {
            winnerName = game.getWinner().getName();
        }
        try {
            for (ClientHandler client : playerMap.keySet()) {
                client.getClient().gameEnd(winnerName);
                server.getLobby().addHandler(client);
            }
        } catch (C4Exception e) {
            Logger.getGlobal().throwing("GameGroup", "startGame()", e);
        }
        server.removeGame(this);

    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Game) {
            if (!((Game) o).hasFinished()) {
                String currentName = ((Game) o).getCurrent().getName();
                ClientHandler client = null;
                for (ClientHandler c : playerMap.keySet()) {
                    if (c.getName().equals(currentName)) {
                        client = c;
                    }
                }
                if (client != null) {
                    try {
                        client.getClient().requestMove(currentName);
                    } catch (C4Exception e) {
                        Logger.getGlobal().throwing(getClass().toString(), "update", e);
                    }
                }
            } else {
                endGame();
            }
        }
    }
}
