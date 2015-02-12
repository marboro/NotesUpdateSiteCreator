package com.dvelop.smartnotes.domino.updatesitecreator.site.feature;

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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dvelop.smartnotes.domino.updatesitecreator.bundle.BundleReader;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Common;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Constants;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Resources;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Strings;
import com.dvelop.smartnotes.domino.updatesitecreator.event.Event;
import com.dvelop.smartnotes.domino.updatesitecreator.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.EventException;
import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;
import com.dvelop.smartnotes.domino.updatesitecreator.jar.JarReader;
import com.dvelop.smartnotes.domino.updatesitecreator.os.OSServices;
import com.dvelop.smartnotes.domino.updatesitecreator.plugin.ManagePluginRefs;
import com.dvelop.smartnotes.domino.updatesitecreator.site.archive.SiteArchive;
import com.dvelop.smartnotes.domino.updatesitecreator.site.feature.factory.SiteFeatureFactory;
import com.dvelop.smartnotes.domino.updatesitecreator.site.feature.plugin.SiteFeaturePlugin;
import com.dvelop.smartnotes.domino.updatesitecreator.site.feature.plugin.SiteFeaturePluginContext;

public class SiteFeature extends Event {
    private Logger logger = Logger.getLogger(SiteFeature.class.getName());
    private EventRegistry eventRegistry;

    public SiteFeature(EventRegistry eventRegistry) {
	super(eventRegistry);
	this.eventRegistry = eventRegistry;
    }

    private Session m_session;
    private Database m_db;
    private View m_viewFeatures;
    private View m_viewPlugins;
    private JarReader m_oJar;

    private Node m_domDoc;
    private Map<String, SiteFeaturePlugin> m_liPlugins = new HashMap<String, SiteFeaturePlugin>();
    private long m_lPluginCount;

    private boolean m_bIsMissing;
    private boolean m_bIsIncluded;
    private String m_sBaseFolderPath;
    private String m_sJarFilePath;
    private String m_sJarFileLastMod;
    private String m_sDataFolderPath;
    private SiteFeatureFactory m_vParentFactory;

    private String m_sPatch;
    private String m_sPrimary;
    private String m_sExclusive;
    private String m_sType;
    private String m_sID;
    private String m_sVersion;

    private String m_sURLOriginal;
    private String m_sURLRemapped;

    private String m_sOS = "";
    private String m_sNL = "";
    private String m_sArch = "";
    private String m_sWS = "";
    private String m_sCategory = "";
    private String m_sLabel = "";
    private String m_sProviderName = "";
    private String m_sDescription = "";
    private String m_sDescriptionURL = "";
    private String m_sCopyright = "";
    private String m_sCopyrightURL = "";
    private String m_sLicense = "";
    private String m_sLicenseURL = "";
    private String m_sImage = "";
    private String m_sColocationAffinity = "";
    private String m_sPlugin = "";
    private String m_sApplication = "";
    private String m_sInstallHandler = "";
    private String m_sInstallLibrary = "";
    private String m_sUpdateLabel = "";
    private String m_sUpdateURL = "";

    private List<String> m_liDiscoveryLabel = new ArrayList<String>();
    private List<String> m_liDiscoveryURL = new ArrayList<String>();
    private List<String> m_liDiscoveryType = new ArrayList<String>();

    private List<String> m_liIncludesID = new ArrayList<String>();
    private List<String> m_liIncludesVersion = new ArrayList<String>();
    private List<String> m_liIncludesName = new ArrayList<String>();
    private List<String> m_liIncludesOptional = new ArrayList<String>();
    private List<String> m_liIncludesOS = new ArrayList<String>();
    private List<String> m_liIncludesWS = new ArrayList<String>();
    private List<String> m_liIncludesArch = new ArrayList<String>();
    private List<String> m_liIncludesNL = new ArrayList<String>();
    private List<String> m_liIncludesSearchLoc = new ArrayList<String>();

    private List<String> m_liImportPlugin = new ArrayList<String>();
    private List<String> m_liImportFeature = new ArrayList<String>();
    private List<String> m_liImportVersion = new ArrayList<String>();
    private List<String> m_liImportMatch = new ArrayList<String>();
    private List<String> m_liImportPatch = new ArrayList<String>();

    private Map<String, String> m_liDataID = new HashMap<String, String>();
    private Map<String, String> m_liDataOS = new HashMap<String, String>();
    private Map<String, String> m_liDataArch = new HashMap<String, String>();
    private Map<String, String> m_liDataWS = new HashMap<String, String>();
    private Map<String, String> m_liDataNL = new HashMap<String, String>();
    private Map<String, String> m_liDataFileName = new HashMap<String, String>();
    private Map<String, Long> m_liDataDownloadSize = new HashMap<String, Long>();
    private Map<String, Long> m_liDataInstallSize = new HashMap<String, Long>();

    private Map<String, String> m_liPluginID = new HashMap<String, String>();
    private Map<String, String> m_liPluginVersion = new HashMap<String, String>();
    private Map<String, String> m_liPluginFragment = new HashMap<String, String>();
    private Map<String, String> m_liPluginOS = new HashMap<String, String>();
    private Map<String, String> m_liPluginArch = new HashMap<String, String>();
    private Map<String, String> m_liPluginWS = new HashMap<String, String>();
    private Map<String, String> m_liPluginNL = new HashMap<String, String>();
    private Map<String, String> m_liPluginDownloadSize = new HashMap<String, String>();
    private Map<String, String> m_liPluginInstallSize = new HashMap<String, String>();
    private Map<String, String> m_liPluginUnpack = new HashMap<String, String>();

    private String m_sViewXMLFeature;
    private String m_sViewXMLArchive;
    private String m_sManifestXML;

    private boolean m_bCancel;
    private SiteFeatureContext oCtx;

    public List<SiteFeaturePlugin> getPluginList() {
	List<SiteFeaturePlugin> result = new ArrayList<SiteFeaturePlugin>();
	result.addAll(m_liPlugins.values());
	return result;
    }

    public long getPluginCount() {
	return m_lPluginCount;
    }

    public boolean isMissing() {
	return m_bIsMissing;
    }

    public String getJarFilePath() {
	return m_sJarFilePath;
    }

    public SiteFeatureFactory getParentFactory() {
	return m_vParentFactory;
    }

    public String getID() {
	return m_sID;
    }

    public String getVersion() {
	return m_sVersion;
    }

    public SiteFeature(Session session, Database db, SiteFeatureContext oCtx, EventRegistry eventRegistry) {
	this(eventRegistry);
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create SiteFeature");
	this.oCtx = oCtx;

	try {

	    List<SiteArchive> vArchives;

	    subscribeEvents();

	    m_session = session;
	    m_db = db;
	    m_vParentFactory = oCtx.vParentFactory;
	    m_viewFeatures = oCtx.viewFeatures;
	    m_viewPlugins = oCtx.viewPlugins;

	    m_sBaseFolderPath = oCtx.sBaseFolderPath;
	    m_sJarFilePath = oCtx.sJarFilePath.replace("/", File.separator);
	    m_sDataFolderPath = (m_sJarFilePath + "]").replace(".jar]", File.separator);
	    m_sURLOriginal = oCtx.sURL;
	    m_sURLRemapped = oCtx.sURL;
	    m_sCategory = oCtx.sCategory;
	    m_bIsIncluded = oCtx.bIsIncluded;

	    if (oCtx.sPatch.length() > 0) {
		logger.fine("feature is patch =" + oCtx.sPatch);
		m_sPatch = oCtx.sPatch; // if it was set in site.xml
	    } else {
		logger.fine("feature is no patch (default)");
		m_sPatch = String.valueOf(false); // otherwise default to false
	    }

	    // 'for site imports only, individual feature imports have no "site"
	    // parent and thus no archives
	    logger.fine("for site imports only, individual feature imports have no \"site\" parent and thus no archives");
	    if (m_vParentFactory.getParent() != null) {

		// 'apply any local archive mappings
		logger.fine("apply any local archive mappings");
		vArchives = m_vParentFactory.getParent().getArchives();
		for (SiteArchive archive : vArchives) {
		    logger.fine("check archive for remote " + archive);
		    if (!archive.isRemote()) {
			logger.fine("archive is remote");
			if (archive.getPath().equals(m_sURLOriginal)) {
			    m_sURLRemapped = archive.getUrl();
			    logger.fine(m_sURLRemapped);
			    m_sJarFilePath = m_sBaseFolderPath + m_sURLRemapped;
			    logger.fine(m_sJarFilePath);
			    m_sJarFilePath = m_sJarFilePath.replaceAll("/", File.separator);
			    logger.fine(m_sJarFilePath);
			    m_sDataFolderPath = (m_sJarFilePath + "]").replaceAll(".jar]", File.separator);
			    logger.fine(m_sDataFolderPath);
			}
		    }
		}

	    }

	    if (new File(m_sJarFilePath).exists()) {

		// 'only process if the feature listed in site.xml actually
		// exists as a jar on disk
		logger.fine("only process if the feature listed in site.xml actually exists as a jar on disk");
		process();
		logger.fine("compute URL");
		computeURL();

	    } else {
		m_bIsMissing = true;
		logger.fine(Strings.sprintf1(Resources.MSG_IGNORE_MISSING_FEATURE, m_sJarFilePath));
	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    public void delete() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start delete");
	try {
	    unsubscribeEvents();
	} catch (EventException e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}

    }

    private void process() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start process");
	try {

	    DocumentBuilder domParser;
	    String sLabel;
	    String sEncoding;

	    sLabel = Strings.sprintf1(Resources.MSG_READING_FEATURE, m_sJarFilePath.substring(0, m_sJarFilePath.lastIndexOf(File.separator)));
	    raiseEvent(Constants.QUEUE_PROGRESS_LABEL, sLabel);

	    logger.fine("get " + Constants.FILE_FEATURE_XML + " from jar");
	    m_oJar = new JarReader(m_sJarFilePath);
	    m_sJarFileLastMod = m_oJar.getLastModified();
	    sEncoding = m_oJar.getFileXMLEncoding(Constants.FILE_FEATURE_XML);
	    m_sManifestXML = m_oJar.getFileAsText(Constants.FILE_FEATURE_XML, sEncoding);
	    logger.fine(m_sManifestXML);

	    domParser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    org.w3c.dom.Document document = domParser.parse(m_oJar.getFileAsInputStream(Constants.FILE_FEATURE_XML, sEncoding));
	    m_domDoc = document.getFirstChild();

	    getFeatureInfo();
	    getMetaInfo(Constants.TAG_DESCRIPTION);
	    getMetaInfo(Constants.TAG_COPYRIGHT);
	    getMetaInfo(Constants.TAG_LICENSE);
	    getInstallHandler();
	    getUpdateSite();
	    getDiscoverySites();
	    getIncludes();
	    getRequiredImports();
	    getNonPluginData();
	    getPlugins();

	    // 'we're done with the jar, clean up associated resources
	    logger.fine("we're done with the jar, clean up associated resources");

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    public void serialize() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start serializing");
	String sKey = "";
	try {

	    Document doc;
	    RichTextStyle rtStyle;
	    RichTextItem rtFiles;
	    RichTextItem rtLicense;
	    RichTextItem rtDescription;
	    RichTextItem rtManifestXML;
	    ManagePluginRefs oRefs;
	    BundleReader oBundle;

	    String sDataFileName;
	    String sDataFilePath;
	    String sTempFilePath;
	    String sTempFolderPath;
	    String sLabel;
	    String sLog;
	    String sURL;

	    String vResourced;
	    Map<String, String> liItem = new HashMap<String, String>();
	    Map<String, String> liRtItem = new HashMap<String, String>();

	    logger.fine("read bundle");
	    oBundle = new BundleReader(m_sJarFilePath, Constants.FILE_FEATURE_BUNDLE);

	    // 'list of rich text items in use
	    logger.fine("list of rich text items in use");
	    liRtItem.put(Constants.ITEM_FEATURE_FILE, Constants.ITEM_FEATURE_FILE);
	    liRtItem.put(Constants.ITEM_FEATURE_LICENSE, Constants.ITEM_FEATURE_LICENSE);
	    liRtItem.put(Constants.ITEM_FEATURE_DESCRIPTION, Constants.ITEM_FEATURE_DESCRIPTION);
	    liRtItem.put(Constants.ITEM_FEATURE_MANIFEST_XML, Constants.ITEM_FEATURE_MANIFEST_XML);

	    // 'map items and feature properties
	    logger.fine("map items and feature properties");
	    liItem.put(Constants.ITEM_FEATURE_INCLUDED, String.valueOf(m_bIsIncluded).toLowerCase());
	    liItem.put(Constants.ITEM_FEATURE_PATCH, m_sPatch);
	    liItem.put(Constants.ITEM_FEATURE_PRIMARY, m_sPrimary);
	    liItem.put(Constants.ITEM_FEATURE_EXCLUSIVE, m_sExclusive);
	    liItem.put(Constants.ITEM_FEATURE_LABEL, m_sLabel);
	    liItem.put(Constants.ITEM_FEATURE_PROVIDERNAME, m_sProviderName);
	    liItem.put(Constants.ITEM_FEATURE_DESCRIPTION_URL, m_sDescriptionURL);
	    liItem.put(Constants.ITEM_FEATURE_COPYRIGHT, m_sCopyright);
	    liItem.put(Constants.ITEM_FEATURE_COPYRIGHT_URL, m_sCopyrightURL);
	    liItem.put(Constants.ITEM_FEATURE_LICENSE_URL, m_sLicenseURL);
	    liItem.put(Constants.ITEM_FEATURE_UPDATE_LABEL, m_sUpdateLabel);
	    liItem.put(Constants.ITEM_FEATURE_UPDATE_URL, m_sUpdateURL);
	    liItem.put(Constants.ITEM_FEATURE_TYPE, m_sType);
	    liItem.put(Constants.ITEM_FEATURE_FILE_LASTMODIFIED, m_sJarFileLastMod);
	    liItem.put(Constants.ITEM_FEATURE_ID, m_sID);
	    liItem.put(Constants.ITEM_FEATURE_VERSION, m_sVersion);
	    liItem.put(Constants.ITEM_FEATURE_OS, m_sOS);
	    liItem.put(Constants.ITEM_FEATURE_NL, m_sNL);
	    liItem.put(Constants.ITEM_FEATURE_ARCH, m_sArch);
	    liItem.put(Constants.ITEM_FEATURE_WS, m_sWS);
	    liItem.put(Constants.ITEM_FEATURE_CATEGORY, m_sCategory);
	    liItem.put(Constants.ITEM_FEATURE_IMAGE, m_sImage);
	    liItem.put(Constants.ITEM_FEATURE_COLOCATIONAFFINITY, m_sColocationAffinity);
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN, m_sPlugin);
	    liItem.put(Constants.ITEM_FEATURE_APPLICATION, m_sApplication);
	    liItem.put(Constants.ITEM_FEATURE_INSTALL_HANDLER, m_sInstallHandler);
	    liItem.put(Constants.ITEM_FEATURE_INSTALL_LIBRARY, m_sInstallLibrary);

	    liItem.put(Constants.ITEM_FEATURE_DISCOVERY_LABEL, m_liDiscoveryLabel.isEmpty() ? "" : m_liDiscoveryLabel.toString());
	    liItem.put(Constants.ITEM_FEATURE_DISCOVERY_URL, m_liDiscoveryURL.isEmpty() ? "" : m_liDiscoveryURL.toString());
	    liItem.put(Constants.ITEM_FEATURE_DISCOVERY_TYPE, m_liDiscoveryType.isEmpty() ? "" : m_liDiscoveryType.toString());

	    liItem.put(Constants.ITEM_FEATURE_INCLUDES_ID, m_liIncludesID.isEmpty() ? "" : m_liIncludesID.toString());
	    liItem.put(Constants.ITEM_FEATURE_INCLUDES_VERSION, m_liIncludesVersion.isEmpty() ? "" : m_liIncludesVersion.toString());
	    liItem.put(Constants.ITEM_FEATURE_INCLUDES_NAME, m_liIncludesName.isEmpty() ? "" : m_liIncludesName.toString());
	    liItem.put(Constants.ITEM_FEATURE_INCLUDES_OPTIONAL, m_liIncludesOptional.isEmpty() ? "" : m_liIncludesOptional.toString());
	    liItem.put(Constants.ITEM_FEATURE_INCLUDES_OS, m_liIncludesOS.isEmpty() ? "" : m_liIncludesOS.toString());
	    liItem.put(Constants.ITEM_FEATURE_INCLUDES_WS, m_liIncludesWS.isEmpty() ? "" : m_liIncludesWS.toString());
	    liItem.put(Constants.ITEM_FEATURE_INCLUDES_ARCH, m_liIncludesArch.isEmpty() ? "" : m_liIncludesArch.toString());
	    liItem.put(Constants.ITEM_FEATURE_INCLUDES_NL, m_liIncludesNL.isEmpty() ? "" : m_liIncludesNL.toString());
	    liItem.put(Constants.ITEM_FEATURE_INCLUDES_SEARCHLOC, m_liIncludesSearchLoc.isEmpty() ? "" : m_liIncludesSearchLoc.toString());

	    liItem.put(Constants.ITEM_FEATURE_IMPORT_PLUGIN, m_liImportPlugin.isEmpty() ? "" : m_liImportPlugin.toString());
	    liItem.put(Constants.ITEM_FEATURE_IMPORT_FEATURE, m_liImportFeature.isEmpty() ? "" : m_liImportFeature.toString());
	    liItem.put(Constants.ITEM_FEATURE_IMPORT_VERSION, m_liImportVersion.isEmpty() ? "" : m_liImportVersion.toString());
	    liItem.put(Constants.ITEM_FEATURE_IMPORT_MATCH, m_liImportMatch.isEmpty() ? "" : m_liImportMatch.toString());
	    liItem.put(Constants.ITEM_FEATURE_IMPORT_PATCH, m_liImportPatch.isEmpty() ? "" : m_liImportPatch.toString());

	    liItem.put(Constants.ITEM_FEATURE_DATA_ID, m_liDataID.values().isEmpty() ? "" : m_liDataID.values().toString());
	    liItem.put(Constants.ITEM_FEATURE_DATA_OS, m_liDataOS.values().isEmpty() ? "" : m_liDataOS.values().toString());
	    liItem.put(Constants.ITEM_FEATURE_DATA_ARCH, m_liDataArch.values().isEmpty() ? "" : m_liDataArch.values().toString());
	    liItem.put(Constants.ITEM_FEATURE_DATA_WS, m_liDataWS.values().isEmpty() ? "" : m_liDataWS.values().toString());
	    liItem.put(Constants.ITEM_FEATURE_DATA_NL, m_liDataNL.values().isEmpty() ? "" : m_liDataNL.values().toString());
	    liItem.put(Constants.ITEM_FEATURE_DATA_DOWNLOAD_SIZE, m_liDataDownloadSize.values().isEmpty() ? "" : m_liDataDownloadSize.values().toString());
	    liItem.put(Constants.ITEM_FEATURE_DATA_INSTALL_SIZE, m_liDataInstallSize.values().isEmpty() ? "" : m_liDataInstallSize.values().toString());
	    liItem.put(Constants.ITEM_FEATURE_DATA_FILENAME, m_liDataFileName.values().isEmpty() ? "" : m_liDataFileName.values().toString());

	    String sPluginKey = Strings.sprintf2(Constants.FORMAT_ID_VERSION, m_sPlugin, m_sVersion);
	    logger.fine("plugin key: " + sPluginKey);
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN_ID, m_liPluginID.get(sPluginKey));
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN_VERSION, m_liPluginVersion.get(sPluginKey));
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN_FRAGMENT, m_liPluginFragment.get(sPluginKey));
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN_OS, m_liPluginOS.get(sPluginKey));
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN_ARCH, m_liPluginArch.get(sPluginKey));
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN_WS, m_liPluginWS.get(sPluginKey));
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN_NL, m_liPluginNL.get(sPluginKey));
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN_DOWNLOADSIZE, m_liPluginDownloadSize.get(sPluginKey));
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN_INSTALLSIZE, m_liPluginInstallSize.get(sPluginKey));
	    liItem.put(Constants.ITEM_FEATURE_PLUGIN_UNPACK, m_liPluginUnpack.get(sPluginKey));

	    liItem.put(Constants.ITEM_FEATURE_VIEW_XML_FEATURE, m_sViewXMLFeature);
	    liItem.put(Constants.ITEM_FEATURE_VIEW_XML_ARCHIVE, m_sViewXMLArchive);

	    sKey = Strings.sprintf2(Constants.FORMAT_ID_VERSION, m_sID, m_sVersion);
	    logger.fine("key: " + sKey);
	    sURL = Strings.sprintf2(Constants.FEATURE_URL, m_sID, m_sVersion);
	    logger.fine("URL: " + sURL);

	    logger.fine("refresh view");
	    m_viewFeatures.refresh();
	    logger.fine("get document by key " + sKey);
	    doc = m_viewFeatures.getDocumentByKey(sKey, true);

	    if (doc == null) {
		// 'no feature doc found for this particular feature, create one
		logger.fine("no feature doc found for this particular feature, create one");
		sLabel = Strings.sprintf1(Resources.MSG_IMPORTING_FEATURE, m_sJarFilePath.substring(0, m_sJarFilePath.lastIndexOf(File.separatorChar)));
		sLog = Strings.sprintf1(Resources.LOG_CREATE_FEATURE_DOC, sURL);
		logger.fine("create document " + Constants.FORM_FEATURE);
		doc = m_db.createDocument();
		doc.replaceItemValue(Constants.ITEM_FORM, Constants.FORM_FEATURE);
	    } else {
		// 'compare jarfiles: if the timestamps are identical, don't
		// update the note
		logger.fine("compare jarfiles: if the timestamps are identical, don't update the note");
		if (doc.getItemValueString(Constants.ITEM_FEATURE_FILE_LASTMODIFIED).equals(m_sJarFileLastMod)) {
		    logger.fine(Strings.sprintf1(Resources.LOG_UPTODATE_FEATURE_DOC, sURL));
		    return;
		}

		sLabel = Strings.sprintf1(Resources.MSG_UPDATING_FEATURE, m_sJarFilePath.substring(0, m_sJarFilePath.lastIndexOf(File.separatorChar)));
		sLog = Strings.sprintf1(Resources.LOG_UPDATE_FEATURE_DOC, sURL);

		// 'existing feature doc found, remove old items
		logger.fine("existing feature doc found, remove old items");
		for (String item : liItem.keySet()) {
		    doc.removeItem(liItem.get(item));
		}

		// '..and rich text items
		logger.fine("..and rich text items");
		for (String rti : liRtItem.keySet()) {
		    doc.removeItem(liRtItem.get(rti));
		}

	    }

	    raiseEvent(Constants.QUEUE_PROGRESS_LABEL, sLabel);
	    raiseEvent(Constants.QUEUE_PROGRESS_BAR, 1);

	    logger.fine("create richtext style");
	    rtStyle = m_session.createRichTextStyle();
	    rtStyle.setFontSize(9);
	    rtStyle.setFont(RichTextStyle.FONT_COURIER);

	    // 'attach feature .jar file
	    logger.fine("attach feature .jar file");
	    rtFiles = doc.createRichTextItem(Constants.ITEM_FEATURE_FILE);
	    rtFiles.appendStyle(rtStyle);
	    rtFiles.embedObject(EmbeddedObject.EMBED_ATTACHMENT, "", m_sJarFilePath, null);
	    rtFiles.addNewLine(1, false);

	    // 'store plugin.xml rich text
	    logger.fine("store plugin.xml rich text");
	    if (m_sManifestXML.length() > 0) {
		rtManifestXML = doc.createRichTextItem(Constants.ITEM_FEATURE_MANIFEST_XML);
		rtManifestXML.appendStyle(rtStyle);
		rtManifestXML.appendText(m_sManifestXML);
		rtManifestXML.compact();
	    }

	    rtStyle.setFont(RichTextStyle.FONT_HELV);

	    // 'store license rich text
	    logger.fine("store license rich text");
	    if (m_sLicense.length() > 0) {
		rtLicense = doc.createRichTextItem(Constants.ITEM_FEATURE_LICENSE);
		rtLicense.appendStyle(rtStyle);
		rtLicense.appendText(oBundle.getProperties(m_sLicense));
		rtLicense.compact();
	    }

	    // 'store description rich text
	    logger.fine("store description rich text");
	    if (m_sDescription.length() > 0) {
		rtDescription = doc.createRichTextItem(Constants.ITEM_FEATURE_DESCRIPTION);
		rtDescription.appendStyle(rtStyle);
		rtDescription.appendText(oBundle.getProperties(m_sDescription));
		rtDescription.compact();
	    }

	    // 'attach non-plugin data files
	    logger.fine("attach non-plugin data files");
	    for (String dataID : m_liDataID.keySet()) {
		sDataFilePath = m_sDataFolderPath + dataID.replace("/", File.separator);
		logger.fine(Strings.sprintf1("Data file path: %s1", sDataFilePath));

		if (!new File(sDataFilePath).exists()) {
		    logger.fine("Error 1000, " + Strings.sprintf2(Resources.ERR_IMPORT_DATA, sDataFilePath, Resources.ERR_FILE_NOT_FOUND));
		}

		// 'copy the file to a unique temp folder with the new filename
		logger.fine("copy the file to a unique temp folder with the new filename");
		File tempDirectory = OSServices.createTempDirectory(Constants.FOLDER_UNIQUE_TEMP);
		sTempFolderPath = tempDirectory.getAbsolutePath();
		sTempFilePath = sTempFolderPath + m_liDataFileName.get(Integer.valueOf(dataID));

		logger.fine(Strings.sprintf2("Copying %s1 to %s2", sDataFilePath, sTempFilePath));

		OSServices.copyFile(sDataFilePath, sTempFilePath);

		// 'and attach it
		logger.fine("..and attach it");
		sLabel = Strings.sprintf1(Resources.MSG_ATTACHING_DATA, m_liDataFileName.get(Integer.valueOf(dataID)));
		raiseEvent(Constants.QUEUE_PROGRESS_LABEL, sLabel);
		logger.fine(Strings.sprintf2("Attaching %s1 as %s2", m_liDataFileName.get(Integer.valueOf(dataID)), sTempFilePath));
		rtFiles.embedObject(EmbeddedObject.EMBED_ATTACHMENT, "", sTempFilePath, null);
		rtFiles.addNewLine(1, false);

		// 'clean up
		logger.fine("clean up");
		logger.finest(Strings.sprintf1("Deleting file %s1", sTempFilePath));
		new File(sTempFilePath).delete();

		logger.fine(Strings.sprintf1("Deleting folder %s1", sTempFolderPath));
		tempDirectory.delete();

	    }

	    rtFiles.compact();

	    // 'save all feature properties to items
	    logger.fine("save all feature properties to items");
	    for (String item : liItem.keySet()) {

		// 'get the resource strings
		logger.fine("get the resource string: " + item);
		vResourced = oBundle.getProperties(item);
		String value = liItem.get(item);
		if (vResourced != "" && !vResourced.equals(item)) {
		    value = vResourced;
		}
		logger.fine("replace value for " + item + " with " + value);
		doc.replaceItemValue(item, value);

	    }

	    // 'render table of doclink'ed plugins and persist plugin UNIDs
	    logger.fine("render table of doclink'ed plugins and persist plugin UNIDs");
	    oRefs = new ManagePluginRefs(m_session, m_db, doc, this, m_viewPlugins);
	    logger.fine("call fixup");
	    oRefs.fixup();

	    doc.save(true, false, true);
	    logger.fine(sLog);

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), sKey);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getFeatureInfo() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get feature info");
	try {

	    // 'get <feature> attributes
	    logger.fine("get <feature> attributes");
	    m_sPrimary = Common.domGetAttribute(m_domDoc, Constants.ATT_PRIMARY);
	    m_sExclusive = Common.domGetAttribute(m_domDoc, Constants.ATT_EXCLUSIVE);
	    m_sID = Common.domGetAttribute(m_domDoc, Constants.ATT_ID);
	    m_sVersion = Common.domGetAttribute(m_domDoc, Constants.ATT_VERSION);
	    m_sLabel = Common.domGetAttribute(m_domDoc, Constants.ATT_LABEL);
	    m_sProviderName = Common.domGetAttribute(m_domDoc, Constants.ATT_PROVIDER_NAME);
	    m_sImage = Common.domGetAttribute(m_domDoc, Constants.ATT_IMAGE);
	    m_sOS = Common.domGetAttribute(m_domDoc, Constants.ATT_OS);
	    m_sArch = Common.domGetAttribute(m_domDoc, Constants.ATT_ARCH);
	    m_sWS = Common.domGetAttribute(m_domDoc, Constants.ATT_WS);
	    m_sNL = Common.domGetAttribute(m_domDoc, Constants.ATT_NL);
	    m_sColocationAffinity = Common.domGetAttribute(m_domDoc, Constants.ATT_COLO_AFFINITY);
	    m_sPlugin = Common.domGetAttribute(m_domDoc, Constants.ATT_PLUGIN);
	    m_sApplication = Common.domGetAttribute(m_domDoc, Constants.ATT_APPLICATION);

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getInstallHandler() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get install handler");
	try {

	    Node domNode;
	    NodeList domNodeList;
	    String sURL;
	    int iTagIndex;

	    // 'get collection of all <install-handler> tags
	    // '(there should be just one, only use the 1st one we find)
	    logger.fine("get collection of all <install-handler> tags (there should be just one, only use the 1st one we find)");
	    domNodeList = m_domDoc.getChildNodes();
	    for (iTagIndex = 1; iTagIndex < domNodeList.getLength(); iTagIndex++) {
		if (!domNodeList.item(iTagIndex).getNodeName().equals(Constants.TAG_INSTALL_HANDLER)) {
		    continue;
		}

		// 'only read nodes directly under <feature> tag
		logger.fine("only read nodes directly under <feature> tag");
		domNode = domNodeList.item(iTagIndex);
		if (domNode.getParentNode().getNodeName().equals(Constants.TAG_FEATURE)) {

		    m_sInstallLibrary = Common.domGetAttribute(domNode, Constants.ATT_LIBRARY);
		    m_sInstallHandler = Common.domGetAttribute(domNode, Constants.ATT_HANDLER);
		    logger.fine("only read the 1st node we find");
		    break; // 'only read the 1st node we find

		}

	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getMetaInfo(String sTag) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get meta info");
	try {

	    Node domNode;
	    NodeList domNodeList;
	    String sURL = "";
	    String sText = "";
	    int iTagIndex;

	    // 'get collection of all <feature><"sTag"> tags
	    // '(there should be just one, only use the 1st one we find)
	    logger.fine("get collection of all <feature><\"sTag\"> tags (there should be just one, only use the 1st one we find)");
	    domNodeList = m_domDoc.getChildNodes();
	    for (iTagIndex = 0; iTagIndex < domNodeList.getLength(); iTagIndex++) {
		if (!domNodeList.item(iTagIndex).getNodeName().equals(sTag)) {
		    continue;
		}
		// 'only read nodes directly under <feature> tag
		logger.fine("only read nodes directly under <feature> tag");
		domNode = domNodeList.item(iTagIndex);
		if (domNode.getParentNode().getNodeName().equals(Constants.TAG_FEATURE)) {

		    // 'get <"sTag" url> attribute
		    logger.fine("get <\"sTag\" url> attribute");
		    sURL = Common.domGetAttribute(domNode, Constants.ATT_URL);
		    sText = "";
		    // 'get description text
		    logger.fine("get description text");
		    if (domNode.hasChildNodes()) {
			NodeList childNodes = domNode.getChildNodes();
			for (int childCount = 0; childCount < childNodes.getLength(); childCount++) {
			    String formattedText = Common.getFormattedText(childNodes.item(childCount).getNodeValue());
			    if (!"".equals(formattedText)) {
				sText = formattedText;
				logger.fine(sText);
				break;
			    }
			}
		    }
		    logger.fine("only read the 1st \"sTag\" node we find");
		    break; // 'only read the 1st "sTag" node we find

		}

	    }

	    if (sTag.equals(Constants.TAG_DESCRIPTION)) {
		logger.fine(Constants.TAG_DESCRIPTION + " " + sText + " " + sURL);
		m_sDescription = sText;
		m_sDescriptionURL = sURL;

	    } else if (sTag.equals(Constants.TAG_COPYRIGHT)) {
		logger.fine(Constants.TAG_COPYRIGHT + " " + sText + " " + sURL);
		m_sCopyright = sText;
		m_sCopyrightURL = sURL;

	    } else if (sTag.equals(Constants.TAG_LICENSE)) {
		logger.fine(Constants.TAG_LICENSE + " " + sText + " " + sURL);
		m_sLicense = sText;
		m_sLicenseURL = sURL;

	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getUpdateSite() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get update site");
	try {

	    Node domNode;
	    NodeList domNodeList;
	    int iTagIndex;

	    // 'get collection of all <update> tags
	    // '(there should be just one, only use the 1st one we find)
	    logger.fine("get collection of all <update> tags (there should be just one, only use the 1st one we find)");
	    domNodeList = m_domDoc.getChildNodes();
	    for (iTagIndex = 1; iTagIndex < domNodeList.getLength(); iTagIndex++) {
		if (!domNodeList.item(iTagIndex).getNodeName().equals(Constants.TAG_UPDATE)) {
		    continue;
		}

		// 'only read nodes directly under <url> tag
		logger.fine("only read nodes directly under <url> tag");
		domNode = domNodeList.item(iTagIndex);
		if (domNode.getParentNode().getNodeName().equals(Constants.TAG_URL)) {

		    // 'get <update> attributes
		    logger.fine("get <update> attributes");
		    m_sUpdateLabel = Common.domGetAttribute(domNode, Constants.ATT_LABEL);
		    m_sUpdateURL = Common.domGetAttribute(domNode, Constants.ATT_URL);
		    logger.fine("we only care about the 1st <update> node we find");
		    break; // 'we only care about the 1st <update> node we find

		}

	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getDiscoverySites() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get discovery sites");
	try {

	    Node domNode;
	    NodeList domNodeList;
	    int iTagIndex;

	    // 'get collection of all <discovery> tags
	    logger.fine("get collection of all <discovery> tags");
	    domNodeList = m_domDoc.getChildNodes();
	    for (iTagIndex = 1; iTagIndex < domNodeList.getLength(); iTagIndex++) {
		if (!domNodeList.item(iTagIndex).getNodeName().equals(Constants.TAG_UPDATE)) {
		    continue;
		}

		// 'only read nodes directly under <url> tag
		logger.fine("only read nodes directly under <url> tag");
		domNode = domNodeList.item(iTagIndex);
		if (domNode.getParentNode().getNodeName().equals(Constants.TAG_URL)) {

		    // 'get <discovery> attributes
		    logger.fine("get <discovery> attributes");
		    m_liDiscoveryLabel.add(Common.domGetAttribute(domNode, Constants.ATT_LABEL));
		    m_liDiscoveryURL.add(Common.domGetAttribute(domNode, Constants.ATT_URL));
		    m_liDiscoveryType.add(Common.domGetAttribute(domNode, Constants.ATT_TYPE));
		}
	    }
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getIncludes() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get includes");
	try {

	    Node domNode;
	    NodeList domNodeList;
	    int iTagIndex;
	    SiteFeatureContext oCtx;
	    String sURL;

	    // 'get collection of all <includes> tags
	    logger.fine("get collection of all <includes> tags");
	    domNodeList = m_domDoc.getChildNodes();
	    for (iTagIndex = 1; iTagIndex < domNodeList.getLength(); iTagIndex++) {
		if (!domNodeList.item(iTagIndex).getNodeName().equals(Constants.TAG_INCLUDES)) {
		    continue;
		}

		// 'only read nodes directly under <feature> tag
		logger.fine("only read nodes directly under <feature> tag");
		domNode = domNodeList.item(iTagIndex);
		if (domNode.getParentNode().getNodeName().equals(Constants.TAG_FEATURE)) {

		    // 'get <includes> attributes
		    logger.fine("get <includes> attributes");
		    m_liIncludesID.add(Common.domGetAttribute(domNode, Constants.ATT_ID));
		    m_liIncludesVersion.add(Common.domGetAttribute(domNode, Constants.ATT_VERSION));
		    m_liIncludesName.add(Common.domGetAttribute(domNode, Constants.ATT_NAME));
		    m_liIncludesOptional.add(Common.domGetAttribute(domNode, Constants.ATT_OPTIONAL));
		    m_liIncludesSearchLoc.add(Common.domGetAttribute(domNode, Constants.ATT_SEARCH_LOCATION));
		    m_liIncludesOS.add(Common.domGetAttribute(domNode, Constants.ATT_OS));
		    m_liIncludesArch.add(Common.domGetAttribute(domNode, Constants.ATT_ARCH));
		    m_liIncludesWS.add(Common.domGetAttribute(domNode, Constants.ATT_WS));
		    m_liIncludesNL.add(Common.domGetAttribute(domNode, Constants.ATT_NL));

		    sURL = Strings.sprintf2(Constants.FEATURE_URL, m_liIncludesID.get(iTagIndex), m_liIncludesVersion.get(iTagIndex));

		    // 'create new feature context
		    logger.fine("create new feature context");
		    oCtx = new SiteFeatureContext();
		    oCtx.vParentFactory = m_vParentFactory;
		    oCtx.viewFeatures = m_viewFeatures;
		    oCtx.viewPlugins = m_viewPlugins;

		    oCtx.sURL = sURL;
		    oCtx.sJarFilePath = m_sBaseFolderPath + sURL;
		    oCtx.sBaseFolderPath = m_sBaseFolderPath;
		    oCtx.bIsIncluded = true;

		    // 'factor new feature object
		    logger.fine("factor new feature object");
		    m_vParentFactory.factorNewFeature(oCtx);

		}

		// 'exit loop if the UI wants to cancel
		if (m_bCancel) {
		    return;
		}

	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getRequiredImports() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get required imports");
	try {
	    Node domNode;
	    NodeList domNodeList;
	    int iTagIndex;
	    String strue;

	    // 'get collection of all <import> tags
	    logger.fine("get collection of all <import> tags");
	    domNodeList = m_domDoc.getChildNodes();
	    for (iTagIndex = 1; iTagIndex < domNodeList.getLength(); iTagIndex++) {
		if (!domNodeList.item(iTagIndex).getNodeName().equals(Constants.TAG_IMPORT)) {
		    continue;
		}

		// 'only read nodes directly under <requires> tag
		logger.fine("only read nodes directly under <requires> tag");
		domNode = domNodeList.item(iTagIndex);
		if (domNode.getParentNode().getNodeName().equals(Constants.TAG_REQUIRES)) {

		    // 'get <import> attributes
		    logger.fine("get <import> attributes");
		    m_liImportPlugin.add(Common.domGetAttribute(domNode, Constants.ATT_PLUGIN));
		    m_liImportFeature.add(Common.domGetAttribute(domNode, Constants.ATT_FEATURE));
		    m_liImportVersion.add(Common.domGetAttribute(domNode, Constants.ATT_VERSION));
		    m_liImportMatch.add(Common.domGetAttribute(domNode, Constants.ATT_MATCH));
		    m_liImportPatch.add(Common.domGetAttribute(domNode, Constants.ATT_PATCH));

		    // 'if this feature patches another feature, mark it as a
		    // feature patch
		    logger.fine("if this feature patches another feature, mark it as a feature patch");
		    strue = "true";
		    if (m_liImportPatch.get(iTagIndex).equalsIgnoreCase("true")) {
			m_sPatch = "true";
		    }

		}

		// 'exit loop if the UI wants to cancel
		if (m_bCancel) {
		    return;
		}

	    }
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getNonPluginData() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get non plugin data");
	try {
	    Node domNode;
	    NodeList domNodeList;
	    int iTagIndex;
	    String sFileName;

	    // 'get collection of all <data> tags
	    logger.fine("get collection of all <data> tags");
	    domNodeList = m_domDoc.getChildNodes();
	    for (iTagIndex = 1; iTagIndex < domNodeList.getLength(); iTagIndex++) {
		if (!domNodeList.item(iTagIndex).getNodeName().equals(Constants.TAG_DATA)) {
		    continue;
		}

		// 'only read nodes directly under <feature> tag
		logger.fine("only read nodes directly under <feature> tag");
		domNode = domNodeList.item(iTagIndex);
		if (domNode.getParentNode().getNodeName().equals(Constants.TAG_FEATURE)) {

		    // 'get <data> attributes
		    logger.fine("get <data> attributes");
		    m_liDataID.put("" + iTagIndex, Common.domGetAttribute(domNode, Constants.ATT_ID));
		    m_liDataOS.put("" + iTagIndex, Common.domGetAttribute(domNode, Constants.ATT_OS));
		    m_liDataArch.put("" + iTagIndex, Common.domGetAttribute(domNode, Constants.ATT_ARCH));
		    m_liDataWS.put("" + iTagIndex, Common.domGetAttribute(domNode, Constants.ATT_WS));
		    m_liDataNL.put("" + iTagIndex, Common.domGetAttribute(domNode, Constants.ATT_NL));
		    m_liDataDownloadSize.put("" + iTagIndex, Common.xClng(Common.domGetAttribute(domNode, Constants.ATT_DOWNLOAD_SIZE)));
		    m_liDataInstallSize.put("" + iTagIndex, Common.xClng(Common.domGetAttribute(domNode, Constants.ATT_INSTALL_SIZE)));

		    // 'build a new filename which has the folderpath
		    // incorporated like "data/other/foo.jar" becomes
		    // "data_other_foo.jar"
		    logger.fine("build a new filename which has the folderpath incorporated like \"data/other/foo.jar\" becomes \"data_other_foo.jar\"");
		    sFileName = m_liDataID.get(iTagIndex).replaceAll("/", "_").replaceAll("\\", "_");

		    m_liDataFileName.put("" + iTagIndex, sFileName);

		}

		// 'exit loop if the UI wants to cancel
		if (m_bCancel) {
		    return;
		}

	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getPlugins() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get plugins");
	try {
	    Node domNode;
	    NodeList domNodeList;
	    int iTagIndex;
	    SiteFeaturePlugin oPlugin;
	    SiteFeaturePluginContext oCtx;
	    String sListKey;

	    // 'get collection of all <feature><plugin> tags
	    logger.fine("get collection of all <feature><plugin> tags");
	    domNodeList = m_domDoc.getChildNodes();
	    for (iTagIndex = 1; iTagIndex < domNodeList.getLength(); iTagIndex++) {
		if (!domNodeList.item(iTagIndex).getNodeName().equals(Constants.TAG_PLUGIN)) {
		    continue;
		}

		// 'only read nodes directly under <feature> tag
		logger.fine("only read nodes directly under <feature> tag");
		domNode = domNodeList.item(iTagIndex);
		if (domNode.getParentNode().getNodeName().equals(Constants.TAG_FEATURE)) {
		    logger.fine("create new SiteFeatureContext");
		    oCtx = new SiteFeaturePluginContext();
		    oCtx.vParentFeature = this;
		    oCtx.viewPlugins = m_viewPlugins;

		    oCtx.sID = Common.domGetAttribute(domNode, Constants.ATT_ID);
		    oCtx.sVersion = Common.domGetAttribute(domNode, Constants.ATT_VERSION);
		    oCtx.sURL = Strings.sprintf2(Constants.PLUGIN_URL, oCtx.sID, oCtx.sVersion);
		    oCtx.sBaseFolderPath = m_sBaseFolderPath;

		    // 'factor new plugin object
		    logger.fine("factor new plugin object");
		    oPlugin = new SiteFeaturePlugin(m_session, m_db, oCtx, eventRegistry);

		    sListKey = Strings.sprintf2(Constants.FORMAT_ID_VERSION, oCtx.sID, oCtx.sVersion).toLowerCase();

		    // 'and add it to the collection, if the plugin jar could be
		    // found and processed
		    logger.fine("and add it to the collection, if the plugin jar could be found and processed");
		    if (!oPlugin.isMissing()) {

			addPluginToCollection(oPlugin);

			// 'get <plugin> attributes
			logger.fine("get <plugin> attributes");
			m_liPluginID.put(sListKey, oPlugin.getID());
			m_liPluginVersion.put(sListKey, oPlugin.getVersion());
			m_liPluginFragment.put(sListKey, String.valueOf(oPlugin.isFragment()));
			m_liPluginOS.put(sListKey, Common.domGetAttribute(domNode, Constants.ATT_OS));
			m_liPluginArch.put(sListKey, Common.domGetAttribute(domNode, Constants.ATT_ARCH));
			m_liPluginWS.put(sListKey, Common.domGetAttribute(domNode, Constants.ATT_WS));
			m_liPluginNL.put(sListKey, Common.domGetAttribute(domNode, Constants.ATT_NL));
			m_liPluginDownloadSize.put(sListKey, Common.domGetAttribute(domNode, Constants.ATT_DOWNLOAD_SIZE));
			m_liPluginInstallSize.put(sListKey, Common.domGetAttribute(domNode, Constants.ATT_INSTALL_SIZE));
			m_liPluginUnpack.put(sListKey, Common.domGetAttribute(domNode, Constants.ATT_UNPACK));

		    } else {
			logger.fine(Strings.sprintf1(Resources.MSG_IGNORE_MISSING_PLUGIN, sListKey));

		    }

		}

		// 'exit loop if the UI wants to cancel
		if (m_bCancel) {
		    return;
		}
	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void addPluginToCollection(SiteFeaturePlugin oPlugin) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("add plugin to collection");
	try {

	    String sListTag;

	    sListTag = Strings.sprintf2(Constants.FORMAT_ID_VERSION, oPlugin.getID(), oPlugin.getVersion());
	    logger.fine(sListTag);
	    if (!m_liPlugins.containsKey(sListTag)) {
		m_liPlugins.put(sListTag, oPlugin);
		m_lPluginCount = m_lPluginCount + 1;
	    } else {
		logger.fine(Strings.sprintf1(Resources.MSG_IGNORE_DUPLICATE_PLUGIN, oPlugin.getJarFilePath()));
	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void computeURL() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("compute URL");
	try {

	    String sTagFeature = "";
	    String sTagCategory = "";
	    String sTagFeatureArchive = "";
	    String sTagDataArchive = "";
	    String sFileName = "";
	    String sFolder = "";
	    String sAttPath = "";
	    String sAttURL = "";
	    String sAttID = "";
	    String sAttVersion = "";
	    String sAttOS = "";
	    String sAttWS = "";
	    String sAttNL = "";
	    String sAttArch = "";
	    String sAttOthers = "";

	    final String TAG_FEATURE = "<feature url=\"%s1\" patch=\"%s2\" %s3>%s4</feature>";
	    final String TAG_CATEGORY = " <category name=\"%s1\"/>";
	    final String TAG_ARCHIVE = "<archive url=\"%s1\" path=\"%s2\"/>";

	    final String ATT_ID = " id=\"%s1\"";
	    final String ATT_VERSION = " version=\"%s1\"";
	    final String ATT_OS = " os=\"%s1\"";
	    final String ATT_WS = " ws=\"%s1\"";
	    final String ATT_NL = " nl=\"%s1\"";
	    final String ATT_ARCH = " arch=\"%s1\"";

	    final String DATA_PATH = "%s1/%s2";
	    final String FILE_NAME = "%s1_%s2.jar";
	    final String URL = "0/%UNID%/$file/%s1?OpenElement";

	    // 'Individual feature imports don't get their URL in "oCtx.sURL"
	    // set, so set it now
	    logger.fine("Individual feature imports don't get their URL in \"oCtx.sURL\" set, so set it now");
	    if (m_sURLOriginal.length() == 0) {
		m_sURLOriginal = Strings.sprintf2(Constants.FEATURE_URL, m_sID, m_sVersion);
		logger.fine(Strings.sprintf1(Resources.LOG_IMPORT_SET_FEATURE_URL, m_sURLOriginal));
	    }

	    // 'optional <feature> attributes
	    logger.fine("optional <feature> attributes");
	    if (m_sID.length() > 0) {
		sAttID = Strings.sprintf1(ATT_ID, Common.encodeXML(m_sID));
		logger.fine("Att ID: " + sAttID);
	    }
	    if (m_sVersion.length() > 0) {
		sAttVersion = Strings.sprintf1(ATT_VERSION, Common.encodeXML(m_sVersion));
		logger.fine("Att Version: " + sAttVersion);
	    }
	    if (m_sOS.length() > 0) {
		sAttOS = Strings.sprintf1(ATT_OS, Common.encodeXML(m_sOS));
		logger.fine("Att OS: " + sAttOS);
	    }
	    if (m_sWS.length() > 0) {
		sAttWS = Strings.sprintf1(ATT_WS, Common.encodeXML(m_sWS));
		logger.fine("Att WS: " + sAttWS);
	    }
	    if (m_sNL.length() > 0) {
		sAttNL = Strings.sprintf1(ATT_NL, Common.encodeXML(m_sNL));
		logger.fine("Att NL: " + sAttNL);
	    }
	    if (m_sArch.length() > 0) {
		sAttArch = Strings.sprintf1(ATT_ARCH, Common.encodeXML(m_sArch));
		logger.fine("Att Arch: " + sAttArch);
	    }

	    sAttOthers = sAttID + sAttVersion + sAttOS + sAttWS + sAttNL + sAttArch;
	    sAttOthers = sAttOthers.trim();
	    logger.fine(sAttOthers);

	    // 'optional <category> tag
	    logger.fine("optional <category> tag");
	    if (m_sCategory.length() > 0) {
		sTagCategory = Strings.CRLF + Strings.sprintf1(TAG_CATEGORY, Common.encodeXML(m_sCategory)) + Strings.CRLF;
		logger.fine("Tag Category: " + sTagCategory);
	    }

	    // '<feature> tag
	    logger.fine("<feature> tag");
	    sFileName = Strings.sprintf2(FILE_NAME, m_sID, m_sVersion);
	    logger.fine("FileName: " + sFileName);
	    sAttURL = Strings.sprintf1(URL, Common.encodeXML(sFileName));
	    logger.fine("Att URL: " + sAttURL);
	    sTagFeature = Strings.sprintf4(TAG_FEATURE, sAttURL, Common.encodeXML(m_sPatch), sAttOthers, sTagCategory);
	    logger.fine("Tag Feature: " + sTagFeature);

	    // '<archive> tag for feature remapping
	    // 'SPR # DMDD7BYT2S: We need to create <archive> tags for ALL
	    // features (not just 'included' ones)
	    logger.fine("<archive> tag for feature remapping SPR # DMDD7BYT2S: We need to create <archive> tags for ALL features (not just 'included' ones)");
	    sTagFeatureArchive = Strings.sprintf2(TAG_ARCHIVE, sAttURL, Common.encodeXML(m_sURLOriginal));
	    logger.fine("Tag Feature Archive: " + sTagFeatureArchive);

	    // 'optional <archive> tags for non-plugin data
	    logger.fine("optional <archive> tags for non-plugin data");
	    for (String fileName : m_liDataFileName.keySet()) {
		sFolder = m_sURLOriginal.substring(0, m_sURLOriginal.lastIndexOf("."));
		logger.fine("Folder: " + sFolder);
		sAttURL = Strings.sprintf1(URL, Common.encodeXML(fileName));
		logger.fine("Att URL: " + sAttURL);
		sAttPath = Strings.sprintf2(DATA_PATH, sFolder, m_liDataID.get(fileName));
		logger.fine("Att Path: " + sAttPath);
		sTagDataArchive = sTagDataArchive + Strings.sprintf2(TAG_ARCHIVE, Common.encodeXML(sAttURL), Common.encodeXML(sAttPath));
		logger.fine("Tag Data Archive: " + sTagDataArchive);
	    }

	    // 'build the xml tags for the view (%UNID% gets substituted in the
	    // view column formula)
	    logger.fine("build the xml tags for the view (%UNID% gets substituted in the view column formula)");
	    m_sViewXMLFeature = sTagFeature;
	    m_sViewXMLArchive = sTagFeatureArchive + sTagDataArchive;

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    public void onReceiveEvent(String sQueueName, Event vEvent) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("on receive event");
	logger.fine("implements the event listener interface from the cEvent class");
	try {
	    if (sQueueName.equals(Constants.QUEUE_CANCEL_UI)) {
		m_bCancel = true; // 'UI wants to cancel;
	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    @Override
    public String toString() {
	return "SiteFeature [m_sBaseFolderPath=" + m_sBaseFolderPath + ", m_sPatch=" + m_sPatch + ", m_sPrimary=" + m_sPrimary + ", m_sExclusive=" + m_sExclusive + ", m_sType="
		+ m_sType + ", m_sID=" + m_sID + ", m_sVersion=" + m_sVersion + ", m_sOS=" + m_sOS + ", m_sNL=" + m_sNL + ", m_sArch=" + m_sArch + ", m_sWS=" + m_sWS
		+ ", m_sCategory=" + m_sCategory + ", m_sLabel=" + m_sLabel + ", m_sProviderName=" + m_sProviderName + "]";
    }

}
