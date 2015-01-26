/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.controller.C4Server;
import com.lucwo.fourcharm.controller.FourCharmController;
import com.lucwo.fourcharm.exception.ServerConnectionException;
import com.lucwo.fourcharm.model.Game;
import com.lucwo.fourcharm.model.ai.GameStrategy;
import com.lucwo.fourcharm.model.ai.MTDfStrategy;
import com.lucwo.fourcharm.model.ai.RandomStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The FourCharmTUI is the Textual User Interface of the FourCharm
 * Connect4 game. It makes use of the FourCharmController, and Game class,
 * as well as the AI strategies. The TUI takes care of communication with
 * a user and uses the controller for communication with the rest of the system.
 *
 * @author Luce Sandfort and Wouter Timmermans
 */
public class FourCharmTUI implements FourCharmView, Observer, Runnable {

    private static final String NOT_IMPLEMENTED = "Not yet implemented";
    private static final Logger LOGGER = LoggerFactory.getLogger(FourCharmTUI.class);


    // ------------------ Instance variables ----------------

    private Scanner inputScanner;
    private boolean running;
    private boolean gameOn;
    private FourCharmController controller;
    private boolean moveNeeded;
    private LinkedBlockingQueue<Integer> moveQueue;
    private boolean hintEnabled;
    private boolean waitingForMove;

    // --------------------- Constructors -------------------

    /**
     * Constructs a new FourCharmTUI given a controller.
     *
     * @param cont The given FourCharmController.
     */
    public FourCharmTUI(FourCharmController cont) {

        inputScanner = new Scanner(System.in, "UTF-8");
        gameOn = false;
        running = true;
        controller = cont;
        moveNeeded = false;
        moveQueue = new LinkedBlockingQueue<>(1);
        hintEnabled = false;
    }

    // ----------------------- Commands ---------------------

    /**
     * Reads the command and places it in an array.
     *
     * @param commandString the command in string format.
     */
    private void parseCommand(String commandString) {
        Scanner commandScanner = new Scanner(commandString);
        String command;
        List<String> args = new ArrayList<>();

        if (commandScanner.hasNext()) {
            command = commandScanner.next();
            while (commandScanner.hasNext()) {
                args.add(commandScanner.next());
            }
            checkCommand(command, args.toArray(new String[args.size()]));
        }
        commandScanner.close();

    }

    /**
     * Checks if the given command is valid.
     *
     * @param commandString the command in string format.
     * @param args          the amount of commands.
     */
    private void checkCommand(String commandString, String[] args) {
        Command command = Command.parseString(commandString);
        if (command != null) {
            if (command == Command.CHAT) {
                handleGlobalChat(args);
            } else if (args.length == command.argCount()) {
                executeCommand(command, args);
            } else {
                showError("The command " + command.toString()
                        + " requires " + command.argCount()
                        + " parameters. You gave " + args.length + ".");
            }
        } else {
            // Maybe it's a move?
            if (moveNeeded) {
                try {
                    int move = Integer.parseInt(commandString) - 1;
                    moveQueue.put(move);
                    moveNeeded = false;
                } catch (NumberFormatException | InterruptedException e) {
                    LOGGER.trace("checkCommand", e);
                    showError("This command has no power here!");
                }
            } else {
                showError("Be patient your grasshopper, not yet your turn is");
            }
        }
    }


    /**
     * Executes the given command.
     *
     * @param command the given command
     * @param args    string array of arguments
     */
    private void executeCommand(Command command, String[] args) {
        switch (command) {
            case CONNECT:
                //Connect to a server
                connect(args);
                break;
            case LOCAL:
                //Play a local game
                createLocalGame(args);
                break;
            case HINT:
                //Ask for a hint in the current game.
                handleHint();
                break;
            case READY:
                //Send ready to the server
                controller.sendReady();
                break;
            case DISCONNECT:
                controller.disconnect();
                break;
            case CHALLENGE:
                //Challenge another player to play a game
                showError(NOT_IMPLEMENTED);
                break;
            case LIST_PLAYERS:
                //List all of the players in the lobby
                showError(NOT_IMPLEMENTED);
                break;
            case HELP:
                //List all the available commands
                showHelp();
                break;
            case EXIT:
                //Exit the application
                running = false;
                break;
            default:
                showError("Command not recognized");
                break;
        }
    }

    private void handleGlobalChat(String[] args) {
        StringBuilder mes = new StringBuilder();
        for (String w : args) {
            mes.append(w);
        }
        controller.globalChat(new String(mes));

    }


    /**
     * Shows the given message.
     *
     * @param message the given message
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Creates a local game.
     *
     * @param args The arguments given to play a game (for instance: CHAT, CHALLENGE, etc.).
     */
    private void createLocalGame(String[] args) {
        if (!gameOn) {
            GameStrategy p1Strat = parseStrategy(args[0]);
            GameStrategy p2Strat = parseStrategy(args[1]);
            if (p1Strat == null && p2Strat == null) {
                controller.startLocalGame(args[0], args[1], null, null);
            } else if (p1Strat == null) {
                controller.startLocalGame(args[0], null, null, p2Strat);
            } else if (p2Strat == null) {
                controller.startLocalGame(null, args[1], p1Strat, null);
            } else {
                controller.startLocalGame(null, null, p1Strat, p2Strat);
            }
        } else {
            showError("Currently playing a game!");
        }

    }

    /**
     * Parses the input string to a strategy.
     *
     * @param strat The input String strategy (-m, -r).
     * @return The strategy.
     */
    private GameStrategy parseStrategy(String strat) {
        GameStrategy strategy = null;
        if ("-m".equals(strat)) {
            strategy = new MTDfStrategy();
        } else if ("-r".equals(strat)) {
            strategy = new RandomStrategy();
        }
        return strategy;
    }

    /**
     * Connects a networkgame.
     *
     * @param args The arguments given to play a game (for instance: CHAT, CHALLENGE, etc.).
     */
    private void connect(String[] args) {
        String host = args[0];
        String port = args[1];
        String playerName = args[2];
        GameStrategy strat = parseStrategy(args[3]);
        try {
            controller.startNetworkGame(host, port, playerName, strat);
        } catch (ServerConnectionException e) {
            LOGGER.trace("connect", e);
            showError(e.getMessage());
        }

    }

    /**
     * Shows all of the available commands.
     */
    private void showHelp() {
        showMessage("These commands I know: ");
        for (Command c : Command.values()) {
            showMessage(c.toString());
        }
    }

    /**
     * Shows the (error) message.
     *
     * @param message the (error) message
     */
    public void showError(String message) {
        System.err.println(message);
    }

    @Override
    public void showChat(String playerName, String message) {
        showMessage("[" + playerName + "] " + message);
    }

    @Override
    public void updateServers(Collection<C4Server> serverList) {
        for (C4Server c : serverList) {
            showMessage(c.getAddress() + ":" + c.getPort());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        LOGGER.debug("Tui is getting message from: {}" + o);
        if (o instanceof Game) {
            gameOn = true;
            Game newGame = (Game) o;
            System.out.println(newGame.getBoard().toString());
            showMessage(((Game) o).getCurrent().getName() + "'s turn");
            if (newGame.hasFinished()) {
                showMessage("The game has finished. ");
                gameOn = false;
                if (newGame.hasWinner()) {
                    showMessage("The winner is " + newGame.getWinner());
                }
            }
        }

    }

    /**
     * Runs the game.
     */
    public void run() {
        showMessage("Welcome to FourCharm Connect4.");
        parseCommands();
    }

    /**
     * Parses the commands.
     */
    private void parseCommands() {
        showPrompt();
        while (running && inputScanner.hasNextLine()) {
            parseCommand(inputScanner.nextLine());
            showPrompt();
        }
        abortMove();
        controller.shutdown();
    }

    /**
     * Show a prompt message at the beginning of each sentence.
     */
    private void showPrompt() {
        System.out.print("FourCharm$ ");
    }


    @Override
    public void showGame(Game game) {
        game.addObserver(this);
    }

    @Override
    public void showNewGame() {
        // Not needed in this TUI
    }

    @Override
    public void showLobby() {
        // TODO Print message of all users.
    }

    @Override
    public void showRematch() {
        //TODO implement.
    }

    @Override
    public void enableInput() {
        moveNeeded = true;
    }

    /**
     * Enable the hint functionality.
     * This must only be done if it is the current turn of a human.
     */
    @Override
    public void enableHint() {
        hintEnabled = true;
    }

    /**
     * Disables the hint functionality.
     */
    @Override
    public void disableHint() {
        hintEnabled = false;
    }

    private void handleHint() {
        if (hintEnabled) {
            // Increment the move because the result is zero based, and the UI is one based.
            showMessage("Calculating, please be patient...");
            showMessage("Best move: " + (controller.getHint() + 1));
        } else {
            showError("The current player is not allowed to use a hint");
        }
    }

    @Override
    public int requestMove() {
        showMessage("Enter a move (1-7)");
        showPrompt();
        Integer move = null;
        waitingForMove = true;
        while (waitingForMove && move == null) {
            try {
                move = moveQueue.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.trace("requestMove", e);
            }
        }

        return move == null ? -1 : move;
    }

    private void abortMove() {
        waitingForMove = false;
    }

    /**
     * Enum class that 'holds' the commands of the Connect4 game.
     */
    private enum Command {
        CHAT("Chatmessage"),
        CONNECT("Host", "Port", "Playername", "| -m (MTDF) | -r (Random) | -h (Human)"),
        LOCAL("Playername | -m (MTDF) | -r (Random)", "Playername | -m (MTDF) | -r (Random)"),
        HINT(),
        READY(),
        DISCONNECT(),
        EXIT(),
        CHALLENGE("Player name"),
        HELP(),
        LIST_PLAYERS();


        String[] parameterNames;

        Command(String... cNames) {
            parameterNames = cNames;
        }

        /**
         * @param cString String that will be parsed to a {@link Command}
         * @return The command enum that is parsed from the input String.
         * {@code null} if the command can't be parsed.
         */
        public static Command parseString(String cString) {
            for (Command c : Command.values()) {
                if (c.name().equalsIgnoreCase(cString)) {
                    return c;
                }

            }
            return null;
        }

        /**
         * @return an integer with the total amount of arguments.
         */
        public int argCount() {
            return parameterNames.length;
        }

        /**
         * @return the Description of the arguments of this command.
         */
        public String argDesc() {
            String desc = "";
            for (String arg : parameterNames) {
                desc += "[" + arg + "]";
            }

            return desc;
        }

        /**
         * @return the string representation of this command.
         */
        @Override
        public String toString() {
            return name() + " " + argDesc();
        }

    }
}
