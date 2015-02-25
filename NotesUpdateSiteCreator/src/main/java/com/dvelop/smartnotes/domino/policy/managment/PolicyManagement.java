package com.dvelop.smartnotes.domino.policy.managment;

import java.util.Iterator;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

public class PolicyManagement {
    public final static String DOC_NOT_SAVED = "This document will not be saved.";
    public final static String CONFLICT_RISK = "Saving this document may result in a replication or save conflict.";
    public final static int ERROR_FLAG = 0;
    public final static String POL_CLIENT_WARNING = "You must use a Notes 6 client or later to create or modify policy documents.";
    public final static String POL_BUG_WARNING = "The Notes version you are using will not properly create or modify policy documents. A Notes 7 client or later should be used.  Do you wish to continue?";
    public final static String ERR_TITLE = "Warning";
    public final static String DONT_SET_STRING = "Don't set value";
    public final static String COMPILE_SUCCESS_TITLE = "Compiled Successfully";
    public final static String COMPILE_SUCCESS_BODY = "There are no syntax errors, the formula compiled successfully.";
    public final static String COMPILE_FAILURE_TITLE = "Syntax Error";
    public final static String COMPILE_FAILURE_BODY = "There is at least one syntax error in the formula.  It did not compile.";
    public final static String COMPILE_FAILURE_SAVE_BODY = "There is at least one syntax error in the formula.  This document could not be saved.";

    // 'Begin DNT
    public final static String LOCALL_PREFIX = "LocAll";
    public final static String MS_PREFIX = "$qual";
    public final static String NOTESINI_PREFIX = "$Pref";
    public final static String DUMMY_STRING = "**$$DUMMY$$**";
    public final static String MG_SET_BOOL = "bpolicyMg";
    public final static String NAME_PLUGIN_LIST = "NamePlugInList";
    public final static String ECLIPSE_PARAMETERS = "EclipseParameters";
    public final static String MAIL_POLICY = "policyMail";

    public final static String ENFORCE_SUFFIX = ", Enforce";
    public final static String SETONCE_SUFFIX = ": SetOnce";
    // 'End DNT
    public final static String APPSUPPORTS1 = "Send invitations as rich data";
    public final static String APPSUPPORTS2 = "Specify custom options for repeating meetings";
    public final static String APPSUPPORTS3 = "Select \"All instances\" when acting on a repeating meeting";
    public final static String APPSUPPORTS4 = "Select \"This and all future instances\" when acting on a repeating meeting";
    public final static String APPSUPPORTS5 = "Preserve rich data as htm attachments";

    public final static int DELETED_CERTifICATE = 1;
    public final static int MODifIED_CERTifICATE = 2;
    // 'added by LBH
    public final static int DELETED_ACCOUNT = 1;
    public final static int MODifIED_ACCOUNT = 2;

    private Session s;
    private Database db;
    private View view;
    private Document doc;
    private String oldDocNm;

    private Database addressBook;

    private class tDNCEntry {
	String sFieldName;
	String sFieldVal;
    }

    private int iIndex;
    private String fieldName;
    private String storedVal;
    private Document policy;
    private Item inhItem;
    private Item enfItem;
    private Item nmItem;
    private String itemNm;
    private String itemName;
    private String itemHTAName;
    private String suffixStr;
    private String blNameStr;
    private int strlen;
    private String settingTypeStr;
    private boolean needsConversion;
    private int POCounter;
    private int SOCounter;
    private int IPCounter;
    private int UP1_Counter;
    private int UP2_Counter;
    private int UP3_Counter;
    private int UP4_Counter;
    private int UP5_Counter;
    private int DPL_Counter;
    private String[] POItemNames; // 'array of item names
    private String[] IPItemNames; // 'array of item names
    private String[] UP1_ItemNames; // 'array of item names
    private String[] UP2_ItemNames; // 'array of item names
    private String[] UP3_ItemNames; // 'array of item names
    private String[] UP4_ItemNames; // 'array of item names
    private String[] UP5_ItemNames; // 'array of item names
    private String[] DPL_ItemNames; // 'array of item names for $DPLocked
    private String[] SO_ItemNames; // 'array of item names for $policyHTASetOnceItems
    private String pRefItemName; // 'real item name refered by the PO item

    public PolicyManagement(Session session) {
	s = session;
    }

    public Database getAddressBook() {
	return addressBook;
    }

    public void setAddressBook(Database addressBook) {
	this.addressBook = addressBook;
    }

    public boolean queryOpen(Document source, int mode, boolean isNewDoc) {
	boolean result = true;
	try {
	    int ret;
	    Vector<String> arrPlugInList = new Vector<String>();
	    int listCounter;
	    String sItemName;
	    String sTmpVal;
	    String spluginName;
	    int iPosOfChar;
	    int iPosOfEnfChar;
	    int iPosOfSOChar;

	    // Let's set values back to the form                                  
	    policy = source;

	    settingTypeStr = policy.getItemValueString("Type");
	    if (settingTypeStr.equals("policyMail")) {
		suffixStr = "$UP";
	    } else {
		suffixStr = "$HA";
	    }

	    needsConversion = !policy.hasItem("$policyHTASetOnceItems");

	    if (needsConversion) {
		if (!settingTypeStr.equals("policyMail")) {
		    for (Object itemObj : policy.getItems()) {
			Item item = (Item) itemObj;
			if (!item.getName().endsWith("$HA") && !item.getText().equals("") && !policy.hasItem(item.getName() + "$HA")) {
			    itemHTAName = item.getName() + "$HA";
			    policy.replaceItemValue(itemHTAName, "5");
			}
		    }
		}
	    }

	    boolean bMgSetfixup = !policy.hasItem(MG_SET_BOOL);

	    if (!settingTypeStr.equals(MAIL_POLICY)) {
		Vector itemValue = policy.getItemValue(ECLIPSE_PARAMETERS);
		for (Iterator iterator = itemValue.iterator(); iterator.hasNext();) {
		    String item = (String) iterator.next();

		    if (!item.equals("")) {
			//'Delete all $qual fields that were generated, so start with clean slate
			iPosOfChar = item.indexOf("=");
			sItemName = item.substring(0, iPosOfChar - 1);
			spluginName = item.substring(item.indexOf("; "));
			String sItemToRemove = "$qual_" + spluginName + "_" + sItemName;
			if (policy.hasItem(sItemToRemove)) {
			    policy.removeItem(sItemToRemove);
			}
		    }
		}
	    }

	    if (bMgSetfixup) {
		if (!settingTypeStr.equals(MAIL_POLICY)) {
		    Vector itemValue = policy.getItemValue(ECLIPSE_PARAMETERS);
		    for (Iterator iterator = itemValue.iterator(); iterator.hasNext();) {
			String item = (String) iterator.next();
			if (!item.equals("")) {
			    //'Build NamePlugInList
			    listCounter = arrPlugInList.size();
			    iPosOfChar = item.indexOf("=");
			    iPosOfEnfChar = item.indexOf(ENFORCE_SUFFIX);
			    iPosOfSOChar = item.indexOf(SETONCE_SUFFIX);
			    String sItemVal;

			    sItemName = item.substring(0, iPosOfChar - 1);
			    if (iPosOfEnfChar > 0) {
				sTmpVal = item.substring(0, iPosOfEnfChar - 1);
				sItemVal = sTmpVal.substring(sTmpVal.length() - iPosOfChar);
			    } else {
				if (iPosOfSOChar > 0) {
				    sTmpVal = item.substring(0, iPosOfSOChar - 1);
				    sItemVal = sTmpVal.substring(sTmpVal.length() - iPosOfChar);
				} else {
				    sItemVal = item.substring(item.length() - iPosOfChar);
				}
			    }
			    spluginName = sItemVal.substring(item.indexOf("; "));
			    arrPlugInList.add(sItemName + "?" + spluginName);
			    listCounter = listCounter + 1;
			}
		    }

		    policy.replaceItemValue(NAME_PLUGIN_LIST, arrPlugInList);
		    policy.replaceItemValue(MG_SET_BOOL, "1");
		}
	    }
	    enfItem = policy.getFirstItem("$policyPOItems");
	    if (enfItem != null) {
		Vector values = enfItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (!v.equals("")) {
			fieldName = v + "$PO";
			policy.replaceItemValue(fieldName, "1");
		    }
		}
	    }

	    inhItem = policy.getFirstItem("$policyifPItems");
	    if (inhItem != null) {
		Vector values = inhItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (!v.equals("")) {
			fieldName = v + "$IP";
			policy.replaceItemValue(fieldName, "1");
		    }
		}
	    }

	    inhItem = policy.getFirstItem("$DPLockedUnstripped");
	    if (inhItem != null) {
		Vector values = inhItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (!v.equals("")) {
			fieldName = v + "$HA";
			policy.replaceItemValue(fieldName, "2");
		    }
		}
	    }

	    inhItem = policy.getFirstItem("$policySetOnceUnstripped");
	    if (inhItem != null) {
		Vector values = inhItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (!v.equals("")) {
			fieldName = v + "$HA";
			policy.replaceItemValue(fieldName, "3");
		    }
		}
	    }

	    inhItem = policy.getFirstItem("$DontChangeItems");
	    if (inhItem != null) {
		Vector values = inhItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (!v.equals("")) {
			fieldName = v + suffixStr;
			policy.replaceItemValue(fieldName, "1");
		    }
		}
	    }

	    iIndex = 0;
	    inhItem = policy.getFirstItem("policyDNCNames");
	    if (inhItem != null) {
		Vector values = inhItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (!v.equals("")) {
			policy.replaceItemValue(v, policy.getItemValue("policyDNCVals").get(iIndex));
			iIndex = iIndex + 1;
		    }
		}
	    }

	    Item ctlItem = policy.getFirstItem("$policyCTLItems");
	    if (ctlItem != null) {
		Vector values = ctlItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (!v.equals("")) {
			fieldName = v + "$CL";
			policy.replaceItemValue(fieldName, "");
		    }
		}
	    }

	    //  ' for spr LMAN6PVR3Q - want this Locked only, do not push any values - don't want Owner field of Settings doc to get pushed           
	    inhItem = policy.getFirstItem("$DPLocked");
	    if (inhItem != null) {
		Vector values = inhItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (v.equals("Owner") || v.equals("OtherUsersPicker")) {
			fieldName = v + "$LO";
			policy.replaceItemValue(fieldName, "2");
		    }
		}
	    }

	    // Bucket 2 Field settings back to the form                           
	    inhItem = policy.getFirstItem("$FL_2");
	    if (inhItem != null) {
		Vector values = inhItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (v.indexOf(":") < 2) {
			if (skipHTASetFields(v.substring(0, v.indexOf(":") - 1))) {
			    if (checkExclusionFields(v.substring(0, v.indexOf(":") - 1))) { //check for $Times item dependency                               
				if (v.substring(0, v.indexOf(":") - 1) == "DefaultLogo") {
				    fieldName = "$Times" + suffixStr;
				}
			    } else {
				fieldName = v.substring(0, v.indexOf(":") - 1) + suffixStr;
			    }

			    //if needsConversion = "True" {                                   
			    policy.replaceItemValue(fieldName, "5");
			    //}else{                                                               
			    //	policy.ReplaceItemValue(fieldName, "2")                      
			    //}                                                            
			}
		    }
		}
	    }

	    // Bucket 3 Field settings back to the form                           
	    inhItem = policy.getFirstItem("$FL_3");
	    if (inhItem != null) {
		Vector values = inhItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (v.indexOf(":") < 2) {
			if (checkExclusionFields(v.substring(0, v.indexOf(":") - 1))) { //check for $Times item dependency                               
			    if (v.substring(0, v.indexOf(":") - 1) == "DefaultLogo") {
				fieldName = "$Times" + suffixStr;
			    }
			} else {
			    fieldName = v.substring(0, v.indexOf(":") - 1) + suffixStr;
			}
			//    fieldName = v & "$UP"                                              
			policy.replaceItemValue(fieldName, "3");
		    }
		}
	    }

	    if (needsConversion || suffixStr.equals("$UP")) {
		inhItem = policy.getFirstItem("$DPLocked");
		if (inhItem != null) {
		    Vector values = inhItem.getValues();
		    for (Iterator iterator = values.iterator(); iterator.hasNext();) {
			String v = (String) iterator.next();
			if (!v.equals("")) {
			    fieldName = v + suffixStr;
			    if (settingTypeStr.equals("policyDesktop")) {
				if (policy.hasItem(fieldName)) {
				    policy.replaceItemValue(fieldName, "2");
				} else {
				    fieldName = "$Pref" + fieldName;
				    policy.replaceItemValue(fieldName, "2");
				}
			    } else {
				if (v.equals("Owner") || v.equals("OtherUsersPicker")) {
				    fieldName = v + "$LO";
				}
				policy.replaceItemValue(fieldName, "2");
			    }
			}
		    }
		}
	    }

	    // This needs to come after the Bucket 2 code                       
	    inhItem = policy.getFirstItem("$policyAlwaysPush");
	    if (inhItem != null) {
		Vector values = inhItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (!v.equals("")) {
			fieldName = v + suffixStr;
			policy.replaceItemValue(fieldName, "4");
		    }
		}
	    }

	    inhItem = policy.getFirstItem("AlwaysSetItems");
	    if (inhItem != null) {
		String alwaysSuffixStr;
		if (suffixStr.equals("$UP")) {
		    alwaysSuffixStr = "$FO";
		} else {
		    alwaysSuffixStr = "$HA";
		}
		Vector values = inhItem.getValues();
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
		    String v = (String) iterator.next();
		    if (!v.equals("")) {
			fieldName = v + alwaysSuffixStr;
			policy.replaceItemValue(fieldName, "5");
		    }
		}
	    }

	    if (needsConversion && settingTypeStr.equals("policyMail")) {
		policy.replaceItemValue("ShowToDosOnCalendar$IP", policy.getItemValue("HideToDosFromCalendar$IP").get(0));
		policy.replaceItemValue("ShowToDosOnCalendar$PO", policy.getItemValue("HideToDosFromCalendar$PO").get(0));
		policy.replaceItemValue("ShowToDosOnCalendar$UP", policy.getItemValue("HideToDosFromCalendar$UP").get(0));

		if (!((String) policy.getItemValue("HideToDosFromCalendar").get(0)).equals("1")) {
		    policy.replaceItemValue("ShowToDosOnCalendar", "1");
		}
	    }

	} catch (NotesException e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    private boolean checkExclusionFields(String strField) {
	// This function will process all field dependency exclusions, meaning fields that
	// once selected. have dependencies of other fields, and can only be added
	// into the apppropriate bucket (Always, Initially, etc..)
	// Ex: Sunday can only be added to ANY bucket if $Times has been selected to that bucket. 

	if (strField.equals("")) {
	    if (strField.equals("Sunday")) {
		return true;
	    } else if (strField.equals("Monday")) {
		return true;
	    } else if (strField.equals("Tuesday")) {
		return true;
	    } else if (strField.equals("Wednesday")) {
		return true;
	    } else if (strField.equals("Thursday")) {
		return true;
	    } else if (strField.equals("Friday")) {
		return true;
	    } else if (strField.equals("Saturday")) {
		return true;
	    } else if (strField.equals("DefaultLogo")) {
		return true;
	    } else {
		return false;
	    }
	}
	return false;
    }

    private boolean skipHTASetFields(String strField) {
	if (strField.equals("")) {
	    if (strField.equals("RecallSenderUIEnabled")) {
		return true;
	    } else if (strField.equals("PolicyRecallCriteria")) {
		return true;
	    } else if (strField.equals("PolicyRecallCriteriaPeriod")) {
		return true;
	    } else if (strField.equals("tmpFollowupStatus")) {
		return true;
	    } else {
		return false;
	    }
	}
	return false;
    }

    public void setOldDocNm(Document doc, boolean isNewDoc) throws NotesException {
	if (isNewDoc) {
	    oldDocNm = "";
	} else {
	    oldDocNm = doc.getItemValueString("FullName");
	}

    }

    public void polSetPostOpen(Document doc) {
	try {
	    Vector items = doc.getItems();
	    for (Iterator iterator = items.iterator(); iterator.hasNext();) {
		Item v = (Item) iterator.next();

		if (v.getName().toUpperCase().endsWith("$PO") || v.getName().toUpperCase().endsWith("$IP") || v.getName().toUpperCase().endsWith("$BL")
			|| v.getName().toUpperCase().endsWith("$HA") || v.getName().toUpperCase().endsWith("$UP") || v.getName().toUpperCase().endsWith("$FO")
			|| v.getName().toUpperCase().startsWith("$QUAL_1") || v.getName().toUpperCase().endsWith("$CL")) {
		    v.setSaveToDisk(false);
		}

	    }
	} catch (NotesException e) {
	    e.printStackTrace();
	}
    }

    public void createINIFields(Document hPolicy, String sParamList, String sPrefType) {
	try {
	    String sItemName;
	    String sItemVal;
	    String sTmpVal;
	    int iPosOfChar;
	    int iPosOfEnfChar;
	    int iPosOfSOChar;
	    Item itemToDisk;
	    String sPrefix;
	    String squalName;
	    String spluginName;
	    Vector<String> arrEclPrefNames = new Vector<String>();
	    Vector<String> arrPlugInList = new Vector<String>();
	    int listCounter;
	    int pCounter;

	    boolean bCreate = true;
	    if (!hPolicy.getItemValueString(sParamList).equals("")) {

		sPrefix = "";

		if (sPrefType.equals(LOCALL_PREFIX)) {
		    sPrefix = LOCALL_PREFIX;
		} else if (sPrefType.equals(NOTESINI_PREFIX)) {
		    sPrefix = NOTESINI_PREFIX;
		}
		Vector value = hPolicy.getItemValue(sParamList);
		for (Iterator iterator = value.iterator(); iterator.hasNext();) {
		    String item = (String) iterator.next();

		    iPosOfChar = item.indexOf("=");
		    sItemName = sPrefix + item.substring(0, iPosOfChar - 1);

		    iPosOfEnfChar = item.indexOf(", Enforce");
		    iPosOfSOChar = item.indexOf(": SetOnce");
		    if (iPosOfEnfChar > 0) {
			hPolicy.replaceItemValue(sItemName + "$PO", "1");
			itemToDisk = hPolicy.getFirstItem(sItemName + "$PO");
			itemToDisk.setSaveToDisk(false);
			if (iPosOfSOChar > 0) {
			    hPolicy.replaceItemValue(sItemName + "$HA", "3");
			} else {
			    hPolicy.replaceItemValue(sItemName + "$HA", "5");
			}
			itemToDisk = hPolicy.getFirstItem(sItemName + "$HA");
			itemToDisk.setSaveToDisk(false);
			sTmpVal = item.substring(0, iPosOfEnfChar - 1);
			sItemVal = sTmpVal.substring(sTmpVal.length() - iPosOfChar);
		    } else {
			if (iPosOfSOChar > 0) {
			    hPolicy.replaceItemValue(sItemName + "$HA", "3");
			    itemToDisk = hPolicy.getFirstItem(sItemName + "$HA");
			    itemToDisk.setSaveToDisk(false);
			    sTmpVal = item.substring(0, iPosOfSOChar - 1);
			    sItemVal = sTmpVal.substring(sTmpVal.length() - iPosOfChar);
			} else {
			    sItemVal = item.substring(item.length() - iPosOfChar);
			    hPolicy.replaceItemValue(sItemName + "$PO", "");
			    itemToDisk = hPolicy.getFirstItem(sItemName + "$PO");
			    itemToDisk.setSaveToDisk(false);
			    hPolicy.replaceItemValue(sItemName + "$HA", "5");
			    itemToDisk = hPolicy.getFirstItem(sItemName + "$HA");
			    itemToDisk.setSaveToDisk(false);
			}
		    }

		    if (sPrefType.equals(MS_PREFIX)) {
			spluginName = sItemVal.substring(sItemVal.indexOf("; "));
			squalName = MS_PREFIX + "_" + spluginName.trim() + "_" + sItemName.trim();
			sItemVal = sItemVal.substring(0, sItemVal.indexOf(";") - 1);

			arrEclPrefNames.add(spluginName);
			arrEclPrefNames.add(sItemName);

			//				'Build NamePlugInList
			listCounter = arrPlugInList.size();
			arrPlugInList.add(sItemName + "?" + spluginName);

			hPolicy.replaceItemValue(squalName, arrEclPrefNames);

			hPolicy.replaceItemValue(sItemName, sItemVal);
			listCounter = listCounter + 1;
		    } else {
			hPolicy.replaceItemValue(sItemName, sItemVal);
		    }
		}
		hPolicy.replaceItemValue("NamePlugInList", arrPlugInList);
	    }
	} catch (NotesException e) {
	    e.printStackTrace();
	}
    }

    public void removeINIFields(Document hPolicy, String sParamList, String sPrefType) {
	try {
	    String sPrefix;
	    String sItemName;
	    String sPluginName = "";
	    String squalContents;
	    String sItemNameAdded;
	    Item iItemToRem;
	    Item iItemAddList;
	    boolean bDontRemove;

	    bDontRemove = false;

	    if (!hPolicy.getItemValueString(sParamList).equals("")) {

		sPrefix = "";

		if (sPrefType.equals(LOCALL_PREFIX)) {
		    sPrefix = LOCALL_PREFIX;
		} else if (sPrefType.equals(NOTESINI_PREFIX)) {
		    sPrefix = NOTESINI_PREFIX;
		}

		iItemAddList = hPolicy.getFirstItem("EclipseParameters");
		Vector itemValue = hPolicy.getItemValue(sParamList);
		for (Iterator iterator = itemValue.iterator(); iterator.hasNext();) {
		    String item = (String) iterator.next();

		    if (sPrefType.equals(MS_PREFIX)) {
			sItemName = item.substring(0, item.indexOf(";"));
			sPluginName = item.substring(item.indexOf(";"));
		    } else {
			sItemName = sPrefix + item;
		    }
		    for (Object object : hPolicy.getItemValue("EclipseParameters")) {
			String addedItems = (String) object;
			sItemNameAdded = addedItems.substring(0, addedItems.indexOf("="));
			if (sItemNameAdded.equals(sItemName)) {
			    bDontRemove = true;
			}
		    }

		    if (!bDontRemove) {
			if (hPolicy.hasItem(sItemName)) {
			    iItemToRem = hPolicy.getFirstItem(sItemName);
			    iItemToRem.setSaveToDisk(false);
			}
			if (hPolicy.hasItem(sItemName + "$PO")) {
			    hPolicy.replaceItemValue(sItemName + "$PO", "");
			}
			if (hPolicy.hasItem(sItemName + "$HA")) {
			    hPolicy.replaceItemValue(sItemName + "$HA", "1");
			}

			if (sPrefType.equals(MS_PREFIX)) {
			    String qualItemName = "$qual_" + sPluginName.trim() + "_" + sItemName.trim();
			    if (hPolicy.hasItem(qualItemName)) {
				iItemToRem = hPolicy.getFirstItem(qualItemName);
				iItemToRem.setSaveToDisk(false);
			    }
			}
		    }
		    bDontRemove = false;
		}
	    }
	    iItemToRem = hPolicy.getFirstItem("RemParameters");
	    iItemToRem.setSaveToDisk(false);
	    iItemToRem = hPolicy.getFirstItem("RemLocParameters");
	    iItemToRem.setSaveToDisk(false);
	    iItemToRem = hPolicy.getFirstItem("RemEclipseParameters");
	    iItemToRem.setSaveToDisk(false);
	} catch (NotesException e) {

	}
    }

    public void convertLongNameFields(Document hPolicy) {
	try {
	    //' this is the index into the list of long names
	    int iLongName;
	    String strLNrefval;
	    String strLNrefName;
	    Item holdItem;

	    iLongName = 1;
	    //'Begin DNT	
	    for (Object object : hPolicy.getItemValue("LongNameFields")) {
		String v = (String) object;
		if (!v.equals("")) {
		    //		'create/set item

		    strLNrefName = "$LNref" + iLongName;

		    strLNrefval = hPolicy.getItemValueString(strLNrefName);
		    hPolicy.replaceItemValue(v, strLNrefval);

		    //		'create/set $HA value
		    strLNrefval = hPolicy.getItemValueString(strLNrefName + "$HA");
		    hPolicy.replaceItemValue(v + "$HA", strLNrefval);
		    holdItem = hPolicy.getFirstItem(v + "$HA");
		    holdItem.setSaveToDisk(false);

		    //		'create/set $IP value
		    strLNrefval = hPolicy.getItemValueString(strLNrefName + "$IP");
		    hPolicy.replaceItemValue(v + "$IP", strLNrefval);
		    holdItem = hPolicy.getFirstItem(v + "$IP");
		    holdItem.setSaveToDisk(false);

		    //		'create/set $PO value
		    strLNrefval = hPolicy.getItemValueString(strLNrefName + "$PO");
		    hPolicy.replaceItemValue(v + "$PO", strLNrefval);
		    holdItem = hPolicy.getFirstItem(v + "$PO");
		    holdItem.setSaveToDisk(false);

		    iLongName = iLongName + 1;
		}
	    }
	    //'End DNT
	} catch (NotesException e) {
	    e.printStackTrace();
	}
    }

    public void computePOItemList(Document hPolicy) {
	//	'This function finds all "prohibit override" enabled items on a policy note.
	//	'A "PO" enabled item means it can't be overridden in child policies.
	//	'An item is "PO" enabled if an accompanying item with the suffix "$PO" exists.
	//	'If the value of that accompanying item is "1" then we store the item name(s) of
	//	'all "PO" enabled items as a list in a special item "$PolicyPOItems"
	try {
	    int pCounter = 0;
	    Vector<String> pItemNames = new Vector<String>(); //'array of item names
	    String pRefItemName; //'real item name refered by the PO item

	    for (Item item : (Vector<Item>) hPolicy.getItems()) {
		if (item.getName().toUpperCase().endsWith("$PO")) {
		    if (item.getValueString().equals("1")) {
			pRefItemName = item.getName().substring(0, item.getName().length() - 3);
			pItemNames.add(pRefItemName);
			pCounter = pCounter + 1;
		    }
		}
	    }

	    hPolicy.replaceItemValue("$PolicyPOItems", pItemNames);
	} catch (NotesException e) {
	    e.printStackTrace();
	}

    }

    public void computeIFPItemList(Document hPolicy) {
	//	'This function finds all "inherit from parent" enabled items on a policy note.
	//	'A "IFP" enabled item means it value is inherited from the parent.
	//	'An item is "IFP" enabled if an accompanying item with the suffix "$IFP" exists.
	//	'If the value of that accompanying item is "1" then we store the item name(s) of
	//	'all "IFP" enabled items as a list in a special item "$PolicyIFPItems"
	try {
	    int pCounter = 0;
	    Vector<String> pItemNames = new Vector<String>(); //'array of item names
	    String pRefItemName; //'real item name refered by the PO item

	    for (Item item : (Vector<Item>) hPolicy.getItems()) {
		if (item.getName().toUpperCase().endsWith("$IP")) {
		    if (item.getValueString().equals("1")) {
			pRefItemName = item.getName().substring(0, item.getName().length() - 3);
			pItemNames.add(pRefItemName);
			pCounter = pCounter + 1;
		    }
		}
	    }

	    hPolicy.replaceItemValue("$PolicyIFPItems", pItemNames);
	} catch (NotesException e) {
	    e.printStackTrace();
	}

    }

    public void computeHAItemList(Document hPolicy) {
	//	'This function finds all "how to apply" items on a policy note.         
	//	'An item is "HA" enabled if an accompanying item with the suffix "$HA"  exists.                                                                 
	//	'If the value of that accompanying item is "1" then we store the item name(s) of                                                              
	//	 'all "HA" enabled items as a list in a special item "$PolicyHTASetOnceItems".                                               
	//	'If the value of that accompanying item is "2" then we store the item name(s) of                                                              
	//	 'all "HA" enabled items as a list in a special item "$DPLocked".
	try {
	    int pCounter1 = 0;
	    int pCounter2 = 0;
	    int pCounter3 = 0;
	    int pCounter5 = 0;
	    Vector<String> pItemNames1 = new Vector<String>(); //'array of item names                    
	    Vector<String> pItemNames2 = new Vector<String>(); //'array of item names                    
	    Vector<String> pItemNames11 = new Vector<String>(); //'array of item names                   
	    Vector<String> pItemNames22 = new Vector<String>(); //'array of item names                   
	    Vector<String> pItemNames3 = new Vector<String>(); //'array of item names                    
	    Vector<String> pItemNamesDNC = new Vector<String>();
	    Vector<String> pItemNames5 = new Vector<String>(); //'array of item names                    
	    String pRefItemName; //'real item name refered by the PO item   
	    String pRefItemName2;
	    Vector<tDNCEntry> arrDNCEntries;
	    Vector<String> arrDNCNames = new Vector<String>();
	    Vector<String> arrDNCVals = new Vector<String>();
	    int iIndex;
	    Item itemToDelete;

	    // pRefItemName holds the Unstripped field name                        
	    // pRefItemName holds the possibly stripped field name                 
	    for (Item item : (Vector<Item>) hPolicy.getItems()) {
		if (item.getName().toUpperCase().endsWith("$HA")) {
		    if (item.getValueString().equals("3")) {
			pRefItemName2 = item.getName().substring(0, item.getName().length() - 3);
			if (item.getName().startsWith("$PREF")) {
			    pRefItemName = pRefItemName2.substring(pRefItemName2.length() - 5);
			} else {
			    pRefItemName = pRefItemName2;
			}
			pItemNames1.add(pRefItemName);
			pItemNames11.add(pRefItemName2);
			pCounter1 = pCounter1 + 1;
		    } else if (item.getValueString().equals("2")) {
			pRefItemName2 = item.getName().substring(0, item.getName().length() - 3);
			if (item.getName().startsWith("$PREF")) {
			    pRefItemName = pRefItemName2.substring(pRefItemName2.length() - 5);
			} else {
			    pRefItemName = pRefItemName2;
			}
			pItemNames2.add(pRefItemName);
			pItemNames22.add(pRefItemName2);
			pCounter2 = pCounter2 + 1;
		    } else if (item.getValueString().equals("1")) {
			pRefItemName = item.getName().substring(0, item.getName().length() - 3);
			if (pRefItemName.equals("")) {
			    if (pRefItemName.endsWith("$SF")) {
				arrDNCNames.add(pRefItemName);
				arrDNCVals.add(hPolicy.getItemValueString(pRefItemName));

				pItemNames3.add(pRefItemName);
				pCounter3 = pCounter3 + 1;

				itemToDelete = hPolicy.getFirstItem(pRefItemName);
				if (itemToDelete != null) {
				    itemToDelete.setSaveToDisk(false);
				}
			    }
			}
		    } else if (item.getValueString().equals("5") || item.getValueString().equals("")) {
			pRefItemName = item.getName().substring(0, item.getName().length() - 3);
			pItemNames5.add(pRefItemName);
			pCounter5 = pCounter5 + 1;
		    }

		}
	    }

	    hPolicy.replaceItemValue("$PolicySetOnceUnstripped", pItemNames11);
	    hPolicy.replaceItemValue("$PolicyHTASetOnceItems", pItemNames1);
	    hPolicy.replaceItemValue("$DPLocked", pItemNames2);
	    hPolicy.replaceItemValue("$DPLockedUnstripped", pItemNames22);
	    hPolicy.replaceItemValue("$DontChangeItems", pItemNames3);
	    hPolicy.replaceItemValue("PolicyDNCNames", arrDNCNames);
	    hPolicy.replaceItemValue("PolicyDNCVals", arrDNCVals);
	    hPolicy.replaceItemValue("AlwaysSetItems", pItemNames5);
	} catch (NotesException e) {
	    e.printStackTrace();
	}
    }

    public boolean verifyUnique(Document hPolicy) {
	try {

	    view = addressBook.getView("Settings");
	    if (view == null) {
		return true;
	    }
	    doc = view.getFirstDocument();
	    if (doc == null) {
		return true;
	    }

	    Vector<Object> fName;
	    while (doc != null) {
		if (doc.getItemValueString("Type").equals(hPolicy.getItemValueString("Type"))) {
		    fName = s.evaluate("@Name([Canonicalize];FullName)", hPolicy);
		    if (fName.size() > 0) {
			Name policyA = s.createName((String) fName.get(0));
			Name policyB = s.createName(doc.getItemValueString("FullName"));

			if (fName.get(0).equals(oldDocNm)) {
			    return false;
			}

			if (policyA.getCanonical().equals(policyB.getCanonical()) && !hPolicy.getUniversalID().equals(doc.getUniversalID())) {
			    //						Msgbox  "A Settings document with this name already exists." & Chr(13) & CONFLICT_RISK,MB_OK + MB_ICONSTOP,"Settings Error"								
			    //						Source.GoToField("FullNameEntry")
			    //						Continue = False
			    return false;
			}
		    }
		}
		doc = view.getNextDocument(doc);
	    }

	} catch (NotesException e) {
	    e.printStackTrace();
	}
	return true;
    }

    public void writeOutProxies(Document hPolicy) {
	try {
	    String proxyFlag;
	    String ftpDisplay;
	    String gopherDisplay;
	    String secDisplay;

	    //		'Sync up the Proxy fields were necessary
	    proxyFlag = hPolicy.getItemValueString("LocAllProxyFlag");
	    ftpDisplay = hPolicy.getItemValueString("$dspLocAllProxy_FTP");
	    gopherDisplay = hPolicy.getItemValueString("$dspLocAllProxy_Gopher");
	    secDisplay = hPolicy.getItemValueString("$dspLocAllProxy_SSL");

	    if (proxyFlag.equals("Yes")) {
		if (ftpDisplay.equals(hPolicy.getItemValueString("LocAllProxy_FTP"))) {
		    hPolicy.replaceItemValue("LocAllProxy_FTP", ftpDisplay);
		}

		if (gopherDisplay.equals(hPolicy.getItemValueString("LocAllProxy_Gopher"))) {
		    hPolicy.replaceItemValue("LocAllProxy_Gopher", gopherDisplay);
		}

		if (secDisplay.equals(hPolicy.getItemValueString("LocAllProxy_SSL"))) {
		    hPolicy.replaceItemValue("LocAllProxy_SSL", secDisplay);
		}

	    }
	} catch (NotesException e) {
	    e.printStackTrace();
	}

    }

    public void incrementGrpPrecedence(Document hPolicy, boolean alreadyExist) {
	try {
	    View gpolView;
	    String precStr;
	    Document doc;

	    gpolView = db.getView("($PoliciesDynamic)");
	    gpolView.refresh();
	    precStr = "Precedence";
	    doc = gpolView.getLastDocument();

	    if (doc == null) {
		hPolicy.replaceItemValue(precStr, 1);
	    } else if (!doc.hasItem(precStr)) {
		hPolicy.replaceItemValue(precStr, 1);
	    } else {
		hPolicy.replaceItemValue(precStr, Integer.parseInt((String) doc.getItemValue(precStr).get(0)) + 1);
	    }
	} catch (NotesException e) {
	    e.printStackTrace();
	}
    }

    public boolean verifyUniquePolicy(Document hPolicy) {
	try {
	    view = addressBook.getView("Settings");
	    if (view == null) {
		return true;
	    }
	    doc = view.getFirstDocument();
	    if (doc == null) {
		return true;
	    }

	    Vector<String> SettingType;
	    Vector<String> fName;
	    while (doc != null) {
		if (doc.getItemValueString("Type").equals(hPolicy.getItemValueString("Type"))) {
		    fName = s.evaluate("@Name([Canonicalize];FullName)", hPolicy);
		    Name policyA = s.createName(fName.get(0));
		    Name policyB = s.createName(doc.getItemValueString("FullName"));

		    if (fName.get(0).equals(oldDocNm)) {
			return false;
		    }

		    if (policyA.getCanonical().equalsIgnoreCase(policyB.getCanonical()) && (hPolicy.getUniversalID().equals(doc.getUniversalID()))) {
			//							Msgbox  "A Settings document with this name already exists." & Chr(13) & CONFLICT_RISK,MB_OK + MB_ICONSTOP,"Settings Error"								
			return false;
		    }
		}
		doc = view.getNextDocument(doc);
	    }

	} catch (NotesException e) {
	    e.printStackTrace();
	}
	return true;
    }
}
