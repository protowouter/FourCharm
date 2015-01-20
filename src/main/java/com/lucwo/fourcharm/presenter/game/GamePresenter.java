/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.game;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.LocalHumanPlayer;
import com.lucwo.fourcharm.presenter.board.BoardPresenter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;


public class GamePresenter implements Observer {

    // ------------------ Instance variables ----------------


    private LocalHumanPlayer currentPlayer;
    @FXML
    private Parent root;
    @FXML
    private FlowPane boardPane;

    private BoardPresenter boardPresenter;


    // ----------------------- Queries ----------------------

    public Parent getView() {
        return root;
    }


    // ----------------------- Commands ---------------------

    public void enableInput() {
        boardPresenter.enableSpaces();
    }

    public void showGame(Game game) {
        game.addObserver(this);
        initBoardPane();
    }


    public void update(Observable o, Object arg) {

        if (o instanceof Game) {
            boardPresenter.initBoard(((Game) o).getBoard());
        }

    }

    public void doPlayerMove(int col) {
        currentPlayer.queueMove(col);
        currentPlayer = null;
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
