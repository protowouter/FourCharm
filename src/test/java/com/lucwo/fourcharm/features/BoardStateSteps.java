/**
 * copyright 2014 Luce Sandfort and Wouter Timmermans 
 */
package com.lucwo.fourcharm.features;


import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.ComputerPlayer;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.board.Board;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import static org.junit.Assert.*;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class BoardStateSteps {
    
    private static String TODO = "Not yet implemented";
    
    private Board board;
    private static ComputerPlayer cP = new ComputerPlayer(new RandomStrategy());
    
    @Given("^an board with only one free spot$")
    public void an_board_with_only_one_free_spot() throws InvalidMoveException{
       
        board = new BinaryBoard();
        
        
        for (int i = 0; i < board.getSpotCount() -1; i++) {
            board.makemove(cP.doMove(board.deepCopy()));
        }
    }
    @When("^I fill the last spot$")
    public void I_fill_the_last_spot() throws InvalidMoveException {
        board.makemove(cP.doMove(board.deepCopy()));
    }
    @Then("^the board should report its when asked$")
    public void the_board_should_report_its_when_asked() {
        assertTrue(board.isFull());
    }


}
