/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter;


import com.lucwo.fourcharm.controller.FourCharmController;
import com.lucwo.fourcharm.presenter.game.GamePresenter;
import com.lucwo.fourcharm.presenter.game.NewGamePresenter;
import com.lucwo.fourcharm.presenter.lobby.LobbyPresenter;
import javafx.fxml.FXMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * TODO fourcharmfactory javadoc.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class FourCharmFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(FourCharmFactory.class);

    // ------------------ Instance variables ----------------
    private FourCharmPresenter fourCharmPresenter;
    private NewGamePresenter newGamePresenter;
    private GamePresenter gamePresenter;
    private LobbyPresenter lobbyPresenter;

    // ----------------------- Commands ---------------------

    /**
     * Gives the FourCharmPresenter given a fourcharmcontroller. Throws and catches a
     * IOException if the input if wrong. Throws a RuntimeException.
     * @param controller The given FourCharmController.
     * @return The FourCharmPresenter
     */
    public FourCharmPresenter getFourCharmPresenter(FourCharmController controller) {
        if (fourCharmPresenter == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getClassLoader().getResourceAsStream("views/main.fxml"));
                fourCharmPresenter = loader.getController();
                fourCharmPresenter.setGamePresenter(getGamePresenter());
                fourCharmPresenter.setNewGamePresenter(getNewGamePresenter());
                fourCharmPresenter.setLobbyPresenter(getLobbyPresenter());
                fourCharmPresenter.setFourCharmController(controller);
                fourCharmPresenter.showNewGame();
            } catch (IOException e) {
                LOGGER.trace("getFourCharmPresenter", e);
            }
        }
        return fourCharmPresenter;
    }

    /**
     * Gives the newGamePresenter. Throws a new RuntimeException if the input if wrong.
     * @return The newGamePresenter.
     */
    public NewGamePresenter getNewGamePresenter() {
        if (newGamePresenter == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getClassLoader().getResourceAsStream("views/game/new.fxml"));
                newGamePresenter = loader.getController();
                newGamePresenter.setFourcharmPresenter(fourCharmPresenter);
                newGamePresenter.init();
            } catch (IOException e) {
                LOGGER.trace("getNewGamePresenter", e);
            }
        }
        return newGamePresenter;
    }

    /**
     * Gives the gamePresenter. Throws a RuntimeException if the input is wrong.
     * @return The GamePresenter.
     */
    public GamePresenter getGamePresenter() {
        if (gamePresenter == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getClassLoader().getResourceAsStream("views/game/show.fxml"));
                gamePresenter = loader.getController();
                gamePresenter.setFourCharmPresenter(fourCharmPresenter);
                gamePresenter.init();

            } catch (IOException e) {
                LOGGER.trace("getGamePresenter", e);
            }
        }

        return gamePresenter;
    }

    public LobbyPresenter getLobbyPresenter() {
        if (lobbyPresenter == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.load(getClass().getClassLoader().getResourceAsStream("views/lobby/show.fxml"));
                lobbyPresenter = loader.getController();
                lobbyPresenter.setFourCharmPresenter(fourCharmPresenter);
                lobbyPresenter.setGamePresenter(gamePresenter);
            } catch (IOException e) {
                LOGGER.trace("getGamePresenter", e);
            }

        }

        return lobbyPresenter;
    }

}



