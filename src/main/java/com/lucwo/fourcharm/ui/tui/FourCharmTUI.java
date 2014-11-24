/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.ui.tui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.ComputerPlayer;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.HumanPlayer;
import com.lucwo.fourcharm.model.Player;
import com.lucwo.fourcharm.model.ai.NegaMaxStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class FourCharmTUI implements Observer {
    
    
    
    // ------------------ Instance variables ----------------
    
    private Game game;
    
    // --------------------- Constructors -------------------
    
    private FourCharmTUI() throws InstantiationException, IllegalAccessException {
        
        BufferedReader dis = new BufferedReader(
                new InputStreamReader(System.in));
        
        game = new Game(BinaryBoard.class, new Player[] {new HumanPlayer(dis),
            new ComputerPlayer(new NegaMaxStrategy()) });
        
        game.addObserver(this);
        
        
    }

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

    // ----------------------- Commands ---------------------
    
    /**
     * Start the game.
     */
    public void play() {
        
        try {
            game.play();
        } catch (InvalidMoveException e) {
            Logger.getGlobal().info("Quitting: someone tried cheating");
            Logger.getGlobal().throwing("FourCharmTUI", "play", e);
        }
        
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

}
