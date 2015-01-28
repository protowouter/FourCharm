/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.server;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.player.ASyncPlayer;
import com.lucwo.fourcharm.model.player.Mark;
import com.lucwo.fourcharm.model.player.Player;
import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidCommandError;
import nl.woutertimmermans.connect4.protocol.exceptions.InvalidMoveError;
import nl.woutertimmermans.connect4.protocol.exceptions.PlayerDisconnectError;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;
import nl.woutertimmermans.connect4.protocol.parameters.LobbyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * TODO: GameGroup class javadoc verder uitbreiden.
 * The GameGroup class extends the ClientGroup abstract class and implements Observer.
 * This class uses the Protocol classes as well. Two Clients play against each other in
 * the GameGroup.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class GameGroup extends ClientGroup implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameGroup.class);

// ------------------ Instance variables ----------------


    //@ invariant playerMap != null;
    private Map<ClientHandler, ASyncPlayer> playerMap;
    //@ invariant game != null;
    private Game game;


    // --------------------- Constructors -------------------

    /**
     * Constructs a new GameGroup given the parameters.
     *
     * @param theServer The server that will be used for the GameGroup.
     * @param client1   The first ClientHandler that will be player 1 in the Game.
     * @param client2   The second ClientHandler that will be player 2 in the Game.
     */
    /*@
        requires theServer != null && client1 != null && client2 != null;
     */
    public GameGroup(FourCharmServer theServer, ClientHandler client1, ClientHandler client2) {
        super(theServer);
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
            client1.getCoreClient().startGame(client1.getName(), client2.getName());
            client2.getCoreClient().startGame(client1.getName(), client2.getName());
        } catch (C4Exception e) {
            LOGGER.trace("constructor", e);
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
     * @throws C4Exception If the client is already in the GameGroup an
     *                     InvalidCommandError will be thrown.
     */
    @Override
    public void join(ClientHandler client, String pName, int gNumber, Set<Extension> exts)
            throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    /**
     * Makes a move for a specific client.
     *
     * @param client the client that is about to make a move
     * @param col    the column the move will be about
     * @throws C4Exception If the move is not allowed an InvalidMoveError will be thrown.
     */
    /*@
        requires client1 != null;
     */
    @Override
    public void doMove(ClientHandler client, int col) throws C4Exception {


        if (!game.getBoard().columnHasFreeSpace(col)) {
            throw new InvalidMoveError("Column " + col
                    + " has no free space, please reconsider this move");
        } else if (game.getCurrent().getName().equals(client.getName())) {

            try {
                forEveryClient(c -> {
                        try {
                            c.getCoreClient().doneMove(client.getName(), col);
                        } catch (C4Exception e) {
                            LOGGER.trace("doMove", e);
                        }
                    });
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
     * @throws C4Exception If the client is not allowed to use this command an InvalidCommandError
     *                     is thrown.
     */
    @Override
    public void ready(ClientHandler client) throws C4Exception {

        throw new InvalidCommandError("You are not allowed to use this command now.");
    }

    /**
     * Handles the removal of a client from the group.
     *
     * @param client The {@link com.lucwo.fourcharm.server.ClientHandler}
     *               which will has been removed from the group.
     */
    /*@
        requires client != null;
     */
    @Override
    public synchronized void removeClientCallback(ClientHandler client) {
        if (!game.hasFinished()) {

            forEveryClient(cH -> {
                    C4Exception c4e = new PlayerDisconnectError("Player " +
                            client.getName() + " disconnected");
                    try {
                        cH.getCoreClient().error(c4e.getErrorCode(), c4e.getMessage());
                    } catch (C4Exception e) {
                        LOGGER.trace("removeClientCallback", e);
                    }
                });
            endGame();
        }

    }

    /*@
        requires client != null;
     */
    @Override
    public void addClientCallback(ClientHandler client) {
        getServer().stateChange(client, LobbyState.GAME);
    }

    /**
     * Starts a new game.
     */
    public void startGame() {

        Thread gameThread = new Thread(game);
        gameThread.setName("Game" + SimpleTimeZone.UTC_TIME);
        gameThread.start();
    }

    /**
     * Ends a game that started before.
     */
    private void endGame() {
        game.shutdown();
        Player winner = game.getWinner();
        final String winnerName = winner == null ? null : winner.getName();
        forEveryClient(client -> {
                try {
                    client.getCoreClient().gameEnd(winnerName);
                } catch (C4Exception e) {
                    LOGGER.trace("startGame", e);
                }
                getServer().getLobby().addHandler(client);
            });
        getServer().removeGame(this);

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
                    if (currentName.equals(c.getName())) {
                        client = c;
                    }
                }
                if (client != null) {

                    forEveryClient(cH -> {
                            try {
                                cH.getCoreClient().requestMove(currentName);
                            } catch (C4Exception e) {
                                LOGGER.trace("update", e);
                            }
                        });
                }
            } else {
                endGame();
            }
        }
    }
}
