/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.game;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.LocalHumanPlayer;
import com.lucwo.fourcharm.presenter.board.BoardPresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;


public class GamePresenter implements Observer {

    // ------------------ Instance variables ----------------


    private LocalHumanPlayer currentPlayer;
    @FXML
    private Parent root;
    @FXML
    private FlowPane boardPane;

    private BoardPresenter boardPresenter;
    private BlockingQueue<Integer> moveQueue;


    // ----------------------- Queries ----------------------

    public Parent getView() {
        return root;
    }


    // ----------------------- Commands ---------------------

    public void init() {
        moveQueue = new LinkedBlockingQueue<>(1);
    }

    public void enableInput() {
        Platform.runLater(boardPresenter::enableSpaces);
    }

    public void showGame(Game game) {
        game.addObserver(this);
        initBoardPane();
        boardPresenter.initBoard(game.getBoard());
    }


    public void update(Observable o, Object arg) {

        if (o instanceof Game) {
            Platform.runLater(() -> boardPresenter.drawBoard(((Game) o).getBoard()));
        }

    }

    public void doPlayerMove(int col) {
        try {
            moveQueue.put(col);
        } catch (InterruptedException e) {
            Logger.getGlobal().throwing(getClass().toString(), "GamePresenter", e);
        }
        boardPresenter.disableSpaces();
    }

    public int getPlayerMove() {
        int move = -1;
        try {
            move = moveQueue.take();
        } catch (InterruptedException e) {
            Logger.getGlobal().throwing(getClass().toString(), "GamePresenter", e);
        }
        return move;
    }
    private void initBoardPane() {

        ClassLoader classloader = getClass().getClassLoader();


        FXMLLoader fxmlLoader = new FXMLLoader(classloader.getResource("views/board/show.fxml"));

        try {
            fxmlLoader.load();
            boardPane.getChildren().add(fxmlLoader.getRoot());
            boardPresenter = fxmlLoader.getController();
            boardPresenter.setGamePresenter(this);
        } catch (IOException e) {
            Logger.getGlobal().throwing(this.getClass().getSimpleName(), "initBoardPane", e);
        }
    }

}
