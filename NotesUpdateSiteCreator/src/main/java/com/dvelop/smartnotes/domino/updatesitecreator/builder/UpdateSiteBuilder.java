package com.dvelop.smartnotes.domino.updatesitecreator.builder;

import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.dvelop.smartnotes.domino.updatesitecreator.common.Resources;
import com.dvelop.smartnotes.domino.updatesitecreator.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesitecreator.importer.ImportSite;
import com.dvelop.smartnotes.domino.updatesitecreator.site.digest.SiteDigest;

public class UpdateSiteBuilder {
    Logger logger = Logger.getLogger(UpdateSiteBuilder.class.getName());
    private Session session;
    private String server;
    private String updateSiteNsfFileName;
    private String updateSiteNsfTitle;
    private String updateSiteTemplateFileName;
    private String updateSitePath;
    private EventRegistry eventRegistry;
    private SiteDigest siteDigest;

    public UpdateSiteBuilder(Session session) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("Create UpdateSiteBuilder");
	this.session = session;
	eventRegistry = new EventRegistry();
	logger.fine(Resources.LOG_SEPARATOR_END);
    }

    public Session getSession() {
	return session;
    }

    public void setSession(Session session) {
	this.session = session;
    }

    public String getServer() {
	return server;
    }

    public void setServer(String server) {
	this.server = server;
    }

    public String getUpdateSiteNsfFileName() {
	return updateSiteNsfFileName;
    }

    public void setUpdateSiteNsfFileName(String updateSiteNsfFileName) {
	this.updateSiteNsfFileName = updateSiteNsfFileName;
    }

    public String getUpdateSiteNsfTitle() {
	return updateSiteNsfTitle;
    }

    public void setUpdateSiteNsfTitle(String updateSiteNsfTitle) {
	this.updateSiteNsfTitle = updateSiteNsfTitle;
    }

    public String getUpdateSiteTemplateFileName() {
	return updateSiteTemplateFileName;
    }

    public void setUpdateSiteTemplateFileName(String updateSiteTemplateFileName) {
	this.updateSiteTemplateFileName = updateSiteTemplateFileName;
    }

    public String getUpdateSitePath() {
	return updateSitePath;
    }

    public void setUpdateSitePath(String updateSitePath) {
	this.updateSitePath = updateSitePath;
    }

    public void buildUpdateSite() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start buildUpdateSite");
	try {
	    logger.fine("trying to get update site Database");
	    Database updateSiteDB = session.getDatabase(server, updateSiteNsfFileName, false);

	    if (updateSiteDB == null) {
		logger.fine("not found");
		logger.fine("trying to get update site Template");
		Database updateSiteTemplate = session.getDatabase(server, updateSiteTemplateFileName);
		logger.fine("is Database open?");
		if (!updateSiteTemplate.isOpen()) {
		    logger.fine("open Database");
		    updateSiteTemplate.open();
		}
		logger.fine("trying to create new update site Database from Template");
		updateSiteDB = updateSiteTemplate.createFromTemplate(server, updateSiteNsfFileName, true);
		logger.fine("set Title");
		updateSiteDB.setTitle(updateSiteNsfTitle);
	    }
	    logger.fine("Update Site Database opened or created");
	    ImportSite importSite = new ImportSite(session, eventRegistry);
	    importSite.setDb(updateSiteDB);
	    logger.fine("process the Site");
	    importSite.process(updateSitePath);
	    logger.fine("serialize it");
	    if (importSite.serialize()) {
		logger.fine("create Site Digest");
		siteDigest = new SiteDigest(session, updateSiteDB);
	    }
	} catch (NotesException e) {
	    logger.log(Level.SEVERE, e.getMessage(), e);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }
}
