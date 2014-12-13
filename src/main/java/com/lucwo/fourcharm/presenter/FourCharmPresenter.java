/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter;

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

    // ----------------------- Queries ----------------------

    public Parent getView() {
        return root;
    }

    // ----------------------- Commands ---------------------


    public void setNewGamePresenter(NewGamePresenter newNewGamePresenter) {
        newGamePresenter = newNewGamePresenter;
    }

    public void setGamePresenter(GamePresenter gPresenter) {
        gamePresenter = gPresenter;
    }


    public void showNewGame() {
        contentArea.setCenter(newGamePresenter.getView());
    }

    public void showGame() {
        gamePresenter.showGame();
        gamePresenter.startGame();
        contentArea.setCenter(gamePresenter.getView());
    }

}
