package com.dvelop.smartnotes.domino.policy;

import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntryCollection;

import com.dvelop.smartnotes.domino.policy.managment.PolicyManagement;
import com.dvelop.smartnotes.domino.resources.Constants;

public class PolicyBuilder {

    private Session session;
    private Database addressbook;
    //    private Document settingsDocument;
    private PolicyManagement policyManagement;
    private String fullName;
    private String description;

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
	this.session = session;
	this.addressbook = addressbook;
	this.policyManagement = policyManagement;
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

    public void createMasterPolicy(Document settingsDocument) {
	try {
	    if (addressbook != null) {
		Document masterPolicy = addressbook.createDocument();
		masterPolicy.replaceItemValue(Constants.ITEM_FORM, TYPE_POLICY_MASTER);
		querryOpen(masterPolicy, 0, true);
		masterPolicy.replaceItemValue("FullNameEntry", fullName);
		masterPolicy.replaceItemValue("PlcyType", "0"); //0 = Explicit, 1 = Organizational
		masterPolicy.replaceItemValue("PlcyDescr", description);
		appendSettingToMatchingCategory(masterPolicy, settingsDocument);

		masterPolicy.computeWithForm(true, true);

		policyManagement.setOldDocNm(masterPolicy, false);
		masterPolicy.save(true, false, false);
	    }
	} catch (NotesException e) {
	    e.printStackTrace();
	}
    }

    private void appendSettingToMatchingCategory(Document masterPolicy, Document settingsDocument) {
	try {
	    String fieldName = null;
	    String fieldValue = settingsDocument.getUniversalID();
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
	    }
	} catch (NotesException e) {
	    e.printStackTrace();
	}

    }

    private boolean querryOpen(Document doc, int mode, boolean isNewDoc) {
	try {
	    final String POL_CLIENT_WARNING = "You must use a Notes 6 client or later to create or modify policy documents.";

	    View view;
	    ViewEntryCollection vc;
	    int viewCount;

	    //' check to see if this is a copy of another Policy doc, if so give it a new Precedence value
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
	    e.printStackTrace();
	}
	return true;
    }

    private boolean querySave(Document doc) {
	try {
	    Vector<String> ptName;
	    Vector<String> fName;
	    Vector<String> parent;
	    Item item;
	    boolean conti;

	    if (doc.isNewNote()) {
		conti = policyManagement.verifyUniquePolicy(doc);
		if (!conti) {
		    return false;
		}
	    }
	    doc.removeItem("$OnBehalfOf");

	    ptName = doc.getItemValue("PtPlcy");
	    fName = doc.getItemValue("FullNameEntry");
	    parent = doc.getItemValue("SetParent");
	    if (parent.get(0).equals("1")) {
		doc.replaceItemValue("FullNameEntry", fName.get(0) + ptName.get(0));
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
	    //	Print "QuerySave Error " & Cstr(Err()) & ": " & Error & " occurred on line " & Cstr(Erl())

	}
	return true;
    }

}
