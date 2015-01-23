/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.controller.FourCharmController;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.presenter.FourCharmFactory;
import com.lucwo.fourcharm.presenter.FourCharmPresenter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**TODO: fourcharmgui javadoc verder uitwerken.
 * The FourCharmGUI is the Graphical User Interface of the FourCharm
 * Connect4 game. It makes use of the FourCharmController, Presenter,
 * Factory and Game class.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */

public class FourCharmGUI extends Application implements FourCharmView {

    // ---------------- Instance Variables ------------------

    private FourCharmFactory fourCharmFactory;
    private FourCharmController controller;
    private FourCharmPresenter fourCharmPresenter;


    // --------------------- Constructors -------------------

    /**
     * Constructs a FourCharmGUI with a given controller.
     * @param contr The given FourCharmController.
     */
    public FourCharmGUI(FourCharmController contr) {
        fourCharmFactory = new FourCharmFactory();
        controller = contr;
    }

    /**
     * Constructs a FourCharmGUI.
     */
    public FourCharmGUI() {

        controller = new FourCharmController();
        controller.setView(this);
        fourCharmFactory = new FourCharmFactory();

    }


    // ----------------------- Commands ---------------------

    /**
     * Starts the Graphical User Interface.
     * @param stage the JavaFX stage
     * @throws Exception All sorts of exceptions thrown by JavaFX
     */
    public void start(Stage stage) throws Exception {

        fourCharmPresenter = fourCharmFactory.getFourCharmPresenter(controller);

        Pane root = (Pane) fourCharmPresenter.getView();

        stage.setScene(new Scene(root, 1000, 1000));
        stage.setTitle("FourCharmGUI");
        stage.show();
    }

    /**
     * Stops the controller (and the GUI as well).
     */
    public void stop() {
        controller.shutdown();
    }

    @Override
    public void showGame(Game game) {
        Platform.runLater(() -> fourCharmPresenter.showGame(game));
    }

    @Override
    public void showNewGame() {
        fourCharmPresenter.showNewGame();
    }

    @Override
    public void showRematch() {
        fourCharmPresenter.showRematch();
    }

    @Override
    public void enableInput() {
        fourCharmPresenter.enableInput();
    }

    @Override
    public int requestMove() {
        return fourCharmPresenter.requestMove();
    }

    @Override
    public void showError(String errorMessage) {

    }
}




