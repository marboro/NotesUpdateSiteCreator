package com.dvelop.smartnotes.domino.updatesitecreator.site.digest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.Stream;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.dvelop.smartnotes.domino.updatesitecreator.common.Common;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Constants;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Strings;
import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;
import com.dvelop.smartnotes.domino.updatesitecreator.jar.JarWriter;
import com.dvelop.smartnotes.domino.updatesitecreator.os.OSServices;

public class SiteDigest {
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

	try {
	    m_session = session;
	    m_db = updateSiteDb;

	    // Call oLog.Write(sprintf1(LOG_REGENERATE_SITE_DIGEST,
	    // FILE_DIGEST_ZIP))

	    process();
	    serialize();

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void process() {
	try {

	    View view;
	    ViewEntryCollection vecoll;
	    ViewEntry entry;

	    view = m_db.getView(Constants.VIEW_FEATURES);
	    if (view == null) {
		// Error 1000, sprintf1(ERR_VIEW_NOT_FOUND, VIEW_FEATURES)
	    }

	    // 'we MUST refresh the view, otherwise the generated digest.zip
	    // might be stale
	    view.refresh();

	    vecoll = view.getAllEntries();
	    entry = vecoll.getFirstEntry();

	    while (entry != null) {

		if (entry.isValid()) {

		    m_doc = entry.getDocument();
		    m_oCtx = new FeatureTagContext();

		    getFeatureAttributes();
		    getMetaData();
		    getRequiredImports();
		    getPlugins();
		    getIncludes();

		    // 'add context object to list
		    m_liCtx.put(entry.getNoteID(), m_oCtx);

		}

		entry = vecoll.getNextEntry(entry);

	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	}
    }

    private void serialize() {
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

		sFeature = Strings.sprintf2(TAG_FEATURE, ctx.FeatureAtts, sFeatureBody);
		sDigest = sDigest + Strings.CRLF + sFeature + Strings.CRLF;
		ctx = null;

	    }

	    sDigest = Constants.XML_DECLARATION + Strings.CRLF + Strings.sprintf1(TAG_DIGEST, sDigest);
	    File tempDirectory = OSServices.createTempDirectory(Constants.FOLDER_UNIQUE_TEMP);
	    sTempFolderPath = tempDirectory.getAbsolutePath();
	    sXMLFileName = sTempFolderPath + File.separator + FILE_DIGEST_XML;
	    sZIPFileName = sTempFolderPath + File.separator + Constants.FILE_DIGEST_ZIP;

	    // 'write digest to temporary digest.xml file
	    stream = m_session.createStream();
	    stream.open(sXMLFileName, "UTF-8");
	    stream.truncate();
	    stream.writeText(sDigest, Constants.EOL_NONE);
	    stream.close();

	    // 'zip digest.xml file to digest.zip
	    // oZip = new JarWriter(sZIPFileName, "");
	    oZip = new JarWriter(sZIPFileName);
	    oZip.addFile(sXMLFileName, FILE_DIGEST_XML);
	    oZip.close();

	    view = m_db.getView(Constants.VIEW_RESOURCES);
	    if (view == null) {
		// Error 1000, sprintf1(ERR_VIEW_NOT_FOUND,VIEW_RESOURCES)
	    }

	    view.refresh();

	    doc = view.getDocumentByKey(Constants.FILE_DIGEST_ZIP, true);
	    if (doc == null) {
		doc = m_db.createDocument();
		doc.replaceItemValue(Constants.ITEM_FORM, FORM_NAME);
	    }

	    // 'remove any previous attachments and attach new digest.zip file
	    doc.removeItem(ITEM_RESOURCE_FILE);
	    rtItem = doc.createRichTextItem(ITEM_RESOURCE_FILE);
	    rtItem.embedObject(EmbeddedObject.EMBED_ATTACHMENT, "", sZIPFileName, null);
	    rtItem.compact();

	    doc.sign();
	    doc.save(true, false, true);

	    // 'delete temporary files
	    // Call oLog.Debug(sprintf1("Deleting file %s1", sXMLFileName))
	    OSServices.fileDelete(sXMLFileName);
	    //
	    // Call oLog.Debug(sprintf1("Deleting file %s1", sZIPFileName))
	    OSServices.fileDelete(sZIPFileName);
	    //
	    // Call oLog.Debug(sprintf1("Deleting folder %s1", sTempFolderPath))
	    OSServices.fileDelete(tempDirectory);

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}
    }

    private void getFeatureAttributes() {
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
	    sProviderName = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_PROVIDERNAME));
	    sID = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_ID));
	    sVersion = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_VERSION));
	    sOS = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_OS));
	    sNL = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_NL));
	    sWS = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_WS));
	    sArch = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_ARCH));
	    sExclusive = Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_EXCLUSIVE));

	    m_oCtx.FeatureAtts = Strings.sprintf9(ATT_FEATURE, sLabel, sProviderName, sID, sVersion, sOS, sNL, sWS, sArch, sExclusive);
	    m_oCtx.FeatureAtts = Common.trimAttributes(m_oCtx.FeatureAtts);

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	}
    }

    private void getMetaData() {
	try {

	    m_oCtx.Description = Strings.sprintf1(TAG_DESCRIPTION, Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_DESCRIPTION)));
	    m_oCtx.Copyright = Strings.sprintf1(TAG_COPYRIGHT, Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_COPYRIGHT)));
	    m_oCtx.License = Strings.sprintf1(TAG_LICENSE, Common.encodeXML(m_doc.getItemValueString(Constants.ITEM_FEATURE_LICENSE)));
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
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

	    if ((vFeature.get(0).length() > 0) || (vPlugin.get(0).length() > 0)) {

		for (i = 0; i < vFeature.size(); i++) {
		    // 'force "match=" tag to the default value if empty
		    if (vMatch.get(i).length() == 0) {
			vMatch.remove(i);
			vMatch.insertElementAt("compatible", i);
		    }

		    sBuffer = Strings.sprintf5(TAG_IMPORT, Common.encodeXML(vFeature.get(i)), Common.encodeXML(vPlugin.get(i)), Common.encodeXML(vVersion.get(i)),
			    Common.encodeXML(vMatch.get(i)), Common.encodeXML(vPatch.get(i)));

		    m_oCtx.Requires = m_oCtx.Requires + Common.trimAttributes(sBuffer) + Strings.CRLF;

		}

		m_oCtx.Requires = Strings.sprintf1(TAG_REQUIRES, Strings.CRLF + m_oCtx.Requires);

	    }
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	}
    }

    private void getPlugins() {
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

	    if (vID.get(0).length() > 0) {
		//
		for (i = 0; i < vID.size(); i++) {

		    // 'suppress some attributes based on their values
		    if (vFragment.get(i).equals("false")) {
			vFragment.remove(i);
			vFragment.insertElementAt("", i);
		    }
		    if (vUnpack.get(i).equals("true")) {
			vUnpack.remove(i);
			vUnpack.insertElementAt("", i);
		    }
		    if (vDownloadSize.get(i).equals("0")) {
			vDownloadSize.remove(i);
			vDownloadSize.insertElementAt("", i);
		    }
		    if (vInstallSize.get(i).equals("0")) {
			vInstallSize.remove(i);
			vInstallSize.insertElementAt("", i);
		    }

		    //
		    sBuffer = Strings.sprintf10(TAG_PLUGIN, Common.encodeXML(vID.get(i)), Common.encodeXML(vVersion.get(i)), Common.encodeXML(vFragment.get(i)),
			    Common.encodeXML(vOS.get(i)), Common.encodeXML(vArch.get(i)), Common.encodeXML(vWS.get(i)), Common.encodeXML(vNL.get(i)),
			    Common.encodeXML(vDownloadSize.get(i)), Common.encodeXML(vInstallSize.get(i)), Common.encodeXML(vUnpack.get(i)));

		    m_oCtx.Plugins = m_oCtx.Plugins + Common.trimAttributes(sBuffer) + Strings.CRLF;

		}

	    }
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	}
    }

    private void getIncludes() {
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

	    if (vID.get(0).length() > 0) {

		for (i = 0; i < vID.size(); i++) {

		    sBuffer = Strings.sprintf9(TAG_INCLUDES, Common.encodeXML(vID.get(i)), Common.encodeXML(vVersion.get(i)), Common.encodeXML(vName.get(i)),
			    Common.encodeXML(vOptional.get(i)), Common.encodeXML(vSearchLoc.get(i)), Common.encodeXML(vOS.get(i)), Common.encodeXML(vArch.get(i)),
			    Common.encodeXML(vWS.get(i)), Common.encodeXML(vNL.get(i)));

		    m_oCtx.Includes = m_oCtx.Includes + Common.trimAttributes(sBuffer) + Strings.CRLF;

		}

	    }
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	}
    }
}
