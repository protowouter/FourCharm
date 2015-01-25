/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.view;

import com.lucwo.fourcharm.controller.FourCharmServerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FourCharmServerTUI implements Runnable {

    private static final String NOT_IMPLEMENTED = "Not yet implemented";
    private static final Logger LOGGER = LoggerFactory.getLogger(FourCharmServerTUI.class);


// ------------------ Instance variables ----------------

    private boolean running;
    private Scanner inputScanner;
    private FourCharmServerController controller;


// --------------------- Constructors -------------------

    public FourCharmServerTUI(FourCharmServerController cont) {
        running = true;
        inputScanner = new Scanner(System.in);
        controller = cont;
    }

// ----------------------- Queries ----------------------

// ----------------------- Commands ---------------------

    public void run() {
        showMessage("Welcome to FourCharm Server");
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
    }

    /**
     * Performs the starting of the server.
     *
     * @param args an array with at least 1 element that can be parsed to a port number.
     */
    private void handleStart(String[] args) {
        try {
            controller.startServer(Integer.parseInt(args[0]));
        } catch (NumberFormatException e) {
            LOGGER.trace("handleStart", e);
            showError(args[0] + " is not a valid port number");
        }

    }

    private void showPrompt() {
        System.out.print("FourCharmServer$ ");
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
            if (args.length == command.argCount()) {
                executeCommand(command, args);
            } else {
                showError("The command " + command.toString()
                        + " requires " + command.argCount()
                        + " parameters. You gave " + args.length + ".");
            }
        } else {
            showError("command " + commandString + " not recognized");
            showHelp();
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
            case START:
                handleStart(args);
                break;
            case EXIT:
                showMessage("Goodbye");
                controller.stopServer();
                running = false;
                break;
            case STOP:
                controller.stopServer();
                break;
            case CHANGE_PORT:
                showError(NOT_IMPLEMENTED);
                break;
            case HELP:
                showHelp();
                break;
            case LIST_PLAYER:
                showError(NOT_IMPLEMENTED);
                break;
            default:
                showError("Command not recognized");
                break;
        }
    }

    /**
     * Enum class that 'holds' the commands of the Connect4 game.
     */
    private enum Command {
        START("port number"),
        STOP(),
        EXIT(),
        CHANGE_PORT("port number"),
        HELP(),
        LIST_PLAYER();


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
