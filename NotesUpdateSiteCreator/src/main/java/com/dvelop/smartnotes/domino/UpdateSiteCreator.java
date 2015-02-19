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

import com.dvelop.smartnotes.domino.common.Constants;
import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.updatesite.UpdateSiteBuilder;
import com.dvelop.smartnotes.domino.updatesite.logging.LogFormatter;
import com.dvelop.smartnotes.domino.widgetcatalog.WidgetCatalogBuilder;

public class UpdateSiteCreator {

    private static class Arguments {
	String server;
	String updateSiteNsfFileName;
	String updateSiteNsfTitle;
	String updateSiteTemplateFileName;
	String updateSitePath;
	String widgetCatalogNsfFileName;
	String widgetCatalogNsfTitle;
	String widgetCatalogTemplateFileName;
	String extensionXMLPath;
	boolean widgetCatalogUpdate;
	String widgetCategory;
	String widgetType;

	public Arguments(String[] args) {
	    this.server = args[0];
	    this.updateSiteNsfFileName = args[1];
	    this.updateSiteNsfTitle = args[2];
	    String ustfn = ("".equals(args[3])) ? "updatesite.ntf" : args[3];
	    this.updateSiteTemplateFileName = ustfn;
	    this.updateSitePath = args[4];
	    this.widgetCatalogNsfFileName = args[5];
	    this.widgetCatalogNsfTitle = args[6];
	    String wctfn = ("".equals(args[7])) ? "toolbox.ntf" : args[7];
	    this.widgetCatalogTemplateFileName = wctfn;
	    this.extensionXMLPath = args[8];
	    this.widgetCatalogUpdate = Boolean.valueOf(args[9]);
	    String wCategory = ("".equals(args[10])) ? "d.3ecm" : args[10];
	    this.widgetCategory = wCategory;
	    String wType = ("".equals(args[11])) ? "T" : args[11].toUpperCase();
	    this.widgetType = wType;
	}

    }

    public static void main(String[] args) {
	Logger root = Logger.getLogger("com.dvelop.smartnotes.domino");
	Logger logger = Logger.getLogger(UpdateSiteCreator.class.getName());
	initializeLogging(root);
	Arguments arguments = new Arguments(args);
	try {
	    logger.fine("Initialize Notes Thread");
	    NotesThread.sinitThread();
	    logger.fine("Notes Thread initialized");
	    Session session = null;
	    try {
		logger.fine("create session");
		session = NotesFactory.createSessionWithFullAccess();
		UpdateSiteBuilder updateSiteBuilder = new UpdateSiteBuilder(session);

		logger.fine("Server: " + arguments.server);
		updateSiteBuilder.setServer(arguments.server);

		logger.fine("UpdateSiteNsfFileName: " + arguments.updateSiteNsfFileName);
		updateSiteBuilder.setUpdateSiteNsfFileName(arguments.updateSiteNsfFileName);

		logger.fine("UpdateSiteNsfTitle: " + arguments.updateSiteNsfTitle);
		updateSiteBuilder.setUpdateSiteNsfTitle(arguments.updateSiteNsfTitle);

		logger.fine("UpdateSiteTemplateFileName: " + arguments.updateSiteTemplateFileName);
		updateSiteBuilder.setUpdateSiteTemplateFileName(arguments.updateSiteTemplateFileName);

		logger.fine("UpdateSitePath: " + arguments.updateSitePath);
		updateSiteBuilder.setUpdateSitePath(arguments.updateSitePath);

		logger.fine("start build update site");
		updateSiteBuilder.buildUpdateSite();

		logger.fine("get URLs");
		Map<String, String> updateSiteURLs = updateSiteBuilder.getUpdateSiteURLs();
		for (String string : updateSiteURLs.keySet()) {
		    System.out.println(string + ": " + updateSiteURLs.get(string));
		}

		WidgetCatalogBuilder widgetCatalogBuilder = new WidgetCatalogBuilder();
		widgetCatalogBuilder.setEventRegistry(updateSiteBuilder.getEventRegistry());
		widgetCatalogBuilder.setExtensionXMLPath(arguments.extensionXMLPath);
		widgetCatalogBuilder.setOverrideExisting(arguments.widgetCatalogUpdate);
		widgetCatalogBuilder.setServer(arguments.server);
		widgetCatalogBuilder.setSession(session);
		widgetCatalogBuilder.setSiteUrl(updateSiteURLs.get(Constants.NRPC_URL));
		widgetCatalogBuilder.setWidgetCatalogNsfFileName(arguments.widgetCatalogNsfFileName);
		widgetCatalogBuilder.setWidgetCatalogNsfTitle(arguments.widgetCatalogNsfTitle);
		widgetCatalogBuilder.setWidgetCatalogTemplateFileName(arguments.widgetCatalogTemplateFileName);
		widgetCatalogBuilder.setWidgetCategory(arguments.widgetCategory);
		widgetCatalogBuilder.setWidgetType(arguments.widgetType);
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
