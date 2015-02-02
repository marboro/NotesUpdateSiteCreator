package com.dvelop.smartnotes.domino.updatesitecreator.importer;

import java.util.List;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.dvelop.smartnotes.domino.updatesitecreator.common.Constants;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Resources;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Strings;
import com.dvelop.smartnotes.domino.updatesitecreator.event.Event;
import com.dvelop.smartnotes.domino.updatesitecreator.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesitecreator.site.Site;
import com.dvelop.smartnotes.domino.updatesitecreator.site.archive.SiteArchive;
import com.dvelop.smartnotes.domino.updatesitecreator.site.category.SiteCategory;
import com.dvelop.smartnotes.domino.updatesitecreator.site.feature.SiteFeature;
import com.dvelop.smartnotes.domino.updatesitecreator.site.feature.plugin.SiteFeaturePlugin;

public class ImportSite extends Event {

    private Session session;
    private Database db;

    private Site site;
    private EventRegistry eventRegistry;

    public ImportSite(Session session, EventRegistry eventRegistry) {
	super(eventRegistry);
	this.session = session;
	this.eventRegistry = eventRegistry;
    }

    public Database getDb() {
	return db;
    }

    public void setDb(Database db) {
	this.db = db;
    }

    public void process(String sSiteFilePath) {
	try {
	    site = new Site(session, db, sSiteFilePath, eventRegistry);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public boolean serialize() {
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

	    view = db.getView(Constants.VIEW_UNDO);
	    if (view == null) {
		// Error 1000, sprintf1(ERR_VIEW_NOT_FOUND, VIEW_UNDO)
	    }

	    // if anything goes wrong during serialization, undo the
	    // serialization
	    try {

		// serialize categories
		vCategories = site.getCategories();
		for (SiteCategory category : vCategories) {
		    oCategory = category;
		    oCategory.serialize();
		}

		// serialize archives
		vArchives = site.getArchives();
		for (SiteArchive archive : vArchives) {
		    oArchive = archive;
		    oArchive.serialize();
		}

		// serialize features
		vFeatures = site.getFeatures();
		for (SiteFeature feature : vFeatures) {
		    oFeature = feature;

		    // first serialize all plugins of each feature
		    vPlugins = oFeature.getPluginList();
		    for (SiteFeaturePlugin plugin : vPlugins) {
			oPlugin = plugin;
			oPlugin.serialize();

		    }

		    // serialize the feature *after* the plugins have been
		    // serialized
		    oFeature.serialize();

		}
	    } catch (Exception e) {
		// something went wrong, clear the error and undo the merge
		// Err = 0;
		view.getAllEntries().removeAll(true);
		return false;
	    }

	    // As of 8.5.3, all documents in the updatesite are now signed.
	    // We can't use .StampAll() anymore to mark all entries as
	    // committed,
	    // because stamping a note invalidates the signature.
	    // Instead, loop over the collection and commit/sign each note.

	    view.refresh();

	    veColl = view.getAllEntries();
	    entry = veColl.getFirstEntry();
	    lCount = veColl.getCount();

	    raiseEvent(Constants.QUEUE_PROGRESS_HEADER, Resources.LBL_SIGN_CONTENT);
	    raiseEvent(Constants.QUEUE_PROGRESS_RESET, null);
	    raiseEvent(Constants.QUEUE_PROGRESS_SET_MIN, 0);
	    raiseEvent(Constants.QUEUE_PROGRESS_SET_MAX, lCount);

	    while (entry != null) {

		if (entry.isValid()) {

		    lIndex = lIndex + 1;
		    doc = entry.getDocument();

		    raiseEvent(Constants.QUEUE_PROGRESS_LABEL, Strings.sprintf2(Resources.LBL_SIGN_DOCUMENTS, "" + lIndex, "" + lCount));
		    raiseEvent(Constants.QUEUE_PROGRESS_BAR, 1);

		    // mark newly imported doc as committed, sign and save
		    doc.replaceItemValue(Constants.ITEM_COMMITTED, "1");
		    doc.sign();
		    doc.save(true, false, true);

		    doc = null;

		}

		entry = veColl.getNextEntry(entry);

	    }

	} catch (Exception e) {
	    return false;
	}
	return true;
    }
}
