/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

/**
 * Created by woutertimmermans on 26-11-14.
 */


public class GameController implements Observer {

    // ------------------ Instance variables ----------------

    PipedWriter playerInput;
    @FXML
    private FlowPane boardPane;
    private Game game;
    private Thread gameThread;
    private Player p1;
    private Player p2;
    private BoardController boardController;


    // ----------------------- Queries ----------------------


    // ----------------------- Commands ---------------------

    public void initialize() {

        playerInput = new PipedWriter();
        BufferedReader playerReader = new BufferedReader(new PipedReader());

        try {
            playerReader = new BufferedReader(new PipedReader(playerInput));
        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmController", "Constructor", e);
        }

        p1 = new ComputerPlayer(new MTDfStrategy(), Mark.P1);
        p2 = new HumanPlayer(playerReader, Mark.P2);



        try {
            game = new Game(BinaryBoard.class, p1, p2);
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.getGlobal().throwing("FourCharmController", "Constructor", e);
        }

        game.addObserver(this);

        gameThread = new Thread(game);

        initBoardPane();
    }


    public void update(Observable o, Object arg) {

        if (o instanceof Game) {
            Platform.runLater(() -> {
                if (game.getCurrent() instanceof HumanPlayer) {
                    boardController.enableSpaces();
                } else {
                    boardController.disableSpaces();
                }
            });

        }

    }

    public void startGame() throws InvalidMoveException {
        update(game, null);
        gameThread.start();
    }

    protected void doPlayerMove(int col) {
        try {
            playerInput.write(col + "\n");
        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmController", "doPlayerMove", e);
        }
    }

    private void initBoardPane() {

        ClassLoader classloader = getClass().getClassLoader();


        FXMLLoader fxmlLoader = new FXMLLoader(classloader.getResource("views/board.fxml"));

        try {
            fxmlLoader.load();
            boardPane.getChildren().add(fxmlLoader.getRoot());
            boardController = fxmlLoader.getController();
            boardController.setGameController(this);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        ((BoardController) fxmlLoader.getController()).initBoard(game.getBoard());

        game.addObserver(fxmlLoader.getController());
    }

}
