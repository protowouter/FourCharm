/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm;

import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.view.FourCharmTUI;
import com.lucwo.fourcharm.view.FourCharmView;

public class FourCharmController {

// ------------------ Instance variables ----------------

    private Game game;
    private FourCharmView view;

// --------------------- Constructors -------------------

    public FourCharmController() {
        view = new FourCharmTUI(this);
        view.showNewGame();
        view.run();
    }

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public static void main(String[] args) {
        new FourCharmController();
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
        view.showGame(game);
        new Thread(game).start();

    }


}
