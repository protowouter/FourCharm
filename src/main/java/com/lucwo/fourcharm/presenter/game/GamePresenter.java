/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.game;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.Mark;
import com.lucwo.fourcharm.presenter.FourCharmPresenter;
import com.lucwo.fourcharm.presenter.board.BoardPresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;


public class GamePresenter implements Observer {

    // ------------------ Instance variables ----------------


    @FXML
    private Parent root;
    @FXML
    private FlowPane boardPane;
    @FXML
    private Label currentPlayer;
    @FXML
    private Button rematch;
    @FXML
    private Button newGame;
    private BoardPresenter boardPresenter;
    private BlockingQueue<Integer> moveQueue;
    private Map<Mark, String> colorMap;
    private FourCharmPresenter fourCharmPresenter;


    // ----------------------- Queries ----------------------

    public Parent getView() {
        return root;
    }


    // ----------------------- Commands ---------------------

    public void init() {
        colorMap = new HashMap<>();
        colorMap.put(Mark.P1, "RoseRoze");
        colorMap.put(Mark.P2, "Babyblauw");
        moveQueue = new LinkedBlockingQueue<>(1);
        initBoardPane();
    }

    public void enableInput() {
        Platform.runLater(boardPresenter::enableSpaces);
    }

    public void showGame(Game game) {
        hideRematch();
        initBoardPane();
        game.addObserver(this);
        boardPresenter.initBoard(game.getBoard());
    }


    public void update(Observable o, Object arg) {

        if (o instanceof Game) {
            Platform.runLater(() -> updateGame((Game) o));
        }

    }

    private void updateGame(Game game) {
        String color = colorMap.get(game.getCurrent().getMark());
        currentPlayer.textProperty().setValue(game.getCurrent().toString() + "'s turn (" + color + ")");
        boardPresenter.drawBoard(game.getBoard());
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
            if (boardPane.getChildren().size() > 0) {
                boardPane.getChildren().remove(0);
            }
            boardPane.getChildren().add(fxmlLoader.getRoot());
            boardPresenter = fxmlLoader.getController();
            boardPresenter.setGamePresenter(this);
        } catch (IOException e) {
            Logger.getGlobal().throwing(this.getClass().getSimpleName(), "initBoardPane", e);
        }
    }

    public void showRematch() {
        rematch.setDisable(false);
        newGame.setDisable(false);
        rematch.setVisible(true);
        newGame.setVisible(true);
    }

    public void hideRematch() {
        rematch.setDisable(true);
        newGame.setDisable(true);
        rematch.setVisible(false);
        newGame.setVisible(false);
    }

    public void handleNewGame() {
        fourCharmPresenter.showNewGame();
    }

    public void handleRematch() {
        fourCharmPresenter.getFourCharmController().rematch();
    }

    public void setFourCharmPresenter(FourCharmPresenter presenter) {
        this.fourCharmPresenter = presenter;
    }
}
