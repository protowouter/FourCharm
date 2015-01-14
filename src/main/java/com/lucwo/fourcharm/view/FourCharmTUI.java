/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.client.Client;
import com.lucwo.fourcharm.model.Game;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Luce Sandfort and Wouter Timmermans
 *
 */
public class FourCharmTUI implements FourCharmView {

    private static final String NOT_IMPLEMENTED = "Not yet implemented";


    // ------------------ Instance variables ----------------

    private Game game;
    private Scanner nameScanner;
    private boolean running;
    private boolean gameOn;
    private Client client;
    
    // --------------------- Constructors -------------------

    protected FourCharmTUI() {

        nameScanner = new Scanner(System.in);
        gameOn = true;
        running = true;
    }

    /**
     * The main method of the FourCharmTui.
     * @param args none applicable
     */
    public static void main(String[] args) {

        Logger globalLogger = Logger.getGlobal();
        LogManager.getLogManager().reset();
        globalLogger.setLevel(Level.FINE);

        ConsoleHandler cH = new ConsoleHandler();
        cH.setLevel(Level.FINE);

        globalLogger.addHandler(cH);


        new FourCharmTUI().run();


    }

    // ----------------------- Commands ---------------------

    /**
     * Reads the command and places it in an array.
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
            checkCommand(command, args.toArray(new String[args.size()]));
        }
        commandScanner.close();

    }

    /**
     * Checks if the given command is valid.
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
            //Start new game if true
            case YES:
                if (!gameOn) {
                    startGame();
                }
                break;
            //Use the chat function
            case CHAT:
                showError(NOT_IMPLEMENTED);
                break;
            //Make a move
            case MOVE:
                int moove = Integer.parseInt(args[0]) - 1;
                client.queueMove(moove);

                break;
            //Ask for a hint in the current game.
            case HINT:
                showError(NOT_IMPLEMENTED);
                break;
            //Challenge another player to play a game
            case CHALLENGE:
                showError(NOT_IMPLEMENTED);
                break;
            //List all of the players in the lobby
            case LIST_PLAYERS:
                showError(NOT_IMPLEMENTED);
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

    @Override
    public void update(Observable o, Object arg) {
        Logger.getGlobal().finer("Tui is getting message from: " + o.toString());
        if (o instanceof Game) {
            Game newGame = (Game) o;
            System.out.println(newGame.getBoard().toString());
            if (newGame.hasFinished()) {
                showMessage("The game has finished. ");
                gameOn = false;
                if (newGame.hasWinner()) {
                    showMessage("The winner is " + newGame.getWinner());
                }
                showMessage("Would you like to play a new game? [yes/exit]");
                if (nameScanner.hasNextLine()) {
                    if (nameScanner.nextLine().equals("Yes")) {
                        startGame();
                    }
                }


            }
        }

    }

    /**
     * Run the game.
     */
    protected void run() {
        showMessage("Welcome to FourCharmGUI Connect4.");
        startGame();


        while (running && nameScanner.hasNextLine()) {
            parseCommand(nameScanner.nextLine());

        }

        game.stop();

    }

    /**
     * Starts the game.
     */
    private void startGame() {
        String name = "";
        showMessage("Please enter your name");

        if (nameScanner.hasNextLine()) {
            name = nameScanner.nextLine();
            showMessage("Welcome " + name);
        }

        try {
            client = new Client(name, InetAddress.getLocalHost(), 8080, this);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        new Thread(client).start();

    }

    /**
     * Enum class that 'holds' the commands of the Connect4 game.
     */
    private enum Command {
        CHAT(new String[]{"Chatmessage"}),
        HINT(new String[0]),
        EXIT(new String[0]),
        CHALLENGE(new String[]{"Player name"}),
        HELP(new String[0]),
        MOVE(new String[]{"The column"}),
        LIST_PLAYERS(new String[0]),
        YES(new String[0]);


        String[] parameterNames;

        Command(String[] cNames) {
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
