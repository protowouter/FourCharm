/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.model.ComputerPlayer;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.HumanPlayer;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
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

        game = new Game(BinaryBoard.class, new ComputerPlayer(new MTDfStrategy(), Mark.P1),
                new HumanPlayer(dis, Mark.P2));

        game.addObserver(this);


    }

    /**
     * @param args none applicable
     */
    public static void main(String[] args) {

        Logger globalLogger = Logger.getGlobal();

        LogManager.getLogManager().reset();

        globalLogger.setLevel(Level.FINEST);


        ConsoleHandler cH = new ConsoleHandler();
        cH.setLevel(Level.FINEST);

        globalLogger.addHandler(cH);

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
            System.out.println(((Game) o).getBoard().toString());
        }

    }
    
    /**
     * Start the game.
     */
    protected void play() {

        update(game, null);

        game.play();

        // Game Finished

        System.out.println(game.getBoard().toString());

        if (game.hasWinner()) {
            System.out.println(game.getWinner().toString() + " Won");
        } else {
            System.out.println("The game is a tie");
        }



    }

}
