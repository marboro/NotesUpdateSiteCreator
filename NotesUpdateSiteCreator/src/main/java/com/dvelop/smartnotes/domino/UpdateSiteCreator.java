package com.dvelop.smartnotes.domino;

import java.io.IOException;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;

import com.dvelop.smartnotes.domino.common.ArgumentResolver;
import com.dvelop.smartnotes.domino.common.Constants;
import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.updatesite.UpdateSiteBuilder;
import com.dvelop.smartnotes.domino.updatesite.logging.LogFormatter;
import com.dvelop.smartnotes.domino.widgetcatalog.WidgetCatalogBuilder;

public class UpdateSiteCreator {

    public static void main(String[] args) {
	Logger root = Logger.getLogger("com.dvelop.smartnotes.domino");
	Logger logger = Logger.getLogger(UpdateSiteCreator.class.getName());
	initializeLogging(root);
	ArgumentResolver arguments = new ArgumentResolver(args);
	try {
	    logger.fine("Initialize Notes Thread");
	    NotesThread.sinitThread();
	    logger.fine("Notes Thread initialized");
	    Session session = null;
	    try {
		logger.fine("create session");
		session = NotesFactory.createSessionWithFullAccess();
		UpdateSiteBuilder updateSiteBuilder = new UpdateSiteBuilder(session);

		logger.fine("Server: " + arguments.getServer());
		updateSiteBuilder.setServer(arguments.getServer());

		logger.fine("UpdateSiteNsfFileName: " + arguments.getUpdateSiteNsfFileName());
		updateSiteBuilder.setUpdateSiteNsfFileName(arguments.getUpdateSiteNsfFileName());

		logger.fine("UpdateSiteNsfTitle: " + arguments.getUpdateSiteNsfTitle());
		updateSiteBuilder.setUpdateSiteNsfTitle(arguments.getUpdateSiteNsfTitle());

		logger.fine("UpdateSiteTemplateFileName: " + arguments.getUpdateSiteTemplateFileName());
		updateSiteBuilder.setUpdateSiteTemplateFileName(arguments.getUpdateSiteTemplateFileName());

		logger.fine("UpdateSitePath: " + arguments.getUpdateSitePath());
		updateSiteBuilder.setUpdateSitePath(arguments.getUpdateSitePath());

		logger.fine("start build update site");
		updateSiteBuilder.buildUpdateSite();

		logger.fine("get URLs");
		Map<String, String> updateSiteURLs = updateSiteBuilder.getUpdateSiteURLs();
		for (String string : updateSiteURLs.keySet()) {
		    System.out.println(string + ": " + updateSiteURLs.get(string));
		}

		WidgetCatalogBuilder widgetCatalogBuilder = new WidgetCatalogBuilder(session);
		widgetCatalogBuilder.setEventRegistry(updateSiteBuilder.getEventRegistry());
		widgetCatalogBuilder.setExtensionXMLPath(arguments.getExtensionXMLPath());
		widgetCatalogBuilder.setOverrideExisting(arguments.isUpdate());
		widgetCatalogBuilder.setServer(arguments.getServer());
		widgetCatalogBuilder.setSiteUrl(updateSiteURLs.get(Constants.NRPC_URL));
		widgetCatalogBuilder.setWidgetCatalogNsfFileName(arguments.getWidgetCatalogNsfFileName());
		widgetCatalogBuilder.setWidgetCatalogNsfTitle(arguments.getWidgetCatalogNsfTitle());
		widgetCatalogBuilder.setWidgetCatalogTemplateFileName(arguments.getWidgetCatalogTemplateFileName());
		widgetCatalogBuilder.setWidgetCategory(arguments.getWidgetCategory());
		widgetCatalogBuilder.setWidgetType(arguments.getWidgetType());
		widgetCatalogBuilder.buildWidgetCatalog();

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
	System.out.println("!!!DONE!!!");
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
