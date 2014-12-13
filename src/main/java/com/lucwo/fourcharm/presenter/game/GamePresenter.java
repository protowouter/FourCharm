/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.game;

import com.lucwo.fourcharm.exception.InvalidMoveException;
import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.presenter.board.BoardPresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;


public class GamePresenter implements Observer {

    // ------------------ Instance variables ----------------


    PipedWriter playerInput;
    @FXML
    private Parent root;
    @FXML
    private FlowPane boardPane;

    private Game game;
    private Thread gameThread;
    private Player p1;
    private Player p2;
    private BoardPresenter boardPresenter;


    // ----------------------- Queries ----------------------

    public Parent getView() {
        return root;
    }


    // ----------------------- Commands ---------------------

    public void setGame(Game newGame) {


        game = newGame;


    }

    private void createGame() {
        ClassLoader classloader = getClass().getClassLoader();
        FXMLLoader fxmlLoader = new FXMLLoader(classloader.getResource("views/game/new.fxml"));

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        fxmlLoader.getRoot();


    }

    public void showGame() {
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
                    boardPresenter.enableSpaces();
                } else {
                    boardPresenter.disableSpaces();
                }
            });

        }

    }

    public void startGame() throws InvalidMoveException {
        update(game, null);
        gameThread.start();
    }

    public void doPlayerMove(int col) {
        try {
            playerInput.write(col + "\n");
        } catch (IOException e) {
            Logger.getGlobal().throwing("FourCharmController", "doPlayerMove", e);
        }
    }

    private void initBoardPane() {

        ClassLoader classloader = getClass().getClassLoader();


        FXMLLoader fxmlLoader = new FXMLLoader(classloader.getResource("views/show.fxml"));

        try {
            fxmlLoader.load();
            boardPane.getChildren().add(fxmlLoader.getRoot());
            boardPresenter = fxmlLoader.getController();
            boardPresenter.setGamePresenter(this);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        ((BoardPresenter) fxmlLoader.getController()).initBoard(game.getBoard());

        game.addObserver(fxmlLoader.getController());
    }

}
