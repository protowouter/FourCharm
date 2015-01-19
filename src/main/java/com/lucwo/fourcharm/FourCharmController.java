/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm;

import com.lucwo.fourcharm.client.ServerHandler;
import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.view.FourCharmTUI;
import com.lucwo.fourcharm.view.FourCharmView;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FourCharmController implements Observer {

// ------------------ Instance variables ----------------

    private FourCharmView view;
    private ServerHandler serverHandler;

// --------------------- Constructors -------------------

    public FourCharmController() {
        view = new FourCharmTUI(this);
        view.showNewGame();
        view.run();
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

    public void startNetworkGame(String hostName, String port, String playerName, GameStrategy strategy) {

    }

    /*@ requires localPlayerNames.length + aiStrategies.length == 2
     *
     */
    public void startLocalGame(String[] localPlayerNames, GameStrategy[] aIStrategies) {
        Player player1;
        Player player2;
        if (localPlayerNames.length == 2) {
            player1 = new LocalHumanPlayer(localPlayerNames[0], Mark.P1);
            player2 = new LocalHumanPlayer(localPlayerNames[1], Mark.P2);
        } else if (aIStrategies.length == 2) {
            player1 = new LocalAIPlayer(aIStrategies[0], Mark.P1);
            player2 = new LocalAIPlayer(aIStrategies[1], Mark.P2);
        } else {
            player1 = new LocalHumanPlayer(localPlayerNames[0], Mark.P1);
            player2 = new LocalAIPlayer(aIStrategies[0], Mark.P2);
        }
        Game game = new Game(BinaryBoard.class, player1, player2);
        game.addObserver(this);
        view.showGame(game);
        new Thread(game).start();

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

    private void handlePlayerTurn(Player player) {
        if (player instanceof LocalHumanPlayer) {
            view.enableInput();
            ((LocalHumanPlayer) player).queueMove(view.requestMove());
        }
    }
}
