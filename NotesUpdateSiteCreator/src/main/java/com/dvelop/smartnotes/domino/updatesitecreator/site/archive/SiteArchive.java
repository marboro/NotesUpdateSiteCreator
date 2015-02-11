package com.dvelop.smartnotes.domino.updatesitecreator.site.archive;

import java.util.HashMap;
import java.util.Map;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Session;
import lotus.domino.View;

import com.dvelop.smartnotes.domino.updatesitecreator.common.Common;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Constants;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Resources;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Strings;
import com.dvelop.smartnotes.domino.updatesitecreator.event.Event;
import com.dvelop.smartnotes.domino.updatesitecreator.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;

public class SiteArchive extends Event {

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
	try {

	    m_session = session;
	    m_db = db;
	    m_viewArchives = oCtx.viewArchives;

	    path = oCtx.sPath;
	    url = oCtx.sURL;

	    if (url.startsWith(Constants.PREFIX_HTTP) || url.startsWith(Constants.PREFIX_HTTPS)) {
		isRemote = true;
		// Call oLog.Debug(sprintf2(LOG_REMOTE_ARCHIVE, m_sPath,
		// m_sURL))
	    } else {
		// Call oLog.Debug(sprintf2(LOG_LOCAL_ARCHIVE, m_sPath, m_sURL))
	    }

	    computeURL();

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    public void serialize() {

	try {

	    Document doc;
	    String sLabel = null;
	    String sLog;
	    String sViewXML;
	    Map<String, String> liItem = new HashMap<String, String>();

	    // only serialize remote archives (until we have full local
	    // mirroring built-in)
	    if (!isRemote) {
		// Call oLog.Debug(sprintf1(LOG_IGNORE_LOCAL_ARCHIVE, m_sPath))
		return;
	    }

	    // map items and archive properties
	    liItem.clear();
	    liItem.put(ITEM_ARCHIVE_URL, url);
	    liItem.put(ITEM_ARCHIVE_PATH, path);
	    liItem.put(ITEM_ARCHIVE_VIEW_XML, viewXML);

	    m_viewArchives.refresh();
	    doc = m_viewArchives.getDocumentByKey(path, true);

	    if (doc == null) {
		// no archive doc found for this particular archive, create new
		// one
		sLabel = Strings.sprintf1(Resources.MSG_IMPORTING_ARCHIVE, path);
		sLog = Strings.sprintf1(Resources.LOG_CREATE_ARCHIVE_DOC, path);
		doc = m_db.createDocument();
		doc.replaceItemValue(Constants.ITEM_FORM, FORM_ARCHIVE);
	    } else {
		// existing archive doc found, check if we need to update it
		sViewXML = doc.getItemValueString(ITEM_ARCHIVE_VIEW_XML);
		if (sViewXML == viewXML) {
		    // Call oLog.Write(sprintf1(LOG_UPTODATE_ARCHIVE_DOC,
		    // m_sPath))
		    return;
		}

		// remove old items
		sLabel = Strings.sprintf1(Resources.MSG_UPDATING_ARCHIVE, path);
		sLog = Strings.sprintf1(Resources.LOG_UPDATE_ARCHIVE_DOC, path);
		for (String item : liItem.keySet()) {
		    doc.removeItem(item);
		}
	    }

	    raiseEvent(Constants.QUEUE_PROGRESS_LABEL, sLabel);
	    raiseEvent(Constants.QUEUE_PROGRESS_BAR, 1);

	    // save all archive properties to items
	    for (String item : liItem.keySet()) {
		doc.replaceItemValue(item, liItem.get(item));
	    }

	    doc.save(true, false, true);
	    // Call oLog.Write(sLog)

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void computeURL() {

	try {

	    // build the xml tag for the view
	    viewXML = Strings.sprintf2(TAG_ARCHIVE, Common.encodeXML(path), Common.encodeXML(url));

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    @Override
    public String toString() {
	return "SiteArchive [path=" + path + ", url=" + url + "]";
    }
}
