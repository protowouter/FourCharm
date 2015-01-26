/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.controller;

import nl.woutertimmermans.connect4.protocol.parameters.LobbyState;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class LobbyList extends Observable {

// ------------------ Instance variables ----------------

    private Map<String, LobbyState> stateMap;

// --------------------- Constructors -------------------

    public LobbyList() {
        stateMap = new HashMap<>();
    }

// ----------------------- Queries ----------------------

    public Map<String, LobbyState> getLobbyState() {
        return stateMap;
    }

// ----------------------- Commands ---------------------

    public void stateChange(String playerName, LobbyState state) {
        if (state != LobbyState.OFFLINE) {
            stateMap.put(playerName, state);
        } else {
            stateMap.remove(playerName);
        }
        setChanged();
        notifyObservers(stateMap);
    }

}
