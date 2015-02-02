package com.dvelop.smartnotes.domino.updatesitecreator.builder;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.dvelop.smartnotes.domino.updatesitecreator.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesitecreator.importer.ImportSite;
import com.dvelop.smartnotes.domino.updatesitecreator.site.digest.SiteDigest;

public class UpdateSiteBuilder {
    private Session session;
    private String server;
    private String updateSiteNsfFileName;
    private String updateSiteNsfTitle;
    private String updateSiteTemplateFileName;
    private String updateSitePath;
    private EventRegistry eventRegistry;
    private SiteDigest siteDigest;

    public UpdateSiteBuilder(Session session) {
	super();
	this.session = session;
	eventRegistry = new EventRegistry();
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
	try {
	    Database updateSiteDB = session.getDatabase(server, updateSiteNsfFileName, false);

	    if (updateSiteDB == null) {
		Database updateSiteTemplate = session.getDatabase(server, updateSiteTemplateFileName);
		if (!updateSiteTemplate.isOpen()) {
		    updateSiteTemplate.open();
		}
		updateSiteDB = updateSiteTemplate.createFromTemplate(server, updateSiteNsfFileName, true);
		updateSiteDB.setTitle(updateSiteNsfTitle);
	    }
	    ImportSite importSite = new ImportSite(session, eventRegistry);
	    importSite.setDb(updateSiteDB);
	    importSite.process(updateSitePath);
	    if (importSite.serialize()) {
		siteDigest = new SiteDigest(session, updateSiteDB);
	    }
	} catch (NotesException e) {
	    e.printStackTrace();
	}

    }
}
