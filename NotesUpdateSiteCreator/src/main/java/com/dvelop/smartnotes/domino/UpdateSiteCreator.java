package com.dvelop.smartnotes.domino;

import java.io.IOException;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;

import com.dvelop.smartnotes.domino.common.ArgumentResolver;
import com.dvelop.smartnotes.domino.policy.PolicyBuilder;
import com.dvelop.smartnotes.domino.resources.Constants;
import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.settings.SettingsBuilder;
import com.dvelop.smartnotes.domino.updatesite.UpdateSiteBuilder;
import com.dvelop.smartnotes.domino.updatesite.logging.LogFormatter;
import com.dvelop.smartnotes.domino.widgetcatalog.WidgetCatalogBuilder;

public class UpdateSiteCreator {

    public static void main(String[] args) {
	Logger root = Logger.getLogger("com.dvelop.smartnotes.domino");
	Logger logger = Logger.getLogger(UpdateSiteCreator.class.getName());
	ArgumentResolver arguments = new ArgumentResolver(args);
	initializeLogging(root, arguments);
	try {
	    logger.fine("Initialize Notes Thread");
	    NotesThread.sinitThread();
	    logger.fine("Notes Thread initialized");
	    Session session = null;
	    try {
		logger.fine("create session");
		session = NotesFactory.createSessionWithFullAccess();
		logger.fine("Start creating the UpdateSite");
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

		logger.fine("Start creating the WidgetCatalog / Toolbox");
		WidgetCatalogBuilder widgetCatalogBuilder = new WidgetCatalogBuilder(session);
		widgetCatalogBuilder.setEventRegistry(updateSiteBuilder.getEventRegistry());
		widgetCatalogBuilder.setExtensionXMLPath(arguments.getExtensionXMLPath());
		widgetCatalogBuilder.setOverrideExisting(arguments.isUpdate());
		widgetCatalogBuilder.setServer(arguments.getServer());
		widgetCatalogBuilder.setSiteUrl(updateSiteURLs.get(arguments.isHttpUrl() ? Constants.HTTP_URL : Constants.NRPC_URL));
		widgetCatalogBuilder.setWidgetCatalogNsfFileName(arguments.getWidgetCatalogNsfFileName());
		widgetCatalogBuilder.setWidgetCatalogNsfTitle(arguments.getWidgetCatalogNsfTitle());
		widgetCatalogBuilder.setWidgetCatalogTemplateFileName(arguments.getWidgetCatalogTemplateFileName());
		widgetCatalogBuilder.setWidgetCategory(arguments.getWidgetCategory());
		widgetCatalogBuilder.setWidgetType(arguments.getWidgetType());
		widgetCatalogBuilder.buildWidgetCatalog();

		logger.fine("Start creating a DesktopSetting in Servers NAB");
		SettingsBuilder settingsBuilder = new SettingsBuilder(session);
		settingsBuilder.setWidgetCatalogDB(widgetCatalogBuilder.getWidgetCatalogDB());
		settingsBuilder.setDesktopSettingName(arguments.getSettingFullName());
		settingsBuilder.setDesktopSettingDescription(arguments.getSettingDescription());
		settingsBuilder.setWidgetCategory(arguments.getWidgetCategory());
		settingsBuilder.createDesktopSetting(false);

		logger.fine("Start creating a Policy for distribution");
		PolicyBuilder policyBuilder = new PolicyBuilder(session, settingsBuilder.getAddressbook(), settingsBuilder.getPolicyManagement());
		policyBuilder.setFullName(arguments.getPolicyFullName());
		policyBuilder.setDescription(arguments.getPolicyDescription());
		policyBuilder.setCategory(arguments.getPolicyCategory());
		policyBuilder.createMasterPolicy(settingsBuilder.getDesktopSettings());

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

    private static void initializeLogging(Logger logger, ArgumentResolver arguments) {
	if (arguments.isLogging()) {
	    Formatter formatter = new LogFormatter();
	    logger.setLevel(Level.ALL);
	    if (arguments.isFileLogging()) {
		try {
		    // This block configure the logger with handler and
		    // formatter
		    FileHandler fh = new FileHandler(arguments.getLogFilePath(), false);
		    fh.setFormatter(formatter);
		    fh.setLevel(Level.ALL);
		    logger.addHandler(fh);

		} catch (SecurityException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	    if (arguments.isConsoleLogging()) {
		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(formatter);
		ch.setLevel(Level.ALL);
		logger.addHandler(ch);
	    }
	    logger.log(Level.INFO, "Log inistialized");
	} else {
	    logger.setLevel(Level.OFF);
	}
    }
}
