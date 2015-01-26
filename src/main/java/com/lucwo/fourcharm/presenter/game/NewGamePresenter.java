/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.game;


import com.lucwo.fourcharm.exception.ServerConnectionException;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
import com.lucwo.fourcharm.model.ai.NegaMaxStrategy;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.presenter.FourCharmPresenter;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO newgamepresenter javadoc.
 */
public class NewGamePresenter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewGamePresenter.class);
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
        LOGGER.debug("Retrieving newGameView");
        return root;
    }

    // ----------------------- Commands ---------------------

    public void init() {
        p1Select.getItems().addAll("Human", "Computer");
        p1Select.getSelectionModel().select(1);
        p1Select.getSelectionModel().selectedItemProperty().addListener(
            (observable) -> {
                showPlayerFields();
            }
        );
        p2Select.getItems().addAll("Human", "Computer");
        p2Select.getSelectionModel().select(1);
        p2Select.getSelectionModel().selectedItemProperty().addListener(
            (observable) -> {
                showPlayerFields();
            }
        );
        p1Strategy.getItems().addAll(new MTDfStrategy(),
                new RandomStrategy(), new NegaMaxStrategy(12));
        p2Strategy.getItems().addAll(new MTDfStrategy(),
                new RandomStrategy(), new NegaMaxStrategy(12));

        InvalidationListener playahlistener = (observable) -> checkAbleToPlayah();
        p1Strategy.getSelectionModel().selectedItemProperty().addListener(playahlistener);
        p2Strategy.getSelectionModel().selectedItemProperty().addListener(playahlistener);


        InvalidationListener stringPlayahListener = (observable) -> checkAbleToPlayah();
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
        if ("Network".equals(choice)) {
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
        enableP1Fields("Computer".equals(p1Selection));

        String p2Selection = p2Select.getSelectionModel().getSelectedItem();
        enableP2Fields("Computer".equals(p2Selection));
        checkAbleToPlayah();

    }

    private void enableP2Fields(boolean computer) {
        boolean localGame = "Local".equals(localNetworkChoice.getSelectionModel().
                getSelectedItem());
        if (localGame) {
            p2Strategy.setDisable(!computer);
            p2Strategy.setVisible(computer);

            p2Name.setDisable(computer);
            p2Name.setVisible(!computer);
        }

    }

    private void enableP1Fields(boolean computer) {
        boolean netWorkGame = "Network".equals(localNetworkChoice.getSelectionModel().
                getSelectedItem());
        p1Strategy.setDisable(!computer);
        p1Strategy.setVisible(computer);

        p1Name.setDisable(computer && !netWorkGame);
        p1Name.setVisible(!computer || netWorkGame);
    }

    public void checkAbleToPlayah() {
        String p1Type = p1Select.getSelectionModel().getSelectedItem();
        String p2Type = p2Select.getSelectionModel().getSelectedItem();
        boolean canPlay;
        if ("Local".equals(localNetworkChoice.getSelectionModel().getSelectedItem())) {
            canPlay = checkLocalGame(p1Type, p2Type);
        } else {
            canPlay = checkNetworkGame(p1Type);
        }

        playButton.setDisable(!canPlay);
    }

    private boolean checkNetworkGame(String p1Type) {
        boolean canPlay;
        canPlay = !"".equals(p1Name.textProperty().get());
        if (canPlay && "Computer".equals(p1Type)) {
            canPlay = !p1Strategy.getSelectionModel().isEmpty();
        }
        if (canPlay) {
            canPlay = !"".equals(serverAddress.textProperty().get());
            if (canPlay) {
                try {
                    Integer.parseInt(serverPort.textProperty().get());
                } catch (NumberFormatException e) {
                    canPlay = false;
                }

            }

        }
        return canPlay;
    }

    private boolean checkLocalGame(String p1Type, String p2Type) {
        boolean canPlay;
        if ("Computer".equals(p1Type)) {
            canPlay = !p1Strategy.getSelectionModel().isEmpty();
        } else {
            canPlay = !"".equals(p1Name.textProperty().get());
        }
        if (canPlay) {
            if ("Computer".equals(p2Type)) {
                canPlay = !p2Strategy.getSelectionModel().isEmpty();
            } else {
                canPlay = !"".equals(p2Name.textProperty().get());
            }
        }
        return canPlay;
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
        }
        name = p1Name.textProperty().get();

        try {
            fourCharmPresenter.getFourCharmController().
                    startNetworkGame(host, port, name, strategy);
        } catch (ServerConnectionException e) {
            LOGGER.trace("startNetworkGame", e);
            fourCharmPresenter.getFourCharmController().showError(e.getMessage());
        }
    }

    private void startLocalGame() {

        String p1Type = p1Select.getSelectionModel().getSelectedItem();
        String p2Type = p2Select.getSelectionModel().getSelectedItem();
        String player1Name = null;
        String player2Name = null;
        GameStrategy p1Strat = null;
        GameStrategy p2Strat = null;

        if ("Human".equals(p1Type)) {
            player1Name = p1Name.textProperty().get();
        } else {
            p1Strat = p1Strategy.getSelectionModel().getSelectedItem();
        }
        if ("Human".equals(p2Type)) {
            player2Name = p2Name.textProperty().get();
        } else {
            p2Strat = p2Strategy.getSelectionModel().getSelectedItem();
        }

        fourCharmPresenter.getFourCharmController().
                startLocalGame(player1Name, player2Name, p1Strat, p2Strat);

    }

    public void setFourcharmPresenter(FourCharmPresenter newFourCharmPresenter) {
        fourCharmPresenter = newFourCharmPresenter;
    }

}
