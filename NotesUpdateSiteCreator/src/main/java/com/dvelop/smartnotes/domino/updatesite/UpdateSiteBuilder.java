package com.dvelop.smartnotes.domino.updatesite;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.dvelop.smartnotes.domino.common.Constants;
import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.updatesite.dominodirectory.AbstractedDominoDirectory;
import com.dvelop.smartnotes.domino.updatesite.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesite.importer.ImportSite;
import com.dvelop.smartnotes.domino.updatesite.site.digest.SiteDigest;
import com.dvelop.smartnotes.domino.updatesite.urlfomatter.URLFormatter;

public class UpdateSiteBuilder {

    private Logger logger = Logger.getLogger(UpdateSiteBuilder.class.getName());
    private Session session;
    private String server;
    private String updateSiteNsfFileName;
    private String updateSiteNsfTitle;
    private String updateSiteTemplateFileName;
    private String updateSitePath;
    private EventRegistry eventRegistry;
    private SiteDigest siteDigest;
    private Database updateSiteDB;

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
	if ("currentServer".equals(server)) {
	    try {
		server = session.getServerName();
	    } catch (NotesException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
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

    public EventRegistry getEventRegistry() {
	return eventRegistry;
    }

    public void buildUpdateSite() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start buildUpdateSite");
	try {
	    logger.fine("trying to get update site Database");
	    updateSiteDB = session.getDatabase(server, updateSiteNsfFileName, false);

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

    public Map<String, String> getUpdateSiteURLs() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get Update Site URLs");
	Map<String, String> result = new HashMap<String, String>();
	try {
	    AbstractedDominoDirectory dominoDirectory = new AbstractedDominoDirectory(session, server);
	    String sHostName = dominoDirectory.getServerHostName(server);
	    logger.fine("get URLs for server " + server);
	    URLFormatter urlFormatter = new URLFormatter(sHostName, updateSiteDB);
	    result.put(Constants.NRPC_URL, urlFormatter.getNRPCURL());
	    result.put(Constants.HTTP_URL, urlFormatter.getHTTPURL());
	    return result;
	} catch (Exception e) {
	    logger.log(Level.SEVERE, e.getMessage(), e);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return null;
    }
}
