/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm;

import com.lucwo.fourcharm.client.ServerHandler;
import com.lucwo.fourcharm.exception.ServerConnectionException;
import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.view.FourCharmGUI;
import com.lucwo.fourcharm.view.FourCharmTUI;
import com.lucwo.fourcharm.view.FourCharmView;
import javafx.application.Application;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

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

// ------------------ Instance variables ----------------

    private FourCharmView view;
    private Game game;
    private ServerHandler serverClient;

// --------------------- Constructors -------------------

    /**
     * Constructs a new controller.
     */
    public FourCharmController() {

    }

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println(arg);
        }
        if (args.length > 0 && "-c".equals(args[0])) {
            FourCharmController con = new FourCharmController();
            FourCharmTUI view = new FourCharmTUI(con);
            con.setView(view);
            new Thread(view).start();
        } else {
            Application.launch(FourCharmGUI.class);
        }

        if (args.length > 1 && "-v".equals(args[1])) {
            Logger.getGlobal().setLevel(Level.FINEST);
            ConsoleHandler cH = new ConsoleHandler();
            cH.setLevel(Level.FINEST);
            Logger.getGlobal().addHandler(cH);
        }

    }

    /**
     * Returns the current {@link com.lucwo.fourcharm.view.FourCharmView} used by this controller
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
    public void startNetworkGame(String hostName, String port, String playerName, GameStrategy strategy) throws ServerConnectionException {
        serverClient = new ServerHandler(playerName, hostName, port, this);
        serverClient.setStrategy(strategy);
        new Thread(serverClient).start();

    }

    /**
     * Starts a local game, without making use of a server.
     *
     * @param localPlayerNames The names of the human player(s).
     * @param aIStrategies     The names of the AI player(s).
     */
    /*@ requires localPlayerNames.length + aiStrategies.length == 2
     */
    public void startLocalGame(String[] localPlayerNames, GameStrategy[] aIStrategies) {
        Player player1;
        Player player2;
        if (localPlayerNames.length == MAX_PLAYERS) {
            player1 = new LocalHumanPlayer(localPlayerNames[0], Mark.P1);
            player2 = new LocalHumanPlayer(localPlayerNames[1], Mark.P2);
        } else if (aIStrategies.length == MAX_PLAYERS) {
            player1 = new LocalAIPlayer(aIStrategies[0], Mark.P1);
            player2 = new LocalAIPlayer(aIStrategies[1], Mark.P2);
        } else {
            player1 = new LocalHumanPlayer(localPlayerNames[0], Mark.P1);
            player2 = new LocalAIPlayer(aIStrategies[0], Mark.P2);
        }
        Game gamepje = new Game(BinaryBoard.class, player1, player2);
        setGame(gamepje);
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
        new Thread(game).start();
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
        // TODO Implement
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
            ((LocalHumanPlayer) player).queueMove(view.requestMove());
        }
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

    /**
     * Shows an error with a message.
     *
     * @param message The errormessage.
     */
    public void showError(String message) {
        view.showError(message);
    }
}
