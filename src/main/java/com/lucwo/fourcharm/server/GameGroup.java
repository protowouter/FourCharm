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
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class GameGroup extends ClientGroup implements Runnable {

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
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        game.run();
        String winnerName = null;
        if (game.hasWinner()) {
            winnerName = game.getWinner().getName();
        }
        try {
            for (ClientHandler clientje : playerMap.keySet()) {
                clientje.getClient().gameEnd(winnerName);
                server.getLobby().addHandler(clientje);
            }
        } catch (C4Exception e) {
            Logger.getGlobal().throwing("GameGroup", "run()", e);
        }
        server.removeGame(this);


    }
}
