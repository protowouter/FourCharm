/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter;

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

    // ----------------------- Queries ----------------------

    public Parent getView() {
        return root;
    }

    // ----------------------- Commands ---------------------


    public void setNewGamePresenter(NewGamePresenter newNewGamePresenter) {
        newGamePresenter = newNewGamePresenter;
    }

    public void setGamePresenter(GamePresenter newGamePresenter) {
        gamePresenter = newGamePresenter;
    }


    public void showNewGame() {
        contentArea.setCenter(newGamePresenter.getView());
    }

    public void showGame(Game game) {
        gamePresenter.setGame(game);
        contentArea.setCenter(gamePresenter.getView());
    }

}
