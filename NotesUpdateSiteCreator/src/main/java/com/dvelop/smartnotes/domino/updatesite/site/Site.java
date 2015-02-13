package com.dvelop.smartnotes.domino.updatesite.site;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lotus.domino.Database;
import lotus.domino.Session;
import lotus.domino.Stream;
import lotus.domino.View;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dvelop.smartnotes.domino.updatesite.common.Common;
import com.dvelop.smartnotes.domino.updatesite.common.Constants;
import com.dvelop.smartnotes.domino.updatesite.event.Event;
import com.dvelop.smartnotes.domino.updatesite.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesite.exceptions.EventException;
import com.dvelop.smartnotes.domino.updatesite.exceptions.OException;
import com.dvelop.smartnotes.domino.updatesite.site.archive.SiteArchive;
import com.dvelop.smartnotes.domino.updatesite.site.archive.SiteArchiveContext;
import com.dvelop.smartnotes.domino.updatesite.site.category.SiteCategory;
import com.dvelop.smartnotes.domino.updatesite.site.category.SiteCategoryContext;
import com.dvelop.smartnotes.domino.updatesite.site.feature.SiteFeature;
import com.dvelop.smartnotes.domino.updatesite.site.feature.SiteFeatureContext;
import com.dvelop.smartnotes.domino.updatesite.site.feature.factory.SiteFeatureFactory;
import com.dvelop.smartnotes.domino.updatesite.stream.BOMStream;

public class Site extends Event {

    private EventRegistry eventRegistry;

    public Site(EventRegistry eventRegistry) {
	super(eventRegistry);
	this.eventRegistry = eventRegistry;
    }

    private Session session;
    private Database db;
    private org.w3c.dom.Document m_domDoc;
    private SiteFeatureFactory m_oFeatureFactory;
    private List<SiteCategory> m_liCategories = new ArrayList<SiteCategory>();
    private List<SiteArchive> m_liArchives = new ArrayList<SiteArchive>();

    private String m_sBaseFolderPath;
    private String m_sFilePath;
    private String m_sType;
    private String m_sURL;
    private String m_sMirrorsURL;
    private String m_sDescription;
    private String m_sDescriptionURL;

    private boolean m_bCancel;

    public List<SiteFeature> getFeatures() {
	List<SiteFeature> result = new ArrayList<SiteFeature>();
	if (m_oFeatureFactory != null) {
	    result.addAll(m_oFeatureFactory.getFeatures().values());
	}
	return result;
    }

    public List<SiteCategory> getCategories() {
	return m_liCategories;
    }

    public List<SiteArchive> getArchives() {
	return m_liArchives;
    }

    public Site(Session session, Database db, String sFilePath, EventRegistry eventRegistry) {
	this(eventRegistry);
	this.session = session;
	this.db = db;
	try {

	    subscribeEvents();

	    if (sFilePath.length() == 0) {
		// Error 5000, sprintf1(ERR_FILE_REQUIRED, Typename(Me))
	    }

	    // Call oLog.Write(sprintf1(LOG_IMPORTING_SITE, sFilePath))

	    m_oFeatureFactory = new SiteFeatureFactory(session, db, eventRegistry);
	    m_oFeatureFactory.setParent(this);

	    m_sFilePath = sFilePath;
	    m_sBaseFolderPath = new File(m_sFilePath).getParent() + Common.getGsOSPathSep();

	    process();

	} catch (EventException e) {
	    e.printStackTrace();
	}

    }

    public void delete() {

	try {
	    unsubscribeEvents();
	} catch (EventException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    private void process() {

	try {

	    Stream stream;
	    DocumentBuilder domParser;
	    BOMStream oBOMStream;

	    if (!new File(m_sFilePath).exists()) {
		// Error 5000, sprintf1(ERR_OPEN_FILE, m_sFilePath)
	    }

	    domParser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

	    // oBOMStream = new BOMStream(session);
	    // stream = oBOMStream.getStream(m_sFilePath);

	    // if (stream.isEOS()) {
	    // stream.close();
	    // // Error 5000, sprintf2(ERR_IMPORT_FILE, m_sFilePath,
	    // // ERR_EMPTY_FILE)
	    // }
	    //
	    // ByteArrayInputStream inputStream = new
	    // ByteArrayInputStream(stream.read());

	    // m_domDoc = domParser.parse(inputStream);
	    m_domDoc = domParser.parse(new File(m_sFilePath));

	    getSiteData();
	    getSiteDescription();
	    getSiteArchives();
	    getSiteCategories();
	    getSiteFeatures();

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), m_sFilePath);
	}

    }

    private void getSiteData() {
	try {

	    // 'get <site> attributes
	    m_sType = Common.domGetAttribute(m_domDoc.getDocumentElement(), Constants.ATT_TYPE);
	    m_sURL = Common.domGetAttribute(m_domDoc.getDocumentElement(), Constants.ATT_URL);
	    m_sMirrorsURL = Common.domGetAttribute(m_domDoc.getDocumentElement(), Constants.ATT_MIRRORSURL);

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void getSiteDescription() {

	try {

	    Node domDescNode;
	    NodeList domNodeList;
	    int iTagIndex;

	    // 'get collection of all <site><description> tags
	    // (there should be just one, so we only use the 1st one we find)
	    domNodeList = m_domDoc.getElementsByTagName(Constants.TAG_DESCRIPTION);
	    for (iTagIndex = 0; iTagIndex < domNodeList.getLength(); iTagIndex++) {

		// only read nodes directly under <site> tag
		domDescNode = domNodeList.item(iTagIndex);
		if (domDescNode.getParentNode().getNodeName() == Constants.TAG_SITE) {

		    // get <description url> attribute
		    m_sURL = Common.domGetAttribute(domDescNode, Constants.ATT_URL);

		    // get description text
		    if (domDescNode.hasChildNodes()) {
			m_sDescription = Common.getFormattedText(domDescNode.getFirstChild().getNodeValue());
		    }

		} // only read the 1st site description we find

	    }

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void getSiteArchives() {
	try {

	    View viewArchives;
	    Node domArcNode;
	    NodeList domNodeList;
	    SiteArchive oArchive;
	    SiteArchiveContext oCtx;
	    int iTagIndex;

	    viewArchives = db.getView(Constants.VIEW_ARCHIVES);
	    if (viewArchives == null) {
		// Error 5000, sprintf1(ERR_VIEW_NOT_FOUND, VIEW_ARCHIVES)
	    }

	    // get collection of <archive> tags
	    domNodeList = m_domDoc.getElementsByTagName(Constants.TAG_ARCHIVE);
	    for (iTagIndex = 0; iTagIndex < domNodeList.getLength(); iTagIndex++) {

		// only read nodes directly under <site> tag
		domArcNode = domNodeList.item(iTagIndex);
		if (domArcNode.getParentNode().getNodeName().equals(Constants.TAG_SITE)) {

		    oCtx = new SiteArchiveContext();
		    oCtx.viewArchives = viewArchives;

		    // get <archive> attributes
		    oCtx.sPath = Common.domGetAttribute(domArcNode, Constants.ATT_PATH);
		    oCtx.sURL = Common.domGetAttribute(domArcNode, Constants.ATT_URL);

		    // factor object and add it to collection
		    oArchive = new SiteArchive(session, db, oCtx, eventRegistry);
		    m_liArchives.add(oArchive);

		}

	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void getSiteCategories() {
	try {

	    View viewCategories;
	    Node domCatNode;
	    Node domDescNode;
	    NodeList domNodeList;
	    SiteCategory oCategory;
	    SiteCategoryContext oCtx;
	    int iTagIndex;

	    viewCategories = db.getView(Constants.VIEW_CATEGORIES);
	    if (viewCategories == null) {
		// Error 5000, sprintf1(ERR_VIEW_NOT_FOUND, VIEW_CATEGORIES);
	    }

	    // get collection of all <site><category-def> tags
	    domNodeList = m_domDoc.getElementsByTagName(Constants.TAG_CATEGORYDEF);
	    for (iTagIndex = 0; iTagIndex < domNodeList.getLength(); iTagIndex++) {

		// only read nodes directly under <site> tag
		domCatNode = domNodeList.item(iTagIndex);
		if (domCatNode.getParentNode().getNodeName().equals(Constants.TAG_SITE)) {

		    oCtx = new SiteCategoryContext();
		    oCtx.viewCategories = viewCategories;

		    // get <category-def> attributes
		    oCtx.sName = Common.domGetAttribute(domCatNode, Constants.ATT_NAME);
		    oCtx.sLabel = Common.domGetAttribute(domCatNode, Constants.ATT_LABEL);

		    // get the <description> child node
		    domDescNode = domCatNode.getFirstChild();
		    while (domDescNode != null) {

			// get description URL and text
			if (domDescNode.getNodeName().equals(Constants.TAG_DESCRIPTION)) {
			    oCtx.sURL = Common.domGetAttribute(domDescNode, Constants.ATT_URL);
			    if (domDescNode.hasChildNodes()) {
				oCtx.sDescription = Common.getFormattedText(domDescNode.getFirstChild().getNodeValue());
			    }
			    break;
			}

			domDescNode = domDescNode.getNextSibling();

		    }

		    // factor object and add it to collection
		    oCategory = new SiteCategory(session, db, oCtx, eventRegistry);
		    m_liCategories.add(oCategory);

		}

	    }

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void getSiteFeatures() {
	try {

	    View viewFeatures;
	    View viewPlugins;
	    Node domFeatNode;
	    Node domCatNode;
	    NodeList domNodeList;
	    SiteFeatureContext oCtx;
	    int iTagIndex;
	    String sURL;
	    String sPatch;
	    String sCategory = "";

	    viewFeatures = db.getView(Constants.VIEW_FEATURES);
	    viewPlugins = db.getView(Constants.VIEW_PLUGINS);

	    if (viewFeatures == null) {
		// Error 5000, sprintf1(ERR_VIEW_NOT_FOUND, VIEW_FEATURES)
	    }
	    if (viewPlugins == null) {
		// Error 5000,sprintf1(ERR_VIEW_NOT_FOUND,VIEW_PLUGINS)
	    }

	    // 'get collection of all <feature> tags
	    domNodeList = m_domDoc.getElementsByTagName(Constants.TAG_SITE).item(0).getChildNodes();
	    for (iTagIndex = 0; iTagIndex < domNodeList.getLength(); iTagIndex++) {
		Node node = domNodeList.item(iTagIndex);
		if (node == null) {
		    continue;
		}
		String localName = node.getNodeName();
		if (localName == null) {
		    continue;
		}
		if ("".equals(localName)) {
		    continue;
		}
		if (!localName.equals(Constants.TAG_FEATURE)) {
		    continue;
		}

		// 'only read nodes directly under <site> tag
		domFeatNode = domNodeList.item(iTagIndex);
		if (domFeatNode.getParentNode().getNodeName().equals(Constants.TAG_SITE)) {

		    // 'get minimum required <feature> attributes (all others
		    // come from feature.xml)
		    sURL = Common.domGetAttribute(domFeatNode, Constants.ATT_URL);
		    sPatch = Common.domGetAttribute(domFeatNode, Constants.ATT_PATCH);

		    // 'get the <category> node
		    domCatNode = domFeatNode.getFirstChild();
		    while (domCatNode != null) {

			// 'get <category name> attribute
			if (domCatNode.getNodeName().equals(Constants.TAG_CATEGORY)) {
			    sCategory = Common.domGetAttribute(domCatNode, Constants.ATT_NAME);
			    break;
			}
			domCatNode = domCatNode.getNextSibling();

		    }

		    // 'setup feature context
		    oCtx = new SiteFeatureContext();
		    oCtx.vParentFactory = m_oFeatureFactory;
		    oCtx.viewFeatures = viewFeatures;
		    oCtx.viewPlugins = viewPlugins;
		    oCtx.sURL = sURL;
		    oCtx.sPatch = sPatch;
		    oCtx.sCategory = sCategory;
		    oCtx.sBaseFolderPath = m_sBaseFolderPath;
		    oCtx.sJarFilePath = m_sBaseFolderPath + sURL;

		    // 'factor new feature object
		    m_oFeatureFactory.factorNewFeature(oCtx);

		}

		// 'exit loop if the UI wants to cancel
		if (m_bCancel) {
		    return;
		}

	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	}
    }

    public void onReceiveEvent(String sQueueName, Event vEvent) {
	// 'implements the event listener interface from the cEvent class
	try {
	    if (sQueueName.equalsIgnoreCase(Constants.QUEUE_CANCEL_UI)) {
		m_bCancel = true;
	    }
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	}
    }
}
