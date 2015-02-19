package com.dvelop.smartnotes.domino.updatesite.importer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.dvelop.smartnotes.domino.common.Constants;
import com.dvelop.smartnotes.domino.common.Strings;
import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.updatesite.event.Event;
import com.dvelop.smartnotes.domino.updatesite.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesite.site.Site;
import com.dvelop.smartnotes.domino.updatesite.site.archive.SiteArchive;
import com.dvelop.smartnotes.domino.updatesite.site.category.SiteCategory;
import com.dvelop.smartnotes.domino.updatesite.site.feature.SiteFeature;
import com.dvelop.smartnotes.domino.updatesite.site.feature.plugin.SiteFeaturePlugin;

public class ImportSite extends Event {
    private Logger logger = Logger.getLogger(ImportSite.class.getName());
    private Session session;
    private Database db;

    private Site site;
    private EventRegistry eventRegistry;

    public ImportSite(Session session, EventRegistry eventRegistry) {
	super(eventRegistry);
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create ImportSite");
	this.session = session;
	this.eventRegistry = eventRegistry;
	logger.fine(Resources.LOG_SEPARATOR_END);
    }

    public Database getDb() {
	return db;
    }

    public void setDb(Database db) {
	this.db = db;
    }

    public void process(String sSiteFilePath) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start process");
	try {
	    logger.fine("create new Site");
	    site = new Site(session, db, sSiteFilePath, eventRegistry);
	    logger.fine("site created");
	} catch (Exception e) {
	    logger.log(Level.SEVERE, e.getMessage(), e);
	}
	logger.fine(Resources.LOG_SEPARATOR_END);
    }

    public boolean serialize() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start serializing");
	try {

	    View view;
	    ViewEntryCollection veColl;
	    ViewEntry entry;
	    Document doc;
	    SiteCategory oCategory;
	    SiteArchive oArchive;
	    SiteFeature oFeature;
	    SiteFeaturePlugin oPlugin;
	    List<SiteCategory> vCategories;
	    List<SiteArchive> vArchives;
	    List<SiteFeature> vFeatures;
	    List<SiteFeaturePlugin> vPlugins;
	    long lCount;
	    long lIndex = 0;

	    logger.fine("get View " + Constants.VIEW_UNDO);
	    view = db.getView(Constants.VIEW_UNDO);
	    if (view == null) {
		logger.warning("Error 1000, " + Strings.sprintf1(Resources.ERR_VIEW_NOT_FOUND, Constants.VIEW_UNDO));
	    }

	    // if anything goes wrong during serialization, undo the
	    // serialization
	    try {

		// serialize categories
		logger.fine("serialize categories");
		vCategories = site.getCategories();
		for (SiteCategory category : vCategories) {
		    logger.fine("serialize category: " + category);
		    oCategory = category;
		    oCategory.serialize();
		}

		// serialize archives
		logger.fine("serialize archives");
		vArchives = site.getArchives();
		for (SiteArchive archive : vArchives) {
		    logger.fine("serialize archive: " + archive);
		    oArchive = archive;
		    oArchive.serialize();
		}

		// serialize features
		logger.fine("serialize features");
		vFeatures = site.getFeatures();
		for (SiteFeature feature : vFeatures) {
		    logger.fine("serialize feature: " + feature);
		    oFeature = feature;

		    // first serialize all plugins of each feature
		    logger.fine("first serialize all plugins of each feature");
		    vPlugins = oFeature.getPluginList();
		    for (SiteFeaturePlugin plugin : vPlugins) {
			logger.fine("serialize plugin: " + plugin);
			oPlugin = plugin;
			oPlugin.serialize();

		    }

		    // serialize the feature *after* the plugins have been
		    // serialized
		    logger.fine("serialize the feature *after* the plugins have been serialized");
		    oFeature.serialize();

		}
	    } catch (Exception e) {
		// something went wrong, clear the error and undo the merge
		logger.warning("something went wrong, clear the error and undo the merge");
		view.getAllEntries().removeAll(true);
		return false;
	    }

	    // As of 8.5.3, all documents in the updatesite are now signed.
	    // We can't use .StampAll() anymore to mark all entries as
	    // committed, because stamping a note invalidates the signature.
	    // Instead, loop over the collection and commit/sign each note.
	    logger.fine("As of 8.5.3, all documents in the updatesite are now signed. \nWe can't use .StampAll() anymore to mark all entries as committed, \nbecause stamping a note invalidates the signature. \nInstead, loop over the collection and commit/sign each note.");
	    logger.fine("refresh view");
	    view.refresh();

	    logger.fine("get all entries");
	    veColl = view.getAllEntries();
	    logger.fine("get first entry");
	    entry = veColl.getFirstEntry();
	    logger.fine("get entrycount");
	    lCount = veColl.getCount();

	    raiseEvent(Constants.QUEUE_PROGRESS_HEADER, Resources.LBL_SIGN_CONTENT);
	    raiseEvent(Constants.QUEUE_PROGRESS_RESET, null);
	    raiseEvent(Constants.QUEUE_PROGRESS_SET_MIN, 0);
	    raiseEvent(Constants.QUEUE_PROGRESS_SET_MAX, lCount);

	    while (entry != null) {
		logger.fine("process entry");
		if (entry.isValid()) {
		    logger.fine("get valid entrys document");
		    lIndex = lIndex + 1;
		    doc = entry.getDocument();

		    raiseEvent(Constants.QUEUE_PROGRESS_LABEL, Strings.sprintf2(Resources.LBL_SIGN_DOCUMENTS, "" + lIndex, "" + lCount));
		    raiseEvent(Constants.QUEUE_PROGRESS_BAR, 1);

		    // mark newly imported doc as committed, sign and save
		    logger.fine("mark newly imported doc as committed, sign and save");
		    doc.replaceItemValue(Constants.ITEM_COMMITTED, "1");
		    doc.sign();
		    doc.save(true, false, true);

		    doc = null;

		}
		logger.fine("get next entry");
		entry = veColl.getNextEntry(entry);

	    }

	} catch (Exception e) {
	    logger.log(Level.SEVERE, e.getMessage(), e);
	    return false;
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return true;
    }
}
