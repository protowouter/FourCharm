/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter;

import com.lucwo.fourcharm.controller.FourCharmController;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.presenter.game.GamePresenter;
import com.lucwo.fourcharm.presenter.game.NewGamePresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

/**
 * TODO: Fourcharmpresenter javadoc.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class FourCharmPresenter {
    // ------------------ Instance variables ----------------

    @FXML
    private Parent root;
    @FXML
    private VBox contentArea;

    private NewGamePresenter newGamePresenter;
    private GamePresenter gamePresenter;
    private FourCharmController fourCharmController;

    // ----------------------- Queries ----------------------

    public Parent getView() {
        return root;
    }

    public FourCharmController getFourCharmController() {
        return fourCharmController;
    }

    // ----------------------- Commands ---------------------

    public void setFourCharmController(FourCharmController contr) {
        fourCharmController = contr;
    }


    public void setNewGamePresenter(NewGamePresenter newNewGamePresenter) {
        newGamePresenter = newNewGamePresenter;
    }

    public void setGamePresenter(GamePresenter gPresenter) {
        gamePresenter = gPresenter;
    }


    public void showNewGame() {
        Platform.runLater(() -> {
            contentArea.getChildren().retainAll(newGamePresenter.getView());
            contentArea.getChildren().add(newGamePresenter.getView());
        });
    }

    public void enableInput() {
        Platform.runLater(gamePresenter::enableInput);
    }

    public int requestMove() {
        return gamePresenter.getPlayerMove();
    }

    public void showGame(Game game) {
        gamePresenter.showGame(game);
        contentArea.getChildren().retainAll(gamePresenter.getView());
        contentArea.getChildren().add(gamePresenter.getView());
    }

    public void shutdown()  { gamePresenter.abortMove(); }

    public void enableHint() {
        Platform.runLater(gamePresenter::enableHint);
    }

    public void disableHint() {
        Platform.runLater(gamePresenter::disableHint);
    }

    public void showRematch() {
        Platform.runLater(gamePresenter::showRematch);
    }
}
