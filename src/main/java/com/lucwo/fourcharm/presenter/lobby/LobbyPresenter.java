/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.lobby;

import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.presenter.FourCharmPresenter;
import com.lucwo.fourcharm.presenter.game.GamePresenter;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class LobbyPresenter {


// ------------------ Instance variables ----------------

    @FXML
    private BorderPane lobbyPane;
    @FXML
    private TextArea messageArea;
    @FXML
    private Button sendButton;
    @FXML
    private TextField chatField;
    @FXML
    private VBox gameArea;
    private FourCharmPresenter fourCharmPresenter;
    private GamePresenter gamePresenter;

// --------------------- Constructors -------------------

// ----------------------- Queries ----------------------

    public Parent getView() {
        return lobbyPane;
    }

// ----------------------- Commands ---------------------

    public void showGame(Game game) {
        gamePresenter.showGame(game);
        gameArea.getChildren().setAll(gamePresenter.getView());
    }

    public void setFourCharmPresenter(FourCharmPresenter fourCharmPresenter) {
        this.fourCharmPresenter = fourCharmPresenter;
    }

    public void setGamePresenter(GamePresenter gPresenter) {
        gamePresenter = gPresenter;
    }

    public void showMessage(String message) {
        messageArea.textProperty().setValue(messageArea.textProperty().get() + "\n" + message);
    }

    public void sendMessage() {
        fourCharmPresenter.getFourCharmController().globalChat(chatField.textProperty().getValue());
        chatField.textProperty().setValue("");
    }
}
