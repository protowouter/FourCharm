/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.game;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.player.Mark;
import com.lucwo.fourcharm.presenter.FourCharmPresenter;
import com.lucwo.fourcharm.presenter.board.BoardPresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * TODO gamepresenter javadoc.
 */
public class GamePresenter implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GamePresenter.class);

    // ------------------ Instance variables ----------------


    @FXML
    private Parent root;
    @FXML
    private VBox boardPane;
    @FXML
    private Label currentPlayer;
    @FXML
    private Button rematch;
    @FXML
    private Button hintButton;
    @FXML
    private Button newGame;
    private BoardPresenter boardPresenter;
    private BlockingQueue<Integer> moveQueue;
    private Map<Mark, String> colorMap;
    private FourCharmPresenter fourCharmPresenter;
    private boolean waitingForMove;


    // ----------------------- Queries ----------------------

    public Parent getView() {
        return root;
    }


    // ----------------------- Commands ---------------------

    public void init() {
        VBox.setVgrow(boardPane, Priority.ALWAYS);
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
        updateGame(game);
    }


    public void update(Observable o, Object arg) {

        if (o instanceof Game) {
            Platform.runLater(() -> updateGame((Game) o));
        }

    }

    private void updateGame(Game game) {
        String color = colorMap.get(game.getCurrent().getMark());
        String currentText;
        if (game.hasFinished()) {
            if (game.hasWinner()) {
                currentText = game.getWinner().getName() + " won (" + color + ")";
            } else {
                currentText = "the game was a tie";
            }
        } else {
            currentText = game.getCurrent().toString() +
                    "'s turn (" + color + ")";
        }
        currentPlayer.textProperty().setValue(currentText);
        boardPresenter.drawBoard(game.getBoard());

    }

    public void doPlayerMove(int col) {
        try {
            moveQueue.put(col);
        } catch (InterruptedException e) {
            LOGGER.trace("doPlayerMove", e);
        }
        boardPresenter.disableSpaces();
    }

    public int getPlayerMove() {
        Integer move = null;
        waitingForMove = true;
        while (waitingForMove && move == null) {
            try {
                move = moveQueue.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.trace("getPlayerMove", e);
            }
        }

        return move == null ? -1 : move;
    }

    public void abortMove() {
        waitingForMove = false;
    }
    private void initBoardPane() {

        ClassLoader classloader = getClass().getClassLoader();


        FXMLLoader fxmlLoader = new FXMLLoader(classloader.getResource("views/board/show.fxml"));

        try {
            fxmlLoader.load();
            if (!boardPane.getChildren().isEmpty()) {
                boardPane.getChildren().remove(0);
            }
            boardPane.getChildren().add(fxmlLoader.getRoot());
            boardPresenter = fxmlLoader.getController();
            boardPresenter.setGamePresenter(this);
        } catch (IOException e) {
            LOGGER.trace("initBoardPane", e);
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

    public void enableHint() {
        hintButton.setDisable(false);
        hintButton.setVisible(true);
    }

    public void disableHint() {
        hintButton.setVisible(false);
        hintButton.setDisable(true);
    }

    public void provideHint() {
        String prevText = currentPlayer.textProperty().get();
        currentPlayer.textProperty().setValue("Calculating hint...");
        new Thread(() -> {
                int hint = fourCharmPresenter.getFourCharmController().getHint();
                Platform.runLater(() -> boardPresenter.highlightColumn(hint));
            }).start();
        currentPlayer.textProperty().setValue(prevText);
    }

    public void handleNewGame() {
        fourCharmPresenter.showNewGame();
    }

    public void handleRematch() {
        fourCharmPresenter.getFourCharmController().rematch();
    }

    public FourCharmPresenter getFourCharmPresenter() {
        return fourCharmPresenter;
    }

    public void setFourCharmPresenter(FourCharmPresenter presenter) {
        this.fourCharmPresenter = presenter;
    }
}
