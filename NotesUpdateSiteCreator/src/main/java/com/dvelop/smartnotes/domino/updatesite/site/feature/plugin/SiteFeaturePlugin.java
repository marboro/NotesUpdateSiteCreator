package com.dvelop.smartnotes.domino.updatesite.site.feature.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.RichTextItem;
import lotus.domino.RichTextStyle;
import lotus.domino.Session;
import lotus.domino.View;

import com.dvelop.smartnotes.domino.common.Common;
import com.dvelop.smartnotes.domino.common.Strings;
import com.dvelop.smartnotes.domino.resources.Constants;
import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.updatesite.event.Event;
import com.dvelop.smartnotes.domino.updatesite.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesite.exceptions.OException;
import com.dvelop.smartnotes.domino.updatesite.jar.JarReader;
import com.dvelop.smartnotes.domino.updatesite.properties.PropertiesReader;
import com.dvelop.smartnotes.domino.updatesite.site.archive.SiteArchive;
import com.dvelop.smartnotes.domino.updatesite.site.feature.SiteFeature;

public class SiteFeaturePlugin extends Event {
    private Logger logger = Logger.getLogger(SiteFeaturePlugin.class.getName());

    private EventRegistry eventRegistry;
    private SiteFeaturePluginContext oCtx;

    public SiteFeaturePlugin(EventRegistry eventRegistry) {
	super(eventRegistry);
	this.eventRegistry = eventRegistry;
    }

    private Session m_session;
    private Database m_db;
    private View m_viewPlugins;
    private Document m_doc;
    private JarReader m_oJar;

    private String m_sViewLookupKey;

    private SiteFeature m_vParentFeature;
    private String m_sBaseFolderPath;
    private String m_sURL;
    private String m_sJarFilePath;
    private String m_sJarFileLastModified;
    private String m_sPropertyFile;
    private boolean m_bIsMissing;
    private boolean m_bIsModified;
    private boolean m_bHasManifest;

    private String m_sName;
    private String m_sProviderName;
    private String m_sID;
    private String m_sVersionOriginal;
    private String m_sVersionFixedUp;
    private String m_sClass;
    private String m_sFragment;

    private String m_sProperties;
    private String m_sManifestMF;
    private String m_sManifestXML;
    private String m_sViewXML;

    public Document getDocument() {
	return m_doc;
    }

    public String getJarFilePath() {
	return m_sJarFilePath;
    }

    public boolean isMissing() {
	return m_bIsMissing;
    }

    public String getID() {
	return m_sID;
    }

    public String getVersion() {
	return m_sVersionFixedUp;
    }

    public boolean isFragment() {
	return Boolean.parseBoolean(m_sFragment);
    }

    public SiteFeaturePlugin(Session session, Database db, SiteFeaturePluginContext oCtx, EventRegistry eventRegistry) {
	this(eventRegistry);
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create SiteFeaturePlugin");
	this.oCtx = oCtx;
	try {
	    List<SiteArchive> vArchives;

	    m_session = session;
	    m_db = db;
	    m_viewPlugins = oCtx.viewPlugins;
	    m_vParentFeature = oCtx.vParentFeature;

	    m_sURL = oCtx.sURL;
	    m_sID = oCtx.sID;
	    m_sVersionOriginal = oCtx.sVersion;
	    m_sBaseFolderPath = oCtx.sBaseFolderPath;
	    m_sViewLookupKey = Strings.sprintf2(Constants.FORMAT_ID_VERSION, oCtx.sID, oCtx.sVersion);
	    m_sFragment = "false";

	    // 'for site imports only (individual feature imports have no "site"
	    // parent and thus no archives)
	    logger.fine("for site imports only (individual feature imports have no \"site\" parent and thus no archives)");
	    if (m_vParentFeature.getParentFactory().getParent() != null) {

		// 'apply any local archive mappings
		logger.fine("apply any local archive mappings");
		vArchives = m_vParentFeature.getParentFactory().getParent().getArchives();
		for (SiteArchive archive : vArchives) {
		    logger.fine("Archive: " + archive);
		    if (!archive.isRemote()) {
			if (archive.getPath().equals(m_sURL)) {
			    m_sURL = archive.getUrl();
			    logger.fine("Archive Path: " + m_sURL);
			}
		    }
		}

	    }

	    // 'only process if the plugin actually exists as a .jar file on
	    // disk
	    logger.fine("only process if the plugin actually exists as a .jar file on disk");
	    if (hasJarFile()) {
		logger.fine("call process");
		process();
		logger.fine("call compute URL");
		computeUrl();
	    } else {
		m_bIsMissing = true;
	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private boolean hasJarFile() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("has jar file");
	try {
	    List<String> liSuffix = new ArrayList<String>();

	    m_sJarFilePath = m_sBaseFolderPath + m_sURL.replace("/", File.separator);
	    m_sVersionFixedUp = m_sVersionOriginal;

	    // 'check if we can find the plugin .jar right away
	    logger.fine("check if we can find the plugin .jar right away");
	    if (new File(m_sJarFilePath).exists()) {
		return true;
	    }

	    // 'vary the version suffixes and check if we can find it now
	    logger.fine("vary the version suffixes and check if we can find it now");
	    liSuffix.add("");
	    liSuffix.add(".0");
	    liSuffix.add(".0.0");
	    liSuffix.add(".0.0.0");

	    for (String suffix : liSuffix) {
		m_sJarFilePath = m_sBaseFolderPath + Strings.sprintf2(Constants.PLUGIN_URL, m_sID, m_sVersionOriginal + suffix);
		if (new File(m_sJarFilePath).exists()) {
		    m_sVersionFixedUp = m_sVersionOriginal + suffix;
		    logger.fine("Version: " + m_sVersionFixedUp);
		    return true;

		}
	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return false;
    }

    private void process() {
	try {

	    Document doc;
	    String sLabel;

	    sLabel = Strings.sprintf1(Resources.MSG_READING_PLUGIN, m_sJarFilePath.substring(0, m_sJarFilePath.lastIndexOf(File.separator)));
	    raiseEvent(Constants.QUEUE_PROGRESS_LABEL, sLabel);

	    logger.fine("Read the Jar-File");
	    m_oJar = new JarReader(m_sJarFilePath);

	    // 'compare jarfiles: If the timestamps are identical, don't do
	    // anything
	    logger.fine("compare jarfiles: If the timestamps are identical, don't do anything");
	    m_sJarFileLastModified = m_oJar.getLastModified();
	    logger.fine("get document by key: " + m_sViewLookupKey);
	    doc = m_viewPlugins.getDocumentByKey(m_sViewLookupKey, true);
	    if (doc != null) {
		if (doc.getItemValueString(Constants.ITEM_PLUGIN_FILE_LASTMODIFIED).equals(m_sJarFileLastModified)) {
		    logger.fine("last modified version!");
		    return;
		}
	    }

	    // 'check for the OSGi manifest.mf file first
	    logger.fine("check for the OSGi manifest.mf file first");
	    if (m_oJar.hasManifest()) {
		logger.fine("call readManifest");
		readManifest();
	    }

	    // 'check if there is a 'plugin.xml' file
	    logger.fine("check if there is a 'plugin.xml' file");
	    if (m_oJar.hasFile(Constants.FILE_PLUGIN_XML)) {
		logger.fine("call readPluginXML");
		readPluginXML();
	    }

	    // 'check if there is a 'fragment.xml' file
	    logger.fine("check if there is a 'fragment.xml' file");
	    if (m_oJar.hasFile(Constants.FILE_FRAGMENT_XML)) {
		logger.fine("call readFragmentXML");
		readFragmentXML();
	    }

	    // 'if we don't have a properties file at this point, default to the
	    // OSGi bundle localization default file
	    logger.fine("if we don't have a properties file at this point, default to the OSGi bundle localization default file");
	    if (m_sPropertyFile.length() == 0) {
		if (m_bHasManifest) {
		    m_sPropertyFile = Constants.BUNDLE_DEFAULT;
		}
	    }

	    // 'load the properties file from the jar
	    // 'http://java.sun.com/j2se/1.5.0/docs/api/java/util/Properties.html
	    logger.fine("load the properties file from the jar http://java.sun.com/j2se/1.5.0/docs/api/java/util/Properties.html");
	    m_sProperties = m_oJar.getFileAsText(m_sPropertyFile, "ISO-8859-1");

	    // 'we're done with the jar, clean up associated resources
	    // 'mark this plugin 'dirty' so that it will get re-serialized
	    logger.fine("we're done with the jar, clean up associated resources mark this plugin 'dirty' so that it will get re-serialized");
	    m_bIsModified = true;
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

	    RichTextStyle rtStyle;
	    RichTextItem rtFile;
	    RichTextItem rtManifestMF;
	    RichTextItem rtManifestXML;
	    PropertiesReader oProps;

	    String vResourced;
	    Map<String, String> liItem = new HashMap<String, String>();
	    Map<String, String> liRtItem = new HashMap<String, String>();
	    String sLabel;
	    String sLog;
	    String sURL;

	    sURL = Strings.sprintf2(Constants.PLUGIN_URL, m_sID, m_sVersionOriginal);

	    // 'if this object hasn't been modified, don't do anything
	    logger.fine("if this object hasn't been modified, don't do anything");
	    if (!m_bIsModified) {
		logger.fine(Strings.sprintf1(Resources.LOG_UPTODATE_PLUGIN_DOC, sURL));
		return;
	    }

	    // 'list of rich text items in use
	    logger.fine("list of rich text items in use");
	    liRtItem.put(Constants.ITEM_PLUGIN_FILE, Constants.ITEM_PLUGIN_FILE);
	    liRtItem.put(Constants.ITEM_PLUGIN_MANIFEST_MF, Constants.ITEM_PLUGIN_MANIFEST_MF);
	    liRtItem.put(Constants.ITEM_PLUGIN_MANIFEST_XML, Constants.ITEM_PLUGIN_MANIFEST_XML);

	    // 'map items and plugin properties
	    logger.fine("map items and plugin properties");
	    liItem.put(Constants.ITEM_PLUGIN_ID, m_sID);
	    liItem.put(Constants.ITEM_PLUGIN_VERSION, m_sVersionFixedUp);
	    liItem.put(Constants.ITEM_PLUGIN_NAME, m_sName);
	    liItem.put(Constants.ITEM_PLUGIN_PROVIDERNAME, m_sProviderName);
	    liItem.put(Constants.ITEM_PLUGIN_CLASS, m_sClass);
	    liItem.put(Constants.ITEM_PLUGIN_VIEW_XML, m_sViewXML);
	    liItem.put(Constants.ITEM_PLUGIN_FRAGMENT, m_sFragment);
	    liItem.put(Constants.ITEM_PLUGIN_FILE_LASTMODIFIED, m_sJarFileLastModified);
	    liItem.put(Constants.ITEM_PLUGIN_MANIFEST_XML_AVAIL, ""); // 'initially
								      // leave
								      // this
								      // empty

	    logger.fine("refresh view");
	    m_viewPlugins.refresh();
	    logger.fine("get document by key: " + m_sViewLookupKey);
	    m_doc = m_viewPlugins.getDocumentByKey(m_sViewLookupKey, true);
	    if (m_doc == null) {
		// 'no plugin doc found for this particular plugin and version,
		// create one
		logger.fine("no plugin doc found for this particular plugin and version, create one");
		sLabel = Strings.sprintf1(Resources.MSG_IMPORTING_PLUGIN, m_sJarFilePath.substring(0, m_sJarFilePath.lastIndexOf(File.separator)));
		sLog = Strings.sprintf1(Resources.LOG_CREATE_PLUGIN_DOC, sURL);
		logger.fine("create document " + Constants.FORM_PLUGIN);
		m_doc = m_db.createDocument();
		m_doc.replaceItemValue(Constants.ITEM_FORM, Constants.FORM_PLUGIN);
	    } else {

		// 'existing plugin doc found
		logger.fine("existing plugin doc found");
		sLabel = Strings.sprintf1(Resources.MSG_UPDATING_PLUGIN, m_sJarFilePath.substring(0, m_sJarFilePath.lastIndexOf(File.separator)));
		sLog = Strings.sprintf1(Resources.LOG_UPDATE_PLUGIN_DOC, sURL);

		// 'remove old items
		logger.fine("remove old items");
		for (String item : liItem.keySet()) {
		    m_doc.removeItem(liItem.get(item));
		}

		// '..and rich text items
		logger.fine("..and rich text items");
		for (String rti : liRtItem.keySet()) {
		    m_doc.removeItem(liItem.get(rti));
		}

	    }

	    raiseEvent(Constants.QUEUE_PROGRESS_LABEL, sLabel);
	    raiseEvent(Constants.QUEUE_PROGRESS_BAR, 1);

	    logger.fine("create richtext style");
	    rtStyle = m_session.createRichTextStyle();
	    rtStyle.setFontSize(9);
	    rtStyle.setFont(RichTextStyle.FONT_COURIER);

	    // 'attach plugin jar file
	    logger.fine("attach plugin jar file");
	    rtFile = m_doc.createRichTextItem(Constants.ITEM_PLUGIN_FILE);
	    rtFile.appendStyle(rtStyle);
	    rtFile.embedObject(EmbeddedObject.EMBED_ATTACHMENT, "", m_sJarFilePath, null);

	    // 'store manifest.mf file content
	    logger.fine("store manifest.mf file content");
	    if (m_sManifestMF.length() > 0) {
		rtManifestMF = m_doc.createRichTextItem(Constants.ITEM_PLUGIN_MANIFEST_MF);
		rtManifestMF.appendStyle(rtStyle);
		rtManifestMF.appendText(m_sManifestMF);
		rtManifestMF.compact();
	    }

	    // 'store plugin.xml file content
	    logger.fine("store plugin.xml file content");
	    if (m_sManifestXML.length() > 0) {
		rtManifestXML = m_doc.createRichTextItem(Constants.ITEM_PLUGIN_MANIFEST_XML);
		rtManifestXML.appendStyle(rtStyle);
		rtManifestXML.appendText(m_sManifestXML);
		rtManifestXML.compact();
		liItem.remove(Constants.ITEM_PLUGIN_MANIFEST_XML_AVAIL);
		liItem.put(Constants.ITEM_PLUGIN_MANIFEST_XML_AVAIL, "1"); // 'set
									   // summary
									   // item
									   // to
									   // indicate
									   // available
									   // plugin.xml
									   // rtitem
	    }

	    // 'get the resource properties file
	    logger.fine("get the resource properties file");
	    oProps = new PropertiesReader();
	    oProps.loadString(m_sProperties);

	    // 'save all plugin properties to items
	    logger.fine("save all plugin properties to items");
	    for (String item : liItem.keySet()) {
		// 'get the resource strings
		logger.fine("get the resource string: " + item);
		vResourced = oProps.getProperties(liItem.get(item));

		logger.fine("replace value for " + item + " with " + vResourced);
		m_doc.replaceItemValue(item, vResourced);

	    }

	    m_doc.save(true, false, true);
	    logger.fine(sLog);
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), m_sViewLookupKey);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void readManifest() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("read manifest");
	try {

	    // 'get the entire content
	    logger.fine("get the entire content");
	    m_sManifestMF = m_oJar.getManifestAsText();

	    // 'get manifest values
	    logger.fine("get manifest values");
	    m_sName = m_oJar.getManifestValue(Constants.MF_BUNDLE_NAME);
	    m_sProviderName = m_oJar.getManifestValue(Constants.MF_BUNDLE_VENDOR);
	    m_sClass = m_oJar.getManifestValue(Constants.MF_BUNDLE_ACTIVATOR);
	    m_sPropertyFile = m_oJar.getManifestValue(Constants.MF_BUNDLE_LOCALIZATION);

	    // 'get resource properties filepath
	    logger.fine("get resource properties filepath");
	    if (m_sPropertyFile.length() > 0) {
		if (m_sPropertyFile.length() > Constants.PROPERTIES_EXTENSION.length()) {
		    int length = m_sPropertyFile.length() - Constants.PROPERTIES_EXTENSION.length();
		    String substring = m_sPropertyFile.substring(length);
		    if (!substring.equalsIgnoreCase(Constants.PROPERTIES_EXTENSION)) {
			m_sPropertyFile = m_sPropertyFile + Constants.PROPERTIES_EXTENSION;
		    }
		} else {
		    m_sPropertyFile = m_sPropertyFile + Constants.PROPERTIES_EXTENSION;
		}
		logger.fine(m_sPropertyFile);
	    }

	    // 'check if this is a fragment
	    logger.fine("check if this is a fragment");
	    if (m_oJar.getManifestValue(Constants.MF_FRAGMENT_HOST).length() > 0) {
		m_sFragment = "true";
	    }

	    m_bHasManifest = true;

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void readPluginXML() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("read plugin XML");
	try {

	    DocumentBuilder domParser;
	    org.w3c.dom.Document domDoc;
	    String sEncoding;

	    sEncoding = m_oJar.getFileXMLEncoding(Constants.FILE_PLUGIN_XML);
	    m_sManifestXML = m_oJar.getFileAsText(Constants.FILE_PLUGIN_XML, sEncoding);
	    logger.fine(m_sManifestXML);

	    domParser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    domDoc = domParser.parse(m_oJar.getFileAsInputStream(Constants.FILE_PLUGIN_XML, sEncoding));

	    // 'get <plugin> attributes only if we havn't gotten them from the
	    // OSGi manifest already
	    logger.fine("get <plugin> attributes only if we havn't gotten them from the OSGi manifest already");
	    if (m_sName.length() == 0) {
		m_sName = Common.domGetAttribute(domDoc, Constants.ATT_NAME);
		logger.fine("Name: " + m_sName);
	    }

	    if (m_sProviderName.length() == 0) {
		m_sProviderName = Common.domGetAttribute(domDoc, Constants.ATT_PROVIDER_NAME);
		logger.fine("Provider Name: " + m_sProviderName);
	    }

	    if (m_sClass.length() == 0) {
		m_sClass = Common.domGetAttribute(domDoc, Constants.ATT_CLASS);
		logger.fine("Class: " + m_sClass);
	    }

	    // 'set the resource properties filepath
	    logger.fine("set the resource properties filepath");
	    if (m_sPropertyFile.length() == 0) {
		m_sPropertyFile = Constants.FILE_PLUGIN_PROPERTIES;
		logger.fine("Property File: " + m_sPropertyFile);
	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void readFragmentXML() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine(Resources.LOG_SEPARATOR_END);
	try {

	    DocumentBuilder domParser;
	    org.w3c.dom.Document domDoc;
	    String sEncoding;

	    sEncoding = m_oJar.getFileXMLEncoding(Constants.FILE_FRAGMENT_XML);
	    m_sManifestXML = m_oJar.getFileAsText(Constants.FILE_FRAGMENT_XML, sEncoding);
	    logger.info(m_sManifestXML);

	    domParser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    domDoc = domParser.parse(m_oJar.getFileAsInputStream(Constants.FILE_FRAGMENT_XML, sEncoding));

	    // 'get <fragment> attributes only if we havn't gotten them from the
	    // OSGi manifest already
	    logger.fine("get <fragment> attributes only if we havn't gotten them from the OSGi manifest already");
	    if (m_sName.length() == 0) {
		m_sName = Common.domGetAttribute(domDoc, Constants.ATT_NAME);
		logger.fine("Name: " + m_sName);
	    }

	    if (m_sProviderName.length() == 0) {
		m_sProviderName = Common.domGetAttribute(domDoc, Constants.ATT_PROVIDER_NAME);
		logger.fine("Provider Name: " + m_sProviderName);
	    }

	    // 'set the resource properties filepath
	    logger.fine("set the resource properties filepath");
	    if (m_sPropertyFile.length() == 0) {
		m_sPropertyFile = Constants.FILE_FRAGMENT_PROPERTIES;
		logger.fine("Property File: " + m_sPropertyFile);
	    }

	    // 'this is a plugin fragment
	    logger.fine("this is a plugin fragment");
	    m_sFragment = "true";

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void computeUrl() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("compute URL");
	try {

	    String sPath;
	    String sURL;

	    final String TAG_ARCHIVE = "<archive url=\"%s1\" path=\"%s2\"/>";
	    final String ATT_URL = "0/%UNID%/$file/%s1_%s2.jar?OpenElement";
	    final String ATT_PATH = "plugins/%s1_%s2.jar";

	    // 're-map all plugin paths to db-relative URLs so that the Eclipse
	    // update manager can find them
	    logger.fine("re-map all plugin paths to db-relative URLs so that the Eclipse update manager can find them");
	    sURL = Strings.sprintf2(ATT_URL, m_sID, m_sVersionFixedUp);
	    sPath = Strings.sprintf2(ATT_PATH, m_sID, m_sVersionFixedUp);

	    // 'build the xml for the view (%UNID% gets substituted in the view
	    // column formula)
	    logger.fine("build the xml for the view (%UNID% gets substituted in the view column formula)");
	    m_sViewXML = Strings.sprintf2(TAG_ARCHIVE, Common.encodeXML(sURL), Common.encodeXML(sPath));
	    logger.fine(m_sViewXML);

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    @Override
    public String toString() {
	return "SiteFeaturePlugin [m_sBaseFolderPath=" + m_sBaseFolderPath + ", m_sURL=" + m_sURL + ", m_sID=" + m_sID + ", m_sVersionOriginal=" + m_sVersionOriginal + "]";
    }
}
