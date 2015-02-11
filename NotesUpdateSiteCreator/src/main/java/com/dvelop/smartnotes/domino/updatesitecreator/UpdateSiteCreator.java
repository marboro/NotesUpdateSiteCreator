package com.dvelop.smartnotes.domino.updatesitecreator;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;

import com.dvelop.smartnotes.domino.updatesitecreator.builder.UpdateSiteBuilder;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Resources;
import com.dvelop.smartnotes.domino.updatesitecreator.logging.LogFormatter;

public class UpdateSiteCreator {

    public static void main(String[] args) {
	Logger root = Logger.getLogger("com.dvelop.smartnotes.domino.updatesitecreator");
	Logger logger = Logger.getLogger(UpdateSiteCreator.class.getName());
	initializeLogging(root);
	try {
	    logger.fine("Initialize Notes Thread");
	    NotesThread.sinitThread();
	    logger.fine("Notes Thread initialized");
	    Session session = null;
	    try {
		logger.fine("create session");
		session = NotesFactory.createSessionWithFullAccess();
		UpdateSiteBuilder updateSiteBuilder = new UpdateSiteBuilder(session);
		logger.fine("Server: " + args[0]);
		updateSiteBuilder.setServer(args[0]);
		logger.fine("UpdateSiteNsfFileName: " + args[1]);
		updateSiteBuilder.setUpdateSiteNsfFileName(args[1]);
		logger.fine("UpdateSiteNsfTitle: " + args[2]);
		updateSiteBuilder.setUpdateSiteNsfTitle(args[2]);
		String ustfn = ("".equals(args[3])) ? "updatesite.ntf" : args[3];
		logger.fine("UpdateSiteTemplateFileName: " + ustfn);
		updateSiteBuilder.setUpdateSiteTemplateFileName(ustfn);
		logger.fine("UpdateSitePath: " + args[4]);
		updateSiteBuilder.setUpdateSitePath(args[4]);
		logger.fine("start build update site");
		updateSiteBuilder.buildUpdateSite();
	    } catch (Exception e) {
		logger.log(Level.SEVERE, e.getMessage(), e);
	    } finally {
		if (session != null) {
		    try {
			logger.fine("recycle Session");
			session.recycle();
		    } catch (NotesException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		    }
		}
		logger.fine("Terminate Notes Thread");
		NotesThread.stermThread();
		logger.fine("Finished");
		logger.fine(Resources.LOG_SEPARATOR_END);
	    }
	} catch (Exception e) {
	    logger.log(Level.SEVERE, e.getMessage(), e);
	}
    }

    private static void initializeLogging(Logger logger) {
	FileHandler fh;
	try {
	    // This block configure the logger with handler and formatter
	    fh = new FileHandler("c:\\temp\\UpdateSiteCreator.log", false);
	    logger.addHandler(fh);
	    logger.setLevel(Level.ALL);
	    Formatter formatter = new LogFormatter();
	    fh.setFormatter(formatter);

	    // the following statement is used to log any messages
	    logger.log(Level.INFO, "Log inistialized");

	} catch (SecurityException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
