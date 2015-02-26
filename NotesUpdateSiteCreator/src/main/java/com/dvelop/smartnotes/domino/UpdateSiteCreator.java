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
		logger.info("Creating the UpdateSite");
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
		logger.fine("start building the update site");
		updateSiteBuilder.buildUpdateSite();
		logger.fine("get URLs");
		Map<String, String> updateSiteURLs = updateSiteBuilder.getUpdateSiteURLs();
		for (String string : updateSiteURLs.keySet()) {
		    logger.info(string + ": " + updateSiteURLs.get(string));
		}

		logger.info("Creating the WidgetCatalog / Toolbox");
		WidgetCatalogBuilder widgetCatalogBuilder = new WidgetCatalogBuilder(session);
		logger.fine("set EventRegistry");
		widgetCatalogBuilder.setEventRegistry(updateSiteBuilder.getEventRegistry());
		logger.fine("ExtensionXML-Path: " + arguments.getExtensionXMLPath());
		widgetCatalogBuilder.setExtensionXMLPath(arguments.getExtensionXMLPath());
		logger.fine("Is Update: " + arguments.isUpdate());
		widgetCatalogBuilder.setOverrideExisting(arguments.isUpdate());
		logger.fine("Server: " + arguments.getServer());
		widgetCatalogBuilder.setServer(arguments.getServer());
		logger.fine("Site-URL: " + updateSiteURLs.get(arguments.isHttpUrl() ? Constants.HTTP_URL : Constants.NRPC_URL));
		widgetCatalogBuilder.setSiteUrl(updateSiteURLs.get(arguments.isHttpUrl() ? Constants.HTTP_URL : Constants.NRPC_URL));
		logger.fine("WidgetCatalogNsfFileName: " + arguments.getWidgetCatalogNsfFileName());
		widgetCatalogBuilder.setWidgetCatalogNsfFileName(arguments.getWidgetCatalogNsfFileName());
		logger.fine("WidgetCatalogNsfTitle: " + arguments.getWidgetCatalogNsfTitle());
		widgetCatalogBuilder.setWidgetCatalogNsfTitle(arguments.getWidgetCatalogNsfTitle());
		logger.fine("WidgetCatalogTemplateFileName: " + arguments.getWidgetCatalogTemplateFileName());
		widgetCatalogBuilder.setWidgetCatalogTemplateFileName(arguments.getWidgetCatalogTemplateFileName());
		logger.fine("WidgetCategory: " + arguments.getWidgetCategory());
		widgetCatalogBuilder.setWidgetCategory(arguments.getWidgetCategory());
		logger.fine("WidgetType: " + arguments.getWidgetType());
		widgetCatalogBuilder.setWidgetType(arguments.getWidgetType());
		logger.fine("now start building the widget catalog");
		widgetCatalogBuilder.buildWidgetCatalog();

		logger.info("Creating a DesktopSetting in Servers NAB");
		SettingsBuilder settingsBuilder = new SettingsBuilder(session);
		logger.fine("set Widget Catalog");
		settingsBuilder.setWidgetCatalogDB(widgetCatalogBuilder.getWidgetCatalogDB());
		logger.fine("SettingFullName: " + arguments.getSettingFullName());
		settingsBuilder.setDesktopSettingName(arguments.getSettingFullName());
		logger.fine("SettingDescription: " + arguments.getSettingDescription());
		settingsBuilder.setDesktopSettingDescription(arguments.getSettingDescription());
		logger.fine("WidgetCategory for distribution: " + arguments.getWidgetCategory());
		settingsBuilder.setWidgetCategory(arguments.getWidgetCategory());
		logger.fine("now create the DesktopSetting");
		settingsBuilder.createDesktopSetting(false);

		logger.info("Creating a Policy for distribution");
		PolicyBuilder policyBuilder = new PolicyBuilder(session, settingsBuilder.getAddressbook(), settingsBuilder.getPolicyManagement());
		logger.fine("PolicyFullName: " + arguments.getPolicyFullName());
		policyBuilder.setFullName(arguments.getPolicyFullName());
		logger.fine("PolicyDescription: " + arguments.getPolicyDescription());
		policyBuilder.setDescription(arguments.getPolicyDescription());
		logger.fine("PolicyCategory: " + arguments.getPolicyCategory());
		policyBuilder.setCategory(arguments.getPolicyCategory());
		logger.fine("now create the policy");
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
