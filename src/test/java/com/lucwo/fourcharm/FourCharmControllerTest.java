/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm;

import com.lucwo.fourcharm.client.ServerHandler;
import com.lucwo.fourcharm.controller.FourCharmController;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;
import com.lucwo.fourcharm.model.player.LocalAIPlayer;
import com.lucwo.fourcharm.model.player.LocalHumanPlayer;
import com.lucwo.fourcharm.model.player.Mark;
import com.lucwo.fourcharm.view.FourCharmTUI;
import com.lucwo.fourcharm.view.FourCharmView;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FourCharmControllerTest {

    private FourCharmController controller;
    @Mocked
    private FourCharmView view;
    @Mocked
    private FourCharmView view2;
    @Mocked
    private Game game;

    @Before
    public void setUp() throws Exception {

        controller = new FourCharmController();
        controller.setView(view);

    }

    @Test
    public void testSetView() throws Exception {

        controller.setView(view2);
        assertEquals("setview should set the view of the controller", view2, controller.getView());

    }

    @Test
    public void testStartNetworkGame(@Mocked ServerHandler client) throws Exception {

        final GameStrategy p1Strat = new RandomStrategy();

        new Expectations() {
            {
                ServerHandler client = new ServerHandler("mallePietje", "localhost", "1335", controller);
                client.setStrategy(p1Strat);
            }
        };

        controller.startNetworkGame("localhost", "1335", "mallePietje", p1Strat);


    }

    @Test
    public void testStartLocalGameHumanPlayers(@Mocked LocalHumanPlayer anyPlayer) throws Exception {

        new Expectations() {
            {
                LocalHumanPlayer p1 = new LocalHumanPlayer("Frits", Mark.P1);
                LocalHumanPlayer p2 = new LocalHumanPlayer("Wester", Mark.P2);
                new Game(BinaryBoard.class, p1, p2);
            }
        };

        controller.startLocalGame("Frits", "Wester", null, null);
    }

    @Test
    public void testStartLocalGameAIPlayers(@Mocked final LocalAIPlayer anyPlayer) throws Exception {
        final GameStrategy p1Strat = new RandomStrategy();
        final GameStrategy p2Strat = new RandomStrategy();

        new Expectations() {
            {
                LocalAIPlayer p1 = new LocalAIPlayer(p1Strat, Mark.P1);
                LocalAIPlayer p2 = new LocalAIPlayer(p2Strat, Mark.P2);
                new Game(BinaryBoard.class, p1, p2);
            }
        };

        controller.startLocalGame(null, null, p1Strat, p2Strat);
    }

    @Test
    public void testSetGame() throws Exception {

        new Expectations() {
            {
                view.showGame(game);
                game.addObserver(controller);
            }
        };
        controller.setGame(game);

    }

    @Test
    public void testGetHumanPlayerMove() throws Exception {

        new Expectations() {
            {
                view.requestMove();
                result = 3;
            }
        };
        assertEquals("getHumanPlayerMove should ask the view for a move",
                3, controller.getHumanPlayerMove());
        controller.getHumanPlayerMove();

    }

    @Test
    public void testUpdateHumanPlayer() throws Exception {
        final LocalHumanPlayer p1 = new LocalHumanPlayer("Henkie", Mark.P1);
        final LocalAIPlayer p2 = new LocalAIPlayer(new RandomStrategy(), Mark.P2);
        final Game game = new Game(BinaryBoard.class, p1, p2);

        controller.setGame(game);

        new Expectations() {
            {
                game.getCurrent(); result = p1;
                view.enableInput();
                view.requestMove(); result = 2;
                game.getBoard().getColumns(); result = 6;
                game.getBoard().columnHasFreeSpace(anyInt); result = true;
            }
        };

        controller.update(game, null);

    }

    @Test
    public void testUpdateAIPlayer() throws Exception {
        final LocalAIPlayer p1 = new LocalAIPlayer(new RandomStrategy(), Mark.P1);

        new Expectations() {
            {
                game.getCurrent();
                result = p1;
            }
        };
        controller.update(game, null);
    }

    @Test
    public void testUpdateFinishedGame() throws Exception {

        new Expectations() {{
            game.hasFinished();
            result = true;
            view.showRematch();
        }};

        controller.update(game, null);
    }

    @Test
    public void testRematch() throws Exception {

        new Expectations() {{
            new Game(BinaryBoard.class, null, null);
        }};

        controller.rematch();

    }

    @Test
    public void testShutdownClient(@Mocked ServerHandler anyHandler) throws Exception {

        new Expectations() {{
            anyHandler.disconnect();
        }};

        controller.startNetworkGame("localhost", "8080", "pietje", new RandomStrategy());
        controller.shutdown();
    }

    @Test
    public void testShutdownGame() throws Exception {

        new Expectations() {{
            game.shutdown();
        }};

        controller.setGame(game);
        controller.shutdown();
    }

    @Test
    public void testMain() throws Exception {

        new Expectations() {{
            new FourCharmTUI(controller);
        }};

        FourCharmController.main(new String[]{"-c"});
    }

    @Test
    public void testShowError() throws Exception {

        new Expectations() {
            {
                view.showError("Hello, this is error");
            }
        };
        controller.showError("Hello, this is error");

    }
}