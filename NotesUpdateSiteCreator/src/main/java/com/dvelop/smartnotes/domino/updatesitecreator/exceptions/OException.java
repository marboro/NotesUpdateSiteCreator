package com.dvelop.smartnotes.domino.updatesitecreator.exceptions;

public class OException {

    public static void raiseError(Exception e, String name, String param) {
	System.err.println("Class: " + name + " Parameter: " + param + " " + e.getLocalizedMessage());
	e.printStackTrace();

    }

}
