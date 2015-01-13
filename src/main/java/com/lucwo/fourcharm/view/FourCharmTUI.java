/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.NegaMaxStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;

import java.io.BufferedReader;
import java.io.IOException;
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
class FourCharmTUI implements Observer, MoveRequestable {
    
    
    
    // ------------------ Instance variables ----------------

    private final Game game;
    private BufferedReader reader;
    
    // --------------------- Constructors -------------------

    protected FourCharmTUI() throws InstantiationException, IllegalAccessException {
        super();

        reader = new BufferedReader(
                new InputStreamReader(System.in));

        game = new Game(BinaryBoard.class, new LocalAIPlayer(new NegaMaxStrategy(10), Mark.P1),
                new ASyncPlayer("Wouter", this, Mark.P2));

        game.addObserver(this);


    }

    /**
     * @param args none applicable
     */
    public static void main(String[] args) {

        Logger globalLogger = Logger.getGlobal();

        LogManager.getLogManager().reset();

        globalLogger.setLevel(Level.INFO);


        ConsoleHandler cH = new ConsoleHandler();
        cH.setLevel(Level.INFO);

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

        Logger.getGlobal().info(game.getBoard().toString());

        if (game.hasWinner()) {
            Logger.getGlobal().info(game.getWinner().toString() + " Won");
        } else {
            Logger.getGlobal().info("The game is a tie");
        }



    }

    @Override
    public int requestMove() {
        System.out.println("Please enter move:");

        int move = 0;

        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            Logger.getGlobal().warning(e.toString());
            Logger.getGlobal().throwing("ASyncPlayer", "determineMove", e);
        }
        Logger.getGlobal().info("Playerinput: " + line);
        if (line == null) {
            requestMove();
        } else {
            for (int i = 0; i < line.length(); i++) {
                int col = line.charAt(i) - '1';
                if ((col >= 0) && (col < game.getBoard().getColumns())
                        && game.getBoard().columnHasFreeSpace(col)) {
                    move = col;
                } else {
                    move = requestMove();
                }
            }
        }
        return move;
    }
}
