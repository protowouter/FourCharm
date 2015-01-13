/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.model.*;
import com.lucwo.fourcharm.model.ai.NegaMaxStrategy;
import com.lucwo.fourcharm.model.board.BinaryBoard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
class FourCharmTUI implements Observer, MoveRequestable {


    // ------------------ Instance variables ----------------

    private final Game game;
    private BufferedReader reader;
    private boolean running;
    
    // --------------------- Constructors -------------------

    protected FourCharmTUI() throws InstantiationException, IllegalAccessException {
        super();

        reader = new BufferedReader(
                new InputStreamReader(System.in));

        game = new Game(BinaryBoard.class, new LocalAIPlayer(new NegaMaxStrategy(10), Mark.P1),
                new ASyncPlayer("Wouter", this, Mark.P2));

        game.addObserver(this);
    }

    /**
     * @param args none applicable
     */
    public static void main(String[] args) {

        Logger globalLogger = Logger.getGlobal();
        LogManager.getLogManager().reset();
        globalLogger.setLevel(Level.INFO);

        ConsoleHandler cH = new ConsoleHandler();
        cH.setLevel(Level.INFO);

        globalLogger.addHandler(cH);

        try {
            new FourCharmTUI().play();
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.getGlobal().throwing("FourCharmTUI", "main", e);
        }


    }

    // ----------------------- Commands ---------------------

    /**
     * Reads the command and places it in an array.
     *
     * @param commandString the command in string format
     */
    private void parseCommand(String commandString) {
        Scanner commandScanner = new Scanner(commandString);
        String command;
        List<String> args = new ArrayList<String>();

        if (commandScanner.hasNext()) {
            command = commandScanner.next();
            while (commandScanner.hasNext()) {
                args.add(commandScanner.next());
            }
            checkCommand(command, args.toArray(new String[0]));
        }
        commandScanner.close();

    }

    /**
     * Checks if the given command is valid.
     *
     * @param commandString the command in string format
     * @param args          the amount of commands
     */
    private void checkCommand(String commandString, String[] args) {
        Command command = Command.parseString(commandString);
        if (command != null) {
            if (args.length == command.argCount()) {
                executeCommand(command, args);
            } else {
                showError("The command " + command.toString()
                        + " requires " + command.argCount()
                        + " parameters. You gave " + args.length + ".");
            }
        } else {
            showError("This command has no power here!");
        }
    }


    /**
     * Executes the given command.
     * @param command the given command
     * @param args string array of arguments
     */
    private void executeCommand(Command command, String[] args) {
        switch (command) {
            //Use the chat function
            case CHAT:
                break;
            //Ask for a hint in the current game.
            case HINT:
                break;
            //Challenge another player to play a game
            case CHALLENGE:
                break;
            //List all of the players in the lobby
            case LIST_PLAYERS:
                break;
            //List all the available commands
            case HELP:
                showHelp();
                break;
            //Exit the game
            case EXIT:
                running = false;
                break;
        }
    }


    /**
     * Shows the given message.
     * @param message the given message
     */
    private void showMessage(String message) {
        System.out.println(message);
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
     * @param message the (error) message
     */
    public void showError(String message) {
        System.err.println(message);

    }

    /**
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        Logger.getGlobal().finer("Tui is getting message from: " + o.toString());
        if (o instanceof Game) {
            System.out.println(((Game) o).getBoard().toString());
        }

    }
    
    /**
     * Start the game.
     */
    protected void play() {

        while (running) {
            update(game, null);
            game.play();

            // Game Finished
            System.out.println(game.getBoard().toString());

            if (game.hasWinner()) {
                System.out.println(game.getWinner().toString() + " Won");
            } else {
                System.out.println("The game is a tie");
            }
        }


    }

    /**
     *
     * @return the requested move
     */
    @Override
    public int requestMove() {
        System.out.println("Please enter move:");

        int move = 0;

        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            Logger.getGlobal().warning(e.toString());
            Logger.getGlobal().throwing("ASyncPlayer", "determineMove", e);
        }
        Logger.getGlobal().info("Playerinput: " + line);
        if (line == null) {
            requestMove();
        } else {
            for (int i = 0; i < line.length(); i++) {
                int col = line.charAt(i) - '1';
                if ((col >= 0) && (col < game.getBoard().getColumns())
                        && game.getBoard().columnHasFreeSpace(col)) {
                    move = col;
                } else {
                    move = requestMove();
                }
            }
        }
        return move;
    }

    /**
     * Enum class that 'holds' the commands.
     */
    private enum Command {
        CHAT(new String[0]),
        HINT(new String[0]),
        EXIT(new String[0]),
        CHALLENGE(new String[0]),
        HELP(new String[0]),
        LIST_PLAYERS(new String[0]);


        String[] commandNames;

        Command(String[] cNames) {
            commandNames = cNames;
        }

        /**
         * @param cString
         * @return
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
         * @return an integer with the total amount of arguments
         */
        public int argCount() {
            return commandNames.length;
        }

        /**
         * @return
         */
        public String argDesc() {
            String desc = "";
            for (String arg : commandNames) {
                desc += "[" + arg + "]";
            }

            return desc;
        }

        /**
         * @return the string representation
         */
        @Override
        public String toString() {
            return name() + " " + argDesc();
        }
    }
}
