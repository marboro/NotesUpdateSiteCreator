package com.dvelop.smartnotes.domino.widgetcatalog;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.EmbeddedObject;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dvelop.smartnotes.domino.common.Common;
import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.updatesite.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesite.os.OSServices;
import com.dvelop.smartnotes.domino.widgetcatalog.credstore.CredStore;
import com.dvelop.smartnotes.domino.widgetcatalog.proxy.ProxyUtil;
import com.dvelop.smartnotes.domino.widgetcatalog.uri.URIUtil;

public class WidgetCatalogBuilder {

    private final String widgetCreatedText = "'VAR_WIDGETNAME' was successfully imported. You should open this widget and configure it. VAR_NEWLINE VAR_NEWLINE Source: VAR_WIDGETXMLPATH";
    private final String failToImportWidgetText = "Widget import failed.VAR_NEWLINE VAR_NEWLINE To correct the errors, verify and correct the source code in the following file:VAR_NEWLINE VAR_WIDGETXMLPATH";
    private final String failToImportText = "'VAR_WIDGETNAME' was imported,with errors.VAR_NEWLINE VAR_NEWLINE To correct the errors, verify and correct the source code in the following file:VAR_NEWLINE VAR_PROXYXMLPATH VAR_OAUTHXMLPATH";
    private final String failToImportProxyOAuthText = "'VAR_WIDGETNAME' was imported,with errors.VAR_NEWLINE VAR_NEWLINE To correct the errors, verify and correct the source code in the following files:VAR_NEWLINE VAR_PROXYXMLPATH VAR_NEWLINE VAR_OAUTHXMLPATH";
    private final String failToOpenOAuthDb = "'VAR_WIDGETNAME' was imported,with OAuth error: Not able to open Credential Store database.VAR_NEWLINE VAR_NEWLINE To correct the error, click Configure Credential Store button on the Configuration view to reset the values and { re-import the widget.";
    private final String failToFindOAuthConfig = "'VAR_WIDGETNAME' was imported,with OAuth error: Not able to find an available OAuth configuration in the file.VAR_NEWLINE VAR_NEWLINE To correct the error, verify and correct the source code in the following file:VAR_NEWLINE VAR_OAUTHXMLPATH";
    private final String failToFindOAuthFile = "'VAR_WIDGETNAME' was imported, with OAuth error: Not able to find an available OAuth file in VAR_OAUTHXMLPATH.VAR_NEWLINE VAR_NEWLINE To correct the error, provide an available OAuth file in VAR_OAUTHXMLPATH and re-import the widget.";
    private final String OAUTH2_CONSUMER_KEY = "ClientId";
    private final String OAUTH2_CONSUMER_SECRET = "ClientSecret";
    private final String OAUTH_CONSUMER_KEY = "ConsumerKey";
    private final String OAUTH_CONSUMER_SECRET = "ConsumerSecret";
    private final String ENC_OAUTH2_CONSUMER_KEY = "EncClientId";
    private final String ENC_OAUTH2_CONSUMER_SECRET = "EncClientSecret";
    private final String ENC_OAUTH_CONSUMER_KEY = "EncConsumerKey";
    private final String ENC_OAUTH_CONSUMER_SECRET = "EncConsumerSecret";
    private final String anonymousContextPath = "/anonymous";

    // REM "Begin Translatable Text”
    private final String ProxyDialogTitle = "Configure Proxy";
    private final String AnonymousDialogTitle = "Configure Anonymous Proxy";

    private final String ErrorDlgTitle = "Error";
    private final String ValidationError = "The fields marked with * are required.";

    private final String ModifySettingTitle = "Modify";
    private final String ModifySettingText = "Do you want to replace an existing setting?";

    private final String CopyPolicyTxt = "You must first select a policy to edit.";

    private final String RemoveDlgTitle = "Nothing selected";
    private final String RemoveSettingTitle = "Remove";
    private final String RemoveSettingTxt = "Do you want to remove the selected setting?";
    private final String NoSelectionError = "You must first select a policy to remove.";

    private final String RemoveAllDlgTitle = "Policy List Empty";
    private final String RmAllTitle = "Remove All";
    private final String RmAllText = "You will remove all settings. Do you want to continue?";
    private final String NoSettingText = "There are no policies in the list to remove.";
    private final String EmptyValueError = "Policy List must be filled in.";

    private final String SpecialCharsError = "Can't input Eual or Semicolon in all text fields.";

    private final String InvalidFieldTitle = "Invalid Field";
    private final String PolicyURLValidateErr = "The URL field is not valid. The value must either be a valid URL, or if it contains a wildcard character (*), it can be only in the last component of the URL.";
    private final String HeadersValidateErr = "The Headers field is not valid. Header names may contain ASCII characters except for ()<>@,;:\\/[]?={} or double quotation marks, spaces or tabs. * may be used as a wildcard character.";
    private final String MimeTypesValidateErr = "The MIME Types field is not valid. MIME types are specified in the form token/token.  Tokens contain ASCII characters except for ()<>@,;:\\/[]?={} Or double quotation marks, spaces Or tabs.";
    private final String CookiesValidateErr = "The Cookies field is not valid. Cookie names contain ASCII characters except for ()<>@,;:\\/[]?={} Or double quotation marks, spaces or tabs.";
    private final String fieldNameValidateErr = "The metadata Name field is not valid. The Name field contains ASCII characters except for ()<>@,;:\\/[]?={} Or double quotation marks, tabs.";
    private final String fieldValueValidateErr = "The metadata Value field is not valid. The Value field contains ASCII characters except for ()<>@,;:\\/[]?={} Or double quotation marks, tabs.";
    private final String NoMappedFieldValueErr = "The Value field does not contain a value appropriate for the parameter specified in the Name field.";
    private final String AllowListValidateErr = "The Allow list is not valid.  Valid contents include a fully qualified domain name (no wildcards), an IP-address with subnet mask specified as address/mask, where each component is a valid IP address, or an IP-address with a * for specific components. * may not be used by itself.";
    private final String DenyListValidateErr = "The Deny list is not valid.  Valid contents include a fully qualified domain name (no wildcards), an IP-address with subnet mask specified as address/mask, where each component is a valid IP address, or an IP-address with a * for specific components.  * may not be used by itself.";

    private final String OAuthDialogTitle = "Configure OAuth Consumer Information";
    private final String ProxyWildURLWarningText = "Setting the URL of the content proxy to * will allow ALL traffic through the proxy and will affect ALL OpenSocial Widgets. A more restrictive rule is recommended. Are you sure you want to continue?";
    private final String ProxyWildURLWarningTitle = "Warning";

    // 'Strings for C4 Integration
    private final String WidgetImportErrTxt = "Widget Import Error";
    private final String ProxyImportErrTxt = "Proxy Import Error";
    private final String OAuthImportErrTxt = "OAuth Import Error";

    private final String InfoTxt = "New Notes Widget";
    private final String AttachFileTitle = "Attach File";
    private final String NoWidgetCreatedError = "Fail to create a new widget.";

    private final String OverrideDlgTitle = "Replace Existing Documents?";
    private final String OverrideOAuthText = "You will import OAuth information for this gadget. If there are existing OAuth documents for this gadget, do you want to replace the existing documents with these new files? ";
    private final String DuplicateProxyError = "Error: There are duplicate proxy rules in the VAR_PROXYXMLNAME file.";
    private final String InvalidProxyPropertyError = "Error: There are invalid values for proxy url, action, header, mime-type, or cookie in the VAR_PROXYXMLNAME file.";
    private final String InvalidAllowDenyListError = "Error: There are invalid values for allow list or deny list in the VAR_PROXYXMLNAME file.";
    // "End Translatable Text”

    private final short DOMNODETYPE_ATTRIBUTE_NODE = 2;
    private final short DOMNODETYPE_CDATASECTION_NODE = 4;
    private final short DOMNODETYPE_COMMENT_NODE = 8;
    private final short DOMNODETYPE_DOCUMENT_NODE = 9;
    private final short DOMNODETYPE_DOCUMENTFRAGMENT_NODE = 11;
    private final short DOMNODETYPE_DOCUMENTTYPE_NODE = 10;
    private final short DOMNODETYPE_ELEMENT_NODE = 1;
    private final short DOMNODETYPE_ENTITY_NODE = 6;
    private final short DOMNODETYPE_ENTITYREFERENCE_NODE = 5;
    private final short DOMNODETYPE_NOTATION_NODE = 12;
    private final short DOMNODETYPE_PROCESSINGINSTRUCTION_NODE = 7;
    private final short DOMNODETYPE_TEXT_NODE = 3;
    private final short DOMNODETYPE_UNKNOWN_NODE = 0;
    private final short DOMNODETYPE_XMLDECL_NODE = 13;

    // 'arrays to support multiple oauth/oauth2 services
    List<String> OAuthSvcNames;
    List<String> OAuthSvcReqURLs;
    List<String> OAuthSvcAuthURLs;
    List<String> OAuthSvcAccessURLs;
    List<String> OAuth2SvcNames;
    List<String> OAuth2SVCScopes;
    List<String> OAuth2SvcAuthURLs;
    List<String> OAuth2SvcTokenURLs;

    private int setProxyValuesErrCode;

    private Logger logger = Logger.getLogger(WidgetCatalogBuilder.class.getName());

    private Session session;
    private String server;
    private String widgetCatalogNsfFileName;
    private String widgetCatalogNsfTitle;
    private String widgetCatalogTemplateFileName;
    private String extensionXMLPath;
    private String siteUrl;
    private EventRegistry eventRegistry;
    private Database widgetCatalogDB;
    private boolean overrideExisting = true;
    private String widgetCategory = "d.3ecm";
    private String widgetType = "T";

    public WidgetCatalogBuilder(Session session) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create Widget Catalog Builder");
	this.session = session;
	logger.fine(Resources.LOG_SEPARATOR_END);
    }

    public Session getSession() {
	return session;
    }

    public void setSession(Session session) {
	this.session = session;
    }

    public String getServer() {
	return server;
    }

    public void setServer(String server) {
	if ("currentServer".equals(server)) {
	    try {
		server = session.getServerName();
	    } catch (NotesException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	this.server = server;
    }

    public String getWidgetCatalogNsfFileName() {
	return widgetCatalogNsfFileName;
    }

    public void setWidgetCatalogNsfFileName(String widgetCatalogNsfFileName) {
	this.widgetCatalogNsfFileName = widgetCatalogNsfFileName;
    }

    public String getWidgetCatalogNsfTitle() {
	return widgetCatalogNsfTitle;
    }

    public void setWidgetCatalogNsfTitle(String widgetCatalogNsfTitle) {
	this.widgetCatalogNsfTitle = widgetCatalogNsfTitle;
    }

    public String getWidgetCatalogTemplateFileName() {
	return widgetCatalogTemplateFileName;
    }

    public void setWidgetCatalogTemplateFileName(String widgetCatalogTemplateFileName) {
	this.widgetCatalogTemplateFileName = widgetCatalogTemplateFileName;
    }

    public String getExtensionXMLPath() {
	return extensionXMLPath;
    }

    public void setExtensionXMLPath(String extensionXMLPath) {
	this.extensionXMLPath = extensionXMLPath;
    }

    public String getSiteUrl() {
	return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
	this.siteUrl = siteUrl;
    }

    public EventRegistry getEventRegistry() {
	return eventRegistry;
    }

    public void setEventRegistry(EventRegistry eventRegistry) {
	this.eventRegistry = eventRegistry;
    }

    public Database getWidgetCatalogDB() {
	return widgetCatalogDB;
    }

    public void setWidgetCatalogDB(Database widgetCatalogDB) {
	this.widgetCatalogDB = widgetCatalogDB;
    }

    public boolean isOverrideExisting() {
	return overrideExisting;
    }

    public void setOverrideExisting(boolean overrideExisting) {
	this.overrideExisting = overrideExisting;
    }

    public String getWidgetCategory() {
	return widgetCategory;
    }

    public void setWidgetCategory(String widgetCategory) {
	this.widgetCategory = widgetCategory;
    }

    public String getWidgetType() {
	return widgetType;
    }

    public void setWidgetType(String widgetType) {
	this.widgetType = widgetType;
    }

    public void buildWidgetCatalog() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("start buildWidgetCatalog");
	try {
	    logger.fine("trying to get widget catalog Database");
	    widgetCatalogDB = session.getDatabase(server, widgetCatalogNsfFileName, false);

	    if (widgetCatalogDB == null) {
		logger.fine("not found");
		logger.fine("trying to get update site Template");
		Database widgetCatalogTemplate = session.getDatabase(server, widgetCatalogTemplateFileName);
		logger.fine("is Database open?");
		if (!widgetCatalogTemplate.isOpen()) {
		    logger.fine("open Database");
		    widgetCatalogTemplate.open();
		}
		logger.fine("trying to create new update site Database from Template");
		widgetCatalogDB = widgetCatalogTemplate.createFromTemplate(server, widgetCatalogNsfFileName, true);
		logger.fine("set Title");
		widgetCatalogDB.setTitle(widgetCatalogNsfTitle);
	    }
	    reconfigureExtensionXML();
	    importWidgetXML();

	} catch (NotesException e) {
	    logger.log(Level.SEVERE, e.getMessage(), e);

	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void reconfigureExtensionXML() {
	try {
	    File file = new File(extensionXMLPath);
	    String saveExtensionXMPath = file.getParent() + File.separator + "save" + file.getName();
	    OSServices.copyFile(extensionXMLPath, saveExtensionXMPath);

	    DocumentBuilder domp = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    org.w3c.dom.Document document = domp.parse(file);
	    NodeList palleteItems = document.getElementsByTagName("palleteItem");
	    String nodeValue;
	    for (int i = 0; i < palleteItems.getLength(); i++) {
		Node item = palleteItems.item(i);
		NamedNodeMap attributes = item.getAttributes();
		Node namedItemID = attributes.getNamedItem("id");
		if (namedItemID != null) {
		    nodeValue = namedItemID.getTextContent();
		    if (!nodeValue.equals("com.dvelop.d3.smartnotes.sidebar.feature")) {
			continue;
		    }
		}
		Node namedItemURL = attributes.getNamedItem("url");
		if (namedItemURL != null) {
		    // nodeValue = namedItemURL.getTextContent();
		    // if (nodeValue.equals("/INSERT/PATH/TO/site.xml")) {
		    namedItemURL.setTextContent(siteUrl);
		    break;
		    // }
		}
	    }
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    DOMSource source = new DOMSource(document);
	    StreamResult result = new StreamResult(file);
	    transformer.transform(source, result);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    // private void updateWidgetCatalog() {
    //
    // }

    private void importWidgetXML() {
	/*
	 * Function ImportWidgetXML Description: Import widget xml to create a
	 * widget document Import proxy xml to create a proxy document in widget
	 * catalog Import OAuth xml to create a OAuth document in remote
	 * credential store db if there is existing proxy or OAuth document, a
	 * dialog will be popped up to ask for replace.
	 */
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("import widget XML");
	String xfilepath = null;
	boolean cflag = false;
	try {

	    if (extensionXMLPath == null || extensionXMLPath.equals("")) {
		return;
	    }

	    // 'create widget document based on file
	    logger.fine("create widget document based on file");

	    String platform;
	    String delim;
	    String ufname;

	    platform = session.getPlatform();
	    if (platform.toLowerCase().startsWith("win")) {
		delim = "\\";
	    } else {
		delim = "/";
	    }
	    File file = new File(extensionXMLPath);
	    ufname = file.getName();
	    if (ufname.toLowerCase().equals("extension.xml")) {
		xfilepath = file.getAbsolutePath();
	    } else {
		xfilepath = file.getAbsolutePath().replace(ufname, "extension.xml");
		// 'make a copy to add the new name
		OSServices.copyFile(file.getAbsolutePath(), xfilepath);
		cflag = true;
	    }

	    // 'fill the title , description, extension.xml
	    logger.fine("fill the title , description, extension.xml");
	    // Stream stream;
	    // String charsetV;
	    // stream = session.createStream();
	    // charsetV = "UTF-8";
	    // stream.open(file.getAbsolutePath(), charsetV);
	    DocumentBuilder domParser;
	    domParser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

	    org.w3c.dom.Document domd;
	    Element rootElem;
	    domd = domParser.parse(file);
	    rootElem = domd.getDocumentElement();

	    Node palleteItem;
	    palleteItem = rootElem.getElementsByTagName("palleteItem").item(0);
	    if (palleteItem == null) {
		// 'report an error
		logger.fine("report an error");
		// MsgBox NoWidgetCreatedError,16,ErrorDlgTitle
		if (cflag) {
		    new File(xfilepath).delete();
		}
	    } else {
		// 'get title
		logger.fine("get title");
		String title;
		String description;
		String sPlatform;
		String imageURL;
		title = Common.domGetAttribute(palleteItem, "title");
		description = Common.domGetAttribute(palleteItem, "description");
		sPlatform = Common.domGetAttribute(palleteItem, "platform");
		imageURL = Common.domGetAttribute(palleteItem, "imageURL");

		if ("".equals(description)) {
		    description = title;
		}

		// 'compose the document
		logger.fine("compose the document");
		Document doc;
		Document statisticRDoc;
		doc = widgetCatalogDB.createDocument();

		doc.replaceItemValue("Form", "TOOL");
		doc.replaceItemValue("Version", "1.1");
		doc.replaceItemValue("xmlReviewNeeded", 1);
		doc.replaceItemValue("OrigAttachInfo", extensionXMLPath);
		doc.replaceItemValue("AttachName", "extension.xml");
		doc.replaceItemValue("Title", title);
		doc.replaceItemValue("Description", description);
		if (!"".equals(sPlatform)) {
		    Vector<String> platforms = new Vector<String>();
		    String[] platformArray = sPlatform.split(",");
		    for (int i = 0; i < platformArray.length; i++) {
			platforms.add(platformArray[i]);
		    }
		    doc.replaceItemValue("Platform", platforms);
		} else {
		    doc.replaceItemValue("Platform", "");
		}
		if (widgetCategory.contains(",")) {
		    Vector<String> categories = new Vector<String>();
		    String[] categoryArray = widgetCategory.split(",");
		    for (int i = 0; i < categoryArray.length; i++) {
			categories.add(categoryArray[i]);
		    }
		    doc.replaceItemValue("Categories", categories);
		} else {
		    doc.replaceItemValue("Categories", widgetCategory);
		}
		if (widgetType.contains(",")) {
		    Vector<String> types = new Vector<String>();
		    String[] typeArray = widgetType.split(",");
		    for (int i = 0; i < typeArray.length; i++) {
			types.add(typeArray[i]);
		    }
		    doc.replaceItemValue("Type", types);
		} else {
		    doc.replaceItemValue("Type", widgetType);
		}

		RichTextItem toolAttach;
		toolAttach = doc.createRichTextItem("ToolAttach");
		toolAttach.embedObject(EmbeddedObject.EMBED_ATTACHMENT, "", xfilepath, null);
		if (!"".equals(imageURL)) {
		    if (imageURL.startsWith("http://") || imageURL.startsWith("https://")) {
			URL website = new URL(imageURL);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			File tempFile = File.createTempFile("thumb", "jpg");
			FileOutputStream fos = new FileOutputStream(tempFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			File thumb = new File(tempFile.getParent() + File.separator + "thumb.jpg");
			OSServices.copyFile(tempFile, thumb);
			imageURL = thumb.getAbsolutePath();
			thumb.deleteOnExit();
		    }
		    RichTextItem thumbDisplay;
		    thumbDisplay = doc.createRichTextItem("ThumbDisplay");
		    thumbDisplay.setSaveToDisk(true);
		    thumbDisplay.embedObject(EmbeddedObject.EMBED_ATTACHMENT, "", imageURL, "thumb.jpg");
		}
		if (cflag) {
		    new File(xfilepath).delete();
		}

		// 'review widget.xml
		logger.fine("review widget.xml");
		reviewXML(doc, extensionXMLPath);
		doc.replaceItemValue("xmlReviewNeeded", 0);
		doc.computeWithForm(false, false);
		doc.save(false, false);

		// ' create a statistic response document
		logger.fine("create a statistic response document");
		updateStatRDoc(doc);

		// 'Update StatDataVersion field
		logger.fine("Update StatDataVersion field");
		updateStatDataVersionField(widgetCatalogDB);

		// 'check the type of widget
		logger.fine("check the type of widget");
		boolean isWebWidget;
		Boolean isOpenSocialWidget;
		String providerId;
		String capabilitiesURL;
		capabilitiesURL = doc.getItemValueString("capabilitiesURL");
		if (!capabilitiesURL.equals("")) {
		    capabilitiesURL = normalizeURL(capabilitiesURL);
		}
		providerId = doc.getItemValueString("providerId");
		isWebWidget = false;
		isOpenSocialWidget = false;
		if (!capabilitiesURL.equals("") && !providerId.equals("") && providerId.equals("com.ibm.rcp.toolbox.web.provider.WebServicesPalleteProvider")) {
		    isWebWidget = true;
		}
		if (!capabilitiesURL.equals("") && !providerId.equals("") && providerId.equals("com.ibm.rcp.toolbox.opensocial.provider.internal.OpenSocialPalleteProvider")) {
		    isOpenSocialWidget = true;
		}

		int importProxyErrorCode;
		int importOAuthErrorCode;
		importProxyErrorCode = -1;
		importOAuthErrorCode = -1;

		if (isOpenSocialWidget) {
		    // 'create a initial proxy doc based on gadget definition
		    logger.fine("create a initial proxy doc based on gadget definition");
		    Document proxyDoc;
		    proxyDoc = createProxyDoc(doc);
		    proxyDoc.replaceItemValue("ProxyEnabled", "FALSE");
		    proxyDoc.save(false, false);
		    View view;
		    view = widgetCatalogDB.getView("GadgetProxyView");
		    view.refresh();

		    // 'precheck: check whether proxy or OAuth config file
		    // exists
		    logger.fine("precheck: check whether proxy or OAuth config file exists");
		    String proxyFilePath = "";
		    String oauthFilePath = "";
		    String proxyFileName = "";
		    String oauthFileName = "";
		    if (ufname.contains("extension.xml")) {
			proxyFilePath = extensionXMLPath.replace("extension.xml", "proxy.xml");
			oauthFilePath = extensionXMLPath.replace("extension.xml", "oauth.xml");
			proxyFileName = proxyFilePath;
			oauthFileName = oauthFilePath;
		    }

		    // 'import proxy.xml and { create Proxy doc in current
		    // widget catalog
		    logger.fine("import proxy.xml and { create Proxy doc in current widget catalog");

		    if (!proxyFileName.equals("")) {
			importProxyErrorCode = importProxyXML(proxyFilePath, capabilitiesURL, doc);
		    }

		    String isOAuth;
		    String isOAuth2;
		    isOAuth = doc.getItemValueString("IsOAuth");
		    isOAuth2 = doc.getItemValueString("IsOAuth2");
		    if ((!isOAuth.equals("") && isOAuth.equals("True")) || (!isOAuth2.equals("") && isOAuth2.equals("True"))) {
			if (oauthFileName.equals("")) {
			    // 'import oauth.xml and { create OAuth doc in
			    // remote credential store db
			    logger.fine("import oauth.xml and { create OAuth doc in remote credential store db");
			    importOAuthErrorCode = importOauthXML(oauthFilePath, capabilitiesURL, doc, overrideExisting);

			} else {
			    importOAuthErrorCode = 1023;
			    createInitalOAuth(doc);

			}

			setInitalPolicy(capabilitiesURL, proxyDoc, doc);

			if (showErrorMsg(importProxyErrorCode, importOAuthErrorCode, title, proxyFilePath, oauthFilePath, extensionXMLPath)) {
			    // ws.Editdocument(False, doc)
			    return;
			}
		    }
		}
		//
		// 'approve widget - decision made not to approve widget on
		// import
		logger.fine("approve widget - decision made not to approve widget on import");
		/*
		 * if ImportProxyErrorCode=0 And ImportOAuthErrorCode=0 Or
		 * isWebWidget { Dim caps As String caps =
		 * doc.GetItemValue("capabilities")(0) doc.securityReviewNeeded
		 * = 0 doc.save False,False if(caps<>""){ doc.Sign doc.Save(
		 * True, False, False ) ProxyEnabled(True,doc,db) view =
		 * db.GetView("By Category") view.Refresh 'push capability and
		 * proxy to credential store if on master server
		 * RunAgentifOnMasterSever(db) } }
		 */

		String msgDetails;
		msgDetails = widgetCreatedText.replace("VAR_WIDGETNAME", title);
		msgDetails = msgDetails.replace("VAR_NEWLINE", "\n");
		msgDetails = msgDetails.replace("VAR_WIDGETXMLPATH", extensionXMLPath);

		// ws.Editdocument(False, doc)
	    }

	} catch (Exception e) {
	    if (cflag) {
		if (xfilepath != null) {
		    new File(xfilepath).delete();
		}
	    }
	    e.printStackTrace();
	    // Select Case Err
	    // Case 4602
	    // Dim FailToImportTxt As String
	    // FailToImportTxt = Replace(FailToImportWidgetText,
	    // "VAR_NEWLINE",Chr(13))
	    // FailToImportTxt = Replace(FailToImportTxt,
	    // "VAR_WIDGETXMLPATH",fname(0))
	    // MsgBox FailToImportTxt,16, WidgetImportErrTxt
	    // Case }else{
	    // MsgBox Error$,16,WidgetImportErrTxt
	    // End Select
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private boolean showErrorMsg(int importProxyErrorCode, int importOAuthErrorCode, String title, String proxyFilePath, String oauthFilePath, String widgetFilePath) {
	// 'display accurate message for end user
	try {
	    logger.fine("display accurate message for end user");
	    String msgDetails;
	    String failImportProxyOAuthTxt;
	    String failToImportProxyTxt;
	    String failToImportOAuthTxt;
	    String failToImportTxt;
	    String failToImportProxyOAuthTxt;

	    failToImportTxt = failToImportText.replace("VAR_WIDGETNAME", title);
	    failToImportTxt = failToImportTxt.replace("VAR_NEWLINE", "\n");
	    failToImportProxyOAuthTxt = failToImportProxyOAuthText.replace("VAR_WIDGETNAME", title);
	    failToImportProxyOAuthTxt = failToImportProxyOAuthTxt.replace("VAR_NEWLINE", "\n");

	    if ((importProxyErrorCode == 0 && importOAuthErrorCode == 0) || (importProxyErrorCode == -1 && importOAuthErrorCode == -1)
		    || (importProxyErrorCode == 0 && importOAuthErrorCode == -1) || (importProxyErrorCode == -1 && importOAuthErrorCode == 0)) {
		return false;
	    }

	    if (importProxyErrorCode != 0 && importProxyErrorCode != -1 && importOAuthErrorCode != 0 && importOAuthErrorCode != -1 && importOAuthErrorCode != 1023) {
		failToImportProxyOAuthTxt = failToImportProxyOAuthTxt.replace("VAR_PROXYXMLPATH", proxyFilePath);
		failToImportProxyOAuthTxt = failToImportProxyOAuthTxt.replace("VAR_OAUTHXMLPATH", oauthFilePath);
		// MsgBox FailToImportProxyOAuthTxt,16,InfoTxt
		return true;
	    } else {
		if (importProxyErrorCode != 0) {
		    failToImportTxt = failToImportTxt.replace("VAR_PROXYXMLPATH", proxyFilePath);
		    failToImportTxt = failToImportTxt.replace("VAR_OAUTHXMLPATH", "");
		}
		if (importOAuthErrorCode == 1021) {
		    failToImportTxt = failToOpenOAuthDb.replace("VAR_NEWLINE", "\n");
		    failToImportTxt = failToImportTxt.replace("VAR_WIDGETNAME", title);
		}
		if (importOAuthErrorCode == 1022) {
		    failToImportTxt = failToFindOAuthConfig.replace("VAR_NEWLINE", "\n");
		    failToImportTxt = failToImportTxt.replace("VAR_WIDGETNAME", title);
		    failToImportTxt = failToImportTxt.replace("VAR_OAUTHXMLPATH", oauthFilePath);
		}
		if (importOAuthErrorCode == 1023) {
		    String delim;
		    String parentFolder;

		    if (session.getPlatform().toLowerCase().startsWith("win")) {
			delim = "\\";
		    } else {
			delim = "/";
		    }
		    parentFolder = widgetFilePath.substring(0, widgetFilePath.lastIndexOf(delim));
		    failToImportTxt = failToFindOAuthFile.replace("VAR_NEWLINE", "\n");
		    failToImportTxt = failToImportTxt.replace("VAR_WIDGETNAME", title);
		    failToImportTxt = failToImportTxt.replace("VAR_OAUTHXMLPATH", parentFolder);
		}
		if (importOAuthErrorCode == 4602) {
		    failToImportTxt = failToImportTxt.replace("VAR_PROXYXMLPATH", "");
		    failToImportTxt = failToImportTxt.replace("VAR_OAUTHXMLPATH", oauthFilePath);
		}
		// MsgBox FailToImportTxt,16,InfoTxt
		return true;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
    }

    private int setInitalPolicy(String capabilitiesURL, Document proxyDoc, Document widgetDoc) {
	/*
	 * Function InitalPolicy Description: When creating a new proxy
	 * document, create initial policy rules based on gadget definition
	 * Return result: error code
	 */
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("set inital policy");
	try {
	    Item choiceTxtField;
	    choiceTxtField = proxyDoc.getFirstItem("CurrentPolicies");

	    if (!hasExistingPolicy(choiceTxtField, capabilitiesURL)) {
		choiceTxtField.appendToTextList(capabilitiesURL + "=GET;;;;");
	    }

	    String isOAuth2;
	    String isOAuth;
	    isOAuth2 = widgetDoc.getItemValueString("IsOAuth2").toUpperCase();
	    isOAuth = widgetDoc.getItemValueString("IsOAuth").toUpperCase();

	    String delim = "?";

	    String oauth10aRequestToken;
	    String oauth10aAccessToken;
	    String oauth2Token;

	    if (isOAuth2.equals("TRUE")) {
		for (Object oauth2AccUrlObj : widgetDoc.getItemValue("OAuth2AccessURL")) {
		    String oauth2AccUrl = (String) oauth2AccUrlObj;
		    oauth2Token = oauth2AccUrl.split(delim)[0];
		    if (!oauth2Token.equals("")) {
			if (!hasExistingPolicy(choiceTxtField, oauth2Token)) {
			    choiceTxtField.appendToTextList(oauth2Token + "=POST;[default],client_id,client_secret;;;");
			}
		    }
		}
	    }

	    if (isOAuth.equals("TRUE")) {
		for (Object oauthReqUrlObj : widgetDoc.getItemValue("oauth_requestUrl")) {
		    String oauthReqUrl = (String) oauthReqUrlObj;

		    oauth10aRequestToken = oauthReqUrl.split(delim)[0];
		    if (!oauth10aRequestToken.equals("")) {
			if (!hasExistingPolicy(choiceTxtField, oauth10aRequestToken)) {
			    choiceTxtField.appendToTextList(oauth10aRequestToken + "=GET;[default],Authorization;;;");
			}
		    }
		}
		for (Object oauthAccUrlObj : widgetDoc.getItemValue("oauth_accessUrl")) {
		    String oauthAccUrl = (String) oauthAccUrlObj;
		    oauth10aAccessToken = oauthAccUrl.split(delim)[0];
		    if (oauth10aAccessToken.equals("")) {
			if (!hasExistingPolicy(choiceTxtField, oauth10aAccessToken)) {
			    choiceTxtField.appendToTextList(oauth10aAccessToken + "=GET;[default],Authorization;;;");
			}
		    }
		}
	    }

	    proxyDoc.save(false, false);
	    return 0;

	} catch (Exception e) {
	    return 666;
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private boolean hasExistingPolicy(Item item, String url) {
	try {
	    ProxyUtil proxyUtil = new ProxyUtil();

	    String[] items;
	    String key;
	    for (Object vObj : item.getValues()) {
		if (vObj instanceof String) {
		    String v = (String) vObj;
		    if (!v.equals("")) {
			items = proxyUtil.splitByEqual(v);
			key = items[0];
			if (key.equals(url)) {
			    return true;
			}
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return false;
    }

    private void createInitalOAuth(Document widgetDoc) {
	/*
	 * Function CreateInitalOAuth Description: No oauth.xml provided, {
	 * check whether widget includes OAuth service. if yes, create
	 * Oauth2ConsumerDoc or OauthConsumerDoc document and response doc for
	 * this widget.
	 */
	try {
	    String serviceName = "";
	    String capabilitiesURL;
	    String isOAuth2;
	    String isOAuth;
	    int i;
	    int error;
	    Database oauthDB;
	    oauthDB = getSecurityStoreDB();
	    if (oauthDB == null) {
		error = 1021;
	    }
	    capabilitiesURL = widgetDoc.getItemValueString("capabilitiesURL");
	    if (!capabilitiesURL.equals("")) {
		capabilitiesURL = normalizeURL(capabilitiesURL);
	    }
	    isOAuth = widgetDoc.getItemValueString("IsOAuth").toUpperCase();
	    isOAuth2 = widgetDoc.getItemValueString("IsOAuth2").toUpperCase();
	    // 'create an OAuth2 doc and a response doc in credential store
	    if (isOAuth2.equals("TRUE") || widgetDoc.hasItem("NumOAuth2Elements")) {
		Document Oauth2ConsumerDoc;
		Oauth2ConsumerDoc = getOAuth2ConsumerDocument(serviceName, capabilitiesURL, oauthDB);
		if (Oauth2ConsumerDoc == null) {
		    if (widgetDoc.hasItem("NumOAuth2Elements")) {
			for (i = 1; i <= Integer.valueOf(widgetDoc.getItemValueString("NumOAuth2Elements")); i++) {
			    serviceName = (String) widgetDoc.getItemValue("OAuth2Service").get(i - 1);
			    Oauth2ConsumerDoc = createOAuth2ConsumerDoc(widgetDoc, oauthDB, capabilitiesURL, serviceName, i - 1);
			    // 'create response doc
			    createOAuthResponse(widgetDoc.getUniversalID(), capabilitiesURL, serviceName, "TRUE", oauthDB);
			}
		    } else {
			serviceName = (String) widgetDoc.getItemValue("OAuth2Service").get(0);
			Oauth2ConsumerDoc = createOAuth2ConsumerDoc(widgetDoc, oauthDB, capabilitiesURL, serviceName, 0);
			// 'create response doc
			createOAuthResponse(widgetDoc.getUniversalID(), capabilitiesURL, serviceName, "TRUE", oauthDB);
		    }
		}
	    }
	    // 'create an OAuth doc and a response doc in credential store
	    if (isOAuth.equals("TRUE") || widgetDoc.hasItem("NumOAuthElements")) {
		Document OauthConsumerDoc;
		OauthConsumerDoc = getOAuthConsumerDocument(serviceName, capabilitiesURL, oauthDB);
		if (OauthConsumerDoc == null) {
		    if (widgetDoc.hasItem("NumOAuthElements")) {
			for (i = 1; i <= Integer.valueOf(widgetDoc.getItemValueString("NumOAuthElements")); i++) {
			    serviceName = (String) widgetDoc.getItemValue("oauth_servcice").get(i - 1);
			    OauthConsumerDoc = createOAuthConsumerDoc(widgetDoc, oauthDB, capabilitiesURL, serviceName, i - 1);
			    // 'create response doc
			    createOAuthResponse(widgetDoc.getUniversalID(), capabilitiesURL, serviceName, "FALSE", oauthDB);
			}
		    } else {
			serviceName = (String) widgetDoc.getItemValue("oauth_servcice").get(0);
			OauthConsumerDoc = createOAuthConsumerDoc(widgetDoc, oauthDB, capabilitiesURL, serviceName, 0);
			// 'create response doc
			createOAuthResponse(widgetDoc.getUniversalID(), capabilitiesURL, serviceName, "FALSE", oauthDB);
		    }
		}
	    }

	} catch (Exception e) {
	    // Select Case Err
	    // Case 1021
	    // Dim FailToImportTxt As String, title As String
	    // title = widgetDoc.Getitemvalue("Title")(0)
	    // FailToImportTxt = Replace(FailToOpenOAuthDb,
	    // "VAR_NEWLINE",Chr(13))
	    // FailToImportTxt = Replace(FailToImportTxt,
	    // "VAR_WIDGETNAME",title)
	    // MsgBox FailToImportTxt,16,InfoTxt
	    // Case }else{
	    // MsgBox Error$,16,WidgetImportErrTxt
	    // End Select
	}

    }

    private Document createOAuthConsumerDoc(Document widgetDoc, Database securityStore, String appId, String serviceName, int iElement) {
	/*
	 * Function CreateOAuthConsumerDoc Description: Create a new oauth
	 * consumer document in target websecuritystore
	 */

	Document odoc = null;
	try {
	    odoc = securityStore.createDocument();
	    odoc.replaceItemValue("Form", "OAuthConsumer_10a");
	    odoc.computeWithForm(false, false);
	    odoc.replaceItemValue("AppId", appId);
	    odoc.replaceItemValue("ServiceName", serviceName);

	    String requestTokenUrl;
	    String authUrl;
	    String accessTokenUrl;

	    requestTokenUrl = (String) widgetDoc.getItemValue("oauth_requestUrl").get(iElement);
	    authUrl = (String) widgetDoc.getItemValue("oauth_authUrl").get(iElement);
	    accessTokenUrl = (String) widgetDoc.getItemValue("oauth_accessUrl").get(iElement);
	    odoc.replaceItemValue("RequestTokenUri", requestTokenUrl);
	    odoc.replaceItemValue("AuthorizationUri", authUrl);
	    odoc.replaceItemValue("AccessTokenUri", accessTokenUrl);

	    String user;
	    user = session.getUserName();

	    String[] authors = new String[3];
	    authors[1] = user;
	    authors[2] = "[Admins]";
	    authors[3] = "LocalDomainServers";

	    odoc.replaceItemValue("Authors", authors);
	    odoc.replaceItemValue("Readers", authors);
	    odoc.save(false, false);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return odoc;
    }

    private Document getOAuthConsumerDocument(String serviceName, String appId, Database securityStore) {
	/*
	 * Function GetOAuthConsumerDocument Description: Get OAuth Consumer
	 * document with specific ServerName and AppId in websecuritystore
	 * databse. Return Noting if could not be found.
	 */
	try {
	    View view;
	    String[] keys = new String[2];
	    view = securityStore.getView("OAuthConsumer_10a");
	    view.refresh();
	    keys[1] = appId;
	    keys[2] = serviceName;
	    /*
	     * view.Getdocumentbykey method is not case sensitive even
	     * exactMatch is set to true We need to get thru all the found match
	     * docs and find correct one
	     */

	    ViewEntryCollection vec;
	    vec = view.getAllEntriesByKey(keys, true);
	    ViewEntry entry;
	    entry = vec.getFirstEntry();
	    while (entry != null) {
		String uri;
		uri = (String) entry.getColumnValues().get(0);
		if (appId.equals(uri)) {
		    return entry.getDocument();
		}
		entry = vec.getNextEntry(entry);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    private void createOAuthResponse(String unid, String appId, String serviceName, String isOAuth2, Database credStore) {
	/*
	 * Function CreateOAuthResponse Description: Each widget document will
	 * create a response document for the OAuth consumer document, which is
	 * used by the gadget. This function will check whether the reponse
	 * document has been created. if not, it will create the document. The
	 * response document will contain the widget document's unid,
	 * appId/serviceName of the OAuth consumer, and the OAuth version.
	 */
	try {
	    View view;
	    String[] keys = new String[4];

	    view = credStore.getView("OAuthRefRecord");
	    keys[1] = unid;
	    keys[2] = appId;
	    keys[3] = serviceName;
	    keys[4] = isOAuth2;

	    /*
	     * view.Getdocumentbykey method is not case sensitive even
	     * exactMatch is set to true We need to get thru all the found match
	     * docs and find correct one
	     */
	    ViewEntryCollection vec;
	    vec = view.getAllEntriesByKey(keys, true);
	    ViewEntry entry;
	    entry = vec.getFirstEntry();
	    Document refDoc = null;

	    while (entry != null) {
		String vu;
		String va;
		String vs;
		String vo;
		vu = (String) entry.getColumnValues().get(0);
		va = (String) entry.getColumnValues().get(1);
		vs = (String) entry.getColumnValues().get(2);
		vo = (String) entry.getColumnValues().get(3);

		if (unid.equals(vu) && appId.equals(va) && serviceName.equals(vs) && isOAuth2.equals(vo)) {
		    refDoc = entry.getDocument();
		}
		entry = vec.getNextEntry(entry);
	    }

	    if (refDoc == null) {
		Document consumerDoc;
		if (Boolean.valueOf(isOAuth2)) {
		    consumerDoc = getOAuth2ConsumerDocument(serviceName, appId, credStore);
		} else {
		    consumerDoc = getOAuthConsumerDocument(serviceName, appId, credStore);
		}

		if (consumerDoc != null) {
		    // 'create the response
		    refDoc = credStore.createDocument();
		    refDoc.makeResponse(consumerDoc);
		    refDoc.replaceItemValue("Form", "OAuthRefRecord");
		    refDoc.replaceItemValue("WidgetUnid", unid);
		    refDoc.replaceItemValue("AppID", appId);
		    refDoc.replaceItemValue("ServiceName", serviceName);
		    refDoc.replaceItemValue("IsOAuth2", isOAuth2);

		    refDoc.save(false, false);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private Document createOAuth2ConsumerDoc(Document widgetDoc, Database securityStore, String appId, String serviceName, int iElement) {
	/*
	 * Function CreateOAuth2ConsumerDoc Description: Create a new oauth2
	 * consumer document in target websecuritystore
	 */
	Document odoc = null;
	try {
	    odoc = securityStore.createDocument();
	    odoc.replaceItemValue("Form", "OAuthConsumer_20");
	    odoc.computeWithForm(false, false);
	    odoc.replaceItemValue("AppId", appId);
	    odoc.replaceItemValue("ServiceName", serviceName);

	    String authUrl;
	    String accessTokenUrl;

	    authUrl = (String) widgetDoc.getItemValue("OAuth2AuthURL").get(iElement);
	    accessTokenUrl = (String) widgetDoc.getItemValue("OAuth2AccessURL").get(iElement);
	    odoc.replaceItemValue("AuthorizationUri", authUrl);
	    odoc.replaceItemValue("AccessTokenUri", accessTokenUrl);

	    String user;
	    user = session.getUserName();

	    String[] authors = new String[3];
	    authors[1] = user;
	    authors[2] = "[Admins]";
	    authors[3] = "LocalDomainServers";

	    odoc.replaceItemValue("Authors", authors);
	    odoc.replaceItemValue("Readers", authors);
	    odoc.save(false, false);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return odoc;
    }

    private Document getOAuth2ConsumerDocument(String serviceName, String appId, Database securityStore) {
	/*
	 * Function GetOAuth2ConsumerDocument Description: Get OAuth2 Consumer
	 * document with specific ServerName and AppId in websecuritystore
	 * databse. Return Noting if could not be found.
	 */
	try {
	    View view;
	    String[] keys = new String[2];
	    view = securityStore.getView("OAuthConsumer_20");
	    view.refresh();
	    keys[1] = appId;
	    keys[2] = serviceName;
	    /*
	     * view.Getdocumentbykey method is not case sensitive even
	     * exactMatch is set to true We need to get thru all the found match
	     * docs and find correct one
	     */

	    ViewEntryCollection vec;
	    vec = view.getAllEntriesByKey(keys, true);
	    ViewEntry entry;
	    entry = vec.getFirstEntry();
	    while (entry != null) {
		String uri;
		uri = (String) entry.getColumnValues().get(0);
		if (appId.equals(uri)) {
		    return entry.getDocument();

		}
		entry = vec.getNextEntry(entry);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    private Database getSecurityStoreDB() {
	/*
	 * Function GetSecurityStoreDB Description: Get target websecuirtystore
	 * database. Return Nothing is the target database is not available
	 */
	try {
	    Document profile;
	    profile = widgetCatalogDB.getProfileDocument("(OTSProfile)", "Toolbox_OTSUniqueKey");

	    String server;
	    String dbName;

	    server = profile.getItemValueString("ServerName");
	    dbName = profile.getItemValueString("NSFName");
	    if (server.equals("") || dbName.equals("")) {
		return null;
	    }

	    return session.getDatabase(server, dbName, false);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    private int importOauthXML(String oauthFilePath, String capabilitiesURL, Document widgetDoc, boolean overrideOAuthDoc) {
	/*
	 * Sub ImportOAuthXML Description: Parse oauth.xml and { create/update
	 * an oauth document in the remote credential store. In 854 release, we
	 * support only one oauth2 service if there are multiple oauth1a and
	 * oauth2. Multi-services has been deferred to next release. Return
	 * value: error code 0, succussfully import OAuth 1021, fail to open
	 * credential store Err, fail to import OAuth
	 */
	try {

	    String serviceN;
	    String isOAuth;
	    String isOAuth2;
	    String auth2Url;
	    String accessToken2Url;
	    String requestTokenUrl;
	    String authUrl;
	    String accessTokenUrl;
	    Vector<Object> vOath2AuthURL;
	    Vector<Object> vOath2AccURL;
	    Vector<Object> vReqTokenUrl;
	    Vector<Object> vOathAuthURL;
	    Vector<Object> vOathAccURL;
	    Document proxyDoc;

	    Database oauthDB;
	    Database catalog;
	    oauthDB = getSecurityStoreDB();
	    catalog = widgetCatalogDB;
	    if (oauthDB == null) {
		// Error 1021
	    }

	    DocumentBuilder domp = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    org.w3c.dom.Document domd = domp.parse(new File(oauthFilePath));
	    Element rootElem;
	    rootElem = domd.getDocumentElement();
	    Node subItem;
	    NodeList oauth1aList = null;
	    NodeList oauth2List = null;
	    int i;
	    Document oauth1aConsumerDoc;
	    Document oauth2ConsumerDoc;

	    String serviceName;
	    String appID = null;
	    boolean foundServiceName;
	    foundServiceName = false;

	    if (rootElem != null) {
		appID = Common.domGetAttribute(rootElem, "ID");
		// 'normalize gadget uri
		if (!appID.equalsIgnoreCase("")) {
		    appID = normalizeURL(appID);
		    // ' no matchable service name
		    if (appID.equals(capabilitiesURL)) {
			return 1022;
		    }
		}
		oauth1aList = rootElem.getElementsByTagName("OAuth1a");
		oauth2List = rootElem.getElementsByTagName("OAuth2");
	    }

	    isOAuth = ((String) widgetDoc.getItemValue("IsOAuth").get(0)).toUpperCase();
	    isOAuth2 = ((String) widgetDoc.getItemValue("IsOAuth2").get(0)).toUpperCase();

	    int j;

	    // 'parse oauth2 services from oauth definition file
	    if (isOAuth2.equals("TRUE") || widgetDoc.hasItem("NumOAuth2Elements")) {
		// 'need to support multiple OAuth2 elements with or without
		// Service Names
		for (i = 1; i < oauth2List.getLength(); i++) {
		    subItem = oauth2List.item(i);
		    serviceName = Common.domGetAttribute(subItem, "ServiceName");
		    // 'check if this oauth service is used in open social
		    // widget
		    j = getOAuthServiceInWidgetDoc(widgetDoc, serviceName, true);
		    // 'only create oauth consumer document if the oauth service
		    // is used
		    if (j != -1) {
			oauth2ConsumerDoc = getOAuth2ConsumerDocument(serviceName, appID, oauthDB);
			if (oauth2ConsumerDoc == null) {
			    oauth2ConsumerDoc = createOAuth2ConsumerDoc(widgetDoc, oauthDB, appID, serviceName, j);
			}
			if (overrideOAuthDoc) {
			    setOAuth2ConsumerDoc(appID, subItem, oauth2ConsumerDoc, oauthDB);
			}

			// 'synch oauth data
			auth2Url = (String) oauth2ConsumerDoc.getItemValue("AuthorizationUri").get(0);
			accessToken2Url = (String) oauth2ConsumerDoc.getItemValue("AccessTokenUri").get(0);
			vOath2AuthURL = widgetDoc.getItemValue("OAuth2AuthURL");
			vOath2AuthURL.set(j, auth2Url);
			widgetDoc.replaceItemValue("OAuth2AuthURL", vOath2AuthURL);

			vOath2AccURL = widgetDoc.getItemValue("OAuth2AccessURL");
			vOath2AccURL.set(j, accessToken2Url);
			widgetDoc.replaceItemValue("OAuth2AccessURL", vOath2AccURL);

			widgetDoc.save(true, false);
		    }
		}
	    }

	    // 'parse oauth services from oauth definition file
	    if (isOAuth2.equals("TRUE") || widgetDoc.hasItem("NumOAuthElements")) {
		// 'need to support multiple OAuth elements with or without
		// Service Names
		for (i = 1; i < oauth1aList.getLength(); i++) {
		    subItem = oauth1aList.item(i);
		    serviceName = Common.domGetAttribute(subItem, "ServiceName");
		    // 'check if this oauth service is used in open social
		    // widget
		    j = getOAuthServiceInWidgetDoc(widgetDoc, serviceName, false);
		    // 'only create oauth consumer document if the oauth service
		    // is used
		    if (j != -1) {
			oauth1aConsumerDoc = getOAuthConsumerDocument(serviceName, appID, oauthDB);
			if (oauth1aConsumerDoc == null) {
			    oauth1aConsumerDoc = createOAuthConsumerDoc(widgetDoc, oauthDB, appID, serviceName, j);
			}
			if (overrideOAuthDoc) {
			    setOAuthConsumerDoc(appID, subItem, oauth1aConsumerDoc, oauthDB);
			}

			// 'synch oauth data
			requestTokenUrl = (String) oauth1aConsumerDoc.getItemValue("RequestTokenUri").get(0);
			authUrl = (String) oauth1aConsumerDoc.getItemValue("AuthorizationUri").get(0);
			accessTokenUrl = (String) oauth1aConsumerDoc.getItemValue("AccessTokenUri").get(0);

			vReqTokenUrl = widgetDoc.getItemValue("oauth_requestUrl");
			vReqTokenUrl.set(j, requestTokenUrl);
			widgetDoc.replaceItemValue("oauth_requestUrl", vReqTokenUrl);

			vOathAuthURL = widgetDoc.getItemValue("oauth_authUrl");
			vOathAuthURL.set(j, authUrl);
			widgetDoc.replaceItemValue("oauth_authUrl", vOathAuthURL);

			vOathAccURL = widgetDoc.getItemValue("oauth_accessUrl");
			vOathAccURL.set(j, accessTokenUrl);
			widgetDoc.replaceItemValue("oauth_accessUrl", vOathAccURL);
			widgetDoc.save(true, false);
		    }

		}
	    }

	    boolean updated;
	    Item choiceTxtField;
	    String proxyURL;
	    proxyDoc = getProxyDocument(widgetDoc, catalog);
	    // 'check the oauth services defined in widget document, in case
	    // some oauth services are missing in oauth definition file
	    if (widgetDoc.hasItem("NumOAuthElements")) {
		for (i = 0; i < Integer.valueOf((String) widgetDoc.getItemValue("NumOAuthElements").get(0)) - 1; i++) {
		    // 'create OAuth document if it's not present and set
		    serviceName = (String) widgetDoc.getItemValue("oauth_servcice").get(i);
		    oauth1aConsumerDoc = getOAuthConsumerDocument(serviceName, appID, oauthDB);
		    if (oauth1aConsumerDoc == null) {
			createOAuthConsumerDoc(widgetDoc, oauthDB, appID, serviceName, i);
			return 1022;
		    }
		    // 'add proxy info if it's not present
		    if (proxyDoc != null) {
			choiceTxtField = proxyDoc.getFirstItem("CurrentPolicies");
			updated = false;
			proxyURL = (String) widgetDoc.getItemValue("oauth_requestUrl").get(i);
			if (!proxyURL.equals("") && (!hasExistingPolicy(choiceTxtField, proxyURL))) {
			    choiceTxtField.appendToTextList(proxyURL + "=GET;[default],Authorization;;;");
			    updated = true;
			}

			proxyURL = (String) widgetDoc.getItemValue("oauth_accessUrl").get(i);
			if (!proxyURL.equals("") && (!hasExistingPolicy(choiceTxtField, proxyURL))) {
			    choiceTxtField.appendToTextList(proxyURL + "=GET;[default],Authorization;;;");
			    updated = true;
			}

			if (updated) {
			    proxyDoc.replaceItemValue("Processed", "False");
			    proxyDoc.save(false, false);
			}
		    }
		    // 'create OAuth resonse
		    createOAuthResponse(widgetDoc.getUniversalID(), appID, serviceName, "FALSE", oauthDB);
		}
	    }

	    if (widgetDoc.hasItem("NumOAuth2Elements")) {
		for (i = 0; i < Integer.valueOf((String) widgetDoc.getItemValue("NumOAuth2Elements").get(0)) - 1; i++) {
		    serviceName = (String) widgetDoc.getItemValue("OAuth2Service").get(i);
		    oauth2ConsumerDoc = getOAuth2ConsumerDocument(serviceName, appID, oauthDB);
		    if (oauth2ConsumerDoc == null) {
			createOAuth2ConsumerDoc(widgetDoc, oauthDB, appID, serviceName, i);
			return 1022;
		    }
		    if (proxyDoc != null) {
			choiceTxtField = proxyDoc.getFirstItem("CurrentPolicies");
			proxyURL = (String) widgetDoc.getItemValue("OAuth2AccessURL").get(i);
			if (proxyURL.equals("") && !hasExistingPolicy(choiceTxtField, proxyURL)) {
			    choiceTxtField.appendToTextList(proxyURL + "=POST;[default],client_id,client_secret;;;");
			    proxyDoc.replaceItemValue("Processed", "False");
			    proxyDoc.save(false, false);
			}
		    }
		    createOAuthResponse(widgetDoc.getUniversalID(), appID, serviceName, "TRUE", oauthDB);
		}
	    }

	} catch (Exception e) {
	    // stream.Close()
	    // ImportOAuthXML = Err
	    // Select Case Err
	    // Case 4602
	    // 'Print "Error : " & Err & " : " & Error$ & Chr(13)& oauthFilePath
	    // & Chr(13)& Chr(13) & domp.Log
	    // Case }else{
	    // 'Print "Error : " & Err & " : " & Error$
	    // End Select
	}
	return 0;
    }

    private Document getProxyDocument(Document widgetDoc, Database catalog) {
	/*
	 * Sub GetProxyDocument Description: Get Proxy Document based on the
	 * Widget doc's UNID
	 */
	try {
	    View view;
	    String[] keys = new String[2];

	    view = catalog.getView("GadgetProxyView");
	    view.refresh();
	    keys[1] = widgetDoc.getUniversalID();
	    /*
	     * view.Getdocumentbykey method is not case sensitive even
	     * exactMatch is set to true We need to get thru all the found match
	     * docs and find correct one
	     */

	    ViewEntryCollection vec;
	    vec = view.getAllEntriesByKey(keys, true);
	    ViewEntry entry;
	    entry = vec.getFirstEntry();
	    while (entry != null) {
		String docUNID;
		docUNID = (String) entry.getColumnValues().get(0);
		if (keys[1].equals(docUNID)) {
		    return entry.getDocument();
		}
		entry = vec.getNextEntry(entry);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    private void setOAuthConsumerDoc(String appID, Node subItem, Document oauth1aConsumerDoc, Database oauthDB) {
	/*
	 * Function OAuthConsumerDoc Description: Comments for Function
	 */
	try {
	    String consumerKeyValue1a;
	    String consumerSecretValue1a;
	    String oauth10aRequestToken;
	    String oauth10aAccessToken;
	    consumerKeyValue1a = Common.domGetAttribute(subItem, "Consumer_Key");
	    consumerSecretValue1a = Common.domGetAttribute(subItem, "Consumer_Secret");
	    oauth10aRequestToken = Common.domGetAttribute(subItem, "OAuth_Request_Token_URI");
	    oauth10aAccessToken = Common.domGetAttribute(subItem, "OAuth_Access_Token_URI");

	    oauth1aConsumerDoc.replaceItemValue("AppId", appID);
	    oauth1aConsumerDoc.replaceItemValue("ServiceName", Common.domGetAttribute(subItem, "ServiceName"));
	    oauth1aConsumerDoc.replaceItemValue("RequestTokenUri", oauth10aRequestToken);
	    oauth1aConsumerDoc.replaceItemValue("AuthorizationUri", Common.domGetAttribute(subItem, "OAuth_Authorization_URI"));
	    oauth1aConsumerDoc.replaceItemValue("AccessTokenUri", oauth10aAccessToken);
	    oauth1aConsumerDoc.replaceItemValue("SignatureMethod", Common.domGetAttribute(subItem, "Signature_Method"));
	    String includeBodyHash;
	    includeBodyHash = Common.domGetAttribute(subItem, "Include_Body_Hash");
	    if (!includeBodyHash.equals("")) {
		oauth1aConsumerDoc.replaceItemValue("IncludeBodyHash", includeBodyHash.toUpperCase());
	    } else {
		oauth1aConsumerDoc.replaceItemValue("IncludeBodyHash", "TRUE");
	    }

	    oauth1aConsumerDoc.removeItem(OAUTH_CONSUMER_KEY);
	    oauth1aConsumerDoc.removeItem(OAUTH_CONSUMER_SECRET);
	    oauth1aConsumerDoc.save(false, false);

	    CredStore credStore = new CredStore();
	    if (!consumerKeyValue1a.equals("")) {
		credStore.encryptField(oauth1aConsumerDoc, consumerKeyValue1a, ENC_OAUTH_CONSUMER_KEY, oauthDB, session);
	    }

	    if (!consumerSecretValue1a.equals("")) {
		credStore.encryptField(oauth1aConsumerDoc, consumerSecretValue1a, ENC_OAUTH_CONSUMER_SECRET, oauthDB, session);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void setOAuth2ConsumerDoc(String appID, Node subItem, Document oauth2ConsumerDoc, Database oauthDB) {
	/*
	 * Function OAuth2ConsumerDoc Description: Comments for Function
	 */
	try {
	    String oAuth2ConsumerKeyValue;
	    String oAuth2ConsumerSecretValue;
	    String oauth2AccessUrl;
	    oAuth2ConsumerKeyValue = Common.domGetAttribute(subItem, "Consumer_Key");
	    oAuth2ConsumerSecretValue = Common.domGetAttribute(subItem, "Consumer_Secret");
	    oauth2AccessUrl = Common.domGetAttribute(subItem, "OAuth_Access_Token_URI");
	    oauth2ConsumerDoc.replaceItemValue("AppId", appID);
	    oauth2ConsumerDoc.replaceItemValue("ServiceName", Common.domGetAttribute(subItem, "ServiceName"));
	    oauth2ConsumerDoc.replaceItemValue("AllowModuleOverrides", Common.domGetAttribute(subItem, "AllowModuleOverides"));
	    oauth2ConsumerDoc.replaceItemValue("AuthorizationUri", Common.domGetAttribute(subItem, "OAuth_Authorization_URI"));
	    oauth2ConsumerDoc.replaceItemValue("AccessTokenUri", oauth2AccessUrl);
	    oauth2ConsumerDoc.replaceItemValue("ClientType", Common.domGetAttribute(subItem, "Client_Type"));
	    oauth2ConsumerDoc.replaceItemValue("GrantType", Common.domGetAttribute(subItem, "Grant_Type"));
	    oauth2ConsumerDoc.replaceItemValue("ClientAuthType", Common.domGetAttribute(subItem, "Client_Auth_Type"));

	    if (Common.domGetAttribute(subItem, "AllowModuleOverides").toLowerCase().equals("true")) {
		oauth2ConsumerDoc.replaceItemValue("AllowModuleOverrides", "True");
	    } else {
		oauth2ConsumerDoc.replaceItemValue("AllowModuleOverrides", "False");
	    }

	    if (Common.domGetAttribute(subItem, "Use_Authorization_Header").toLowerCase().equals("true")) {
		oauth2ConsumerDoc.replaceItemValue("UseAuthorizationHeader", "True");
	    } else {
		oauth2ConsumerDoc.replaceItemValue("UseAuthorizationHeader", "False");
	    }

	    if (Common.domGetAttribute(subItem, "Use_Url_Parameter").toLowerCase().equals("true")) {
		oauth2ConsumerDoc.replaceItemValue("UseUrlParameter", "True");
	    } else {
		oauth2ConsumerDoc.replaceItemValue("UseUrlParameter", "False");
	    }

	    if (Common.domGetAttribute(subItem, "Shared").toLowerCase().equals("true")) {
		oauth2ConsumerDoc.replaceItemValue("SharedTokens", "True");
	    } else {
		oauth2ConsumerDoc.replaceItemValue("SharedTokens", "False");
	    }

	    // 'remove the ClientId and ClientSecret field
	    oauth2ConsumerDoc.removeItem(OAUTH2_CONSUMER_KEY);
	    oauth2ConsumerDoc.removeItem(OAUTH2_CONSUMER_SECRET);

	    oauth2ConsumerDoc.save(false, false);

	    // 'encrypt consumer key and secret
	    CredStore credStore = new CredStore();
	    if (!oAuth2ConsumerKeyValue.equals("")) {
		credStore.encryptField(oauth2ConsumerDoc, oAuth2ConsumerKeyValue, ENC_OAUTH2_CONSUMER_KEY, oauthDB, session);
	    }
	    if (!oAuth2ConsumerSecretValue.equals("")) {
		credStore.encryptField(oauth2ConsumerDoc, oAuth2ConsumerSecretValue, ENC_OAUTH2_CONSUMER_SECRET, oauthDB, session);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private int getOAuthServiceInWidgetDoc(Document widgetDoc, String serviceName, boolean isOAuth2) {
	try {
	    int i;
	    if (isOAuth2) {
		Vector<Object> vOAuth2SvcName;
		vOAuth2SvcName = widgetDoc.getItemValue("OAuth2Service");

		for (i = 0; i <= Integer.valueOf((String) widgetDoc.getItemValue("NumOAuth2Elements").get(0)) - 1; i++) {
		    if (serviceName.equals((String) vOAuth2SvcName.get(i))) {
			return i;
		    }
		}
	    } else {
		Vector<Object> vOAuthSvcName;
		vOAuthSvcName = widgetDoc.getItemValue("oauth_servcice");

		for (i = 0; i <= Integer.valueOf((String) widgetDoc.getItemValue("NumOAuthElements").get(0)) - 1; i++) {
		    if (serviceName.equals((String) vOAuthSvcName.get(i))) {
			return i;
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return -1;
    }

    private int importProxyXML(String filepath, String gadgetURL, Document widgetDoc) {
	/*
	 * Sub ImportProxyXML Description: Parse proxy.xml and { create a proxy
	 * document in current widget catalog Return value: error code 0,
	 * succussfully import proxy Err, fail to import proxy
	 */
	try {
	    DocumentBuilder domp = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    org.w3c.dom.Document domd = domp.parse(new File(filepath));
	    Element rootElem;
	    NodeList proxyList = null;
	    rootElem = domd.getDocumentElement();
	    Node subItem;
	    Document proxyDoc;
	    Database catalog;
	    catalog = widgetCatalogDB;
	    proxyDoc = getProxyDocument(widgetDoc, catalog);

	    setProxyValuesErrCode = 1000;
	    if (rootElem != null) {
		proxyList = rootElem.getElementsByTagName("gadget");
	    }

	    if ((proxyList != null) && proxyList.getLength() > 0) {
		int i;
		int errCode;
		String isContentProxy;
		String capabilitiesURL;
		String proxyFileName;
		String delim;
		String platform = session.getPlatform();
		if (platform.toLowerCase().startsWith("win")) {
		    delim = "\\";
		} else {
		    delim = "/";
		}
		proxyFileName = new File(filepath).getName();

		for (i = 0; i < proxyList.getLength(); i++) {
		    subItem = proxyList.item(i);
		    isContentProxy = Common.domGetAttribute(subItem, "ContentProxy");
		    capabilitiesURL = Common.domGetAttribute(subItem, "url");
		    if (!capabilitiesURL.equals("")) {
			// 'normalize gadget uri
			capabilitiesURL = normalizeURL(capabilitiesURL);
			if (capabilitiesURL.equals(gadgetURL)) {
			    if (proxyDoc == null) {
				proxyDoc = createProxyDoc(widgetDoc);
			    }
			    if (!isContentProxy.equals("") && isContentProxy.toLowerCase().equals("true")) {
				// 'set content proxy
				proxyDoc = setProxyValues(true, capabilitiesURL, (Element) subItem, proxyDoc, proxyFileName);
			    } else {
				// 'set gadget proxy
				proxyDoc = setProxyValues(false, capabilitiesURL, (Element) subItem, proxyDoc, proxyFileName);

			    }
			}
		    }
		}
		if (proxyDoc != null) {
		    validateProxyValues(proxyDoc);
		}

		return 0;
	    }

	} catch (Exception e) {
	    // stream.Close()
	    // ImportProxyXML = Err
	    // Select Case Err
	    // Case 4602
	    // 'Print "Error : " & Err & " : " & Error$ & Chr(13)& filepath &
	    // Chr(13)& Chr(13) & domp.Log
	    // Case 1036
	    // Dim InvalidAllowDenyListErrorTxt As String
	    // InvalidAllowDenyListErrorTxt = Replace(InvalidAllowDenyListError,
	    // "VAR_PROXYXMLNAME",proxyFileName)
	    // 'Print InvalidAllowDenyListErrorTxt
	    // Case }else{
	    // 'Print "Error : " & Err & " : " & Error$
	    // End Select
	}
	return -1;
    }

    private void validateProxyValues(Document proxyDoc) {
	/*
	 * Function ValidateProxyValues Description: Validate allow list and
	 * deny list
	 */
	try {
	    ProxyUtil proxyUtil = new ProxyUtil();

	    // ' validate allow and deny list for content proxy and gadget proxy
	    String gadgetProxyAL;
	    String gadgetProxyDL;
	    String contentProxyAL;
	    String contentProxyDL;
	    String emptyValue;
	    int err;
	    gadgetProxyAL = (String) proxyDoc.getItemValue("AllowList").get(0);
	    gadgetProxyDL = (String) proxyDoc.getItemValue("DenyList").get(0);
	    contentProxyAL = (String) proxyDoc.getItemValue("ContentProxyAllowList").get(0);
	    contentProxyDL = (String) proxyDoc.getItemValue("ContentProxyDenyList").get(0);
	    emptyValue = "";
	    err = 1000;
	    if (!proxyUtil.isValidIPFilter(gadgetProxyAL)) {
		proxyDoc.replaceItemValue("AllowList", emptyValue);
		err = 1036;
	    }
	    if (!proxyUtil.isValidIPFilter(gadgetProxyDL)) {
		proxyDoc.replaceItemValue("DenyList", emptyValue);
		err = 1036;
	    }
	    if (!proxyUtil.isValidIPFilter(contentProxyAL)) {
		proxyDoc.replaceItemValue("ContentProxyAllowList", emptyValue);
		err = 1036;
	    }
	    if (!proxyUtil.isValidIPFilter(contentProxyDL)) {
		proxyDoc.replaceItemValue("ContentProxyDenyList", emptyValue);
		err = 1036;
	    }
	    proxyDoc.save(false, false);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private Document setProxyValues(boolean isContentProxy, String capabilitiesURL, Element rootElem, Document proxyDoc, String proxyFileName) {
	/*
	 * Function ProxyValues Description: Comments for Function
	 */
	try {
	    Node subItem;
	    NodeList allowNodeList = null;
	    NodeList denyNodeList = null;
	    NodeList proxyRuleNodeList = null;

	    ProxyUtil proxyUtilObject = new ProxyUtil();

	    if (rootElem != null) {
		allowNodeList = rootElem.getElementsByTagName("allow");
		denyNodeList = rootElem.getElementsByTagName("deny");
		proxyRuleNodeList = rootElem.getElementsByTagName("ProxyRule");
	    }

	    int i;
	    String allowIPList = "";
	    String denyIPList = "";
	    String proxyRuleList;
	    String proxyRuleURL;
	    String proxyRuleActions;
	    String proxyRuleHeaders;
	    String proxyRuleMIMETypes;
	    String proxyRuleCookies;
	    Item choiceTxtField;
	    Item allowIPListField;
	    Item denyIPListField;
	    if (allowNodeList != null) {
		if (isContentProxy) {
		    allowIPListField = proxyDoc.getFirstItem("ContentProxyAllowList");
		} else {
		    allowIPListField = proxyDoc.getFirstItem("AllowList");
		}
		for (i = 0; i < allowNodeList.getLength(); i++) {
		    subItem = allowNodeList.item(i);
		    allowIPList = allowIPList + Common.domGetAttribute(subItem, "ip");
		    if (i + 1 != allowNodeList.getLength()) {
			allowIPList = allowIPList + ",";
		    }
		    allowIPListField.appendToTextList(allowIPList);
		}
	    }

	    if (denyNodeList != null) {
		if (isContentProxy) {
		    denyIPListField = proxyDoc.getFirstItem("ContentProxyDenyList");
		} else {
		    denyIPListField = proxyDoc.getFirstItem("DenyList");
		}
		for (i = 0; i < denyNodeList.getLength(); i++) {
		    subItem = denyNodeList.item(i);
		    denyIPList = denyIPList + Common.domGetAttribute(subItem, "ip");
		    if (i + 1 != denyNodeList.getLength()) {
			denyIPList = denyIPList + ",";
		    }
		    denyIPListField.appendToTextList(denyIPList);
		}
	    }

	    if (proxyRuleNodeList != null) {
		String delimiter;
		delimiter = ";";
		String duplicateProxyErrorTxt;
		String invalidProxyPropertyErrorTxt;
		if (isContentProxy) {
		    choiceTxtField = proxyDoc.getFirstItem("CurrentContentPolicies");
		} else {
		    choiceTxtField = proxyDoc.getFirstItem("CurrentPolicies");
		}
		for (i = 0; i < proxyRuleNodeList.getLength(); i++) {
		    subItem = proxyRuleNodeList.item(i);
		    proxyRuleURL = Common.domGetAttribute(subItem, "url");
		    proxyRuleActions = Common.domGetAttribute(subItem, "actions");
		    proxyRuleHeaders = Common.domGetAttribute(subItem, "headers");
		    proxyRuleMIMETypes = Common.domGetAttribute(subItem, "mime_types");
		    proxyRuleCookies = Common.domGetAttribute(subItem, "cookies");

		    // 'Validate whether there are duplicate proxy rule
		    Vector<Object> sourceArray;
		    boolean hasContain;
		    sourceArray = choiceTxtField.getValues();
		    hasContain = false;
		    String[] items;
		    String key;
		    for (Object vObj : sourceArray) {
			if (vObj instanceof String) {
			    String v = (String) vObj;
			    if (!v.equals("")) {
				items = proxyUtilObject.splitByEqual(v);
				key = items[0];
				if (key.equals(proxyRuleURL)) {
				    hasContain = true;
				    break;
				}
			    }
			}
		    }

		    if (hasContain) {
			setProxyValuesErrCode = 1030;
			duplicateProxyErrorTxt = DuplicateProxyError.replace("VAR_PROXYXMLNAME", proxyFileName);
			// 'Print DuplicateProxyErrorTxt
		    } else {
			if ((!proxyUtilObject.isValidURI(proxyRuleURL)) || (!proxyUtilObject.isValidActions(proxyRuleActions))
				|| (!proxyUtilObject.isValidHeaders(proxyRuleHeaders)) || (!proxyUtilObject.isValidMimeTypes(proxyRuleMIMETypes))
				|| (!proxyUtilObject.isValidCookies(proxyRuleCookies))) {
			    setProxyValuesErrCode = 1031;
			    invalidProxyPropertyErrorTxt = InvalidProxyPropertyError.replace("VAR_PROXYXMLNAME", proxyFileName);
			    // 'Print InvalidProxyPropertyErrorTxt
			} else {
			    proxyRuleList = proxyRuleURL.trim() + "=" + proxyRuleActions.trim() + delimiter + proxyRuleHeaders.trim() + delimiter + proxyRuleMIMETypes.trim()
				    + delimiter + proxyRuleCookies.trim() + delimiter;
			    choiceTxtField.appendToTextList(proxyRuleList);
			}
		    }
		}
	    }

	    if (isContentProxy) {
		proxyDoc.replaceItemValue("PolicyURL_1", "");
		proxyDoc.replaceItemValue("Actions_1", "");
		proxyDoc.replaceItemValue("GadgetURL_1", capabilitiesURL);
	    } else {
		proxyDoc.replaceItemValue("PolicyURL", "");
		proxyDoc.replaceItemValue("Actions", "");
		proxyDoc.replaceItemValue("GadgetURL", capabilitiesURL);
	    }
	    proxyDoc.replaceItemValue("Processed", "False");
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return proxyDoc;

    }

    private Document createProxyDoc(Document widgetDoc) {
	/*
	 * Sub CreateProxyDoc Description: Create a new proxy document in
	 * catalog
	 */
	Document proxyDoc = null;
	try {
	    String capabilitiesURL;
	    capabilitiesURL = widgetDoc.getItemValueString("capabilitiesURL");
	    if (!capabilitiesURL.equals("")) {
		capabilitiesURL = normalizeURL(capabilitiesURL);
	    }
	    proxyDoc = widgetDoc.getParentDatabase().createDocument();
	    proxyDoc.makeResponse(widgetDoc);
	    proxyDoc.replaceItemValue("Form", "GadgetProxy");
	    proxyDoc.replaceItemValue("GadgetURL", capabilitiesURL);
	    proxyDoc.replaceItemValue("WidgetUNID", widgetDoc.getUniversalID());
	    proxyDoc.replaceItemValue("Processed", "False");
	    // ' InitalPolicy(Capabilitiesurl, proxyDoc, Widgetdoc)
	    proxyDoc.computeWithForm(false, false);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return proxyDoc;
    }

    private String normalizeURL(String url) {
	/*
	 * Function NormalizeURL Description: Normalize a url
	 */
	try {
	    URIUtil uriUtilObject = new URIUtil();
	    return uriUtilObject.normalizeGadgetURL(url);
	} catch (Exception e) {
	    return url;

	}
    }

    private void updateStatDataVersionField(Database db) {
	/*
	 * Function UpdateStatDataVersionField Description: Update
	 * StatDataVersion field
	 */
	try {
	    Document profileDoc;
	    profileDoc = db.getProfileDocument("(StatDataVersion)", "");
	    String statDataVersion = "";
	    Vector itemValue = profileDoc.getItemValue("StatDataVersion");
	    if (itemValue != null && itemValue.size() > 0) {
		statDataVersion = String.valueOf(itemValue.get(0));
	    }
	    if (statDataVersion.equals("")) {
		View allWidgetsView;
		// allWidgetsView = db.getView("AllWidgets");
		allWidgetsView = db.getView("($All)");
		int count;
		if (allWidgetsView != null) {
		    count = allWidgetsView.getEntryCount();
		    if (count == 1) {
			int CURRENT_DATA_VERSION;
			int ConflictAction;
			CURRENT_DATA_VERSION = 1;
			ConflictAction = 3;
			profileDoc.replaceItemValue("StatDataVersion", CURRENT_DATA_VERSION);
			profileDoc.replaceItemValue("$ConflictAction", ConflictAction);
			profileDoc.save(true, false);
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void updateStatRDoc(Document doc) {
	/*
	 * Sub UpdateStatRDoc Description: Write widgets information into
	 * statistic document so that we can read all of widget information from
	 * a single backend view.
	 */
	try {
	    Document statisticRDoc = null;
	    String statisticRDocUNID;
	    EmbeddedObject thumb;

	    DocumentCollection rdocCollection;
	    Document rdocCurrent;
	    String statisticFormName;

	    if (!doc.isNewNote()) {
		statisticFormName = "statistic";
		rdocCollection = doc.getResponses();
		rdocCurrent = rdocCollection.getFirstDocument();
		boolean flag;
		flag = true;
		while (rdocCurrent != null && flag) {
		    if (rdocCurrent.getItemValueString("Form").toLowerCase().equals(statisticFormName)) {
			statisticRDoc = rdocCurrent;
			flag = false;
		    }
		    rdocCurrent = rdocCollection.getNextDocument(rdocCurrent);
		}
	    }

	    if (statisticRDoc == null) {
		statisticRDoc = widgetCatalogDB.createDocument();
		statisticRDoc.makeResponse(doc);
		statisticRDoc.replaceItemValue("Form", "statistic");
		statisticRDoc.replaceItemValue("WidgetUNID", doc.getUniversalID());
		statisticRDoc.replaceItemValue("downloadCount", 0);
		statisticRDoc.replaceItemValue("ratingAverage", 0);

		doc.replaceItemValue("statisticResponseUNID", statisticRDoc.getUniversalID());
		doc.save(false, false);
	    }

	    statisticRDoc.replaceItemValue("Title", doc.getItemValue("Title"));
	    statisticRDoc.replaceItemValue("LastModified", doc.getLastModified());
	    statisticRDoc.replaceItemValue("Author", doc.getItemValue("DocAuthor"));
	    statisticRDoc.replaceItemValue("Platform", doc.getItemValue("Platform"));
	    statisticRDoc.replaceItemValue("Categories", doc.getItemValue("Categories"));
	    statisticRDoc.replaceItemValue("Type", doc.getItemValue("Type"));
	    statisticRDoc.replaceItemValue("Description", doc.getItemValue("Description"));
	    statisticRDoc.replaceItemValue("xmlReviewNeeded", doc.getItemValue("xmlReviewNeeded"));
	    statisticRDoc.replaceItemValue("securityReviewNeeded", doc.getItemValue("securityReviewNeeded"));
	    statisticRDoc.replaceItemValue("capabilities", doc.getItemValue("capabilities"));

	    thumb = doc.getAttachment("thumb.jpg");
	    if (thumb != null) {
		statisticRDoc.replaceItemValue("hasThumbnail", 1);
		statisticRDoc.replaceItemValue("thumbUrl", "/" + doc.getUniversalID() + "/$FILE/thumb.jpg");
	    } else {
		statisticRDoc.replaceItemValue("hasThumbnail", 0);
		statisticRDoc.replaceItemValue("thumbUrl", "");
	    }

	    statisticRDoc.computeWithForm(false, false);
	    statisticRDoc.save(false, false);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void reviewXML(Document doc, String file) {
	try {
	    DocumentBuilder domp = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    org.w3c.dom.Document domd = domp.parse(new File(file));
	    Element rootElem;
	    rootElem = domd.getDocumentElement();
	    Element dataElem;
	    boolean modified;
	    NodeList nl;
	    int i;
	    int k;

	    String[] gfIds = new String[1];
	    OAuthSvcNames = new ArrayList<String>();
	    OAuthSvcReqURLs = new ArrayList<String>();
	    OAuthSvcAuthURLs = new ArrayList<String>();
	    OAuthSvcAccessURLs = new ArrayList<String>();
	    OAuth2SvcNames = new ArrayList<String>();
	    OAuth2SVCScopes = new ArrayList<String>();
	    OAuth2SvcAuthURLs = new ArrayList<String>();
	    OAuth2SvcTokenURLs = new ArrayList<String>();

	    // %REM
	    // Looking for palleteItem - providerId gets set
	    // %END REM

	    Element palleteItem;
	    palleteItem = findElement("palleteItem", rootElem);
	    if (palleteItem != null) {
		String providerId;
		providerId = Common.domGetAttribute(palleteItem, "providerId");
		doc.replaceItemValue("providerId", providerId);
	    }

	    // %REM
	    // Looking for object-capabilities
	    // %END REM

	    dataElem = findElement("palleteItem\\data", rootElem);
	    if (dataElem != null) {
		Element capElem;
		Element featureElem;
		capElem = findElement("object-capabilities", dataElem);
		if (capElem == null) {
		    capElem = domd.createElement("object-capabilities");
		    dataElem.appendChild(capElem);
		}
		String url;
		url = Common.domGetAttribute(capElem, "url");
		// 'get widget doc capabilitiesURL item value from
		// object-capabilities url attribute
		if (!url.equals("")) {
		    doc.replaceItemValue("capabilitiesURL", url);
		}
		nl = capElem.getElementsByTagName("grant-feature");
		doc.removeItem("capabilities");
		String caps = "";
		if (nl.getLength() > 0) {
		    doc.replaceItemValue("securityReviewNeeded", 1);
		    // 'get each of the grant-feature ids attribute values
		    for (i = 0; i < nl.getLength(); i++) {
			featureElem = (Element) nl.item(i);
			String featureString;
			featureString = featureElem.getAttribute("id");
			caps = caps + featureString;
			if (i + 1 != nl.getLength()) {
			    caps = caps + ",";
			}
		    }
		}
		// 'set the capabilities item value to the array of
		// grant-feature
		// ids attribute values
		doc.replaceItemValue("capabilities", caps);

		// %REM
		// Looking for OAuth info
		// %END REM

		Element oauthElem;
		Element serviceElem;
		String service;
		Element accessElem;
		Element authElem;
		String accessUrl;
		String authUrl;
		Element requestElem;
		String requestUrl;

		oauthElem = findElement("OAuth", dataElem);
		if (oauthElem == null) {
		    doc.replaceItemValue("IsOAuth", "False");
		} else {
		    serviceElem = findElement("Service", oauthElem);
		    if (serviceElem != null) {
			nl = oauthElem.getElementsByTagName("Service");
			doc.replaceItemValue("NumOAuthElements", nl.getLength());
			// 'used by the For Loop on the Widget for to create the
			// num
			// rows in pass thru table
			doc.replaceItemValue("IsOAuth", "True");
			for (i = 0; i < nl.getLength(); i++) {
			    serviceElem = (Element) nl.item(i);
			    service = serviceElem.getAttribute("name");

			    OAuthSvcNames.add(service);
			    // 'get AccessToken url
			    accessElem = findElement("Access", serviceElem);

			    if (accessElem != null) {
				accessUrl = accessElem.getAttribute("url");
				OAuthSvcAccessURLs.add(accessUrl);
			    } else {
				OAuthSvcAccessURLs.add("");
			    }

			    // 'get Authorization url
			    authElem = findElement("Authorization", serviceElem);
			    if (authElem != null) {
				authUrl = authElem.getAttribute("url");
				OAuthSvcAuthURLs.add(authUrl);
			    } else {
				OAuthSvcAuthURLs.add("");
			    }

			    // 'get request token url
			    requestElem = findElement("Request", serviceElem);
			    if (requestElem != null) {
				requestUrl = requestElem.getAttribute("url");
				OAuthSvcReqURLs.add(requestUrl);
			    } else {
				OAuthSvcReqURLs.add("");
			    }
			}
		    } else {
			doc.replaceItemValue("IsOAuth", "False");
			OAuthSvcNames.add("");
			OAuthSvcAccessURLs.add("");
			OAuthSvcAuthURLs.add("");
			OAuthSvcReqURLs.add("");
		    }
		}

		// %REM
		// Looking for OAuth2 info
		// %END REM
		Element oauth2Elem;
		Element oauth2ServiceElem;
		String oauth2ServiceName;
		String oauth2ServiceScope;
		Element oauth2AccessElem;
		String oauth2AccessUrl;
		Element oauth2AuthElem;
		String oauth2AuthUrl;

		oauth2Elem = findElement("OAuth2", dataElem);
		if (oauth2Elem == null) {
		    doc.replaceItemValue("IsOAuth2", "False");
		} else {
		    oauth2ServiceElem = findElement("Service", oauth2Elem);
		    if (oauth2ServiceElem != null) {
			nl = oauth2Elem.getElementsByTagName("Service");
			doc.replaceItemValue("NumOAuth2Elements", nl.getLength());
			// 'used by the For Loop on the Widget for to create the
			// num
			// rows in pass thru table
			doc.replaceItemValue("IsOAuth2", "True");
			for (i = 0; i < nl.getLength(); i++) {
			    oauth2ServiceElem = (Element) nl.item(i);
			    oauth2ServiceName = oauth2ServiceElem.getAttribute("name");
			    OAuth2SvcNames.add(oauth2ServiceName);

			    // 'Get oauth2 service scope
			    oauth2ServiceScope = oauth2ServiceElem.getAttribute("scope");
			    OAuth2SVCScopes.add(oauth2ServiceScope);

			    // 'get AccessToken url
			    oauth2AccessElem = findElement("Token", oauth2ServiceElem);
			    if (oauth2AccessElem != null) {
				oauth2AccessUrl = oauth2AccessElem.getAttribute("url");
				OAuth2SvcTokenURLs.add(oauth2AccessUrl);
			    } else {
				OAuth2SvcTokenURLs.add("");
			    }

			    // 'get Authorization url
			    oauth2AuthElem = findElement("Authorization", oauth2ServiceElem);
			    if (oauth2AuthElem != null) {
				oauth2AuthUrl = oauth2AuthElem.getAttribute("url");
				OAuth2SvcAuthURLs.add(oauth2AuthUrl);
			    } else {
				OAuth2SvcAuthURLs.add("");
			    }
			}
		    } else {
			doc.replaceItemValue("IsOAuth2", "False");
			OAuth2SvcNames.add("");
			OAuth2SVCScopes.add("");
			OAuth2SvcTokenURLs.add("");
			OAuth2SvcAuthURLs.add("");
		    }
		}
	    }

	    // 'write all the oath/oauth2 data to the widget note (**** may want
	    // to
	    // only write these values if there is any oauth/oauth2 data ****)
	    doc.replaceItemValue("oauth_servcice", new Vector<String>(OAuthSvcNames));
	    doc.replaceItemValue("oauth_requestUrl", new Vector<String>(OAuthSvcReqURLs));
	    doc.replaceItemValue("oauth_authUrl", new Vector<String>(OAuthSvcAuthURLs));
	    doc.replaceItemValue("oauth_accessUrl", new Vector<String>(OAuthSvcAccessURLs));
	    doc.replaceItemValue("OAuth2Service", new Vector<String>(OAuth2SvcNames));
	    doc.replaceItemValue("OAuth2ServiceScope", new Vector<String>(OAuth2SVCScopes));
	    doc.replaceItemValue("OAuth2AuthURL", new Vector<String>(OAuth2SvcAuthURLs));
	    doc.replaceItemValue("OAuth2AccessURL", new Vector<String>(OAuth2SvcTokenURLs));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private Element findElement(String path, Element root) {
	NodeList nl;
	int ind;
	int k;
	String seekname;

	Element candidateElem;
	Element curElem;
	Element parentElem;
	boolean wildcard = false;
	boolean match;
	Node curNode;
	parentElem = root;
	seekname = path.split("\\\\")[0];
	int indexOf = path.indexOf("\\");
	if (indexOf >= 0) {
	    path = path.substring(indexOf + 1);
	} else {
	    path = "";
	}
	while (seekname.equals("*")) {
	    wildcard = true;
	    seekname = path.split("\\")[1];
	    path = new File(path).getName();
	}
	if (wildcard) {
	    nl = parentElem.getElementsByTagName(seekname);
	    for (ind = 0; ind < nl.getLength(); ind++) {
		curElem = (Element) nl.item(ind);
		if (path.equals("")) {
		    return curElem;
		}
		candidateElem = findElement(path, curElem);
		if (candidateElem != null) {
		    return candidateElem;
		}
	    }
	} else {
	    // ' not a wildcard; iterate thru children looking for one with the
	    // right name
	    curNode = parentElem.getFirstChild();
	    while (curNode != null) {
		if (curNode.getNodeType() == DOMNODETYPE_ELEMENT_NODE) {
		    curElem = (Element) curNode;
		    if (curElem.getNodeName().equals(seekname)) {
			if (path.length() == 0) {
			    return curElem;
			} else {
			    candidateElem = findElement(path, curElem);
			    if (candidateElem != null) {
				return candidateElem;

			    }
			}
		    }
		}
		curNode = curNode.getNextSibling();
	    }
	}
	return null;
    }
}
