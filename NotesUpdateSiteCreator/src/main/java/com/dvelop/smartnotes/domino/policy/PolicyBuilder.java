package com.dvelop.smartnotes.domino.policy;

import java.util.Vector;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntryCollection;

import com.dvelop.smartnotes.domino.policy.managment.PolicyManagement;
import com.dvelop.smartnotes.domino.resources.Constants;
import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.updatesite.exceptions.OException;

public class PolicyBuilder {

    private Logger logger = Logger.getLogger(PolicyBuilder.class.getName());

    private static final String ITEM_POLICY_CATEGORY = "PlcyCat";
    private static final String ITEM_POLICY_DESCRIPTION = "PlcyDescr";
    private static final String ITEM_POLICY_TYPE = "PlcyType";
    private static final String ITEM_FULL_NAME_ENTRY = "FullNameEntry";
    private Session session;
    private Database addressbook;
    private PolicyManagement policyManagement;
    private String fullName;
    private String description;
    private String category;

    private final String TYPE_POLICY_MASTER = "PolicyMaster";
    private final String TYPE_PROFILE = "Profile";
    private final String TYPE_POLICY_REGISTRATION = "PolicyRegistration";
    private final String TYPE_POLICY_ARCHIVE = "PolicyArchive";
    private final String TYPE_POLICY_ARCHIVE_CRITERIA = "PolicyArchiveCriteria";
    private final String TYPE_POLICY_ARCHIVE_SELECTION = "PolicyArchiveSelection";
    private final String TYPE_POLICY_QUOTA = "PolicyQuota";
    private final String TYPE_POLICY_SECURITY = "PolicySecurity";
    private final String TYPE_POLICY_DESKTOP = "PolicyDesktop";
    private final String TYPE_POLICY_SETUP = "PolicySetup";
    private final String TYPE_POLICY_MAIL = "PolicyMail";

    public PolicyBuilder(Session session, Database addressbook, PolicyManagement policyManagement) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create PolicyBuilder");
	this.session = session;
	this.addressbook = addressbook;
	this.policyManagement = policyManagement;
	logger.fine(Resources.LOG_SEPARATOR_END);
    }

    public String getFullName() {
	return fullName;
    }

    public void setFullName(String fullName) {
	if (!fullName.startsWith("/")) {
	    fullName = "/" + fullName;
	}
	this.fullName = fullName;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    public void createMasterPolicy(Document settingsDocument) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create master policy");
	try {
	    if (addressbook != null) {
		Document masterPolicy = addressbook.createDocument();
		masterPolicy.replaceItemValue(Constants.ITEM_FORM, TYPE_POLICY_MASTER);
		querryOpen(masterPolicy, 0, true);
		masterPolicy.replaceItemValue(ITEM_FULL_NAME_ENTRY, fullName);
		masterPolicy.replaceItemValue(ITEM_POLICY_TYPE, "0"); //0 = Explicit, 1 = Organizational
		masterPolicy.replaceItemValue(ITEM_POLICY_DESCRIPTION, description);
		masterPolicy.replaceItemValue(ITEM_POLICY_CATEGORY, category);
		appendSettingToMatchingCategory(masterPolicy, settingsDocument);

		masterPolicy.computeWithForm(true, true);
		querySave(masterPolicy);
		policyManagement.setOldDocNm(masterPolicy, false);
		masterPolicy.save(true, false, false);
	    }
	} catch (NotesException e) {
	    OException.raiseError(e, PolicyBuilder.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void appendSettingToMatchingCategory(Document masterPolicy, Document settingsDocument) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("append setting to matching category");
	try {
	    String fieldName = null;
	    String fieldValue = settingsDocument.getUniversalID();
	    logger.fine("appending setting to " + settingsDocument.getItemValueString(Constants.ITEM_FORM));
	    if (settingsDocument.getItemValueString(Constants.ITEM_FORM).equals(TYPE_POLICY_ARCHIVE)) {
		fieldName = "ArcSets";
	    } else if (settingsDocument.getItemValueString(Constants.ITEM_FORM).equals(TYPE_POLICY_ARCHIVE_CRITERIA)) {
	    } else if (settingsDocument.getItemValueString(Constants.ITEM_FORM).equals(TYPE_POLICY_ARCHIVE_SELECTION)) {
	    } else if (settingsDocument.getItemValueString(Constants.ITEM_FORM).equals(TYPE_POLICY_DESKTOP)) {
		fieldName = "DesktopSets";
	    } else if (settingsDocument.getItemValueString(Constants.ITEM_FORM).equals(TYPE_POLICY_MAIL)) {
		fieldName = "MailSets";
	    } else if (settingsDocument.getItemValueString(Constants.ITEM_FORM).equals(TYPE_POLICY_QUOTA)) {
	    } else if (settingsDocument.getItemValueString(Constants.ITEM_FORM).equals(TYPE_POLICY_REGISTRATION)) {
		fieldName = "RegSets";
	    } else if (settingsDocument.getItemValueString(Constants.ITEM_FORM).equals(TYPE_POLICY_SECURITY)) {
		fieldName = "SecSets";
	    } else if (settingsDocument.getItemValueString(Constants.ITEM_FORM).equals(TYPE_POLICY_SETUP)) {
		fieldName = "SetupSets";
	    } else {

	    }
	    if (fieldName != null) {
		masterPolicy.appendItemValue(fieldName, fieldValue);
	    } else {
		logger.fine("no matching category found");
	    }
	} catch (NotesException e) {
	    OException.raiseError(e, PolicyBuilder.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}

    }

    private boolean querryOpen(Document doc, int mode, boolean isNewDoc) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("queryOpen");
	try {
	    View view;
	    ViewEntryCollection vc;
	    int viewCount;

	    //' check to see if this is a copy of another Policy doc, if so give it a new Precedence value
	    logger.fine("check to see if this is a copy of another Policy doc, if so give it a new Precedence value");
	    if (!isNewDoc) {
		if (doc.hasItem("Precedence")) {
		    int numPrec = (Integer) doc.getItemValue("Precedence").get(0);
		    view = addressbook.getView("($PoliciesDynamic)");
		    vc = view.getAllEntriesByKey(numPrec, true);
		    viewCount = vc.getCount();
		    if (viewCount > 1) {
			policyManagement.incrementGrpPrecedence(doc, true);
		    }
		}
	    }
	} catch (NotesException e) {
	    OException.raiseError(e, PolicyBuilder.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return true;
    }

    private boolean querySave(Document doc) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("querySave");
	try {
	    Vector<String> ptName;
	    Vector<String> fName;
	    Vector<String> parent;
	    Item item;

	    if (doc.isNewNote()) {

		if (!policyManagement.verifyUniquePolicy(doc)) {
		    logger.fine(PolicyManagement.CONFLICT_RISK);
		    return false;
		}
	    }
	    doc.removeItem("$OnBehalfOf");

	    ptName = doc.getItemValue("PtPlcy");
	    fName = doc.getItemValue(ITEM_FULL_NAME_ENTRY);
	    parent = doc.getItemValue("SetParent");
	    if (parent.get(0).equals("1")) {
		doc.replaceItemValue(ITEM_FULL_NAME_ENTRY, fName.get(0) + ptName.get(0));
		doc.replaceItemValue("SetParent", "1");
	    }

	    if (!doc.getItemValueString("AssignedUsrGrpNames").equals("") && doc.getItemValueString("Precedence").equals("1")) {
		policyManagement.incrementGrpPrecedence(doc, false);
		item = doc.getFirstItem("Precedence");
		item.setSaveToDisk(true);
	    }

	    if (doc.getItemValueString("AssignedUsrGrpNames").equals("")) {
		item = doc.getFirstItem("Precedence");
		item.setSaveToDisk(false);
	    }

	} catch (NotesException e) {
	    OException.raiseError(e, PolicyBuilder.class.getName(), null);

	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return true;
    }

}
