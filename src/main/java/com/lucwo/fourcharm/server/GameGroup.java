/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import com.lucwo.fourcharm.model.ASyncPlayer;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.MoveRequestable;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidMoveError;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameGroup extends ClientGroup {

// ------------------ Instance variables ----------------

    ClientHandler player1;
    ClientHandler player2;
    Map<ClientHandler, MoveQueue> moveQueues;
    Game game;


    // --------------------- Constructors -------------------
    public GameGroup(ClientHandler client1, ClientHandler client2) {
        addHandler(client1);
        addHandler(client2);
        MoveQueue p1Queue = new MoveQueue(client1);
        MoveQueue p2Queue = new MoveQueue(client2);
        moveQueues = new HashMap<>();
        moveQueues.put(client1, p1Queue);
        moveQueues.put(client2, p2Queue);

        game = new Game(BinaryBoard.class, new ASyncPlayer(client1.getName(), p1Queue, Mark.P1),
                new ASyncPlayer(client2.getName(), p2Queue, Mark.P2));
        client1.getClient().startGame(client1.getName(), client2.getName());
        client2.getClient().startGame(client1.getName(), client2.getName());
        new Thread(game).start();
    }

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    /**
     * Client wants to go from the PreLobbyGroup to the LobbyGroup.
     *
     * @param client  The client which performed this command.
     * @param pName   Player name
     * @param gNumber Group number
     * @param exts    Set of extensions supported
     * @throws C4Exception client is already in the GameGroup
     */
    @Override
    public void join(ClientHandler client, String pName, int gNumber, Set<String> exts) throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    @Override
    public void doMove(ClientHandler client, int col) throws C4Exception {

        try {
            moveQueues.get(client).getQueue().add(col);
            for (ClientHandler c : getClients()) {
                c.getClient().doneMove(client.getName(), col);
            }
        } catch (IllegalStateException e) {
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


    private class MoveQueue implements MoveRequestable {

        // ---------------- Instantie variabelen ------------------

        private BlockingQueue<Integer> rij;
        private ClientHandler client;

        // ---------------- Constructor ---------------------------

        public MoveQueue(ClientHandler c) {
            client = c;
            rij = new LinkedBlockingQueue<>(1);
        }

        // ---------------- Queries -------------------------------

        // ---------------- Commands ------------------------------

        /**
         * Asks for a move and puts this move in the queue.
         *
         * @return the requested move
         */
        @Override
        public int requestMove() {

            client.getClient().requestMove(client.getName());

            int column = -1;
            try {
                column = rij.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return column;
        }

        public BlockingQueue<Integer> getQueue() {
            return rij;
        }
    }
}
