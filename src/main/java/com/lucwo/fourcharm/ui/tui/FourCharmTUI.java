/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.ui.tui;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.ComputerPlayer;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.HumanPlayer;
import com.lucwo.fourcharm.model.Player;
import com.lucwo.fourcharm.model.ai.NegaMaxStrategy;
import com.lucwo.fourcharm.model.board.ReferenceBoard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
class FourCharmTUI implements Observer {
    
    
    
    // ------------------ Instance variables ----------------

    private final Game game;
    
    // --------------------- Constructors -------------------

    protected FourCharmTUI() throws InstantiationException, IllegalAccessException {
        super();

        BufferedReader dis = new BufferedReader(
                new InputStreamReader(System.in));

        game = new Game(ReferenceBoard.class, new Player[]{new HumanPlayer(dis),
                new ComputerPlayer(new NegaMaxStrategy())});

        game.addObserver(this);


    }

    /**
     * @param args none applicable
     */
    public static void main(String[] args) {

        try {
            new FourCharmTUI().play();
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.getGlobal().throwing("FourCharmTUI", "main", e);
        }


    }

    // ----------------------- Commands ---------------------

    /* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        Logger.getGlobal().finer("Tui is getting message from: " + o.toString());
        if (o instanceof Game) {
            Logger.getGlobal().info(((Game) o).getBoard().toString());
        }

    }
    
    /**
     * Start the game.
     */
    protected void play() {

        try {
            game.play();
        } catch (InvalidMoveException e) {
            Logger.getGlobal().info("Quitting: someone tried cheating");
            Logger.getGlobal().throwing("FourCharmTUI", "play", e);
        }

        // Game Finished

        Logger.getGlobal().info(game.getBoard().toString());

        if (game.hasWinner()) {
            Logger.getGlobal().info(game.getWinner().toString() + " Won");
        } else {
            Logger.getGlobal().info("The game is a tie");
        }



    }

}
