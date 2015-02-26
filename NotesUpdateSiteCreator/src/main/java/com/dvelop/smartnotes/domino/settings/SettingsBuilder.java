package com.dvelop.smartnotes.domino.settings;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.dvelop.smartnotes.domino.policy.managment.PolicyManagement;
import com.dvelop.smartnotes.domino.resources.Constants;
import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.updatesite.exceptions.OException;

public class SettingsBuilder {
    private static final String CATALOG_CATEGORIES_TO_INSTALL = "catalogCategoriesToInstall";
    private static final String TOOLBOX_CATALOG_DB_NAME = "toolboxCatalogDBName";
    private static final String TOOLBOX_CATALOG_SERVER = "toolboxCatalogServer";
    private static final String POLICY_DESCRIPTION = "PlcyDescr";
    private static final String FULL_NAME = "FullName";
    private static final String POLICY_DESKTOP = "PolicyDesktop";

    private Session session;
    private Database widgetCatalogDB;
    private String desktopSettingName;
    private String desktopSettingDescription;
    private String widgetCategory;
    boolean bDiscoverCounter = false;
    private PolicyManagement policyManagement;
    private String desktopSettingUniversalId;
    private Document desktopSettings;
    private Database addressbook;

    private Logger logger = Logger.getLogger(SettingsBuilder.class.getName());

    public SettingsBuilder(Session session) {
	this.session = session;
    }

    public Session getSession() {
	return session;
    }

    public void setSession(Session session) {
	this.session = session;
    }

    public Database getWidgetCatalogDB() {
	return widgetCatalogDB;
    }

    public void setWidgetCatalogDB(Database widgetCatalogDB) {
	this.widgetCatalogDB = widgetCatalogDB;
    }

    public String getDesktopSettingName() {
	return desktopSettingName;
    }

    public void setDesktopSettingName(String desktopSettingName) {
	this.desktopSettingName = desktopSettingName;
    }

    public String getDesktopSettingDescription() {
	return desktopSettingDescription;
    }

    public void setDesktopSettingDescription(String desktopSettingDescription) {
	this.desktopSettingDescription = desktopSettingDescription;
    }

    public String getWidgetCategory() {
	return widgetCategory;
    }

    public void setWidgetCategory(String widgetCategory) {
	this.widgetCategory = widgetCategory;
    }

    public String getDesktopSettingUniversalId() {
	return desktopSettingUniversalId;
    }

    public Document getDesktopSettings() {
	return desktopSettings;
    }

    public PolicyManagement getPolicyManagement() {
	return policyManagement;
    }

    public Database getAddressbook() {
	return addressbook;
    }

    public void createDesktopSetting(boolean testmode) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create Desktop setting");
	try {
	    findAddressBook(testmode);
	    createDesktopSettingsDocument();
	} catch (NotesException e) {
	    OException.raiseError(e, SettingsBuilder.class.getName(), "" + testmode);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void findAddressBook(boolean testmode) throws NotesException {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("find Addressbook");
	if (testmode) {
	    logger.fine("TESTMODE! Using \"namestest.nsf\"");
	    addressbook = session.getDatabase(session.getServerName(), "namestest.nsf");
	} else {
	    Vector<Database> addressBooks = session.getAddressBooks();
	    for (Iterator<Database> addressbookIterator = addressBooks.iterator(); addressbookIterator.hasNext();) {
		addressbook = (Database) addressbookIterator.next();
		if (addressbook.isPublicAddressBook() && addressbook.getServer().equals(session.getServerName())) {
		    if (!addressbook.isOpen()) {
			if (!addressbook.open()) {
			    addressbook.openWithFailover("", "");
			}
		    }
		    logger.fine("found public addressbook");
		}
	    }
	}
	logger.fine(Resources.LOG_SEPARATOR_END);
    }

    private void createDesktopSettingsDocument() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create Desktop settings document");
	try {
	    createSettingDocument(POLICY_DESKTOP);
	    desktopSettings.replaceItemValue(FULL_NAME, desktopSettingName);
	    desktopSettings.replaceItemValue(POLICY_DESCRIPTION, desktopSettingDescription);
	    desktopSettings.replaceItemValue(TOOLBOX_CATALOG_SERVER, widgetCatalogDB.getServer());
	    desktopSettings.replaceItemValue(TOOLBOX_CATALOG_DB_NAME, widgetCatalogDB.getFilePath());
	    desktopSettings.replaceItemValue(CATALOG_CATEGORIES_TO_INSTALL, widgetCategory);
	    saveSettingDocument();
	} catch (NotesException e) {
	    OException.raiseError(e, SettingsBuilder.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}

    }

    private void saveSettingDocument() throws NotesException {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("save setting document");
	desktopSettings.computeWithForm(true, true);
	if (querySave(desktopSettings)) {
	    policyManagement.setOldDocNm(desktopSettings, false);
	    desktopSettings.save(true, false, false);
	    desktopSettingUniversalId = desktopSettings.getUniversalID();
	}
	logger.fine(Resources.LOG_SEPARATOR_END);
    }

    private void createSettingDocument(String policyType) throws NotesException {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create setting document of type " + policyType);
	desktopSettings = addressbook.createDocument();
	desktopSettings.replaceItemValue(Constants.ITEM_FORM, policyType);
	policyManagement = new PolicyManagement(session);
	policyManagement.setAddressBook(addressbook);
	policyManagement.queryOpen(desktopSettings, 1, true);
	policyManagement.setOldDocNm(desktopSettings, true);
	logger.fine(Resources.LOG_SEPARATOR_END);
    }

    private boolean querySave(Document policy) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("query save");
	try {
	    boolean bFormulasOk;
	    Item curItem;

	    curItem = policy.getFirstItem("LocalReplica$SF");
	    if (!policy.getItemValueString("LocalReplicaEntry").equals("")) {
		if (curItem.getType() != Item.FORMULA) {
		    logger.fine(PolicyManagement.COMPILE_FAILURE_SAVE_BODY + " - " + curItem.getName());
		    return false;
		}
	    }

	    curItem = policy.getFirstItem("DeployVer$SF");
	    if (!policy.getItemValueString("DeployVerEntry").equals("")) {
		if (curItem.getType() != Item.FORMULA) {
		    logger.fine(PolicyManagement.COMPILE_FAILURE_SAVE_BODY + " - " + curItem.getName());
		    return false;
		}
	    }

	    curItem = policy.getFirstItem("$PrefReplDefEncrypt$SF");
	    if (!policy.getItemValueString("$PrefReplDefEncryptEntry").equals("")) {
		if (curItem.getType() != Item.FORMULA) {
		    logger.fine(PolicyManagement.COMPILE_FAILURE_SAVE_BODY + " - " + curItem.getName());
		    return false;
		}
	    }

	    curItem = policy.getFirstItem("EclipseParameters$SF");
	    if (!policy.getItemValueString("EclipseParametersEntry").equals("")) {
		if (curItem.getType() != Item.FORMULA) {
		    logger.fine(PolicyManagement.COMPILE_FAILURE_SAVE_BODY + " - " + curItem.getName());
		    return false;
		}
	    }

	    curItem = policy.getFirstItem("Parameters$SF");
	    if (!policy.getItemValueString("ParametersEntry").equals("")) {
		if (curItem.getType() != Item.FORMULA) {
		    logger.fine(PolicyManagement.COMPILE_FAILURE_SAVE_BODY + " - " + curItem.getName());
		    return false;
		}
	    }

	    curItem = policy.getFirstItem("LocParameters$SF");
	    if (!policy.getItemValueString("LocParametersEntry").equals("")) {
		if (curItem.getType() != Item.FORMULA) {
		    logger.fine(PolicyManagement.COMPILE_FAILURE_SAVE_BODY + " - " + curItem.getName());
		    return false;
		}
	    }

	    logger.fine("create ini fields");
	    policyManagement.createINIFields(policy, "Parameters", PolicyManagement.NOTESINI_PREFIX);
	    policyManagement.createINIFields(policy, "LocParameters", PolicyManagement.LOCALL_PREFIX);
	    policyManagement.createINIFields(policy, "EclipseParameters", PolicyManagement.MS_PREFIX);

	    logger.fine("remove ini fields");
	    policyManagement.removeINIFields(policy, "RemParameters", PolicyManagement.NOTESINI_PREFIX);
	    policyManagement.removeINIFields(policy, "RemLocParameters", PolicyManagement.LOCALL_PREFIX);
	    policyManagement.removeINIFields(policy, "RemEclipseParameters", PolicyManagement.MS_PREFIX);

	    policyManagement.convertLongNameFields(policy);

	    if (bDiscoverCounter) {
		incrementDiscoverCounter(policy);
		bDiscoverCounter = false;
	    }

	    logger.fine("compute item lists");
	    policyManagement.computePOItemList(policy);
	    policyManagement.computeIFPItemList(policy);
	    policyManagement.computeHAItemList(policy);

	    if (!policyManagement.verifyUnique(policy)) {
		logger.fine(PolicyManagement.CONFLICT_RISK);
		return false;
	    }

	    policy.removeItem("$OnBehalfOf");

	    policyManagement.writeOutProxies(policy);

	} catch (Exception e) {
	    OException.raiseError(e, SettingsBuilder.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return true;

    }

    private void incrementDiscoverCounter(Document doc) throws NotesException {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("increment discover counter");
	String sCtrField = "$PrefDISCOVER_COUNTER";
	int i;
	i = Integer.parseInt((String) doc.getItemValue(sCtrField).get(0));
	i++;
	doc.replaceItemValue(sCtrField, i);
	logger.fine(Resources.LOG_SEPARATOR_END);
    }
}
