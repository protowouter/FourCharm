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
import com.lucwo.fourcharm.view.FourCharmView;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FourCharmController implements Observer {

    public static final int MAX_PLAYERS = 2;

// ------------------ Instance variables ----------------

    private FourCharmView view;

// --------------------- Constructors -------------------

    public FourCharmController() {
        view = new FourCharmGUI(this);
    }

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.FINEST);


        ConsoleHandler cH = new ConsoleHandler();
        cH.setLevel(Level.FINEST);

        Logger.getGlobal().addHandler(cH);
        new FourCharmController();
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
        ServerHandler handler = new ServerHandler(playerName, hostName, port, this);
        handler.setStrategy(strategy);
        new Thread(handler).start();

    }

    /**
     * Starts a local game, without making use of a server.
     * @param localPlayerNames
     * @param aIStrategies
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
        Game game = new Game(BinaryBoard.class, player1, player2);
        setGame(game);
    }

    public void setGame(Game game) {
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
        }

    }


    /**
     * When playing a local game this method is used to retrieve the current move from
     * a humanplayer, this is only done when the current player is human, because an AI
     * doesn't need human input. When playing an networkgame this method is not used, in that
     * case it is the responsibility of the {@link com.lucwo.fourcharm.client.ServerHandler}
     * to call the {@link #getHumanPlayerMove} method.
     * @param player The player which current turn it is.
     */
    private void handlePlayerTurn(Player player) {
        if (player instanceof LocalHumanPlayer) {
            view.enableInput();
            ((LocalHumanPlayer) player).queueMove(view.requestMove());
        }
    }
}
