/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter;

import com.lucwo.fourcharm.FourCharmController;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.presenter.game.GamePresenter;
import com.lucwo.fourcharm.presenter.game.NewGamePresenter;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class FourCharmPresenter {
    // ------------------ Instance variables ----------------

    @FXML
    private Parent root;
    @FXML
    private BorderPane contentArea;

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
        contentArea.setCenter(newGamePresenter.getView());
    }

    public void enableInput() {
    }

    public int requestMove() {
        return -1;
    }

    public void showGame(Game game) {
        gamePresenter.showGame(game);
        contentArea.setCenter(gamePresenter.getView());
    }

}
