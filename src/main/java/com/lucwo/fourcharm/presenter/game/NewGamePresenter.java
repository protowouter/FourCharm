/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.game;


import com.lucwo.fourcharm.exception.ServerConnectionException;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.presenter.FourCharmPresenter;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.logging.Logger;

public class NewGamePresenter {
    // ------------------ Instance variables ----------------

    @FXML
    private Parent root;
    @FXML
    private ChoiceBox<String> p1Select;
    @FXML
    private ChoiceBox<String> p2Select;
    @FXML
    private Button playButton;
    @FXML
    private ChoiceBox<GameStrategy> p1Strategy;
    @FXML
    private ChoiceBox<GameStrategy> p2Strategy;
    @FXML
    private TextField p1Name;
    @FXML
    private TextField p2Name;
    @FXML
    private ChoiceBox<String> localNetworkChoice;
    @FXML
    private TextField serverAddress;
    @FXML
    private TextField serverPort;
    @FXML
    private Label player2Label;


    private FourCharmPresenter fourCharmPresenter;


    // --------------------- Constructors -------------------

    // ----------------------- Queries ----------------------

    public Parent getView() {
        Logger.getGlobal().fine("Retrieving newGameView");
        return root;
    }

    // ----------------------- Commands ---------------------

    public void init() {
        p1Select.getItems().addAll("Human", "Computer");
        p1Select.getSelectionModel().select(1);
        p1Select.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    showPlayerFields();
                }
        );
        p2Select.getItems().addAll("Human", "Computer");
        p2Select.getSelectionModel().select(1);
        p2Select.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        showPlayerFields();
                    }
        );
        p1Strategy.getItems().addAll(new MTDfStrategy(), new RandomStrategy());
        p2Strategy.getItems().addAll(new MTDfStrategy(), new RandomStrategy());

        ChangeListener<GameStrategy> playahlistener = (observableValue, oldValue, newValue) -> checkAbleToPlayah();
        p1Strategy.getSelectionModel().selectedItemProperty().addListener(playahlistener);
        p2Strategy.getSelectionModel().selectedItemProperty().addListener(playahlistener);


        ChangeListener<String> stringPlayahListener = (observableValue, oldValue, newValue) -> checkAbleToPlayah();
        p1Name.textProperty().addListener(stringPlayahListener);
        p2Name.textProperty().addListener(stringPlayahListener);
        serverAddress.textProperty().addListener(stringPlayahListener);
        serverPort.textProperty().addListener(stringPlayahListener);

        localNetworkChoice.getItems().addAll("Local", "Network");
        localNetworkChoice.getSelectionModel().select(1);
        localNetworkChoice.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue, newValue) -> localNetworkChoice());
        localNetworkChoice();
        showPlayerFields();

    }

    public void localNetworkChoice() {
        String choice = localNetworkChoice.getSelectionModel().getSelectedItem();
        if (choice.equals("Network")) {
            enableNetworkFields(true);
            disablePlayer2Fields();
        } else {
            enableNetworkFields(false);
            enablePlayer2Fields();
        }

    }

    private void enablePlayer2Fields() {
        player2Label.setVisible(true);
        p2Select.setDisable(false);
        p2Select.setVisible(true);
        showPlayerFields();
    }

    private void disablePlayer2Fields() {
        player2Label.setVisible(false);
        p2Select.setDisable(true);
        p2Select.setVisible(false);
        p2Name.setDisable(true);
        p2Name.setVisible(false);
        p2Select.setVisible(false);
        p2Strategy.setDisable(true);
        p2Strategy.setVisible(false);
    }

    private void enableNetworkFields(boolean enabled) {
        serverAddress.setDisable(!enabled);
        serverAddress.setVisible(enabled);
        serverPort.setDisable(!enabled);
        serverPort.setVisible(enabled);
    }

    public void showPlayerFields() {
        String p1Selection = p1Select.getSelectionModel().getSelectedItem();
        enbaleP1Fields(p1Selection.equals("Computer"));

        String p2Selection = p2Select.getSelectionModel().getSelectedItem();
        enableP2Fields(p2Selection.equals("Computer"));
        checkAbleToPlayah();

    }

    private void enableP2Fields(boolean computer) {
        boolean localGame = localNetworkChoice.getSelectionModel().getSelectedItem().equals("Local");
        if (localGame) {
            p2Strategy.setDisable(!computer);
            p2Strategy.setVisible(computer);

            p2Name.setDisable(computer);
            p2Name.setVisible(!computer);
        }

    }

    private void enbaleP1Fields(boolean enabled) {
        p1Strategy.setDisable(!enabled);
        p1Strategy.setVisible(enabled);

        p1Name.setDisable(enabled);
        p1Name.setVisible(!enabled);
    }

    public void checkAbleToPlayah() {
        String p1Type = p1Select.getSelectionModel().getSelectedItem();
        String p2Type = p2Select.getSelectionModel().getSelectedItem();
        boolean canPlay = false;
        if (localNetworkChoice.getSelectionModel().getSelectedItem().equals("Local")) {
            if (p1Type.equals("Computer")) {
                canPlay = !p1Strategy.getSelectionModel().isEmpty();
            } else {
                canPlay = !p1Name.textProperty().get().equals("");
            }
            if (canPlay) {
                if (p2Type.equals("Computer")) {
                    canPlay = !p2Strategy.getSelectionModel().isEmpty();
                } else {
                    canPlay = !p2Name.textProperty().get().equals("");
                }
            }
        } else {
            if (p1Type.equals("Computer")) {
                canPlay = !p1Strategy.getSelectionModel().isEmpty();
            } else {
                canPlay = !p1Name.textProperty().get().equals("");
            }
            if (canPlay) {
                canPlay = !serverAddress.textProperty().get().equals("");
                if (canPlay) {
                    try {
                        Integer.parseInt(serverPort.textProperty().get());
                    } catch (NumberFormatException e) {
                        canPlay = false;
                    }

                }

            }
        }

        playButton.setDisable(!canPlay);
    }

    public void startNewGame() {
        if ("Local".equals(localNetworkChoice.getSelectionModel().getSelectedItem())) {
            startLocalGame();
        } else {
            startNetworkGame();
        }
    }

    private void startNetworkGame() {
        String p1Type = p1Select.getSelectionModel().getSelectedItem();
        GameStrategy strategy = null;
        String name;
        String host = serverAddress.textProperty().get();
        String port = serverPort.textProperty().get();
        if ("Computer".equals(p1Type)) {
            strategy = p1Strategy.getSelectionModel().getSelectedItem();
            name = strategy.toString();
        } else {
            name = p1Name.textProperty().get();
        }

        try {
            fourCharmPresenter.getFourCharmController().startNetworkGame(host, port, name, strategy);
        } catch (ServerConnectionException e) {
            // TODO: Show Error message
        }
    }

    private void startLocalGame() {

        String p1Type = p1Select.getSelectionModel().getSelectedItem();
        String p2Type = p2Select.getSelectionModel().getSelectedItem();
        String[] pNames;
        GameStrategy[] pStrategies;

        if (p1Type.equals("Human") && p2Type.equals("Human")) {
            pNames = new String[]{p1Name.textProperty().get(), p2Name.textProperty().get()};
            pStrategies = new GameStrategy[0];
        } else if (p1Type.equals("Computer") && p2Type.equals("Computer")) {
            pNames = new String[0];
            pStrategies = new GameStrategy[]{p1Strategy.getSelectionModel().getSelectedItem(),
                    p2Strategy.getSelectionModel().getSelectedItem()};
        } else {
            if (p1Type.equals("Human")) {
                pNames = new String[]{p1Name.textProperty().get()};
                pStrategies = new GameStrategy[]{p2Strategy.getSelectionModel().getSelectedItem()};
            } else {
                pNames = new String[]{p2Name.textProperty().get()};
                pStrategies = new GameStrategy[]{p1Strategy.getSelectionModel().getSelectedItem()};
            }
        }

        fourCharmPresenter.getFourCharmController().startLocalGame(pNames, pStrategies);

    }

    public void setFourcharmPresenter(FourCharmPresenter newFourCharmPresenter) {
        fourCharmPresenter = newFourCharmPresenter;
    }

}
