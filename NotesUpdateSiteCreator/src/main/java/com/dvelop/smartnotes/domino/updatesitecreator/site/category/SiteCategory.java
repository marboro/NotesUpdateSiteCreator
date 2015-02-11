package com.dvelop.smartnotes.domino.updatesitecreator.site.category;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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

public class SiteCategory extends Event {

    private Logger logger = Logger.getLogger(SiteCategory.class.getName());

    public SiteCategory(EventRegistry eventRegistry) {
	super(eventRegistry);
    }

    private final String FORM_CATEGORY = "fmCategory";

    private final String ITEM_CAT_NAME = "category.name";
    private final String ITEM_CAT_LABEL = "category.label";
    private final String ITEM_CAT_DESCRIPTION = "category.description";
    private final String ITEM_CAT_URL = "category.url";
    private final String ITEM_CAT_VIEW_XML = "category.view.xml";

    private final String TAG_CAT_DEF_OPEN = "<category-def name=\"%s1\" label=\"%s2\">";
    private final String TAG_DESCRIPTION = " <description url=\"%s1\">%s2</description>";
    private final String TAG_CAT_DEF_CLOSE = "</category-def>";

    private Session m_session;
    private Database m_db;
    private View m_viewCategories;
    private String m_sName;
    private String m_sLabel;
    private String m_sDescription;
    private String m_sURL;
    private String m_sViewXML;

    public SiteCategory(Session session, Database db, SiteCategoryContext oCtx, EventRegistry eventRegistry) {
	this(eventRegistry);
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create SiteCategory");
	try {
	    m_session = session;
	    m_db = db;
	    m_viewCategories = oCtx.viewCategories;

	    m_sName = oCtx.sName;
	    m_sLabel = oCtx.sLabel;
	    m_sDescription = oCtx.sDescription;
	    m_sURL = oCtx.sURL;

	    logger.fine(Strings.sprintf2(Resources.LOG_CATEGORY, m_sName, m_sLabel));
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
	    String sLabel;
	    String sLog;
	    String sViewXML;
	    Map<String, String> liItem = new HashMap<String, String>();

	    // map items and category properties
	    logger.fine("map items and category properties");
	    liItem.clear();
	    liItem.put(ITEM_CAT_NAME, m_sName);
	    liItem.put(ITEM_CAT_LABEL, m_sLabel);
	    liItem.put(ITEM_CAT_DESCRIPTION, m_sDescription);
	    liItem.put(ITEM_CAT_URL, m_sURL);
	    liItem.put(ITEM_CAT_VIEW_XML, m_sViewXML);

	    m_viewCategories.refresh();
	    doc = m_viewCategories.getDocumentByKey(m_sName, true);

	    if (doc == null) {
		// no category doc found for this particular category, create
		// new one
		sLabel = Strings.sprintf1(Resources.MSG_IMPORTING_CATEGORY, m_sName);
		sLog = Strings.sprintf1(Resources.LOG_CREATE_CATEGORY_DOC, m_sName);
		doc = m_db.createDocument();
		doc.replaceItemValue(Constants.ITEM_FORM, FORM_CATEGORY);
	    } else {
		// existing category doc found, check if we need to update it
		sViewXML = doc.getItemValueString(ITEM_CAT_VIEW_XML);
		if (sViewXML.equals(m_sViewXML)) {
		    // Call oLog.Write(sprintf1(LOG_UPTODATE_CATEGORY_DOC,
		    // m_sName))
		    return;
		}

		// remove old items
		sLabel = Strings.sprintf1(Resources.MSG_UPDATING_CATEGORY, m_sName);
		sLog = Strings.sprintf1(Resources.LOG_UPDATE_CATEGORY_DOC, m_sName);
		for (String item : liItem.keySet()) {
		    doc.removeItem(item);
		}
	    }

	    raiseEvent(Constants.QUEUE_PROGRESS_LABEL, sLabel);
	    raiseEvent(Constants.QUEUE_PROGRESS_BAR, 1);

	    // save all category properties to items
	    for (String item : liItem.keySet()) {
		doc.replaceItemValue(item, liItem.get(item));
	    }

	    doc.save(true, false, true);
	    // Call oLog.Write(sLog)

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}

    }

    private void computeURL() {

	try {

	    // build the xml tags for the view
	    m_sViewXML = "";
	    m_sViewXML = m_sViewXML + Strings.sprintf2(TAG_CAT_DEF_OPEN, Common.encodeXML(m_sName), Common.encodeXML(m_sLabel)) + Strings.CRLF;
	    m_sViewXML = m_sViewXML + Strings.sprintf2(TAG_DESCRIPTION, Common.encodeXML(m_sURL), Common.encodeXML(m_sDescription)) + Strings.CRLF;
	    m_sViewXML = m_sViewXML + TAG_CAT_DEF_CLOSE;

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    @Override
    public String toString() {
	return "SiteCategory [m_sName=" + m_sName + ", m_sLabel=" + m_sLabel + ", m_sDescription=" + m_sDescription + ", m_sURL=" + m_sURL + "]";
    }

}
