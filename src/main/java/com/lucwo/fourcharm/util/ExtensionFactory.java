/*
 * Copyright (c) 2015. Luce Sandfort and Wouter Timmermans
 */

package com.lucwo.fourcharm.util;

import nl.woutertimmermans.connect4.protocol.exceptions.InvalidParameterError;
import nl.woutertimmermans.connect4.protocol.parameters.Extension;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class ExtensionFactory {

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
            Logger.getGlobal().throwing("ExtensionFactory", "chat", invalidParameterError);
        }
        return result;
    }

}
