/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.presenter.game;


import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.presenter.FourCharmPresenter;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
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
        p1Select.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                showPlayerFields();
            }
        );
        p2Select.getItems().addAll("Human", "Computer");
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

    }

    public void showPlayerFields() {
        String p1Selection = p1Select.getSelectionModel().getSelectedItem();
        boolean p1Computer = p1Selection.equals("Computer");
        p1Strategy.setDisable(!p1Computer);
        p1Strategy.setVisible(p1Computer);

        p1Name.setDisable(p1Computer);
        p1Name.setVisible(!p1Computer);

        String p2Selection = p2Select.getSelectionModel().getSelectedItem();
        boolean p2Computer = p2Selection.equals("Computer");
        p2Strategy.setDisable(!p2Computer);
        p2Strategy.setVisible(p2Computer);

        p2Name.setDisable(p2Computer);
        p2Name.setVisible(!p2Computer);
        checkAbleToPlayah();

    }

    public void checkAbleToPlayah() {
        String p1Type = p1Select.getSelectionModel().getSelectedItem();
        String p2Type = p2Select.getSelectionModel().getSelectedItem();
        boolean canPlay = false;
        if (p1Type.equals("Computer")) {
            canPlay = !p1Strategy.getSelectionModel().isEmpty();
        } else {
            canPlay = !p1Name.textProperty().get().equals("");
        }
        if (canPlay) {
            if(p2Type.equals("Computer")) {
                canPlay = !p2Strategy.getSelectionModel().isEmpty();
            } else {
                canPlay = !p2Name.textProperty().get().equals("");
            }
        }
        playButton.setDisable(!canPlay);
    }

    public void startNewGame() {
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
            if(p1Type.equals("Human")) {
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
