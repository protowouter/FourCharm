/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package main.java.ui.tui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import main.java.model.ComputerPlayer;
import main.java.model.Game;
import main.java.model.HumanPlayer;
import main.java.model.Player;
import main.java.model.ai.NegaMaxStrategy;
import main.java.model.board.InvalidMoveException;
import main.java.model.board.ReferenceBoard;

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
        
        game = new Game(ReferenceBoard.class, new Player[] {new HumanPlayer(dis),
            new ComputerPlayer(new NegaMaxStrategy()) });
        
        game.addObserver(this);
        
        try {
            game.play();
        } catch (InvalidMoveException e) {
            Logger.getGlobal().info("Quitting: someone tried cheating");
        }
        
        
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
     * @param args none applicable
     */
    public static void main(String[] args) {
        
        try {
            new FourCharmTUI();
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.getGlobal().throwing("FourCharmTUI", "main", e);
        }
        

           
    }

}
