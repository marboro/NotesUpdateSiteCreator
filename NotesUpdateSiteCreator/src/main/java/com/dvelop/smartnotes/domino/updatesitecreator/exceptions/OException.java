package com.dvelop.smartnotes.domino.updatesitecreator.exceptions;

import java.util.logging.Logger;

public class OException {
    // private static Logger logger =
    // Logger.getLogger(OException.class.getName());

    public static void raiseError(Exception e, String name, String param) {
	Logger logger = Logger.getLogger(name);
	String msg = "Class: " + name + " Parameter: " + param + " " + e.getLocalizedMessage();
	logger.severe(msg);
	System.err.println(msg);
	e.printStackTrace();

    }
}
