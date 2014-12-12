/*
 * Copyright (c) 2014. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import com.lucwo.fourcharm.model.Mark;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Created by woutertimmermans on 12-12-14.
 */




public class SpaceController {

    // ------------------ Instance variables ----------------

    @FXML
    private Button spaceButton;

    // --------------------- Constructors -------------------

    // ----------------------- Queries ----------------------

    // ----------------------- Commands ---------------------

    public void setMark(Mark m) {
        spaceButton.textProperty().setValue(m.toString());
    }

    @FXML
    protected void processClick() {

    }
}
