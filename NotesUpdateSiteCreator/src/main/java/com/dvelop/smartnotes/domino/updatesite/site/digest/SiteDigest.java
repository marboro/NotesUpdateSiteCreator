package com.dvelop.smartnotes.domino.updatesite.site.digest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.Stream;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.dvelop.smartnotes.domino.updatesite.common.Common;
import com.dvelop.smartnotes.domino.updatesite.common.Constants;
import com.dvelop.smartnotes.domino.updatesite.common.Resources;
import com.dvelop.smartnotes.domino.updatesite.common.Strings;
import com.dvelop.smartnotes.domino.updatesite.exceptions.OException;
import com.dvelop.smartnotes.domino.updatesite.jar.JarWriter;
import com.dvelop.smartnotes.domino.updatesite.os.OSServices;

public class SiteDigest {
    private Logger logger = Logger.getLogger(SiteDigest.class.getName());
    private static final String TAG_DIGEST = "<digest>%s1</digest>";
    private static final String TAG_FEATURE = "<feature %s1>%s2</feature>";
    private static final String ATT_FEATURE = "label=\"%s1\" provider-name=\"%s2\" id=\"%s3\" version=\"%s4\" os=\"%s5\" nl=\"%s6\" ws=\"%s7\" arch=\"%s8\" exclusive=\"%s9\"";
    private static final String TAG_REQUIRES = "<requires>%s1</requires>";
    private static final String TAG_IMPORT = "<import feature=\"%s1\" plugin=\"%s2\" version=\"%s3\" match=\"%s4\" patch=\"%s5\"/>";
    private static final String TAG_DESCRIPTION = "<description>%s1</description>";
    private static final String TAG_COPYRIGHT = "<copyright>%s1</copyright>";
    private static final String TAG_LICENSE = "<license>%s1</license>";
    private static final String TAG_PLUGIN = "<plugin id=\"%s1\" version=\"%s2\" fragment=\"%s3\" os=\"%s4\" arch=\"%s5\" ws=\"%s6\" nl=\"%s7\" download-size=\"%s8\" install-size=\"%s9\" unpack=\"%s10\"/>";
    private static final String TAG_INCLUDES = "<includes id=\"%s1\" version=\"%s2\" name=\"%s3\" optional=\"%s4\" search-location=\"%s5\" os=\"%s6\" arch=\"%s7\" ws=\"%s8\" nl=\"%s9\" />";

    private Session m_session;
    private Database m_db;
    private Document m_doc;
    private FeatureTagContext m_oCtx;
    private Map<String, FeatureTagContext> m_liCtx = new HashMap<String, FeatureTagContext>();

    public SiteDigest(Session session, Database updateSiteDb) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create SiteDigest");

	try {
	    m_session = session;
	    m_db = updateSiteDb;

	    logger.fine(Strings.sprintf1(Resources.LOG_REGENERATE_SITE_DIGEST, Constants.FILE_DIGEST_ZIP));

	    logger.fine("call process");
	    process();
	    logger.fine("call serialize");
	    serialize();

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}

    }

    private void process() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start process");
	try {

	    View view;
	    ViewEntryCollection vecoll;
	    ViewEntry entry;

	    logger.fine("get view " + Constants.VIEW_FEATURES);
	    view = m_db.getView(Constants.VIEW_FEATURES);
	    if (view == null) {
		logger.fine("Error 1000, " + Strings.sprintf1(Resources.ERR_VIEW_NOT_FOUND, Constants.VIEW_FEATURES));
	    }

	    // 'we MUST refresh the view, otherwise the generated digest.zip
	    // might be stale
	    logger.fine("we MUST refresh the view, otherwise the generated digest.zip might be stale");
	    view.refresh();

	    logger.fine("get all entries");
	    vecoll = view.getAllEntries();
	    logger.fine("get first entry");
	    entry = vecoll.getFirstEntry();

	    while (entry != null) {

		if (entry.isValid()) {
		    logger.fine("entry is valid");

		    m_doc = entry.getDocument();
		    logger.fine("create feature tag context");
		    m_oCtx = new FeatureTagContext();

		    getFeatureAttributes();
		    getMetaData();
		    getRequiredImports();
		    getPlugins();
		    getIncludes();

		    // 'add context object to list
		    logger.fine("add context object to list");
		    m_liCtx.put(entry.getNoteID(), m_oCtx);

		}
		logger.fine("get next entry");
		entry = vecoll.getNextEntry(entry);

	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void serialize() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start serializing");
	try {

	    Stream stream;
	    View view;
	    Document doc;
	    RichTextItem rtItem;
	    JarWriter oZip;
	    String sTempFolderPath;
	    String sXMLFileName;
	    String sZIPFileName;
	    String sDigest = "";
	    String sFeature;
	    String sFeatureBody;

	    final String FORM_NAME = "fmResource";
	    final String FILE_DIGEST_XML = "digest.xml";
	    final String ITEM_RESOURCE_FILE = "resource.file";

	    for (FeatureTagContext ctx : m_liCtx.values()) {
		sFeatureBody = Strings.CRLF + ctx.Description + Strings.CRLF + ctx.Copyright + Strings.CRLF + ctx.License + Strings.CRLF + ctx.Requires + Strings.CRLF
			+ ctx.Plugins + Strings.CRLF + ctx.Includes + Strings.CRLF;
		logger.fine("feature body: " + sFeatureBody);
		sFeature = Strings.sprintf2(TAG_FEATURE, ctx.FeatureAtts, sFeatureBody);
		logger.fine("feature: " + sFeature);
		sDigest = sDigest + Strings.CRLF + sFeature + Strings.CRLF;
		logger.fine("digest: " + sDigest);
	    }

	    sDigest = Constants.XML_DECLARATION + Strings.CRLF + Strings.sprintf1(TAG_DIGEST, sDigest);
	    File tempDirectory = OSServices.createTempDirectory(Constants.FOLDER_UNIQUE_TEMP);
	    sTempFolderPath = tempDirectory.getAbsolutePath();
	    sXMLFileName = sTempFolderPath + File.separator + FILE_DIGEST_XML;
	    sZIPFileName = sTempFolderPath + File.separator + Constants.FILE_DIGEST_ZIP;

	    // 'write digest to temporary digest.xml file
	    logger.fine("write digest to temporary digest.xml file");
	    stream = m_session.createStream();
	    stream.open(sXMLFileName, "UTF-8");
	    stream.truncate();
	    stream.writeText(sDigest, Constants.EOL_NONE);
	    stream.close();

	    // 'zip digest.xml file to digest.zip
	    logger.fine("zip digest.xml file to digest.zip");
	    oZip = new JarWriter(sZIPFileName);
	    oZip.addFile(sXMLFileName, FILE_DIGEST_XML);
	    oZip.close();

	    logger.fine("get view " + Constants.VIEW_RESOURCES);
	    view = m_db.getView(Constants.VIEW_RESOURCES);
	    if (view == null) {
		logger.fine("Error 1000, " + Strings.sprintf1(Resources.ERR_VIEW_NOT_FOUND, Constants.VIEW_RESOURCES));
	    }

	    logger.fine("view refresh");
	    view.refresh();

	    logger.fine("get document by key: " + Constants.FILE_DIGEST_ZIP);
	    doc = view.getDocumentByKey(Constants.FILE_DIGEST_ZIP, true);
	    if (doc == null) {
		logger.fine("create document " + FORM_NAME);
		doc = m_db.createDocument();
		doc.replaceItemValue(Constants.ITEM_FORM, FORM_NAME);
	    }

	    // 'remove any previous attachments and attach new digest.zip file
	    logger.fine("remove any previous attachments and attach new digest.zip file");
	    doc.removeItem(ITEM_RESOURCE_FILE);
	    rtItem = doc.createRichTextItem(ITEM_RESOURCE_FILE);
	    rtItem.embedObject(EmbeddedObject.EMBED_ATTACHMENT, "", sZIPFileName, null);
	    rtItem.compact();

	    logger.fine("sign and save document");
	    doc.sign();
	    doc.save(true, false, true);

	    // 'delete temporary files
	    logger.fine("delete temporary files");
	    logger.fine(Strings.sprintf1("Deleting file %s1", sXMLFileName));
	    OSServices.fileDelete(sXMLFileName);

	    logger.fine(Strings.sprintf1("Deleting file %s1", sZIPFileName));
	    OSServices.fileDelete(sZIPFileName);

	    logger.fine(Strings.sprintf1("Deleting folder %s1", sTempFolderPath));
	    OSServices.fileDelete(tempDirectory);

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getFeatureAttributes() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get feature attributes");
	try {

	    String sLabel;
	    String sProviderName;
	    String sID;
	    String sVersion;
	    String sOS;
	    String sNL;
	    String sWS;
	    String sArch;
	    String sExclusive;

	    sLabel = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_LABEL));
	    logger.fine("Label: " + sLabel);
	    sProviderName = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_PROVIDERNAME));
	    logger.fine("Provider Name: " + sProviderName);
	    sID = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_ID));
	    logger.fine("ID: " + sID);
	    sVersion = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_VERSION));
	    logger.fine("Version: " + sVersion);
	    sOS = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_OS));
	    logger.fine("OS: " + sOS);
	    sNL = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_NL));
	    logger.fine("NL: " + sNL);
	    sWS = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_WS));
	    logger.fine("WS: " + sWS);
	    sArch = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_ARCH));
	    logger.fine("Arch: " + sArch);
	    sExclusive = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_EXCLUSIVE));
	    logger.fine("Exclusive: " + sExclusive);

	    m_oCtx.FeatureAtts = Strings.sprintf9(ATT_FEATURE, sLabel, sProviderName, sID, sVersion, sOS, sNL, sWS, sArch, sExclusive);
	    m_oCtx.FeatureAtts = Common.trimAttributes(m_oCtx.FeatureAtts);
	    logger.fine("CTX Feature Atts: " + m_oCtx.FeatureAtts);
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_START);
	}
    }

    private void getMetaData() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get meta data");
	try {

	    m_oCtx.Description = Strings.sprintf1(TAG_DESCRIPTION, Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_DESCRIPTION)));
	    logger.fine("Description: " + m_oCtx.Description);
	    m_oCtx.Copyright = Strings.sprintf1(TAG_COPYRIGHT, Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_COPYRIGHT)));
	    logger.fine("Copyright: " + m_oCtx.Copyright);
	    m_oCtx.License = Strings.sprintf1(TAG_LICENSE, Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_LICENSE)));
	    logger.fine("License: " + m_oCtx.License);
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getRequiredImports() {
	try {

	    Vector<String> vFeature;
	    Vector<String> vPlugin;
	    Vector<String> vVersion;
	    Vector<String> vMatch;
	    Vector<String> vPatch;
	    int i;
	    String sBuffer = "";

	    vFeature = m_doc.getItemValue(Constants.ITEM_FEATURE_IMPORT_FEATURE);
	    vPlugin = m_doc.getItemValue(Constants.ITEM_FEATURE_IMPORT_PLUGIN);
	    vVersion = m_doc.getItemValue(Constants.ITEM_FEATURE_IMPORT_VERSION);
	    vMatch = m_doc.getItemValue(Constants.ITEM_FEATURE_IMPORT_MATCH);
	    vPatch = m_doc.getItemValue(Constants.ITEM_FEATURE_IMPORT_PATCH);

	    if ((vFeature.size() > 0 && vFeature.get(0).length() > 0) || (vPlugin.size() > 0 && vPlugin.get(0).length() > 0)) {

		for (i = 0; i < vFeature.size(); i++) {
		    // 'force "match=" tag to the default value if empty
		    logger.fine("force \"match=\" tag to the default value if empty");
		    if (vMatch.get(i).length() == 0) {
			vMatch.remove(i);
			vMatch.insertElementAt("compatible", i);
		    }

		    sBuffer = Strings.sprintf5(TAG_IMPORT, Common.encodeXML(vFeature.get(i)), Common.encodeXML(vPlugin.get(i)), Common.encodeXML(vVersion.get(i)),
			    Common.encodeXML(vMatch.get(i)), Common.encodeXML(vPatch.get(i)));
		    logger.fine("Buffer: " + sBuffer);
		    m_oCtx.Requires = m_oCtx.Requires + Common.trimAttributes(sBuffer) + Strings.CRLF;

		}

		m_oCtx.Requires = Strings.sprintf1(TAG_REQUIRES, Strings.CRLF + m_oCtx.Requires);
		logger.fine("Requires: " + m_oCtx.Requires);
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

	    Vector<String> vID;
	    Vector<String> vVersion;
	    Vector<String> vFragment;
	    Vector<String> vOS;
	    Vector<String> vArch;
	    Vector<String> vWS;
	    Vector<String> vNL;
	    Vector<String> vDownloadSize;
	    Vector<String> vInstallSize;
	    Vector<String> vUnpack;
	    int i;
	    String sBuffer;

	    vID = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_ID);
	    vVersion = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_VERSION);
	    vFragment = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_FRAGMENT);
	    vOS = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_OS);
	    vArch = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_ARCH);
	    vWS = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_WS);
	    vNL = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_NL);
	    vDownloadSize = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_DOWNLOADSIZE);
	    vInstallSize = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_INSTALLSIZE);
	    vUnpack = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_UNPACK);

	    if (vID.size() > 0 && vID.get(0).length() > 0) {

		for (i = 0; i < vID.size(); i++) {
		    String sId = "";
		    String sVersion = "";
		    String sFragment = "";
		    String sOS = "";
		    String sArch = "";
		    String sWS = "";
		    String sNL = "";
		    String sDownloadSize = "";
		    String sInstallSize = "";
		    String sUnpack = "";
		    if (vID.size() > 0) {
			sId = Common.encodeXML(vID.get(i));
			logger.fine("ID: " + sId);
		    }
		    if (vVersion.size() > 0) {
			sVersion = Common.encodeXML(vVersion.get(i));
			logger.fine("Version: " + sVersion);
		    }
		    if (vFragment.size() > 0) {
			sFragment = Common.encodeXML(vFragment.get(i));
			logger.fine("Fragment: " + sFragment);
		    }
		    if (vOS.size() > 0) {
			sOS = Common.encodeXML(vOS.get(i));
			logger.fine("OS: " + sOS);
		    }
		    if (vArch.size() > 0) {
			sArch = Common.encodeXML(vArch.get(i));
			logger.fine("Arch: " + sArch);
		    }
		    if (vWS.size() > 0) {
			sWS = Common.encodeXML(vWS.get(i));
			logger.fine("WS: " + sWS);
		    }
		    if (vNL.size() > 0) {
			sNL = Common.encodeXML(vNL.get(i));
			logger.fine("NL: " + sNL);
		    }
		    if (vDownloadSize.size() > 0) {
			sDownloadSize = Common.encodeXML(vDownloadSize.get(i));
			logger.fine("Download Size: " + sDownloadSize);
		    }
		    if (vInstallSize.size() > 0) {
			sInstallSize = Common.encodeXML(vInstallSize.get(i));
			logger.fine("Install Size: " + sInstallSize);
		    }
		    if (vUnpack.size() > 0) {
			sUnpack = Common.encodeXML(vUnpack.get(i));
			logger.fine("Unpack: " + sUnpack);
		    }

		    // 'suppress some attributes based on their values
		    logger.fine("suppress some attributes based on their values");
		    if (sFragment.equals("false")) {
			vFragment.remove(i);
			vFragment.insertElementAt("", i);
			logger.fine("no fragment");
		    }
		    if (sUnpack.equals("true")) {
			vUnpack.remove(i);
			vUnpack.insertElementAt("", i);
			logger.fine("do not unpack");
		    }
		    if (sDownloadSize.equals("0")) {
			vDownloadSize.remove(i);
			vDownloadSize.insertElementAt("", i);
			logger.fine("no download size");
		    }
		    if (sInstallSize.equals("0")) {
			vInstallSize.remove(i);
			vInstallSize.insertElementAt("", i);
			logger.fine("no install size");
		    }

		    sBuffer = Strings.sprintf10(TAG_PLUGIN, sId, sVersion, sFragment, sOS, sArch, sWS, sNL, sDownloadSize, sInstallSize, sUnpack);
		    logger.fine("Buffer: " + sBuffer);
		    m_oCtx.Plugins = m_oCtx.Plugins + Common.trimAttributes(sBuffer) + Strings.CRLF;

		}
		logger.fine("Plugins: " + m_oCtx.Plugins);

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
	    Vector<String> vID;
	    Vector<String> vVersion;
	    Vector<String> vName;
	    Vector<String> vOptional;
	    Vector<String> vOS;
	    Vector<String> vWS;
	    Vector<String> vArch;
	    Vector<String> vNL;
	    Vector<String> vSearchLoc;
	    int i;
	    String sBuffer;

	    vID = m_doc.getItemValue(Constants.ITEM_FEATURE_INCLUDES_ID);
	    vVersion = m_doc.getItemValue(Constants.ITEM_FEATURE_INCLUDES_VERSION);
	    vName = m_doc.getItemValue(Constants.ITEM_FEATURE_INCLUDES_NAME);
	    vOptional = m_doc.getItemValue(Constants.ITEM_FEATURE_INCLUDES_OPTIONAL);
	    vOS = m_doc.getItemValue(Constants.ITEM_FEATURE_INCLUDES_OS);
	    vWS = m_doc.getItemValue(Constants.ITEM_FEATURE_INCLUDES_WS);
	    vArch = m_doc.getItemValue(Constants.ITEM_FEATURE_INCLUDES_ARCH);
	    vNL = m_doc.getItemValue(Constants.ITEM_FEATURE_INCLUDES_NL);
	    vSearchLoc = m_doc.getItemValue(Constants.ITEM_FEATURE_INCLUDES_SEARCHLOC);

	    if (vID.size() > 0 && vID.get(0).length() > 0) {

		for (i = 0; i < vID.size(); i++) {
		    String sID = "";
		    String sVersion = "";
		    String sName = "";
		    String sOptional = "";
		    String sOS = "";
		    String sWS = "";
		    String sArch = "";
		    String sNL = "";
		    String sSearchLoc = "";

		    if (vID.size() > 0) {
			sID = Common.encodeXML(vID.get(i));
			logger.fine("ID: " + sID);
		    }
		    if (vVersion.size() > 0) {
			sVersion = Common.encodeXML(vVersion.get(i));
			logger.fine("Version: " + sVersion);
		    }
		    if (vName.size() > 0) {
			sName = Common.encodeXML(vName.get(i));
			logger.fine("Name: " + sName);
		    }
		    if (vOptional.size() > 0) {
			sOptional = Common.encodeXML(vOptional.get(i));
			logger.fine("Optional: " + sOptional);
		    }
		    if (vOS.size() > 0) {
			sOS = Common.encodeXML(vOS.get(i));
			logger.fine("OS: " + sOS);
		    }
		    if (vWS.size() > 0) {
			sWS = Common.encodeXML(vWS.get(i));
			logger.fine("WS: " + sWS);
		    }
		    if (vArch.size() > 0) {
			sArch = Common.encodeXML(vArch.get(i));
			logger.fine("Arch: " + sArch);
		    }
		    if (vNL.size() > 0) {
			sNL = Common.encodeXML(vNL.get(i));
			logger.fine("NL: " + sNL);
		    }
		    if (vSearchLoc.size() > 0) {
			sSearchLoc = Common.encodeXML(vSearchLoc.get(i));
			logger.fine("Search Loc: " + sSearchLoc);
		    }

		    sBuffer = Strings.sprintf9(TAG_INCLUDES, sID, sVersion, sName, sOptional, sSearchLoc, sOS, sArch, sWS, sNL);
		    logger.fine("Buffer: " + sBuffer);

		    m_oCtx.Includes = m_oCtx.Includes + Common.trimAttributes(sBuffer) + Strings.CRLF;

		}
		logger.fine("Includes: " + m_oCtx.Includes);
	    }
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }
}
