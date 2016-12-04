/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import com.lucwo.fourcharm.client.ServerHandler;
import com.lucwo.fourcharm.exception.ServerConnectionException;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.player.LocalAIPlayer;
import com.lucwo.fourcharm.model.player.LocalHumanPlayer;
import com.lucwo.fourcharm.model.player.Mark;
import com.lucwo.fourcharm.model.player.Player;
import com.lucwo.fourcharm.view.FourCharmGUI;
import com.lucwo.fourcharm.view.FourCharmTUI;
import com.lucwo.fourcharm.view.FourCharmView;
import javafx.application.Application;
import nl.woutertimmermans.connect4.protocol.exceptions.C4Exception;
import nl.woutertimmermans.connect4.protocol.parameters.LobbyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Functions as the Controller part of MVC for the FourCharm system.
 * The controllers maintains a connection with a server if applicable
 * and handles the communication between the model and the view for human players.
 * This class uses a Serverhandler for communication with a server and a Object implementing
 * {@link com.lucwo.fourcharm.view.FourCharmView} to communicate with users of the system.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */

public class FourCharmController implements Observer {

    public static final int MAX_PLAYERS = 2;
    private static final GameStrategy HINT_AI = new MTDfStrategy();
    private static final Logger LOGGER = LoggerFactory.getLogger(FourCharmController.class);

// ------------------ Instance variables ----------------

    private FourCharmView view;
    private Game game;
    private ServerHandler serverClient;
    private Player player1;
    private Player player2;
    private LobbyList lobbyStateList;
    private Set<C4Server> servers;

// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    /**
     * Constructs a new controller.
     */
    public FourCharmController() {

        lobbyStateList = new LobbyList();
        servers = new HashSet<>();
    }


    public static void main(String[] args) {
        boolean tui = false;
        boolean server = false;

        for (String arg : args) {
            tui = "-c".equals(arg) || tui;
        }
        for (String arg : args) {
            server = "-s".equals(arg) || server;
        }

        if (server) {
            FourCharmServerController con = new FourCharmServerController();
        } else if (tui) {
            FourCharmController con = new FourCharmController();
            FourCharmTUI view = new FourCharmTUI(con);
            con.setView(view);
            Thread viewThread = new Thread(view);
            viewThread.setName("FourTui");
            viewThread.start();
        } else {
            Application.launch(FourCharmGUI.class);
        }

    }

    /**
     * Returns the current {@link com.lucwo.fourcharm.view.FourCharmView} used by this controller.
     *
     * @return the current view
     */
    public FourCharmView getView() {
        return view;
    }

    /**
     * Sets the view (textual or graphical).
     *
     * @param v The given view.
     */
    public void setView(FourCharmView v) {
        view = v;
    }

    /**
     * Starts a networked game, making use of a server.
     *
     * @param hostName   String for hostname
     * @param port       String for port
     * @param playerName The name which will be reported to the server.
     * @param strategy   The strategy that will be used for the ai or null of no ai will be used.
     * @throws ServerConnectionException When unable to connect to a server.
     */
    public void startNetworkGame(String hostName, String port,
                                 String playerName, GameStrategy strategy)
            throws ServerConnectionException {
        serverClient = new ServerHandler(playerName, hostName, port, this);
        serverClient.setStrategy(strategy);
        new Thread(serverClient).start();
        view.showLobby();
        view.showMessage("Hey there handsome, you are now connected to the server");

    }

    /**
     * Starts a local game, without making use of a server.
     * This method requires that exactly two parameters are null and two not null.
     *
     * @param humanName1 name of a human player1, or null if player 1 is an AI
     * @param humanName2 name of a human player2, or null if player 2 is an AI
     * @param stratp1    strategy used by AI player1, or null if player 1 is a human
     * @param stratp2    strategy used by AI player2, or null if player 2 is a human
     */
    /*@ requires stratp1 == null && stratp2 == null || humanName1 == null && humanName2 == null
        || humanName1 == null && stratp2 == null || humanName2 == null && stratp1 == null
     */
    public void startLocalGame(String humanName1, String humanName2,
                               GameStrategy stratp1, GameStrategy stratp2) {
        if (humanName1 != null) {
            player1 = new LocalHumanPlayer(humanName1, Mark.P1);
        } else {
            player1 = new LocalAIPlayer(stratp1, Mark.P1);
        }
        if (humanName2 != null) {
            player2 = new LocalHumanPlayer(humanName2, Mark.P2);
        } else {
            player2 = new LocalAIPlayer(stratp2, Mark.P2);
        }
        setGame(new Game(BinaryBoard.class, player1, player2));
    }

    /**
     * Sets a game and starts the game in a new Thread.
     *
     * @param g The game that needs to be started.
     */
    public void setGame(Game g) {
        game = g;
        game.addObserver(this);
        view.showGame(game);
        Thread gameThread = new Thread(game);
        gameThread.setName("GameThread");
        gameThread.start();
    }

    public void gameEnd(String player) {
        game.shutdown();
        game = null;
        if (player != null) {
            view.showMessage(player + " won the game");
        } else {
            view.showMessage("The game was a tie");
        }
        view.showLobby();
    }

    public void sendReady() {
        if (serverClient != null) {
            try {
                serverClient.sendReady();
            } catch (C4Exception e) {
                LOGGER.trace("sendReady", e);
                view.showError(e.getMessage());
            }
        } else {
            view.showError("You are not connected to a server");
        }
    }

    /**
     * Enables the move input of the view and retrieves the move from the view.
     *
     * @return the move the human has decided upon.
     */
    public int getHumanPlayerMove() {
        view.enableInput();
        return view.requestMove();
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

        if (o instanceof Game && !((Game) o).hasFinished()) {
            handlePlayerTurn(((Game) o).getCurrent());
        } else if (o instanceof Game && ((Game) o).hasFinished()) {
            view.showRematch();
        }

    }

    /**
     *
     */
    public void rematch() {
        setGame(new Game(BinaryBoard.class, player1, player2));
    }

    /**
     * When playing a local game this method is used to retrieve the current move from
     * a humanplayer, this is only done when the current player is human, because an AI
     * doesn't need human input. When playing an networkgame this method is not used, in that
     * case it is the responsibility of the {@link com.lucwo.fourcharm.client.ServerHandler}
     * to call the {@link #getHumanPlayerMove} method.
     *
     * @param player The player which current turn it is.
     */
    private void handlePlayerTurn(Player player) {
        if (player instanceof LocalHumanPlayer) {
            view.enableInput();
            view.enableHint();
            ((LocalHumanPlayer) player).queueMove(view.requestMove());
            view.disableHint();
        }
    }

    public boolean inLobby() {
        return serverClient != null;
    }


    /**
     * Returns the best move according to a AI. This can be used by views when
     * a human user wants a hint on the best next move.
     *
     * @return The best move for the current board.
     * The move is zero based. The leftmost column would
     * be move 0.
     */
    public int getHint() {
        view.disableHint();
        return HINT_AI.determineMove(game.getBoard(), game.getCurrent().getMark());
    }

    /**
     * Closes the game.
     */
    public void shutdown() {
        if (game != null) {
            game.shutdown();
        }
        if (serverClient != null) {
            serverClient.disconnect();
        }
    }

    public void disconnect() {
        if (serverClient != null) {
            serverClient.disconnect();
            serverClient = null;
            view.showMessage("You are now disconnected from the server");
            view.showNewGame();
        } else {
            showError("You are not connected to a server");
        }
    }

    /**
     * Shows an error with a message.
     *
     * @param message The errormessage.
     */
    public void showError(String message) {
        view.showError(message);
    }

    public void showChat(String playerName, String message) {
        view.showChat(playerName, message);
    }

    public void globalChat(String message) {
        if (serverClient != null) {
            serverClient.globalChat(message);
        }
    }

    public void localChat(String message) {
        if (serverClient != null) {
            serverClient.localChat(message);
        }
    }

    public void stateChange(String playerName, LobbyState lobbyState) {
        lobbyStateList.stateChange(playerName, lobbyState);
    }

    public LobbyList getLobbyList() {
        return lobbyStateList;
    }

}
