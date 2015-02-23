package com.dvelop.smartnotes.domino.updatesite.site.archive;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Session;
import lotus.domino.View;

import com.dvelop.smartnotes.domino.common.Common;
import com.dvelop.smartnotes.domino.common.Strings;
import com.dvelop.smartnotes.domino.resources.Constants;
import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.updatesite.event.Event;
import com.dvelop.smartnotes.domino.updatesite.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesite.exceptions.OException;

public class SiteArchive extends Event {
    private Logger logger = Logger.getLogger(SiteArchive.class.getName());
    private final String FORM_ARCHIVE = "fmArchive";

    private final String ITEM_ARCHIVE_PATH = "archive.path";
    private final String ITEM_ARCHIVE_URL = "archive.url";
    private final String ITEM_ARCHIVE_VIEW_XML = "archive.view.xml";

    private final String TAG_ARCHIVE = "<archive path=\"%s1\" url=\"%s2\"/>";

    public SiteArchive(EventRegistry eventRegistry) {
	super(eventRegistry);
    }

    private Session m_session;
    private Database m_db;
    private View m_viewArchives;
    private String path;
    private String url;
    private String viewXML;
    private boolean isRemote;

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public String getViewXML() {
	return viewXML;
    }

    public void setViewXML(String viewXML) {
	this.viewXML = viewXML;
    }

    public boolean isRemote() {
	return isRemote;
    }

    public void setRemote(boolean isRemote) {
	this.isRemote = isRemote;
    }

    public String getUrl() {
	return url;
    }

    public SiteArchive(Session session, Database db, SiteArchiveContext oCtx, EventRegistry eventRegistry) {
	this(eventRegistry);
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create SiteArchive");
	try {

	    m_session = session;
	    m_db = db;
	    m_viewArchives = oCtx.viewArchives;

	    path = oCtx.sPath;
	    url = oCtx.sURL;

	    if (url.startsWith(Constants.PREFIX_HTTP) || url.startsWith(Constants.PREFIX_HTTPS)) {
		isRemote = true;
		logger.fine(Strings.sprintf2(Resources.LOG_REMOTE_ARCHIVE, path, url));
	    } else {
		logger.fine(Strings.sprintf2(Resources.LOG_LOCAL_ARCHIVE, path, url));
	    }

	    logger.fine("compute URL");
	    computeURL();

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}

    }

    public void serialize() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start serializing");
	try {

	    Document doc;
	    String sLabel = null;
	    String sLog;
	    String sViewXML;
	    Map<String, String> liItem = new HashMap<String, String>();

	    // only serialize remote archives (until we have full local
	    // mirroring built-in)
	    logger.fine("only serialize remote archives (until we have full local mirroring built-in)");
	    if (!isRemote) {
		logger.fine(Strings.sprintf1(Resources.LOG_IGNORE_LOCAL_ARCHIVE, path));
		return;
	    }

	    // map items and archive properties
	    logger.fine("map items and archive properties");
	    liItem.clear();
	    liItem.put(ITEM_ARCHIVE_URL, url);
	    liItem.put(ITEM_ARCHIVE_PATH, path);
	    liItem.put(ITEM_ARCHIVE_VIEW_XML, viewXML);

	    logger.fine("refresh archives view");
	    m_viewArchives.refresh();
	    logger.fine("get document by key " + path);
	    doc = m_viewArchives.getDocumentByKey(path, true);

	    if (doc == null) {
		// no archive doc found for this particular archive, create new
		// one
		logger.fine("no archive doc found for this particular archive, create new one");
		sLabel = Strings.sprintf1(Resources.MSG_IMPORTING_ARCHIVE, path);
		sLog = Strings.sprintf1(Resources.LOG_CREATE_ARCHIVE_DOC, path);
		logger.fine("create document " + FORM_ARCHIVE);
		doc = m_db.createDocument();
		doc.replaceItemValue(Constants.ITEM_FORM, FORM_ARCHIVE);
	    } else {
		// existing archive doc found, check if we need to update it
		logger.fine("existing archive doc found, check if we need to update it");
		sViewXML = doc.getItemValueString(ITEM_ARCHIVE_VIEW_XML);
		if (sViewXML == viewXML) {
		    logger.fine(Strings.sprintf1(Resources.LOG_UPTODATE_ARCHIVE_DOC, path));
		    return;
		}

		// remove old items
		logger.fine("remove old items");
		sLabel = Strings.sprintf1(Resources.MSG_UPDATING_ARCHIVE, path);
		sLog = Strings.sprintf1(Resources.LOG_UPDATE_ARCHIVE_DOC, path);
		for (String item : liItem.keySet()) {
		    doc.removeItem(item);
		}
	    }

	    raiseEvent(Constants.QUEUE_PROGRESS_LABEL, sLabel);
	    raiseEvent(Constants.QUEUE_PROGRESS_BAR, 1);

	    // save all archive properties to items
	    logger.fine("save all archive properties to items");
	    for (String item : liItem.keySet()) {
		logger.fine("replace value for " + item + " with " + liItem.get(item));
		doc.replaceItemValue(item, liItem.get(item));
	    }

	    doc.save(true, false, true);
	    logger.fine(sLog);

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}

    }

    private void computeURL() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start compute URL");
	try {

	    // build the xml tag for the view
	    logger.fine("build the xml tag for the view");
	    viewXML = Strings.sprintf2(TAG_ARCHIVE, Common.encodeXML(path), Common.encodeXML(url));
	    logger.fine(viewXML);

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}

    }

    @Override
    public String toString() {
	return "SiteArchive [path=" + path + ", url=" + url + "]";
    }
}
