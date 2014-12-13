/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.game;


import com.lucwo.fourcharm.model.Player;
import com.lucwo.fourcharm.presenter.FourCharmPresenter;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.util.logging.Logger;

public class NewGamePresenter {
    // ------------------ Instance variables ----------------

    @FXML
    private Parent root;
    @FXML
    private ChoiceBox<Player> p1Select;
    @FXML
    private ChoiceBox<Player> p2Select;
    @FXML
    private Button playButton;

    private FourCharmPresenter fourCharmPresenter;


    // --------------------- Constructors -------------------

    // ----------------------- Queries ----------------------

    public Parent getView() {
        Logger.getGlobal().fine("Retrieving newGameView");
        return root;
    }

    // ----------------------- Commands ---------------------

    public void startNewGame() {

        fourCharmPresenter.showGame();

    }

    public void setFourcharmPresenter(FourCharmPresenter newFourCharmPresenter) {
        fourCharmPresenter = newFourCharmPresenter;
    }

}
