/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter;

import com.lucwo.fourcharm.controller.FourCharmController;
import com.lucwo.fourcharm.controller.LobbyList;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.presenter.game.GamePresenter;
import com.lucwo.fourcharm.presenter.game.NewGamePresenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.woutertimmermans.connect4.protocol.parameters.LobbyState;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * TODO: Fourcharmpresenter javadoc.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class FourCharmPresenter implements Observer {

    public static final String GLOBAL = "Global";
    public static final String LOCAL = "Local";
    // ------------------ Instance variables ----------------

    @FXML
    private Parent root;
    @FXML
    private BorderPane gamePane;
    @FXML
    private TextField chatField;
    @FXML
    private TextArea chatArea;
    @FXML
    private Button chatButton;
    @FXML
    private TextArea lobbyArea;
    @FXML
    private ChoiceBox<String> chatContextChoice;

    private NewGamePresenter newGamePresenter;
    private GamePresenter gamePresenter;
    private FourCharmController fourCharmController;
    private Stage stage;

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

    public void init() {
        chatContextChoice.getItems().addAll(LOCAL, GLOBAL);

    }

    public void setNewGamePresenter(NewGamePresenter newNewGamePresenter) {
        newGamePresenter = newNewGamePresenter;
    }

    public void setGamePresenter(GamePresenter gPresenter) {
        gamePresenter = gPresenter;
    }


    public void showNewGame() {
        Platform.runLater(() -> gamePane.setCenter(newGamePresenter.getView()));
    }

    public void enableInput() {
        Platform.runLater(gamePresenter::enableInput);
    }

    public int requestMove() {
        return gamePresenter.getPlayerMove();
    }

    public void showGame(Game game) {
        Platform.runLater(() -> {
            gamePresenter.showGame(game);
            gamePane.setCenter(gamePresenter.getView());
        });


    }

    public void shutdown() {
        gamePresenter.abortMove();
    }

    public void enableHint() {
        Platform.runLater(gamePresenter::enableHint);
    }

    public void disableHint() {
        Platform.runLater(gamePresenter::disableHint);
    }

    public void showRematch() {
        Platform.runLater(gamePresenter::showRematch);
    }

    public void showLobby() {
        Button ready = new Button();
        ready.setText("Ready");
        ready.setOnMouseClicked((event) -> sendReady());
        Platform.runLater(() -> gamePane.setCenter(ready));
    }

    public void sendReady() {
        gamePane.setCenter(new Label("You are ready"));
        fourCharmController.sendReady();
    }


    public void showMessage(String message) {
        Platform.runLater(() -> chatArea.textProperty().setValue(chatArea.textProperty().get() + "\n" + message));
    }

    public void sendChat() {
        String context = chatContextChoice.getSelectionModel().getSelectedItem();
        if (context.equals(LOCAL)) {
            fourCharmController.localChat(chatField.textProperty().getValue());
        } else {
            fourCharmController.globalChat(chatField.textProperty().getValue());
        }
        chatField.textProperty().setValue("");
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage s) {
        stage = s;
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof LobbyList) {
            StringBuilder players = new StringBuilder();
            for (Map.Entry<String, LobbyState> e : ((LobbyList) o).getLobbyState().entrySet()) {
                players.append(e.getKey());
                players.append(" [");
                players.append(e.getValue());
                players.append("]");
                players.append("\n");
            }
            Platform.runLater(() -> lobbyArea.textProperty().setValue(new String(players)));
        }
    }
}
