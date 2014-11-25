/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */
package com.lucwo.fourcharm.cucumber;


import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.ComputerPlayer;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.board.Board;
import com.lucwo.fourcharm.model.board.ReferenceBoard;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class BoardStateSteps {

    private static final String TODO = "Not yet implemented";
    private static final ComputerPlayer cP = new ComputerPlayer(new RandomStrategy());
    private Board binBoard;
    private Board refBoard;
    
    @Given("^a board with only one free spot$")
    public void a_board_with_only_one_free_spot() throws InvalidMoveException{

        binBoard = new BinaryBoard();
        refBoard = new ReferenceBoard();


        for (int i = 0; i < (this.binBoard.getSpotCount() - 1); i++) {
            binBoard.makemove(cP.doMove(binBoard.deepCopy()));
        }

        for (int i = 0; i < (this.refBoard.getSpotCount() - 1); i++) {
            refBoard.makemove(cP.doMove(refBoard.deepCopy()));
        }
    }
    @When("^I fill the last spot$")
    public void I_fill_the_last_spot() throws InvalidMoveException {
        binBoard.makemove(cP.doMove(binBoard.deepCopy()));
        refBoard.makemove(cP.doMove(refBoard.deepCopy()));
    }
    @Then("^the board should report its full$")
    public void the_board_should_report_its_when_asked() {
        assertTrue(binBoard.isFull());
        assertTrue(refBoard.isFull());
    }
    
    @And("^I should not be able to make an move$")
    public void unableToMove() {   
        
        try {
            binBoard.makemove(5);
            refBoard.makemove(5);
            fail();
         } catch (InvalidMoveException e) {
             
         } 
        
    }
    


}
