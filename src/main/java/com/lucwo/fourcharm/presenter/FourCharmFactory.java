/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter;


import com.lucwo.fourcharm.FourCharmController;
import com.lucwo.fourcharm.presenter.game.GamePresenter;
import com.lucwo.fourcharm.presenter.game.NewGamePresenter;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class FourCharmFactory {

    // ------------------ Instance variables ----------------
    private FourCharmPresenter fourCharmPresenter;
    private NewGamePresenter newGamePresenter;
    private GamePresenter gamePresenter;

    // ----------------------- Commands ---------------------

    public FourCharmPresenter getFourCharmPresenter(FourCharmController controller) {
        if (fourCharmPresenter == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getClassLoader().getResourceAsStream("views/main.fxml"));
                fourCharmPresenter = loader.getController();
                fourCharmPresenter.setGamePresenter(getGamePresenter());
                fourCharmPresenter.setNewGamePresenter(getNewGamePresenter());
                fourCharmPresenter.setFourCharmController(controller);
                fourCharmPresenter.showNewGame();
            } catch (IOException e) {
                throw new RuntimeException("Unable to load main.fxml", e);
            }
        }
        return fourCharmPresenter;
    }

    public NewGamePresenter getNewGamePresenter() {
        if (newGamePresenter == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getClassLoader().getResourceAsStream("views/game/new.fxml"));
                newGamePresenter = loader.getController();
                newGamePresenter.setFourcharmPresenter(fourCharmPresenter);
                newGamePresenter.init();
            } catch (IOException e) {
                throw new RuntimeException("Unable to load new.fxml", e);
            }
        }
        return newGamePresenter;
    }

    public GamePresenter getGamePresenter() {
        if (gamePresenter == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getClassLoader().getResourceAsStream("views/game/show.fxml"));
                gamePresenter = loader.getController();
                gamePresenter.setFourCharmPresenter(fourCharmPresenter);
                gamePresenter.init();

            } catch (IOException e) {
                throw new RuntimeException("Unable to load show.fxml");
            }
        }

        return gamePresenter;
    }

}



