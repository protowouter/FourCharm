/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.util;

import nl.woutertimmermans.connect4.protocol.exceptions.InvalidParameterError;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ExtensionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionFactory.class);

    public static Set<Extension> createExtensionSet() {
        Set<Extension> result = new HashSet<>();
        result.add(chat());
        return result;
    }

    public static Extension chat() {
        Extension result = new Extension();
        try {
            result.setValue("Chat");
        } catch (InvalidParameterError invalidParameterError) {
            LOGGER.trace("chat", invalidParameterError);
        }
        return result;
    }

}
